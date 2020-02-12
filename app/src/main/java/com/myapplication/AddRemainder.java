package com.myapplication;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import dataTypes.MyNotification;

public class AddRemainder extends AppCompatActivity {
    FloatingActionButton add;
    EditText dateEditText;
    EditText title, description;
    ProgressDialog progressDialog;

    final Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_remainder);
        setTitle("Remainder Details");
        dateEditText = (EditText) findViewById(R.id.date);
        title = (EditText) findViewById(R.id.title);
        add = (FloatingActionButton) findViewById(R.id.add);
        description = (EditText) findViewById(R.id.description);
        progressDialog = new ProgressDialog(this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
        progressDialog.setCanceledOnTouchOutside(false);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addData();
            }
        });

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };
        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(AddRemainder.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

    }

    private void updateLabel() {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        dateEditText.setText(sdf.format(myCalendar.getTime()));
    }

    public void addData() {

        if (dateEditText.getText().toString().isEmpty()) {
            dateEditText.setError("Select Date");
            dateEditText.requestFocus();
            return;
        }
        if (title.getText().toString().isEmpty()) {

            title.setError("Specify title");
            title.requestFocus();
            return;
        }
        progressDialog.setMessage("Adding data");
        progressDialog.show();
        Map<String, Object> data = new HashMap<>();

        data.put("date", dateEditText.getText().toString());
        data.put("title", title.getText().toString());
        data.put("description",description.getText().toString());
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("remainders").add(data)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        progressDialog.dismiss();
                        if (task.isComplete()) {
                            MyNotification.add(getApplicationContext(), title.getText().toString(), description.getText().toString(), myCalendar);
                            Toast.makeText(AddRemainder.this,"Sucessfully added",Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        else {
                            Toast.makeText(AddRemainder.this,"Failed",Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }
}
