package com.example.user.books;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private EditText user_name;
    private EditText pass_word;
    String un, p;
    TextView signUp;
    private Button btn;
    private final String str1= "Correct username and password.";
    private final String str2= "Incorrect username and password.";
    SharedPreferences sp;
    boolean login = false;
    ProgressBar loginprogress;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sp = getSharedPreferences("Settings" ,MODE_PRIVATE);

        signUp= (TextView) findViewById(R.id.signup);
        SpannableString content = new SpannableString("Are you new? Sign up here.");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        signUp.setText(content);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference("user");

        Toolbar toolbar = findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        user_name = findViewById(R.id.ed1);
        pass_word = findViewById(R.id.ed2);
        loginprogress = findViewById(R.id.loginprogress);

        addListenerOnButton();
        addListenerOnSignup();
    }

    public void addListenerOnButton()
    {
        btn = findViewById(R.id.login);
        btn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ApplySharedPref")
            @Override
            public void onClick(View view) {
                boolean flag = true;
                un = user_name.getText().toString();
                p = pass_word.getText().toString();
                if(un.equals("")) {
                    flag = false;
                    user_name.setError("Cannot be empty!");
                }
                if(p.equals("")) {
                    flag = false;
                    pass_word.setError("Cannot be empty!");
                }
                if(flag) {
                    Log.i("Hmmm", un + p);
                    loginprogress.setVisibility(View.VISIBLE);
                    user_name.setEnabled(false);
                    pass_word.setEnabled(false);
                    myRef.child(un).addListenerForSingleValueEvent(postListener);
                }
            }
        });
    }

    public void addListenerOnSignup()
    {
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    //final Firebase databaseRef = new Firebase(SyncStateContract.Constants.FIREBASE_URL);

    ValueEventListener postListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            //User u = null;
            if(dataSnapshot.exists()) {
                User u = dataSnapshot.getValue(User.class);
                if(p.equals(u.getPassword())) {
                    Toast.makeText(LoginActivity.this, str1, Toast.LENGTH_SHORT).show();
                    sp.edit().putBoolean("NotLoggedIn", false).apply();
                    sp.edit().putString("username", u.getUserName()).apply();
                    sp.edit().putString("name", u.getName()).apply();
                    sp.edit().putString("email", u.getEmail()).apply();
                    sp.edit().putString("phone", u.getPhone()).apply();
                    sp.edit().putString("image", u.getSelectedImage()).apply();
                    sp.edit().putInt("points", u.getPoints()).apply();
                    sp.edit().putInt("borrowed", u.getBorrowed()).apply();
                    sp.edit().putInt("lentb", u.getLentb()).apply();

                    ((Global) getApplicationContext()).setUsername(un);
                    ((Global) getApplicationContext()).loadNotifications();
                    Intent intent = new Intent(LoginActivity.this, MainHome.class);
                    startActivity(intent);
                    LoginActivity.this.finish();
                } else Toast.makeText(LoginActivity.this, str2, Toast.LENGTH_SHORT).show();
            } else Toast.makeText(LoginActivity.this, str2, Toast.LENGTH_SHORT).show();
            loginprogress.setVisibility(View.GONE);
            user_name.setEnabled(true);
            pass_word.setEnabled(true);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            Toast.makeText(LoginActivity.this, "Failed to load.", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
