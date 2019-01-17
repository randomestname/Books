package com.example.user.books;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class SignUpActivity extends AppCompatActivity {

    EditText name, usern, email, phone, pass;
    Button btn;
    String n, u, e, ph, ps;
    private static final String TAG = "SignUpActivity";
    Bitmap selectedImage= null;
    ImageView ivPreview;
    String encodedImage;
    boolean sup;
    SharedPreferences sp;
    //Bundle bundle;
    int access;
    Uri photoUri;

    DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Sign Up");

        sp = getSharedPreferences("Settings" ,MODE_PRIVATE);

        db= FirebaseDatabase.getInstance().getReference("user");

        btn= findViewById(R.id.button);
        name= findViewById(R.id.ed1);
        usern= findViewById(R.id.ed2);
        email= findViewById(R.id.ed3);
        phone= findViewById(R.id.ed4);
        pass= findViewById(R.id.ed5);
        ivPreview= findViewById(R.id.imageView4);

        access = getIntent().getIntExtra("access", 0);
        Log.e("TAG","Access: " + String.valueOf(access));
        if (access == 4) {
            getSupportActionBar().setTitle("Edit Profile");
            name.setText(sp.getString("name", null));
            usern.setText(sp.getString("username", null));
            usern.setFocusable(false);
            usern.setEnabled(false);
            usern.setCursorVisible(false);
            usern.setKeyListener(null);
            usern.setBackgroundColor(Color.TRANSPARENT);
            email.setText(sp.getString("email", null));
            email.setFocusable(false);
            email.setEnabled(false);
            email.setCursorVisible(false);
            email.setKeyListener(null);
            email.setBackgroundColor(Color.TRANSPARENT);
            phone.setText(sp.getString("phone", null));
            //pass.setText(bundle.getString("password"));

            if (sp.getString("image", null)!= null)
            {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 8;
                byte[] decodedString = Base64.decode(sp.getString("image", null), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length, options);
                ivPreview.setImageBitmap(decodedByte);
            }
        }

        addListenerOnButton();
        onAddImage();
    }

    public void addListenerOnButton()
    {

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                n = name.getText().toString();
                u = usern.getText().toString();
                e = email.getText().toString();
                ph = phone.getText().toString();
                ps = pass.getText().toString();
                ByteArrayOutputStream _bs = new ByteArrayOutputStream();
                if (selectedImage!= null)
                {
                    selectedImage.compress(Bitmap.CompressFormat.JPEG, 5, _bs);
                    byte[] byteArray = _bs.toByteArray();
                    encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
                }

                if (n.equals("") || u.equals("") || e.equals("") || ph.equals("") || ps.equals(""))
                    Toast.makeText(SignUpActivity.this, "Enter all required details", Toast.LENGTH_SHORT).show();
                else
                {
                    if (access == 0)
                        db.child(u).addListenerForSingleValueEvent(postListener);
                    else
                    {
                        User user1= new User(u, n, e, ph, ps, encodedImage, sp.getInt("points", 0),
                                sp.getInt("borrowed", 0), sp.getInt("lentb", 0));
                        db.child(u).setValue(user1);
                        Intent intent = new Intent(SignUpActivity.this, MainHome.class);
                        //set user
                        sp.edit().putString("username", u).apply();
                        ((Global) getApplicationContext()).setUsername(u);
                        sp.edit().putString("name", n).apply();
                        sp.edit().putString("email", e).apply();
                        sp.edit().putString("phone", ph).apply();
                        if (encodedImage!= null)
                            sp.edit().putString("image", encodedImage).apply();
                        startActivity(intent);
                    }
                }

            }
        });
    }

    ValueEventListener postListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists()){
                // use "username" already exists
                Toast.makeText(SignUpActivity.this, "Username already taken", Toast.LENGTH_SHORT).show();
            }
            else {
                // User does not exist.
                //enter into database
                User user1= new User(u, n, e, ph, ps, encodedImage);
                db.child(u).setValue(user1);

                if (access == 0) {
                    db.child(u).child("notification").child("loginfirst").setValue("You have received 100 points for your first registration!");
                    Toast.makeText(SignUpActivity.this, "Account Created!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(SignUpActivity.this, MainHome.class);
                    //set user
                    sp.edit().putBoolean("NotLoggedIn", false).apply();
                    sp.edit().putString("username", u).apply();
                    sp.edit().putString("name", n).apply();
                    sp.edit().putString("email", e).apply();
                    sp.edit().putString("phone", ph).apply();
                    sp.edit().putString("image", encodedImage).apply();
                    sp.edit().putInt("points", 100).apply();
                    sp.edit().putInt("borrowed", 0).apply();
                    sp.edit().putInt("lentb", 0).apply();
                    ((Global) getApplicationContext()).setUsername(u);
                    startActivity(intent);
                }
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            Toast.makeText(SignUpActivity.this, "Failed to load.", Toast.LENGTH_SHORT).show();
        }
    };

    // PICK_PHOTO_CODE is a constant integer
    public final static int PICK_PHOTO_CODE = 1046;

    public void onAddImage() {
        //f = findViewById(R.id.imageButton);
        ivPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPickPhoto(view);
            }
        });
    }

    // Trigger gallery selection for a photo
    public void onPickPhoto(View view) {
        Intent intent1 = CropImage
                .activity(photoUri).getIntent(getApplicationContext());
        startActivityForResult(intent1, CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                try {
                    selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // Load the selected image into a preview
                ivPreview.setImageBitmap(selectedImage);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}