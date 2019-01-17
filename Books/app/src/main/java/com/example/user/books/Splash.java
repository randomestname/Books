package com.example.user.books;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Splash extends AppCompatActivity {

    /** Duration of wait **/
    private final int SPLASH_DISPLAY_LENGTH = 2000;
    SharedPreferences sp;
    private LottieAnimationView animationView;
    private  String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = getSharedPreferences("Settings", MODE_PRIVATE);
        ((Global) this.getApplication()).setDay(sp.getBoolean("DayNight", true));
        if (((Global) this.getApplication()).isDay()) setTheme(R.style.AppTheme);
        else setTheme(R.style.AppTheme2);
        setContentView(R.layout.activity_splash);

        if(!sp.getBoolean("NotLoggedIn", true)){
            userID = sp.getString("username", null);
            ((Global) this.getApplication()).setUsername(userID);
        }

        /* New Handler to start the Menu-Activity and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                Intent mainIntent;
                if(sp.getBoolean("NotLoggedIn", true))
                    mainIntent = new Intent(Splash.this, LoginActivity.class);
                else {
                    ((Global) Splash.this.getApplication()).tutorialNotDone(false);
                    mainIntent = new Intent(Splash.this, MainHome.class);
                }
                Splash.this.startActivity(mainIntent);
                Splash.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);

        animationView = findViewById(R.id.animation_view);
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f).setDuration(6500);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                animationView.setProgress((Float) valueAnimator.getAnimatedValue());
            }
        });
        animator.start();

        ((Global) this.getApplication()).setNadapter(new NotificationAdapter(Splash.this));


        isInternetAvailable(getApplicationContext());
    }

    public boolean isInternetAvailable(Context context)
    {
        NetworkInfo info = (NetworkInfo) ((ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();

        if (info == null)
        {
            Log.d("Internet","no internet connection");
            Toast.makeText(Splash.this,"No Internet Connection",Toast.LENGTH_LONG).show();
            return false;
        }
        else
        {
            if(info.isConnected())
            {
                Log.d("Internet"," internet connection available...");
                return true;
            }
            else
            {
                Log.d("Internet"," internet connection");
                return true;
            }

        }
    }


}