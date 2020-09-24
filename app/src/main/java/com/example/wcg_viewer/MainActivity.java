package com.example.wcg_viewer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";

    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;
    private Fragment mOverviewFragment, mResultTaskFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolbar);

        mDrawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

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
                if(mOverviewFragment == null) mOverviewFragment = OverviewFragment.newInstance();
                mToolbar.setTitle(R.string.menu_home);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, OverviewFragment.newInstance()).commit();
                break;
            case R.id.navigation_item_tasks:
                if(mResultTaskFragment == null) mResultTaskFragment = RecentTaskFragment.newInstance();
                mToolbar.setTitle(R.string.menu_tasks);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,  RecentTaskFragment.newInstance()).commit();
                break;
            default:
                Log.e(TAG, "onNavigationItemSelected: id error = " + item.getItemId());
                Toast.makeText(this, "onNavigationItemSelected: id error = " + item.getItemId(), Toast.LENGTH_SHORT).show();
                return false;
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}