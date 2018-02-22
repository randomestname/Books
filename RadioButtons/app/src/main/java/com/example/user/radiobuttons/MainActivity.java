package com.example.user.radiobuttons;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private RadioGroup rg;
    private RadioButton r;
    private Button b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        b = findViewById(R.id.button);
        b.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        rg = findViewById(R.id.rg);
                        String s;
                        if(rg.getCheckedRadioButtonId() != -1 ) {
                            r = findViewById(rg.getCheckedRadioButtonId());
                            s = r.getText().toString();
                            startActivity(new Intent(MainActivity.this, Main2Activity.class));
                        } else {
                            s = "No button selected!";
                        }
                        Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }
}
