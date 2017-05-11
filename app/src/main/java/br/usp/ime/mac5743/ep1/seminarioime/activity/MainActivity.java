package br.usp.ime.mac5743.ep1.seminarioime.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
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
import br.usp.ime.mac5743.ep1.seminarioime.pojo.Professor;
import br.usp.ime.mac5743.ep1.seminarioime.pojo.Seminar;
import br.usp.ime.mac5743.ep1.seminarioime.util.Preferences;
import br.usp.ime.mac5743.ep1.seminarioime.util.Roles;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private SharedPreferences sharedPref;
    private TextView tvNusp;
    private TextView tvName;
    private String nusp;
    private String name;

    private List<Seminar> seminarList;
    private RecyclerView seminarListView;

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
        seminarListView = (RecyclerView) findViewById(R.id.seminar_list);

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        seminarListView.setLayoutManager(mLinearLayoutManager);

        loadSeminars();

        SeminarCardListAdapter seminarCardListAdapter = new SeminarCardListAdapter(seminarList, this);
        seminarListView.setAdapter(seminarCardListAdapter);

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
            fab.setVisibility(View.VISIBLE);
        }
    }

    private void openDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.seminar_add_title));

        final EditText input = new EditText(this);
        input.setPadding(50,20,50,20);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (RestAPIUtil.addSeminar(input.getText().toString())) {
                    Toast.makeText(getApplicationContext(), getString(R.string.seminar_add_success), Toast.LENGTH_LONG).show();
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

        builder.show();
    }

    private void loadSeminars() {

        seminarList = new ArrayList<>();

        try {
            JSONArray ja = RestAPIUtil.getAllSeminars().getJSONArray("data");

            for (int i = 0; i < ja.length(); i++) {
                JSONObject jo = ja.getJSONObject(i);
                seminarList.add(new Seminar(jo.getString("id"), jo.getString("name")));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        Log.i("T", seminarList.toString());
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Load user data and set to fields
        tvName = (TextView) findViewById(R.id.menu_name);
        tvNusp = (TextView) findViewById(R.id.menu_nusp);
        nusp = sharedPref.getString(Preferences.NUSP.name(), null);
        name = sharedPref.getString(Preferences.NAME.name(), null);
        tvName.setText(name);
        tvNusp.setText(nusp);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_seminars) {
            Toast.makeText(this, "Hi", Toast.LENGTH_LONG).show();
        } else if (id == R.id.nav_edit_account) {
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
        editor.commit();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
