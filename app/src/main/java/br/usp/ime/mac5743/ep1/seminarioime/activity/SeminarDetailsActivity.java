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
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;

import br.usp.ime.mac5743.ep1.seminarioime.R;
import br.usp.ime.mac5743.ep1.seminarioime.adapter.StudentListAdapter;
import br.usp.ime.mac5743.ep1.seminarioime.api.RestAPIUtil;
import br.usp.ime.mac5743.ep1.seminarioime.bluetooth.BluetoothActivity;
import br.usp.ime.mac5743.ep1.seminarioime.bluetooth.ConnectionThread;
import br.usp.ime.mac5743.ep1.seminarioime.pojo.Student;
import br.usp.ime.mac5743.ep1.seminarioime.util.Preferences;
import br.usp.ime.mac5743.ep1.seminarioime.util.Roles;

public class SeminarDetailsActivity extends AppCompatActivity {

    private static String seminarId;
    private String seminarName;
    private static ArrayList<Student> studentList;
    private static RecyclerView studentListView;
    static StudentListAdapter studentCardListAdapter;


    int ENABLE_BLUETOOTH = 1;
    int SELECT_PAIRED_DEVICE = 2;

    TextView tvSeminarName;
    static TextView tvSeminarCounter;
    Button cancelBtn;
    Button startListeningBtn;
    private static ConnectionThread connect;
    private static SharedPreferences sharedPref;

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
    }

    private void setSeminarDateToUI() {

        setStudentsList();

        tvSeminarName = (TextView) findViewById(R.id.seminar_title);
        tvSeminarCounter = (TextView) findViewById(R.id.seminar_counter);

        tvSeminarName.setText(seminarName);
        if (studentList != null) {
            tvSeminarCounter.setText(this.studentList.size() + " " + getString(R.string.student_counter));
        } else {
            tvSeminarCounter.setText("0 " + getString(R.string.student_counter));
        }

        // Load seminars
        studentListView = (RecyclerView) findViewById(R.id.seminar_attendance_list);

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        studentListView.setLayoutManager(mLinearLayoutManager);

        studentCardListAdapter = new StudentListAdapter(studentList, this);
        studentListView.setAdapter(studentCardListAdapter);
        studentListView.refreshDrawableState();

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
            Snackbar.make(view, "Ótimo! Hardware BluetoothActivity está funcionando :)", Snackbar.LENGTH_LONG).show();
            if (!btAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, ENABLE_BLUETOOTH);
                Snackbar.make(view, "Solicitando ativação do BluetoothActivity...", Snackbar.LENGTH_LONG).show();
            } else {
                Snackbar.make(view, "BluetoothActivity já ativado :)", Snackbar.LENGTH_LONG).show();
                searchPairedDevices(view);
            }
        }
    }

    public void confirmAttendanceViaQRCode(View view) {
        Intent intent = new Intent(this, ReadQRCodeActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == ENABLE_BLUETOOTH) {
            if (resultCode == RESULT_OK) {
                //statusMessage.setText("BluetoothActivity ativado :)");
            } else {
                Toast.makeText(this, getString(R.string.bluetooth_is_off), Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == SELECT_PAIRED_DEVICE) {
            if (resultCode == RESULT_OK) {
                //statusMessage.setText("Você selecionou " + data.getStringExtra("btDevName") + "\n"
                //         + data.getStringExtra("btDevAddress"));
                connect = new ConnectionThread(data.getStringExtra("btDevAddress"));
                connect.start();
            } else {
                //statusMessage.setText("Nenhum dispositivo selecionado :(");
            }
        }

    }

    public void searchPairedDevices(View view) {
        Intent searchPairedDevicesIntent = new Intent(this, BluetoothActivity.class);
        startActivityForResult(searchPairedDevicesIntent, SELECT_PAIRED_DEVICE);
    }

    public static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            Bundle bundle = msg.getData();
            byte[] data = bundle.getByteArray("data");
            String string = new String(data);

            if (sharedPref.getString(Preferences.ROLE.name(), null).equalsIgnoreCase(Roles.PROFESSOR.name())) {
                restartBL();
                if (string != null) {
                    if (RestAPIUtil.confirmAttendance(string, seminarId)) {
                        setStudentsList();
                        studentCardListAdapter.setStudents(studentList);
                        studentListView.setAdapter(studentCardListAdapter);
                        if (studentList != null) {
                            tvSeminarCounter.setText(studentList.size() + " are registered");
                        } else {
                            tvSeminarCounter.setText("0 Students");
                        }
                    }
                } else {
                    //statusMessage.setText("Ocorreu um erro durante a conexão D:");
                }
            } else if (sharedPref.getString(Preferences.ROLE.name(), null).equalsIgnoreCase(Roles.STUDENT.name())) {
                if (!string.isEmpty()) {
                    sendConfirmationAfterConnection();
                }
            }
        }
    };


    private static void setStudentsList() {
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
                connect = new ConnectionThread();
                connect.start();
                cancelBtn.setVisibility(View.VISIBLE);
                startListeningBtn.setVisibility(View.GONE);
            }
        }
    }

    public void stopListeningBluetooth(View view) {
        connect.cancel();
        cancelBtn.setVisibility(View.GONE);
        startListeningBtn.setVisibility(View.VISIBLE);
    }

    private static void sendConfirmationAfterConnection() {
        connect.write(sharedPref.getString(Preferences.NUSP.name(), null).getBytes());
    }

    public static void restartBL() {
        connect.cancel();
        connect = new ConnectionThread();
        connect.start();
    }
}
