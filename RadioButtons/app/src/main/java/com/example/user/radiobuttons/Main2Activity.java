package com.example.user.radiobuttons;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import java.util.Calendar;

public class Main2Activity extends AppCompatActivity {

    Button b;
    int y, m, d;
    static final int dateDialogID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        b = findViewById(R.id.button2);

        final Calendar cal = Calendar.getInstance();
        y = cal.get(Calendar.YEAR);
        m = cal.get(Calendar.MONTH);
        d = cal.get(Calendar.DAY_OF_MONTH);

        b.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DatePickerDialog dpd = new DatePickerDialog(Main2Activity.this,
                                dpickerListener, y, m ,d);
                        dpd.show();
                    }
                }
        );
    }

    private DatePickerDialog.OnDateSetListener dpickerListener =
            new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    y = year;
                    m = month;
                    d = day;
                    Toast.makeText(Main2Activity.this,
                            d + "/" + m + "/" + y, Toast.LENGTH_SHORT).show();
                }
            };
}
