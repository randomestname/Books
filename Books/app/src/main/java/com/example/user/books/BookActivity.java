package com.example.user.books;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class BookActivity extends AppCompatActivity {
    String bId;
    TextView t6, t7, t9, t10, t11, t12, t13, t14;
    Button b2, cancel;
    ImageView _imv;
    SharedPreferences sp;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef, dbu;
    int access;
    Lent book = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = getSharedPreferences("Settings" ,MODE_PRIVATE);
        if(((Global) this.getApplication()).isDay()) setTheme(R.style.AppTheme);
        else setTheme(R.style.AppTheme2);
        setContentView(R.layout.activity_book);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Book Details");

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference("lent");
        dbu = mFirebaseDatabase.getReference("user");

        t6 = findViewById(R.id.textView6);
        t7 = findViewById(R.id.textView7);
        t9 = findViewById(R.id.textView9);
        t10 = findViewById(R.id.textView10);
        t11 = findViewById(R.id.textView11);
        t12 = findViewById(R.id.textView12);
        t13 = findViewById(R.id.textView13);
        t14 = findViewById(R.id.textView14);
        b2 = findViewById(R.id.button2);
        _imv = findViewById(R.id.imageView3);

        //myRef.addValueEventListener(postListener);

        //if (b2.getText().toString()=="Borrow")
        onClickBorrow();
        /*else if (b2.getText().toString()=="Buy")
            onClickBuy();*/
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            access = bundle.getInt("access", 0);
            bId = bundle.getString("bookId");
        }
        List<Lent> tdata = ((Global) getApplicationContext()).getCdata();
        for(Lent l : tdata) {
            if (l.getBookId() != null)
                if (l.getBookId().equals(bId)) {
                    book = l;
                    break;
                }
        }
        if(book != null) {
            t6.setText(book.getTitle());
            t7.setText(book.getAuthor());
            //t8.setText("Id: "+ book.getBookId());
            if (book.isAvailable())
                t9.setText("Status: Available");
            else
                t9.setText("Status: Unavailable");
            t10.setText(book.getSubject());
            t11.setText("Publisher: "+ book.getPublisher());
            t12.setText("To "+ "Borrow");
            t13.setText("Owned by: "+ book.getOwner());
            //_imv.setImageBitmap(book.getImage());
            if (t12.getText().toString().equals("To Sell"))
                t14.setText("Rs. "+"57000");
            else t14.setVisibility(TextView.INVISIBLE);


            if (book.getSelectedImage()!= null)
            {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 8;
                byte[] decodedString = Base64.decode(book.getSelectedImage(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length, options);
            /*byte[] bitmapData = book.getImage().toByteArray();
            Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapData , 0, bitmapData.length);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inMutable = true;
            Bitmap bmp = BitmapFactory.decodeByteArray(book.getImage(), 0, book.getImage().length, options);*/
                _imv.setImageBitmap(decodedByte);
            }


            /*if(getIntent().hasExtra("byteArray")) {
                Bitmap _bitmap = BitmapFactory.decodeByteArray(
                        getIntent().getByteArrayExtra("byteArray"),0,getIntent().getByteArrayExtra("byteArray").length);
                _imv.setImageBitmap(_bitmap);
            }*/

            if (access == 0 && book.isAvailable()) //or if owner, compare usernames
                b2.setText("Borrow");
            if (access == 3 ) {//|| sp.getString("username", null)== book.getOwner()) //is user
                if (book.getBorrower() == null)
                    b2.setText("Remove");
                else {
                    cancel = findViewById(R.id.cancel);
                    cancel.setVisibility(View.VISIBLE);
                    b2.setText("Confirm");
                    t12.setPaintFlags(t12.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    t12.setText("Borrow request by: " + book.getBorrower());

                    t12.setTextIsSelectable(true);
                    getSupportActionBar().setTitle("Book to lend");
                    final Lent finalBook = book;
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ((Global) getApplicationContext()).sendNotification(finalBook.getBorrower(),
                                    "Your request to borrow book, " + finalBook.getTitle() + " was declined.");
                            myRef.child(bId).child("available").setValue(true);
                            myRef.child(bId).child("dateb").removeValue();
                            myRef.child(bId).child("borrower").removeValue();
                            BookActivity.this.finish();
                        }
                    });
                    t12.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent i = new Intent(BookActivity.this, AccountActivity.class);
                            i.putExtra("username", book.getBorrower());
                            startActivity(i);
                        }
                    });
                }
            }
            if (access == 1)
                b2.setText("Return");
            if (access==1)
                t12.setText("Return on: " + book.getDater());
        }
    }

    private static int requestCode = 100;
    public void onClickBorrow()
    {
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //start dialog
                if (b2.getText().toString().equals("Borrow")) {
                    Intent inti = new Intent(BookActivity.this, DetailsDialog.class);
                    inti.putExtra("bookId", bId);
                    startActivityForResult(inti, requestCode);
                }

                if (b2.getText().toString().equals("Remove")) {

                    final AlertDialog alertDialog = new AlertDialog.Builder(BookActivity.this).create();
                    alertDialog.setTitle("Remove Book");
                    alertDialog.setMessage("Are you sure you want to remove this book?");

                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Continue", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            myRef.child(bId).removeValue();
                            ((Global) getApplication()).setRefreshNeeded(true);
                            BookActivity.this.finish();
                        }
                    });

                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.cancel();
                        }
                    });

                    alertDialog.show();
                }

                if (b2.getText().toString().equals("Return") || b2.getText().toString().equals("Confirm")) {
                    startActivityForResult(new Intent(BookActivity.this, SimpleScannerActivity.class),requestCode);
                }
            }
        });
    }

    public void onActivityResult(int request_Code, int resultCode, Intent data) {
        if (requestCode == request_Code) {
            if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                String returnedResult = data.getData().toString();
                if (returnedResult.equals("close")) {
                    BookActivity.this.finish();
                    return;
                }
                if (returnedResult.equals(bId)) {
                    if (access == 1) {
                        myRef.child(bId).child("available").setValue(true);
                        myRef.child(bId).child("dateb").removeValue();
                        myRef.child(bId).child("borrower").removeValue();
                        Toast.makeText(BookActivity.this, "Book returned!", Toast.LENGTH_SHORT).show();
                    } else if (access == 3) {
                        myRef.child(bId).child("available").setValue(false);
                        Toast.makeText(BookActivity.this, "Book lent!", Toast.LENGTH_SHORT).show();
                        makeChanges(book.getOwner(), book.getBorrower());
                    }
                    ((Global) getApplication()).setRefreshNeeded(true);
                    BookActivity.this.finish();
                } else {
                    Toast.makeText(BookActivity.this, "Transaction error!", Toast.LENGTH_SHORT).show();

                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_items, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        item.setVisible(false);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    void makeChanges(final String u2, final String u1) {
        dbu.child(u1).child("borrowed").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long random = (Long) dataSnapshot.getValue();
                int ran = random.intValue();
                ran++;
                dbu.child(u1).child("borrowed").setValue(ran);
                sp.edit().putInt("borrowed", ran).apply();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        dbu.child(u2).child("lentb").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long random = (Long) dataSnapshot.getValue();
                int ran = random.intValue();
                ran++;
                dbu.child(u2).child("lentb").setValue(ran);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
