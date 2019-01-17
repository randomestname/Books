package com.example.user.books;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.content.SharedPreferences;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Vector;

import static android.content.Context.MODE_PRIVATE;

public class AddBookActivity extends AppCompatActivity {

    Button btn;
    ImageButton f;
    EditText ed1, ed2, ed4;
    Spinner spinner1;
    ArrayAdapter adapter;
    String title, author, subject, publisher, user;
    String encodedImage;
    Bitmap selectedImage= null;
    SharedPreferences sp;
    public AlertDialog dialog;
    ImageView ivPreview;
    DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = getSharedPreferences("Settings" ,MODE_PRIVATE);
        if(((Global) this.getApplication()).isDay()) setTheme(R.style.AppTheme);
        else setTheme(R.style.AppTheme2);
        setContentView(R.layout.activity_add_book);

        db = FirebaseDatabase.getInstance().getReference("lent");
        user = sp.getString("username", null);

        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add a New Book");

        spinner1 = findViewById(R.id.spinner);
        adapter = ArrayAdapter.createFromResource(this, R.array.temp, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.select_dialog_item);
        spinner1.setAdapter(adapter);
        spinner1.setSelection(0, false);


        onBtnClick();
        onAddImage();
    }

    void onBtnClick()
    {
        btn= findViewById(R.id.button);
        ed1 = findViewById(R.id.editText);
        ed2 = findViewById(R.id.editText2);
        ed4 = findViewById(R.id.editText4);
        ivPreview = findViewById(R.id.imageView);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean flag = true;
                //insert query, add date, username, bookid
                title = ed1.getText().toString();
                author = ed2.getText().toString();
                subject = spinner1.getSelectedItem().toString();
                publisher = ed4.getText().toString();
                ByteArrayOutputStream _bs = new ByteArrayOutputStream();
                if (selectedImage!= null)
                {
                    selectedImage.compress(Bitmap.CompressFormat.JPEG, 5, _bs);
                    byte[] byteArray = _bs.toByteArray();
                    encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
                }

                else {
                    Toast.makeText(AddBookActivity.this, "Please attach photo", Toast.LENGTH_SHORT).show();
                    flag = false;
                }
                if(title.equals("")) {
                    ed1.setError("Cannot be empty");
                    flag = false;
                }
                if(author.equals("")) {
                    ed2.setError("Cannot be empty");
                    flag = false;
                }
                if(publisher.equals("")) {
                    ed4.setError("Cannot be empty");
                    flag = false;
                }
                if(flag) {
                    //intent.putExtra("byteArray", _bs.toByteArray());
                    String id= db.push().getKey();
                    Lent lent= new Lent(id, title, author, subject, publisher, user, encodedImage);
                    db.child(id).setValue(lent);
                    sp.edit().putInt("lentb", sp.getInt("lentb", 0)+1);

                    Intent intent= new Intent(AddBookActivity.this, BookActivity.class);
                    intent.putExtra("access", 3);
                    intent.putExtra("bookId", id);
                    startActivity(intent);
                    AddBookActivity.this.finish();
                }

            }
        });
    }

    // PICK_PHOTO_CODE is a constant integer
    public final static int PICK_PHOTO_CODE = 1046;

    public void onAddImage() {
        f = findViewById(R.id.imageButton);
        f.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPickPhoto(view);
            }
        });
    }


    // Trigger gallery selection for a photo
    public void onPickPhoto(View view) {
        // Create intent for picking a photo from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Bring up gallery to select a photo
            startActivityForResult(intent, PICK_PHOTO_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            Uri photoUri = data.getData();
            // Do something with the photo based on Uri
            try {
                selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Load the selected image into a preview
            ivPreview.setImageBitmap(selectedImage);
            doOCR();
        }
    }

    void doOCR() {
        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        if(!textRecognizer.isOperational()) {
            Log.i("HMMM", "Could not read data!");
        } else {
            Frame frame = new Frame.Builder().setBitmap(selectedImage).build();
            SparseArray<TextBlock> items = textRecognizer.detect(frame);
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < items.size(); ++i) {
                TextBlock myItem = items.valueAt(i);
                String temp1 = myItem.getValue();
                sb.append(temp1);
                sb.append("\n");
            }
            String temp2[] = sb.toString().split("\n");
            makeOuterDialog(temp2);
        }
    }

    void makeOuterDialog(String text[]) {
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddBookActivity.this, android.R.layout.select_dialog_item);
        for(String a: text) {
            arrayAdapter.add(a);
        }

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(AddBookActivity.this);
        builderSingle.setTitle("Choose from OCR results:");

        builderSingle.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builderSingle.setAdapter(arrayAdapter, null);
        dialog = builderSingle.create();
        dialog.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String strName = (String) adapterView.getItemAtPosition(i);
                makeInnerDialog(strName);
            }
        });
        dialog.show();
    }

    void makeInnerDialog(final String text) {
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddBookActivity.this, android.R.layout.select_dialog_item);
        arrayAdapter.add("Title");
        arrayAdapter.add("Author");
        arrayAdapter.add("Publisher");

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(AddBookActivity.this);
        builderSingle.setTitle("Set \"" + text + "\" as:");

        builderSingle.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String strName = arrayAdapter.getItem(which);
                if(strName != null)
                    switch (strName) {
                        case "Title":
                            ed1.setText(text);
                            break;
                        case "Author":
                            ed2.setText(text);
                            break;
                        case "Publisher":
                            ed4.setText(text);
                            break;
                        default:
                            break;
                    }
            }
        });
        dialog = builderSingle.create();
        dialog.show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }
}
