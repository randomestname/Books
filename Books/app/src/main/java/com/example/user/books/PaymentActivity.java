package com.example.user.books;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PaymentActivity extends AppCompatActivity implements BillingProcessor.IBillingHandler, View.OnClickListener {

    Button watch_ad, rs50, rs100, rs150, rs300, rs500;
    private InterstitialAd mInterstitialAd;
    BillingProcessor bp;
    int value;
    int poi = 0;
    String username;
    DatabaseReference dbu;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = getSharedPreferences("Settings" ,MODE_PRIVATE);
        if(((Global) this.getApplication()).isDay()) setTheme(R.style.AppTheme);
        else setTheme(R.style.AppTheme2);
        setContentView(R.layout.activity_payment);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Get Points");

        watch_ad = findViewById(R.id.watch_ad);
        rs50 = findViewById(R.id.rs50);
        rs100 = findViewById(R.id.rs100);
        rs150 = findViewById(R.id.rs150);
        rs300 = findViewById(R.id.rs300);
        rs500 = findViewById(R.id.rs500);

        dbu = FirebaseDatabase.getInstance().getReference("user");
        username = ((Global) getApplicationContext()).getUsername();

        //Setting up ads
        final AdRequest request = new AdRequest.Builder()
                .addTestDevice("33BE2250B43518CCDA7DE426D04EE231")  // An example device ID
                .build();
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd.loadAd(request);

        watch_ad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                } else {
                    Log.d("TAG", "The interstitial wasn't loaded yet.");
                }
            }
        });

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                value = 1;
                checkPoints(username);
                mInterstitialAd.loadAd(request);
            }
        });

        //Setting up payments
        bp = new BillingProcessor(this, null, this);
        rs50.setOnClickListener(this);
        rs100.setOnClickListener(this);
        rs150.setOnClickListener(this);
        rs300.setOnClickListener(this);
        rs500.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rs50:
                value = 10;
                break;
            case R.id.rs100:
                value = 25;
                break;
            case R.id.rs150:
                value = 50;
                break;
            case R.id.rs300:
                value = 150;
                break;
            case R.id.rs500:
                value = 500;
                break;
        }
        bp.purchase(PaymentActivity.this, "android.test.purchased");
    }

    @Override
    public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {
        bp.consumePurchase("android.test.purchased");
        checkPoints(username);
    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, @Nullable Throwable error) {

    }

    @Override
    public void onBillingInitialized() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(!bp.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        if(bp != null)
            bp.release();
        super.onDestroy();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    void checkPoints(final String username) {
        dbu.child(username).child("points").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long poin = (Long) dataSnapshot.getValue();
                poi = poin.intValue();
                Log.e("TAG", "Points of " + username + " on read: " + String.valueOf(poi));
                Log.e("TAG", "Point thingy: " + String.valueOf(poi) + " " + String.valueOf(value));
                poi += value;
                dbu.child(username).child("points").setValue(poi);
                sp.edit().putInt("points", poi).apply();
                if(value == 1) ((Global) PaymentActivity.this.getApplication()).sendNotification(username,"1 point has been added!");
                else ((Global) PaymentActivity.this.getApplication()).sendNotification(username, String.valueOf(value) + " points have been added!");
                value = 0;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
