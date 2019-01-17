package com.example.user.books;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;

import java.util.Arrays;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class SwipeViewFragment extends Fragment {

    List<String> cdata;
    View view;
    RecyclerView cview;
    CardView notifications, dayNight, addBook, deleteBook, catContainer;
    TextView ndtv, nottv, p;
    ImageView notification_dot;
    Toolbar toolbar;
    SharedPreferences sp;

    public SwipeViewFragment() {
        
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_swipe_view, container, false);

        cview = view.findViewById(R.id.catview);
        notifications = view.findViewById(R.id.notifications);
        dayNight = view.findViewById(R.id.dayNight);
        addBook = view.findViewById(R.id.addBook);
        deleteBook = view.findViewById(R.id.deleteBook);
        ndtv = view.findViewById(R.id.ndtv);
        notification_dot = view.findViewById(R.id.notification_dot);
        nottv = view.findViewById(R.id.nottv);
        toolbar = getActivity().findViewById(R.id.toolbar);
        catContainer = view.findViewById(R.id.catContainer);
        p = view.findViewById(R.id.points);

        sp = getActivity().getSharedPreferences("Settings" ,MODE_PRIVATE);

        cdata = Arrays.asList(getResources().getStringArray(R.array.temp));
        final SwipeAdapter cadapter = new SwipeAdapter(getContext(), cdata);
        RecyclerView.LayoutManager slayoutManager = new GridLayoutManager(getContext(), 1);
        cview.setLayoutManager(slayoutManager);
        cview.setAdapter(cadapter);
        cview.setNestedScrollingEnabled(false);

        ((Global) getActivity().getApplication()).setNottv(nottv);
        ((Global) getActivity().getApplication()).setNotification_dot(notification_dot);
        ((Global) getActivity().getApplication()).notficationFunc();

        if(((Global) getActivity().getApplication()).isDay())
            ndtv.setText("Current: Day");
        else
            ndtv.setText("Current: Night");

        p.setText("Points: " + String.valueOf(sp.getInt("points", 100)));

        notifications.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(getActivity(), NotificationActivity.class));
                    }
                }
        );
        dayNight.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(((Global) getActivity().getApplication()).isDay())
                            ((Global) getActivity().getApplication()).setDay(false);
                        else
                            ((Global) getActivity().getApplication()).setDay(true);
                        restart();
                    }
                }
        );
        addBook.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent= new Intent(getActivity(), AddBookActivity.class);
                        startActivity(intent);
                    }
                }
        );
        deleteBook.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //((Global) getActivity().getApplication()).addNotfication("New notfication created!", true);
                        startActivity(new Intent(getActivity(), PaymentActivity.class));
                    }
                }
        );

        if(((Global) getActivity().getApplication()).isTutorial()) {
            ((Global) getActivity().getApplication()).setTutorial();
            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int height = displayMetrics.heightPixels;
            int width = displayMetrics.widthPixels / 2;
            final TapTargetSequence tapTargetSequence = new TapTargetSequence(getActivity())
                    .targets(
                TapTarget.forToolbarNavigationIcon(toolbar, "Navigation Drawer", "Navigate the app\n♦ Home\n♦ Library \n♦ Account")
                        .drawShadow(true),
                TapTarget.forView(notifications, "Notifications", "Check new notifications here")
                        .targetRadius(115)
                        .transparentTarget(true)
                        .cancelable(false)
                        .drawShadow(true),
                TapTarget.forView(dayNight, "Day Night", "Change the theme of the app")
                        .targetRadius(115)
                        .transparentTarget(true)
                        .cancelable(false)
                        .drawShadow(true),
                TapTarget.forView(addBook, "Add a Book", "Every book you add and lent by someone else will give you 4 points per week")
                        .targetRadius(115)
                        .transparentTarget(true)
                        .cancelable(false)
                        .drawShadow(true),
                TapTarget.forView(deleteBook, "Buy Points", "Buy points to borrow more books")
                        .targetRadius(115)
                        .transparentTarget(true)
                        .cancelable(false)
                        .drawShadow(true),
                TapTarget.forBounds(new Rect(width, height, width, height), "Subjects", "Find books subject wise \n\nScroll down to see all\n\n")
                        .targetRadius(150)
                        .transparentTarget(true)
                        .cancelable(false)
                        .drawShadow(true)
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
        return view;
    }

    void restart() {
        Intent i = new Intent(getContext(), MainHome.class);
        getActivity().startActivity(i);
        getActivity().finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((Global) getActivity().getApplication()).notficationFunc();
    }
}
