package br.usp.ime.mac5743.ep1.seminarioime.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import br.usp.ime.mac5743.ep1.seminarioime.R;
import br.usp.ime.mac5743.ep1.seminarioime.adapter.StudentListAdapter;
import br.usp.ime.mac5743.ep1.seminarioime.api.RestAPIUtil;
import br.usp.ime.mac5743.ep1.seminarioime.bluetooth.BluetoothActivity;
import br.usp.ime.mac5743.ep1.seminarioime.bluetooth.ConnectionThread;
import br.usp.ime.mac5743.ep1.seminarioime.pojo.Student;
import br.usp.ime.mac5743.ep1.seminarioime.util.Preferences;
import br.usp.ime.mac5743.ep1.seminarioime.util.Roles;

public class SeminarDetailsActivity extends AppCompatActivity {

    private String seminarId;
    private String seminarName;
    private ArrayList<Student> studentList;
    private RecyclerView studentListView;
    private StudentListAdapter studentCardListAdapter;
    private static WeakReference<SeminarDetailsActivity> myClassWeakReference;

    int ENABLE_BLUETOOTH = 1;
    int SELECT_PAIRED_DEVICE = 2;

    TextView tvSeminarName;
    private TextView tvSeminarCounter;
    Button cancelBtn;
    Button startListeningBtn;
    SharedPreferences sharedPref;

    private boolean isListening = false;
    private Set<ConnectionThread> connections = Collections.synchronizedSet(new HashSet<ConnectionThread>());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seminar_details);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Bundle b = getIntent().getExtras();
        seminarId = b.getString("seminarId");
        seminarName = b.getString("seminarName");
        toolbar.setTitle(seminarId);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setSeminarDateToUI();

        cancelBtn = (Button) findViewById(R.id.stop_listening);
        startListeningBtn = (Button) findViewById(R.id.listen_bluetooth);
        cancelBtn.setVisibility(View.GONE);

        if (sharedPref.getString(Preferences.ROLE.name(), null).equalsIgnoreCase(Roles.PROFESSOR.name())) {
            LinearLayout ln = (LinearLayout) findViewById(R.id.professor_actions_layout);
            ln.setVisibility(View.VISIBLE);
        } else {
            LinearLayout ln2 = (LinearLayout) findViewById(R.id.student_actions_layout);
            ln2.setVisibility(View.VISIBLE);
        }

        myClassWeakReference = new WeakReference<>(this);
    }

    private void setSeminarDateToUI() {

        tvSeminarName = (TextView) findViewById(R.id.seminar_title);
        tvSeminarCounter = (TextView) findViewById(R.id.seminar_counter);

        tvSeminarName.setText(seminarName);
        try {
            int seminarCount = RestAPIUtil.getAttendanceList(seminarId).size();
            tvSeminarCounter.setText(seminarCount + " " + getString(R.string.student_counter));
        } catch (Exception e) {
            tvSeminarCounter.setText(seminarId);
        }

        if (sharedPref.getString(Preferences.ROLE.name(), null).equalsIgnoreCase(Roles.PROFESSOR.name())) {

            setStudentsList();

            // Load seminars
            studentListView = (RecyclerView) findViewById(R.id.seminar_attendance_list);

            LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
            studentListView.setLayoutManager(mLinearLayoutManager);

            studentCardListAdapter = new StudentListAdapter(studentList, this);
            studentListView.setAdapter(studentCardListAdapter);
            studentListView.refreshDrawableState();
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void confirmAttendanceViaBluetooth(View view) {
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter == null) {
            Snackbar.make(view, "Que pena! Hardware BluetoothActivity não está funcionando :(", Snackbar.LENGTH_LONG).show();
        } else {
//            Snackbar.make(view, "Ótimo! Hardware BluetoothActivity está funcionando :)", Snackbar.LENGTH_LONG).show();
            if (!btAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, ENABLE_BLUETOOTH);
//                Snackbar.make(view, "Solicitando ativação do BluetoothActivity...", Snackbar.LENGTH_LONG).show();
            } else {
//                Toast.makeText( this, "BluetoothActivity já ativado :)", Toast.LENGTH_LONG).show();
                searchPairedDevices(view);
            }
        }
    }

    public void confirmAttendanceViaQRCode(View view) {
        Intent intent = new Intent(this, ReadQRCodeActivity.class);
        intent.putExtra(ReadQRCodeActivity.SEMINAR_ID, this.seminarId);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == ENABLE_BLUETOOTH) {
            if (resultCode != RESULT_OK) {
                Toast.makeText(this, getString(R.string.bluetooth_is_off), Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == SELECT_PAIRED_DEVICE) {
            if (resultCode == RESULT_OK) {
                ConnectionThread connect = new ConnectionThread(data.getStringExtra("btDevAddress"));
                connect.setHandler(new MyHandler(connect));
                connections.add(connect);
                connect.start();
            }
        }

    }

    public void searchPairedDevices(View view) {
        Intent searchPairedDevicesIntent = new Intent(this, BluetoothActivity.class);
        startActivityForResult(searchPairedDevicesIntent, SELECT_PAIRED_DEVICE);
    }

    private static void refreshStudentsView(SeminarDetailsActivity activity) {

        activity.setStudentsList();
        activity.studentCardListAdapter.setStudents(activity.studentList);
        activity.studentListView = (RecyclerView) activity.findViewById(R.id.seminar_attendance_list);
        activity.studentListView.setAdapter(activity.studentCardListAdapter);
        activity.tvSeminarCounter = (TextView) activity.findViewById(R.id.seminar_counter);

        if (activity.studentList != null) {
            activity.tvSeminarCounter.setText(String.format(activity.getString(R.string.total_students_confirmed), activity.studentList.size()));
        } else {
            activity.tvSeminarCounter.setText(activity.getString(R.string.zero_students_confirmed));
        }

    }

    private static class MyHandler extends Handler {
        private AppCompatActivity activity;
        private ConnectionThread connection;

        private MyHandler(ConnectionThread connection) {
            this.connection = connection;
        }

        @Override
        public void handleMessage(Message msg) {

            Log.d("SeminarDetails", "passou:" + msg);

            Bundle bundle = msg.getData();
            byte[] data = bundle.getByteArray("data");
            String string = null;
            if (data != null) string = new String(data);
            if (myClassWeakReference == null || myClassWeakReference.get() == null) {
                connection.cancel();
            } else {
                SeminarDetailsActivity activity = myClassWeakReference.get();
                if (activity.sharedPref.getString(Preferences.ROLE.name(), null).equalsIgnoreCase(Roles.PROFESSOR.name())) {
                    //                if (string != null) {
                    if (ConnectionThread.INIT_CONNECTION.equals(string)) {
                        activity.restartBL();
                    } else if (bundle.getInt(ConnectionThread.ACTION_FIELD) == ConnectionThread.FINISH_ACTION) {
                        activity.removeConnection(connection);
                    } else {
                        if (string != null) {
                            String[] dados = string.split(",");
                            if (dados.length > 1 && activity.seminarId != null && activity.seminarId.equals(dados[1])) {
                                if (RestAPIUtil.confirmAttendance(dados[0], activity.seminarId)) {
                                    refreshStudentsView(activity);
                                    Toast.makeText(activity, String.format(activity.getString(R.string.student_confirmed), dados[0]), Toast.LENGTH_SHORT).show();
                                }
                            } else {

                                Toast.makeText(activity, activity.getString(R.string.incorret_seminar), Toast.LENGTH_SHORT).show();
                            }
                        }
                        connection.cancel();
                    }
                    //                }
                } else if (activity.sharedPref.getString(Preferences.ROLE.name(), null).equalsIgnoreCase(Roles.STUDENT.name())) {
                    if (string != null && !string.isEmpty()) {
                        if (ConnectionThread.INIT_CONNECTION.equals(string)) {
                            activity.sendConfirmationAfterConnection(connection);
                        } else {
                            connection.cancel();
                        }
                    } else if (bundle.getInt(ConnectionThread.ACTION_FIELD) == ConnectionThread.FINISH_ACTION) {
                        activity.removeConnection(connection);
                    }
                }
            }
        }

    }

    private void setStudentsList() {
        ArrayList<JSONObject> arrayJo = RestAPIUtil.getAttendanceList(seminarId);
        studentList = new ArrayList<>();
        if (arrayJo != null) {
            for (JSONObject jo : arrayJo) {
                Student student = new Student();
                try {
                    student.setNusp(jo.getString("student_nusp"));
                    student.setName(RestAPIUtil.getStudent(student.getNusp()).getJSONObject("data").getString("name"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                studentList.add(student);
            }
        }
    }

    public void generateQRCode(View view) {
        Intent intent = new Intent(this, ShowQRCodeActivity.class);
        intent.putExtra(ShowQRCodeActivity.SEMINAR_ID, seminarId);
        startActivity(intent);
    }

    public void listenBluetooth(View view) {
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter != null) {
            if (!btAdapter.isEnabled()) {
                Toast.makeText(this, getString(R.string.bluetooth_is_off), Toast.LENGTH_SHORT).show();
            } else {
                restartBL();
                cancelBtn.setVisibility(View.VISIBLE);
                startListeningBtn.setVisibility(View.GONE);
            }
        }
        isListening = true;
    }

    public void stopListeningBluetooth(View view) {
        cancelConnections();
        cancelBtn.setVisibility(View.GONE);
        startListeningBtn.setVisibility(View.VISIBLE);

        isListening = false;
    }

    private void sendConfirmationAfterConnection(ConnectionThread connect) {
        connect.write((sharedPref.getString(Preferences.NUSP.name(), null) + "," + seminarId).getBytes());
    }

    public synchronized void cancelConnections() {
        for (ConnectionThread c : connections) {
            c.cancel();
        }
        connections.clear();

    }

    public synchronized void restartBL() {
        ConnectionThread connect = new ConnectionThread();
        connect.setHandler(new MyHandler(connect));
        connect.start();
        connections.add(connect);
    }

    public synchronized void removeConnection(ConnectionThread c) {
        connections.remove(c);
    }

    public void onDestroy() {
        super.onDestroy();
        cancelConnections();
    }

}
