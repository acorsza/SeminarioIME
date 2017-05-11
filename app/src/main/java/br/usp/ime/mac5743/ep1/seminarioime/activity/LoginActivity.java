package br.usp.ime.mac5743.ep1.seminarioime.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import br.usp.ime.mac5743.ep1.seminarioime.R;
import br.usp.ime.mac5743.ep1.seminarioime.api.RestAPIUtil;
import br.usp.ime.mac5743.ep1.seminarioime.util.Preferences;

public class LoginActivity extends AppCompatActivity {

    // JSON Values
    private final String DATA = "data";
    private final String NAME = "name";

    private EditText etNusp;
    private EditText etPassword;
    private Spinner spRole;
    private Switch swAutoConnect;

    private String nusp;
    private String password;
    private String role;

    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etNusp = (EditText) findViewById(R.id.nusp);
        etPassword = (EditText) findViewById(R.id.password);
        spRole = (Spinner) findViewById(R.id.roles);
        swAutoConnect = (Switch) findViewById(R.id.auto_connect);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (sharedPref.getBoolean(Preferences.AUTO_CONNECT.name(), false)) {
            nusp = sharedPref.getString(Preferences.NUSP.name(), null);
            password = sharedPref.getString(Preferences.PASSWORD.name(), null);
            role = sharedPref.getString(Preferences.ROLE.name(), null);
            requestLoginOnServer(null);
        }
    }

    public void requestLoginOnServer(View view) {
        if (view != null) {
            nusp = etNusp.getText().toString();
            password = etPassword.getText().toString();
            role = spRole.getSelectedItem().toString();
        }
        if (RestAPIUtil.login(nusp, password, role)) {
            Toast.makeText(this, getString(R.string.login_successful), Toast.LENGTH_SHORT).show();
            if (swAutoConnect.isChecked()) {
                setAutoConnectOn();
            }
            saveUserDataToPreferences();
            loadDashboard();

        } else {
            Toast.makeText(this, getString(R.string.login_failed), Toast.LENGTH_LONG).show();
        }
    }

    public void goToRegistrationActivity(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    private void setAutoConnectOn() {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(Preferences.AUTO_CONNECT.name(), true);
        editor.commit();
    }

    private void saveUserDataToPreferences() {
        try {
            JSONObject jo = RestAPIUtil.getStudent(nusp).getJSONObject(DATA);

            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(Preferences.NUSP.name(), nusp);
            editor.putString(Preferences.NAME.name(), jo.getString(NAME).split(" ")[0]);
            editor.putString(Preferences.FULL_NAME.name(), jo.getString(NAME));
            editor.putString(Preferences.PASSWORD.name(), password);
            editor.putString(Preferences.ROLE.name(), role);
            editor.commit();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
    }

    private void loadDashboard() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
