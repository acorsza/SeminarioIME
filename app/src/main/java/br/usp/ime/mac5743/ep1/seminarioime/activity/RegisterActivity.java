package br.usp.ime.mac5743.ep1.seminarioime.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

import java.util.ArrayList;

import br.usp.ime.mac5743.ep1.seminarioime.R;
import br.usp.ime.mac5743.ep1.seminarioime.api.RestAPIUtil;
import br.usp.ime.mac5743.ep1.seminarioime.util.Preferences;
import br.usp.ime.mac5743.ep1.seminarioime.util.Roles;

public class RegisterActivity extends AppCompatActivity {

    private static final String CLASS_NAME = "RegisterActivity";
    private EditText etNusp;
    private EditText etName;
    private EditText etPassword;
    private Spinner spRole;
    private Switch swAutoConnect;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        etNusp = (EditText) findViewById(R.id.nusp);
        etName = (EditText) findViewById(R.id.name);
        etPassword = (EditText) findViewById(R.id.password);
        spRole = (Spinner) findViewById(R.id.roles);
        swAutoConnect = (Switch) findViewById(R.id.auto_connect);
        //RestAPIUtil.getAllStudents();
        //RestAPIUtil.getStudent("1234");
        /*
        new Thread(){
            public void run(){
                RestAPIUtil.getAllStudents();
            }
        }.start();
        */
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void requestRegistrationOnServer(View view) {
        Log.d(CLASS_NAME, "requestRegistrationOnServer");

        String errorMessage = null;
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        String nusp = etNusp.getText().toString();
        String name = etName.getText().toString();
        String password = etPassword.getText().toString();
        String role = spRole.getSelectedItem().toString();

        ArrayList<String> invalidFields = new ArrayList();

        if (nusp == null || nusp.equals("")) {
            invalidFields.add("nusp");
        }

        if (name == null || name.equals("")) {
            invalidFields.add("name");
        }

        if (password == null || password.equals("")) {
            invalidFields.add("password");
        }

        if (invalidFields.size() > 0) {
            Snackbar.make(view, getString(R.string.empty_fields_message) + invalidFields.toString(), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        } else {
            if (role.equalsIgnoreCase(Roles.PROFESSOR.name())) {
                errorMessage = RestAPIUtil.addProfessor(nusp, name, password);
            } else if (role.equalsIgnoreCase(Roles.STUDENT.name())) {
                errorMessage = RestAPIUtil.addStudent(nusp, name, password);
            }
        }

        if (errorMessage != null) {
            alertDialog.setTitle(getString(R.string.registration_failed));
            alertDialog.setMessage(errorMessage);
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        } else {
            if (swAutoConnect.isChecked()) {
                sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(Preferences.NUSP.name(), nusp);
                editor.putString(Preferences.PASSWORD.name(), password);
                editor.commit();
            }
            alertDialog.setTitle(getString(R.string.registration_succeeded));
            alertDialog.setMessage(getString(R.string.account_created));
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.ok),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            loadDashboard(alertDialog);
                        }
                    });
            alertDialog.show();
        }
    }

    private void loadDashboard(AlertDialog dialog) {
        dialog.dismiss();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
