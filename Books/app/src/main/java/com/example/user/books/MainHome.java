package com.example.user.books;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

public class MainHome extends AppCompatActivity implements DrawerLocker {

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private boolean firstEntry = true;
    List<NavItemObject> listViewItems;
    MaterialSearchView searchView;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sp = getSharedPreferences("Settings" ,MODE_PRIVATE);
        if(((Global) this.getApplication()).isFirst()){
            ((Global) this.getApplication()).setDay(sp.getBoolean("DayNight", true));
        }
        if(((Global) this.getApplication()).isDay()) {
            sp.edit().putBoolean("DayNight", true).apply();
            setTheme(R.style.AppTheme);
        } else {
            sp.edit().putBoolean("DayNight", false).apply();
            setTheme(R.style.AppTheme2);
        }
        setContentView(R.layout.activity_home);

        listViewItems = new ArrayList<>();

        if(((Global) this.getApplication()).isFirst()) {
            ((Global) this.getApplication()).setFpos(0);
            ((Global) this.getApplication()).setFirst(false);
            listViewItems.add(new NavItemObject("Home", R.drawable.ic_action_home));
            listViewItems.add(new NavItemObject("Library", R.drawable.ic_action_library));
            listViewItems.add(new NavItemObject("Account", R.drawable.ic_action_account));
            listViewItems.add(new NavItemObject("Logout", R.drawable.ic_action_logout));
            ((Global) this.getApplication()).setListViewItems(listViewItems);
        }
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerList = findViewById(R.id.left_drawer);
        searchView = findViewById(R.id.search);

        //Adding toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Adding navigation drawer
        listViewItems = ((Global) this.getApplication()).getListViewItems();
        mDrawerList.setAdapter(new CustomAdapter(this, listViewItems));

        mDrawerToggle = new ActionBarDrawerToggle(MainHome.this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {

            //Called when a drawer has settled in a completely closed state.
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                if(getSupportActionBar() != null) {
                    int tm = ((Global) MainHome.this.getApplication()).getFpos();
                    getSupportActionBar().setTitle(getTitle(tm));
                }
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            //Called when a drawer has settled in a completely open state.
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if(getSupportActionBar() != null)
                    getSupportActionBar().setTitle("Options");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.setDrawerIndicatorEnabled(true);

        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for(int i = 0; i < listViewItems.size() - 1; i++)
                    if(position != i) mDrawerList.getChildAt(i).setBackgroundColor(Color.WHITE);
                    else mDrawerList.getChildAt(i).setBackgroundColor(Color.parseColor("#DCDCDC"));
                    selectItemFragment(position);
                    if (position== listViewItems.size()-1) position= 0;
                    ((Global) MainHome.this.getApplication()).setFpos(position);
            }
        });

        //First Entry into activity
        if(firstEntry) {
            selectItemFragment(((Global) this.getApplication()).getFpos());
        }
    }

    //Adding Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(firstEntry) {
            if(mDrawerList.getChildAt(((Global) this.getApplication()).getFpos()) != null)
                mDrawerList.getChildAt(((Global) this.getApplication()).getFpos()).setBackgroundColor(Color.parseColor("#DCDCDC"));
            if(getSupportActionBar() != null)
                getSupportActionBar().setTitle(listViewItems.get(((Global) this.getApplication()).getFpos()).getName());
            firstEntry = false;
        }
        return true;
    }

    //Adding Drawer
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        mDrawerLayout.isDrawerOpen(mDrawerList);
        return super.onPrepareOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    //Selects fragment from navigation drawer
    @SuppressLint("RtlHardcoded")
    private void selectItemFragment(int position){
        if(position != ((Global) this.getApplication()).getFpos() || firstEntry) {
            Fragment fragment;
            FragmentManager fragmentManager = getSupportFragmentManager();
            switch (position) {
                default:
                case 0:
                    fragment = new SwipeViewFragment();
                    break;
                case 1:
                    fragment = new LibraryFragment();
                    break;
                case 2:
                    fragment = new AccountFragment();
                    break;
                case 3:
                    final AlertDialog alertDialog = new AlertDialog.Builder(MainHome.this).create();
                    alertDialog.setTitle("Log Out");
                    alertDialog.setMessage("Are you sure you want to log out?");

                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.cancel();
                        }
                    });

                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Exit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });

                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "LogOut", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            sp.edit().putBoolean("NotLoggedIn", true).apply();
                            ((Global)MainHome.this.getApplication()).tutorialNotDone(true);
                            ((Global) MainHome.this.getApplication()).removeListener();
                            startActivity(new Intent(MainHome.this, LoginActivity.class));
                            MyDBHandler dbHandler = new MyDBHandler(MainHome.this, null);
                            dbHandler.deleteTable();
                            ((Global) MainHome.this.getApplication()).getNadapter().clear();
                            MainHome.this.finish();
                        }
                    });
                    alertDialog.show();
                    return;
            }
            fragment.setEnterTransition(new Slide(Gravity.RIGHT));
            fragmentManager.beginTransaction().replace(R.id.main_fragment_container, fragment).commit();
        }
        if(!firstEntry) {
            mDrawerList.setItemChecked(position, true);
            mDrawerLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mDrawerLayout.closeDrawer(mDrawerList);
                }
            }, 25);
        }
    }

    String getTitle(int i) {
        return listViewItems.get(i).getName();
    }

    //Back pressed logic
    boolean doubleBackToExitPressedOnce;
    public void onBackPressed() {
        if(searchView.isSearchOpen()) {
            searchView.closeSearch();
        } else if(mDrawerLayout.isDrawerOpen(mDrawerList)) {
            mDrawerLayout.closeDrawer(mDrawerList);
        } else {
            FragmentManager fm = getSupportFragmentManager();
            if(fm.getBackStackEntryCount() >= 1)
                fm.popBackStack();
            else {
                //Logic for double press back to exit when on main activity
                if (doubleBackToExitPressedOnce) {
                    super.onBackPressed();
                    return;
                }
                doubleBackToExitPressedOnce = true;
                Toast.makeText(this, "Press one more time to exit",
                        Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 1500);
            }
        }
    }

    public void setDrawerLocked(boolean enabled, String name){
        if(getSupportActionBar() != null)
            getSupportActionBar().setTitle(name);
        if(enabled){
            AppBarLayout appBarLayout = findViewById(R.id.appbar);
            appBarLayout.setExpanded(true, true);
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            mDrawerToggle.setDrawerIndicatorEnabled(false);
        }else{
            AppBarLayout appBarLayout = findViewById(R.id.appbar);
            appBarLayout.setExpanded(true, true);
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            mDrawerToggle.setDrawerIndicatorEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

