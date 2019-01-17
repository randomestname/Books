package com.example.user.books;

import android.app.Application;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Global extends Application {
    private int fpos; // Fragment Position
    private boolean isFirst = true, refreshNeeded[] = {false, false}, day = true;
    private boolean tutorial[] = {true, true, true};
    List<NavItemObject> listViewItems = new ArrayList<>();
    NotificationAdapter nadapter;
    List<NotiItemObject> ndata = new ArrayList<>();
    List<Lent> cdata = new ArrayList<>(); //Category data
    int unReadNoti = 0;
    TextView nottv;
    ImageView notification_dot;
    String username;
    SharedPreferences sp;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
        attachListener();
    }

    public int getFpos() {
        return fpos;
    }

    public void setFpos(int fpos) {
        this.fpos = fpos;
    }

    public boolean isFirst() {
        return isFirst;
    }

    public boolean isDay() {
        return day;
    }

    public void setDay(boolean day) {
        this.day = day;
    }

    public void setFirst(boolean first) {
        isFirst = first;
    }

    public boolean isRefreshNeeded1() {
        return refreshNeeded[1];
    }
    public boolean isRefreshNeeded0() {
        return refreshNeeded[0];
    }

    public void setRefreshNeeded(boolean refreshNeeded, int pos) {
        this.refreshNeeded[pos] = refreshNeeded;
    }

    public void setRefreshNeeded(boolean refreshNeeded) {
        this.refreshNeeded[1] = refreshNeeded;
    }

    public boolean isTutorial() { return tutorial[fpos]; }

    public void setTutorial() { this.tutorial[fpos] = false; }

    public void tutorialNotDone(boolean value) {
        for(int i = 0; i < tutorial.length; i++) tutorial[i] = value;
    }

    public List<NavItemObject> getListViewItems() {
        return listViewItems;
    }

    public void setListViewItems(List<NavItemObject> listViewItems) {
        this.listViewItems = listViewItems;
    }

    public List<NotiItemObject> getNdata() {
        return ndata;
    }

    public void setNdata(List<NotiItemObject> ndata) {
        this.ndata = ndata;
    }

    public void setUnReadNoti() {
        this.unReadNoti = 0;
    }

    public void incUnReadNoti() { unReadNoti++; }

    public void decUnReadNoti() { unReadNoti--; }

    public void setNottv(TextView nottv) {
        this.nottv = nottv;
    }

    public List<Lent> getCdata() {
        return cdata;
    }

    public void setCdata(List<Lent> cdata) {
        this.cdata = cdata;
    }

    public void setNotification_dot(ImageView notification_dot) {
        this.notification_dot = notification_dot;
    }

    public void setNadapter(NotificationAdapter nadapter) {
        this.nadapter = nadapter;
    }

    public NotificationAdapter getNadapter() {
        return nadapter;
    }

    public void addNotfication(String noti, boolean needsUpdate) {
        nadapter.addItem(noti);
        if(needsUpdate) {
            try {
                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                final Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                r.play();
                new android.os.Handler().postDelayed(new Runnable(){
                    @Override
                    public void run() {
                        r.stop();
                    }
                }, 1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            notficationFunc();
        }
    }

    public void notficationFunc() {
        String nottvs = "Unread: " + String.valueOf(unReadNoti);
        nottv.setText(nottvs);
        if(unReadNoti <= 0)
            notification_dot.setVisibility(View.GONE);
        else
            notification_dot.setVisibility(View.VISIBLE);
    }

    private DatabaseReference myRef, myRef2;
    FirebaseDatabase mFirebaseDatabase;
    Query query;

    public void sendNotification(String user, String data) {
        myRef.child(user).child("notification").child(myRef.push().getKey()).setValue(data);
        myRef.child(user).child("storedNoti").child(myRef.push().getKey()).setValue(data);
    }

    public void sendNotification(String user, String data, String bId) {
        data = "I:" + bId + " " + data;
        myRef.child(user).child("notification").child(myRef.push().getKey()).setValue(data);
        myRef.child(user).child("storedNoti").child(myRef.push().getKey()).setValue(data);
    }

    public void attachListener() {
        sp = getSharedPreferences("Settings" ,MODE_PRIVATE);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference("user");
        query = myRef.child(username).child("notification");
        query.addValueEventListener(upostListener);

        //myRef.child(username).child("points").addValueEventListener(pointPostListener);
        myRef.child(username).addValueEventListener(pointPostListener);

        myRef2 = mFirebaseDatabase.getReference("lent");
        myRef2.addValueEventListener(postListener);
    }

    public void removeListener() {
        query.removeEventListener(upostListener);
        //myRef.child(username).child("points").removeEventListener(pointPostListener);
        myRef.child(username).removeEventListener(pointPostListener);
    }

    ValueEventListener upostListener = new ValueEventListener() {

        @Override
        public void onDataChange(DataSnapshot snapshot) {
            for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                String noti = postSnapshot.getValue(String.class);
                addNotfication(noti, !isFirst);
            }
            myRef.child(username).child("notification").removeValue();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            System.out.println("The read failed: " + databaseError.getMessage());
        }

    };

    ValueEventListener postListener = new ValueEventListener() {

        @Override
        public void onDataChange(DataSnapshot snapshot) {
            int i = 0;
            cdata.clear();
            for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                Lent book = postSnapshot.getValue(Lent.class);
                /*String t1 = myRef2.getKey();
                String t2 = book.getBookId();
                Log.e("TAG", t1 + " " + t2);
                myRef2.child("booklist").child(t1).setValue(t2);*/
                cdata.add(book);
                i++;
                Log.e("TAG", String.valueOf(i));
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            System.out.println("The read failed: " + databaseError.getMessage());
        }
    };

    ValueEventListener innerPostListener = new ValueEventListener() {

        @Override
        public void onDataChange(DataSnapshot snapshot) {
            Lent book = snapshot.getValue(Lent.class);
            cdata.add(book);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            System.out.println("The read failed: " + databaseError.getMessage());
        }
    };

    void loadNotifications() {
        myRef.child(username).child("storedNoti").addListenerForSingleValueEvent(loadNotiPostListener);
    }

    ValueEventListener loadNotiPostListener = new ValueEventListener() {

        @Override
        public void onDataChange(DataSnapshot snapshot) {
            boolean flag = true;
            for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                String noti = postSnapshot.getValue(String.class);
                Log.e("TAG", "Notification: " + noti);
                if(flag) {
                    addNotfication(noti, !isFirst);
                    flag = false;
                }
                else addNotfication(noti, false);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            System.out.println("The read failed: " + databaseError.getMessage());
        }

    };

    ValueEventListener pointPostListener = new ValueEventListener() {

        @Override
        public void onDataChange(DataSnapshot snapshot) {
            Long temp = (Long) snapshot.child("points").getValue();
            int poi = temp.intValue();
            sp.edit().putInt("points", poi).apply();
            temp = (Long) snapshot.child("borrowed").getValue();
            poi = temp.intValue();
            sp.edit().putInt("borrowed", poi).apply();
            temp = (Long) snapshot.child("lentb").getValue();
            poi = temp.intValue();
            sp.edit().putInt("lentb", poi).apply();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            System.out.println("The read failed: " + databaseError.getMessage());
        }

    };
}