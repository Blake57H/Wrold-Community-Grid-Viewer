package com.example.wcg_viewer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.text.DateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, RecentTaskFragment.Callbacks {
    private static final String TAG = "MainActivity";
    private static final String KEY_TOOLBAR_TITLE = "toolbar_title";

    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;
    private Fragment mOverviewFragment, mResultTaskFragment;
    private TextView mLastUpdateTextView;
    private String toolbarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mToolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolbar);

        mDrawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.navigation_item_overview);
        toolbarTitleSetter(navigationView.getCheckedItem());
        mLastUpdateTextView = navigationView.getHeaderView(0).findViewById(R.id.nav_header_last_update_time_textView);

        setLastUpdateTextView();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, OverviewFragment.newInstance()).commit();
            navigationView.setCheckedItem(R.id.navigation_item_overview);
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            return;
        }
        super.onBackPressed();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_item_overview:
                if (mOverviewFragment == null) mOverviewFragment = OverviewFragment.newInstance();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, OverviewFragment.newInstance()).commit();
                break;
            case R.id.navigation_item_tasks:
                if (mResultTaskFragment == null)
                    mResultTaskFragment = RecentTaskFragment.newInstance();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, RecentTaskFragment.newInstance()).commit();
                break;
            case R.id.navigation_item_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            default:
                Log.e(TAG, "onNavigationItemSelected: id error = " + item.getItemId());
                Toast.makeText(this, "onNavigationItemSelected: id error = " + item.getItemId(), Toast.LENGTH_SHORT).show();
                return false;
        }
        toolbarTitleSetter(item);
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }



    private void toolbarTitleSetter(MenuItem item) {
        if (item == null) {
            mToolbar.setTitle(R.string.app_name);
            return;
        }

        switch (item.getItemId()) {
            case R.id.navigation_item_overview:
                mToolbar.setTitle(R.string.menu_home);
                break;
            case R.id.navigation_item_tasks:
                mToolbar.setTitle(R.string.menu_tasks);
                break;
            default:
        }
    }

    private void setLastUpdateTextView() {
        if (mLastUpdateTextView == null) {
            Toast.makeText(this, "mLastUpdateTextView not found", Toast.LENGTH_SHORT).show();
            return;
        }
        //Date date = SettingsDataLab.getInstance(this).getLastUpdateDate();
        String date = PreferenceManager.getDefaultSharedPreferences(this).getString(getString(R.string.preferences_key_last_updated), null);
        String dateString;
        if (date != null) {
            DateFormat df = DateFormat.getDateTimeInstance();
            dateString = getString(R.string.nav_header_subtitle, date);
        } else {
            dateString = getString(R.string.nav_header_subtitle, getString(R.string.nav_header_subtitle_never));
        }
        mLastUpdateTextView.setText(dateString);
    }

    @Override
    public void lastUpdateDateChanged() {
        setLastUpdateTextView();
    }
}