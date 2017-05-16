package br.usp.ime.mac5743.ep1.seminarioime.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import br.usp.ime.mac5743.ep1.seminarioime.R;
import br.usp.ime.mac5743.ep1.seminarioime.util.Preferences;

public abstract class MenuActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener {
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView( getContentView() );
            sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitle( getIdTitle());
            setSupportActionBar(toolbar);


            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_seminars) {
            goToSeminarActivity();
        } else if (id == R.id.nav_edit_account) {
            Intent intent = new Intent(this, EditAccountActivity.class);
            startActivity(intent);
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

    private void goToSeminarActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @SuppressWarnings("SameReturnValue")
    protected abstract int getIdTitle();

    @SuppressWarnings("SameReturnValue")
    protected abstract int getContentView();

    @Override
    public void onBackPressed() {
    }
}
