package com.example.user.books;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    int access = 0;
    String cat = "none", userID;
    RecyclerView rview;
    List<Lent> data;
    BookAdapter bookAdapter;
    MaterialSearchView searchView;
    Toolbar toolbar;

    public HomeFragment() {

    }
    @SuppressLint("ValidFragment")
    public HomeFragment(int access) {
        this.access = access;
    }

    @SuppressLint("ValidFragment")
    public HomeFragment(String cat) {
        this.cat = cat;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        int ori = getScreenOrientation();
        if(ori == Configuration.ORIENTATION_PORTRAIT) ori = 3; else ori = 4;
        data = ((Global) getContext().getApplicationContext()).getCdata();
        userID = ((Global) getContext().getApplicationContext()).getUsername();

        if(!cat.equals("none")) {
            List<Lent> tdata = new ArrayList<>();
            ((DrawerLocker)getActivity()).setDrawerLocked(true, cat);
            for(Lent l : data)
                if(l.getSubject() != null && l.isAvailable() && !l.getOwner().equals(userID) && l.getBorrower() == null)
                    if(l.getSubject().equals(cat))
                        tdata.add(l);
            bookAdapter = new BookAdapter(getContext(), tdata, access);
            ori = 1;
        } else {
            if(userID != null) {
                List<Lent> tdata = new ArrayList<>();
                if (access == 1) {
                    for (Lent l : data)
                        if(l.getBorrower() != null)
                            if (l.getBorrower().equals(userID) && !l.isAvailable())
                                tdata.add(l);
                } else if (access == 2) {
                    for (Lent l : data)
                        if(l.getOwner() != null)
                            if (l.getOwner().equals(userID) && !l.isAvailable())
                                tdata.add(l);
                } else if (access == 3) {
                    for (Lent l : data) {
                        if(l.isAvailable()) {
                            if (l.getOwner() != null)
                                if (l.getOwner().equals(userID)) {
                                    tdata.add(l);
                                    continue;
                                }
                            if (l.getBorrower() != null)
                                if (l.getBorrower().equals(userID))
                                    tdata.add(l);
                        }
                    }
                }
                bookAdapter = new BookAdapter(getContext(), tdata, access);
            }
        }
        rview = new RecyclerView(getContext());
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), ori);
        rview.setLayoutManager(layoutManager);
        rview.setAdapter(bookAdapter);
        rview.setPadding(8, 0, 8, 0);

        setHasOptionsMenu(true);
        searchView = getActivity().findViewById(R.id.search);

        return rview;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_items, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        toolbar = getActivity().findViewById(R.id.toolbar);
        searchView.setMenuItem(item);
        searchView.setOnQueryTextListener(
                new MaterialSearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        searchView.clearFocus();
                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        bookAdapter.filter(newText);
                        return true;
                    }
                }
        );
    }

    //Back press for search
    /*@Override
    public void onBackPressed() {
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        } else {
            super.onBackPressed();
        }
    }*/

    //Get orientation
    public int getScreenOrientation()
    {
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        int orientation;
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        if(width < height){
            orientation = Configuration.ORIENTATION_PORTRAIT;
        } else {
            orientation = Configuration.ORIENTATION_LANDSCAPE;
        }
        return orientation;
    }

    @Override
    public void onResume() {
        searchView.closeSearch();
        searchView.clearFocus();

        if(((Global) getActivity().getApplication()).isRefreshNeeded0()) {
            SystemClock.sleep(250);
            FragmentManager fm = getActivity().getSupportFragmentManager();
            if (fm.getBackStackEntryCount() >= 1)
                fm.popBackStack();
            ((Global) getActivity().getApplication()).setRefreshNeeded(false, 0);
        }
        super.onResume();
    }

    @Override
    public void onDestroy() {
        ((DrawerLocker)getActivity()).setDrawerLocked(false, "Home");
        super.onDestroy();
    }

}