package br.usp.ime.mac5743.ep1.seminarioime.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import br.usp.ime.mac5743.ep1.seminarioime.R;
import br.usp.ime.mac5743.ep1.seminarioime.adapter.SeminarCardListAdapter;
import br.usp.ime.mac5743.ep1.seminarioime.api.RestAPIUtil;
import br.usp.ime.mac5743.ep1.seminarioime.pojo.Seminar;
import br.usp.ime.mac5743.ep1.seminarioime.util.Preferences;
import br.usp.ime.mac5743.ep1.seminarioime.util.Roles;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private SharedPreferences sharedPref;

    private List<Seminar> seminarList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.seminars);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Load seminars
        loadSeminars();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Add a seminar
                openDialog();
            }
        });

        if (sharedPref.getString(Preferences.ROLE.name(), null).equalsIgnoreCase(Roles.PROFESSOR.name())) {
            fab.setVisibility(View.VISIBLE);
        } else {
            fab.setVisibility(View.INVISIBLE);
        }
    }

    private void openDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.seminar_add_title));

        final EditText input = new EditText(this);
        input.setPadding(50, 20, 50, 20);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (RestAPIUtil.addSeminar(input.getText().toString())) {
                    Toast.makeText(getApplicationContext(), getString(R.string.seminar_add_success), Toast.LENGTH_LONG).show();
                    loadSeminars();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.seminar_add_failed), Toast.LENGTH_LONG).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setEnabled(false);

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s)) {
                    dialog.getButton(
                            AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                } else {
                    dialog.getButton(
                            AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                }

            }
        });
    }

    private void loadSeminars() {
        try {
            seminarList = new ArrayList<>();
            RecyclerView seminarListView = (RecyclerView) findViewById(R.id.seminar_list);

            LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
            seminarListView.setLayoutManager(mLinearLayoutManager);
            JSONArray ja = RestAPIUtil.getAllSeminars().getJSONArray("data");

            for (int i = 0; i < ja.length(); i++) {
                JSONObject jo = ja.getJSONObject(i);
                seminarList.add(new Seminar(jo.getString("id"), jo.getString("name")));
            }

            SeminarCardListAdapter seminarCardListAdapter = new SeminarCardListAdapter(seminarList, this);
            seminarListView.setAdapter(seminarCardListAdapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
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
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_edit_account) {
            Intent intent = new Intent(this, EditAccountActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_logoff) {
            logoff();
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logoff() {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.apply();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
