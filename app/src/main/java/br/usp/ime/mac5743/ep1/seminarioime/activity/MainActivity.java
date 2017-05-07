package br.usp.ime.mac5743.ep1.seminarioime.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
