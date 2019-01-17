package com.example.user.books;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BorrowedBookActivity extends AppCompatActivity {

    String bId;
    int access;
    TextView t17, t18, t19, t21, t23, t24;
    String currentDateString = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
    ImageView iv;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private  String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(((Global) this.getApplication()).isDay()) setTheme(R.style.AppTheme);
        else setTheme(R.style.AppTheme2);
        setContentView(R.layout.activity_borrowed_book);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            access = bundle.getInt("access", 0);
            bId = bundle.getString("bookId");
        }

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference("lent");
        myRef.child(bId).addValueEventListener(postListener);


        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (access == 1)
            getSupportActionBar().setTitle("Borrowed Book");
        else if (access == 3)
            getSupportActionBar().setTitle("Book to be borrowed");


        t17 = findViewById(R.id.textView17);
        t18 = findViewById(R.id.textView18);
        t19 = findViewById(R.id.textView19);
        t21 = findViewById(R.id.textView21);
        t23 = findViewById(R.id.textView23);
        t24 = findViewById(R.id.textView24);

        // select query with book id

        //t21.setText(currentDateString);
        //t23.setText(currentDateString);
        //Extracting data
        List<Lent> tdata = ((Global) getApplicationContext()).getCdata();
        Lent book = null;
        for(Lent l : tdata) {
            if (l.getBookId() != null)
                if (l.getBookId().equals(bId)) {
                    book = l;
                    break;
                }
        }
        if(book != null) {
            t17.setText(book.getTitle());
            t18.setText("Author: " + book.getAuthor());
            t19.setText("Id: " + book.getBookId());
            if (access == 1 || access == 3)
                t24.setText("Owned by: " + book.getOwner());
            if (access == 2)
                t24.setText("Borrowed by: " + book.getBorrower());
            t21.setText("Date borrowed: " + book.getDateb());
            t23.setText("Date return: " + book.getDater());
            printData(bId);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    //final Firebase databaseRef = new Firebase(SyncStateContract.Constants.FIREBASE_URL);

    ValueEventListener postListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            Lent book = dataSnapshot.getValue(Lent.class);
            if(access == 2) {
                if((book.isAvailable() && book.getBorrower() == null)) {
                    Toast.makeText(BorrowedBookActivity.this, "Book returned!", Toast.LENGTH_SHORT).show();
                    BorrowedBookActivity.this.finish();
                    ((Global) getApplication()).setRefreshNeeded(true);
                }
            }
            else if(access == 3 || access == 0) {
                if ((!book.isAvailable() && book.getBorrower() != null)) {
                    Toast.makeText(BorrowedBookActivity.this, "Book borrowed!", Toast.LENGTH_SHORT).show();
                    //add a removed book notification
                    BorrowedBookActivity.this.finish();
                    ((Global) getApplication()).setRefreshNeeded(true);
                }
            }

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

            //Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            Toast.makeText(BorrowedBookActivity.this, "Failed to load.", Toast.LENGTH_SHORT).show();
        }
    };

    //QR maker
    void printData(String text) {
        iv= findViewById(R.id.imageView2);
        if(text.equals("")) text = "Random";
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE,350,350);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            iv.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myRef.child(bId).removeEventListener(postListener);
    }
}
