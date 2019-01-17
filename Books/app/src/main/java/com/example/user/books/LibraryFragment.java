package com.example.user.books;


import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;

import java.util.ArrayList;
import java.util.List;


public class LibraryFragment extends Fragment {

    View view;
    Toolbar toolbar;
    TabLayout tabs;
    ViewPager viewPager;
    Adapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.library_tabs,container, false);
        // Setting ViewPager for each Tabs
        viewPager = view.findViewById(R.id.viewpager);
        viewPager.setPageMargin(40);
        adapter = new Adapter(getChildFragmentManager());
        adapter.addFragment(new HomeFragment(1), "Borrowed");
        adapter.addFragment(new HomeFragment(2), "Lent");
        adapter.addFragment(new HomeFragment(3), "Pending");
        viewPager.setAdapter(adapter);
        // Set Tabs
        tabs = view.findViewById(R.id.view_tabs);
        tabs.setupWithViewPager(viewPager);

        toolbar = getActivity().findViewById(R.id.toolbar);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(((Global) getActivity().getApplication()).isRefreshNeeded1()) {
            viewPager.setAdapter(adapter);
            ((Global) getActivity().getApplication()).setRefreshNeeded(false);
        }
        if(((Global) getActivity().getApplication()).isTutorial()) {
            ((Global) getActivity().getApplication()).setTutorial();
            SystemClock.sleep(500);
            TapTargetSequence tapTargetSequence = new TapTargetSequence(getActivity())
                    .targets(
                            TapTarget.forView(((ViewGroup) tabs.getChildAt(0)).getChildAt(0),"Borrowed books"
                                    ,"The books which have been borrowed by you will come under this tab")
                            .transparentTarget(true)
                            .cancelable(false)
                            .drawShadow(true)
                            .targetRadius(50),
                            TapTarget.forView(((ViewGroup) tabs.getChildAt(0)).getChildAt(1),"Lent books"
                                    ,"The books which have been lent by you will come under this tab\n\nRemember you can also swipe to change tabs")
                            .transparentTarget(true)
                            .cancelable(false)
                            .drawShadow(true)
                            .targetRadius(50),
                            TapTarget.forView(((ViewGroup) tabs.getChildAt(0)).getChildAt(2),"Pending books"
                                    ,"The books which you have added but have not been lent yet")
                            .transparentTarget(true)
                            .cancelable(false)
                            .drawShadow(true)
                            .targetRadius(50)
                    ).listener(new TapTargetSequence.Listener() {
                        @Override
                        public void onSequenceFinish() { }
                        @Override
                        public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) { }
                        @Override
                        public void onSequenceCanceled(TapTarget lastTarget) { }
                    });
            tapTargetSequence.start();
        }
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        private Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        private void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}
