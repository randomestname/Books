package com.example.user.books;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    List<NotiItemObject> ndata;
    RecyclerView nview;
    NotiItemObject notit; //Temporary object if needed
    int notipos; //Temporary notification position if needed
    final boolean[] flag = {true, true}; //0-> Undo pressed or not 1-> Repeated delete?
    MyDBHandler dbHandler = new MyDBHandler(NotificationActivity.this, null);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(((Global) this.getApplication()).isDay()) setTheme(R.style.AppTheme);
        else setTheme(R.style.AppTheme2);
        setContentView(R.layout.activity_notification);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Notifications");

        nview = findViewById(R.id.nview);
        ndata = new ArrayList<>();

        ndata = ((Global) this.getApplication()).getNdata();
        final NotificationAdapter nadapter = ((Global) this.getApplication()).getNadapter();
        RecyclerView.LayoutManager nlayoutManager = new GridLayoutManager(NotificationActivity.this, 1);
        nview.setLayoutManager(nlayoutManager);
        nview.setAdapter(nadapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                notipos = viewHolder.getAdapterPosition();
                notit = nadapter.removeItem(notipos);
                flag[0] = true;
                flag[1] = true;
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
                Snackbar mySnackbar = Snackbar.make(nview,
                        "Notification deleted", Snackbar.LENGTH_SHORT);
                mySnackbar.setAction("Undo",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                nadapter.addItem(notipos, notit);
                                flag[0] = false;
                                flag[1] = false;
                            }
                        });
                mySnackbar.addCallback(
                        new Snackbar.Callback() {
                            @Override
                            public void onDismissed(Snackbar transientBottomBar, int event) {
                                deleteHelper(notit.getId());
                                super.onDismissed(transientBottomBar, event);
                            }
                        }
                );
                mySnackbar.show();
            }
        });
        itemTouchHelper.attachToRecyclerView(nview);
    }

    @Override
    protected void onPause() {
        super.onPause();
        int i, start = 0, end;
        if(!ndata.isEmpty())
            start = ndata.get(0).getId();
        for(i = 0; i < ndata.size(); i++){
            ndata.get(i).setRead();
            if(i == 25) break;
        } i--;
        if(!ndata.isEmpty()) {
            end = ndata.get(i).getId();
            dbHandler.updateNotification(start, end);
        }
        ((Global) this.getApplication()).setUnReadNoti();
        if(notit != null) deleteHelper(notit.getId());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void deleteHelper(int pos) {
        if(flag[0] && flag[1]) {
            dbHandler.deleteNotification(pos);
            flag[1] = false;
        }
    }
}
