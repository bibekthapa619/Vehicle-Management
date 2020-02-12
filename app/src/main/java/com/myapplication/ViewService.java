package com.myapplication;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import dataTypes.MyNotification;

public class ViewService extends AppCompatActivity {

    EditText dateEditText;
    EditText serviceCenter;
    EditText phoneNum;
    EditText odometerReading;
    EditText serviceType;
    EditText amount;
    EditText description;
    ProgressDialog progressDialog;
    FirebaseFirestore db;
    FloatingActionButton fab;
    final Calendar myCalendar = Calendar.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_service);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        setTitle("Service Details");
        dateEditText= (EditText) findViewById(R.id.date);
        serviceCenter =(EditText)findViewById(R.id.serviceCenter);
        phoneNum =(EditText)findViewById(R.id.phoneNum);
        odometerReading =(EditText)findViewById(R.id.odometerReading);
        serviceType=(EditText)findViewById(R.id.serviceType);
        amount=(EditText)findViewById(R.id.amount);
        description=(EditText)findViewById(R.id.description);
        fab = (FloatingActionButton) findViewById(R.id.save);
        progressDialog=new ProgressDialog(this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
        progressDialog.setCanceledOnTouchOutside(false);
        db=FirebaseFirestore.getInstance();
        init();
        editable(false);

    }
    private void editable(boolean ed)
    {

            amount.setEnabled(ed);
            dateEditText.setEnabled(ed);
            serviceCenter.setEnabled(ed);
            phoneNum.setEnabled(ed);
            odometerReading.setEnabled(ed);
            serviceType.setEnabled(ed);
            description.setEnabled(ed);
            if (ed)
            {
                fab.show();
            }
            else
            {
                fab.hide();
            }


    }

    private void init()
    {
        progressDialog.setMessage("loading");
        progressDialog.show();
        db.collection("service")
                .document(getIntent().getStringExtra("serviceId")).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        progressDialog.dismiss();
                        if(task.isSuccessful())
                        {
                            DocumentSnapshot d= task.getResult();
                            dateEditText.setText(d.getString("date"));
                            serviceCenter.setText(d.getString("serviceCenter"));
                            phoneNum.setText(d.getString("contactNum"));
                            odometerReading.setText(d.getString("odometerReading"));
                            serviceType.setText(d.getString("serviceType"));
                            amount.setText(d.getString("amount"));
                            description.setText(d.getString("description"));
                        }
                        else {
                            Toast.makeText(getApplicationContext(),"Unable to load",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
