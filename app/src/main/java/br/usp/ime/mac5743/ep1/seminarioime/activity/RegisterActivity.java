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

import org.json.JSONException;
import org.json.JSONObject;

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

    private String nusp;
    private String name;
    private String password;
    private String role;

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
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString("PASSWD", etPassword.getText().toString() );
        savedInstanceState.putString( "FULLNAME", etName.getText().toString() );
        savedInstanceState.putString( "NUSP", etNusp.getText().toString() );
        savedInstanceState.putInt( "ROLE", spRole.getSelectedItemPosition() );

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        etNusp.setText(savedInstanceState.getString("NUSP"));
        etName.setText(savedInstanceState.getString("FULLNAME"));
        etPassword.setText(savedInstanceState.getString("PASSWD"));
        spRole.setSelection( savedInstanceState.getInt("ROLE") );
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void requestRegistrationOnServer(View view) {
        String errorMessage = null;
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        nusp = etNusp.getText().toString();
        name = etName.getText().toString();
        password = etPassword.getText().toString();
        role = spRole.getSelectedItem().toString();

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
                saveUserDataToPreferences();
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
    }

    private void loadDashboard(AlertDialog dialog) {
        dialog.dismiss();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void saveUserDataToPreferences() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(Preferences.NUSP.name(), nusp);
        editor.putString(Preferences.NAME.name(), name.split(" ")[0]);
        editor.putString(Preferences.FULL_NAME.name(), name);
        editor.putString(Preferences.PASSWORD.name(), password);
        editor.putString(Preferences.ROLE.name(), role);
        editor.commit();
    }
}
