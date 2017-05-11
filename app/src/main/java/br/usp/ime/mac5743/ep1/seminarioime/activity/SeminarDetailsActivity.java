package br.usp.ime.mac5743.ep1.seminarioime.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;

import br.usp.ime.mac5743.ep1.seminarioime.R;
import br.usp.ime.mac5743.ep1.seminarioime.api.RestAPIUtil;
import br.usp.ime.mac5743.ep1.seminarioime.bluetooth.BluetoothActivity;
import br.usp.ime.mac5743.ep1.seminarioime.bluetooth.ConnectionThread;
import br.usp.ime.mac5743.ep1.seminarioime.util.Preferences;

import static br.usp.ime.mac5743.ep1.seminarioime.R.id.nusp;
import static br.usp.ime.mac5743.ep1.seminarioime.R.id.toolbar;
import static br.usp.ime.mac5743.ep1.seminarioime.api.RestAPIUtil.getAttendanceList;

public class SeminarDetailsActivity extends AppCompatActivity {

    private String seminarId;
    private String seminarName;
    private ArrayList<JSONObject> studentList;


    int ENABLE_BLUETOOTH = 1;
    int SELECT_PAIRED_DEVICE = 2;
    int SELECT_DISCOVERED_DEVICE = 3;

    TextView tvSeminarName;
    TextView tvSeminarCounter;
    static TextView statusMessage;
    ConnectionThread connect;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seminar_details);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        Bundle b = getIntent().getExtras();
        this.seminarId = b.getString("seminarId");
        this.seminarName = b.getString("seminarName");

        setSeminarDateToUI();


    }

    private void setSeminarDateToUI() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(this.seminarId);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setStudentsList();

        statusMessage = (TextView) findViewById(R.id.status_message);
        tvSeminarName = (TextView) findViewById(R.id.seminar_title);
        tvSeminarCounter = (TextView) findViewById(R.id.seminar_counter);

        tvSeminarName.setText(this.seminarName);
        if (this.studentList != null) {
            tvSeminarCounter.setText(this.studentList.size() + " " + getString(R.string.student_counter));
        } else {
            tvSeminarCounter.setText("0 " + getString(R.string.student_counter));
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
        RestAPIUtil.confirmAttendance(sharedPref.getString(Preferences.NUSP.name(),null),seminarId);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == ENABLE_BLUETOOTH) {
            if (resultCode == RESULT_OK) {
                statusMessage.setText("BluetoothActivity ativado :)");
            } else {
                statusMessage.setText("BluetoothActivity não ativado :(");
            }
        } else if (requestCode == SELECT_PAIRED_DEVICE) {
            if (resultCode == RESULT_OK) {
                statusMessage.setText("Você selecionou " + data.getStringExtra("btDevName") + "\n"
                        + data.getStringExtra("btDevAddress"));
                ConnectionThread connect = new ConnectionThread(data.getStringExtra("btDevAddress"));
                connect.start();
            } else {
                statusMessage.setText("Nenhum dispositivo selecionado :(");
            }
        }

    }

    public void searchPairedDevices(View view) {

        Intent searchPairedDevicesIntent = new Intent(this, BluetoothActivity.class);
        startActivityForResult(searchPairedDevicesIntent, SELECT_PAIRED_DEVICE);
    }

    public void discoverDevices(View view) {

        Intent searchPairedDevicesIntent = new Intent(this, BluetoothActivity.class);
        startActivityForResult(searchPairedDevicesIntent, SELECT_DISCOVERED_DEVICE);
    }

    public void sendMessage(View view) {
        String messageBoxString = "Mensagem";
        byte[] data = messageBoxString.getBytes();
        //connect.write(data);
    }

    public static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            Bundle bundle = msg.getData();
            byte[] data = bundle.getByteArray("data");
            String dataString = new String(data);

            if (dataString.equals("---N"))
                statusMessage.setText("Ocorreu um erro durante a conexão D:");
            else if (dataString.equals("---S"))
                statusMessage.setText("Conectado :D");
        }
    };

    private void setStudentsList() {
        this.studentList = RestAPIUtil.getAttendanceList(this.seminarId);
    }
}
