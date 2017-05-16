package br.usp.ime.mac5743.ep1.seminarioime.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import br.usp.ime.mac5743.ep1.seminarioime.R;
import br.usp.ime.mac5743.ep1.seminarioime.api.RestAPIUtil;
import br.usp.ime.mac5743.ep1.seminarioime.util.Preferences;
import br.usp.ime.mac5743.ep1.seminarioime.util.Roles;

public class EditAccountActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String CLASS_NAME = "EditAccountActivity";
    private EditText etNusp;
    private EditText etName;
    private EditText etPassword;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.edit_account);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void fillEditTextForm() {
        etNusp = (EditText) findViewById(R.id.nusp);
        etPassword = (EditText) findViewById(R.id.password);
        etName = (EditText) findViewById(R.id.name);

        etNusp.setText(sharedPref.getString(Preferences.NUSP.name(), null));
        etName.setText(sharedPref.getString(Preferences.FULL_NAME.name(), null));
        etPassword.setText(sharedPref.getString(Preferences.PASSWORD.name(), null));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Load user data and set to fields
        TextView tvName = (TextView) findViewById(R.id.menu_name);
        TextView tvNusp = (TextView) findViewById(R.id.menu_nusp);
        String nusp = sharedPref.getString(Preferences.NUSP.name(), null);
        String name = sharedPref.getString(Preferences.NAME.name(), null);
        tvName.setText(name);
        tvNusp.setText(nusp);
        fillEditTextForm();
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_seminars) {
            goToSeminarActivity();
        } else if (id == R.id.nav_edit_account) {
            Toast.makeText(this, "Hi", Toast.LENGTH_LONG).show();
        } else if (id == R.id.nav_logoff) {
            //SharedPreferences sharedPref = getPreferences(MODE_PRIVATE);
            sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.clear();
            editor.commit();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void requestUpdateUserOnServer(View view) {
        String errorMessage = null;

        String nusp = etNusp.getText().toString();
        String name = etName.getText().toString();
        String password = etPassword.getText().toString();
        String role = sharedPref.getString(Preferences.ROLE.name(), null);

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
                errorMessage = RestAPIUtil.editProfessor(nusp, name, password);
            } else if (role.equalsIgnoreCase(Roles.STUDENT.name())) {
                errorMessage = RestAPIUtil.editStudent(nusp, name, password);
            }
            if (errorMessage != null) {
                Toast.makeText(this, getString(R.string.edit_account_failed), Toast.LENGTH_LONG).show();
            } else {
                sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(Preferences.NUSP.name(), nusp);
                editor.putString(Preferences.FULL_NAME.name(), name);
                editor.putString(Preferences.PASSWORD.name(), password);
                editor.apply();
                Toast.makeText(this, getString(R.string.edit_account_success), Toast.LENGTH_LONG).show();
                goToSeminarActivity();
            }
        }
    }

    private void goToSeminarActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
