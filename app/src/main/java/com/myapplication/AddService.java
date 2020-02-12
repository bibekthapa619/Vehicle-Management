package com.myapplication;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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

public class AddService extends AppCompatActivity {

    EditText dateEditText;
    EditText serviceCenter;
    EditText phoneNum;
    EditText odometerReading;
    EditText serviceType;
    EditText amount;
    EditText description;
    ProgressDialog progressDialog;
    FirebaseFirestore db;
    final Calendar myCalendar = Calendar.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_service);
        setTitle("Service Details");
        dateEditText= (EditText) findViewById(R.id.date);
        serviceCenter =(EditText)findViewById(R.id.serviceCenter);
        phoneNum =(EditText)findViewById(R.id.phoneNum);
        odometerReading =(EditText)findViewById(R.id.odometerReading);
        serviceType=(EditText)findViewById(R.id.serviceType);
        amount=(EditText)findViewById(R.id.amount);
        description=(EditText)findViewById(R.id.description);
        progressDialog=new ProgressDialog(this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
        progressDialog.setCanceledOnTouchOutside(false);

        db=FirebaseFirestore.getInstance();

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
                new DatePickerDialog(AddService.this, date, myCalendar
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
    private  boolean checkLatestDate(String newDate,String oldDate)
    {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        if(oldDate.equals(""))
        {
            return true;
        }
        try{
            Date date1= sdf.parse(newDate);
            Date date= sdf.parse(oldDate);
            if(date1.after(date))
            {
                return true;
            }
        }
        catch (Exception e)
        {
//            Toast.makeText(FuelDetails.this,"Unable to parse",Toast.LENGTH_SHORT).show();

        }
        return false;
    }

    public void  addService(final View target)
    {
        if(dateEditText.getText().toString().isEmpty())
        {
            dateEditText.setError("Select Date");
            dateEditText.requestFocus();
            return;
        }
        if(serviceType.getText().toString().isEmpty())
        {
            serviceType.setError("Enter service type");
            serviceType.requestFocus();
            return;
        }
        if(odometerReading.getText().toString().isEmpty())
        {
            odometerReading.setError("Current odometer reading missing");
            odometerReading.requestFocus();
            return;
        }
        if(amount.getText().toString().isEmpty())
        {
            amount.setError("Enter cost");
            amount.requestFocus();
            return;
        }
        progressDialog.setMessage("Adding data");
        progressDialog.show();
        Map<String,Object> data= new HashMap<>();
        data.put("vehicleId",getIntent().getStringExtra("vehicleId"));
        data.put("date",dateEditText.getText().toString());
        data.put("serviceCenter",serviceCenter.getText().toString());
        data.put("contactNum",phoneNum.getText().toString());
        data.put("odometerReading",odometerReading.getText().toString());
        data.put("serviceType",serviceType.getText().toString());
        data.put("amount",amount.getText().toString());
        data.put("description", description.getText().toString());

        db.collection("service").add(data)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        progressDialog.dismiss();
                        if(task.isComplete())
                        {
                            Calendar tempCalendar =myCalendar;
                            tempCalendar.add(Calendar.DAY_OF_YEAR,80);
                            MyNotification.add(getApplicationContext(),"Servicing Remainder","80 days since you last serviced your "+getIntent().getStringExtra("model"),tempCalendar);

                            db.collection("expenses").document(getIntent().getStringExtra("vehicleId")).get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if(task.isSuccessful())
                                            {
                                                DocumentSnapshot d= task.getResult();
                                                String current=d.getString("service");
                                                db.collection("expenses").document(getIntent().getStringExtra("vehicleId"))
                                                        .update("service",addString(current,amount.getText().toString()));
                                            }
                                        }
                                    });

                            if(Integer.parseInt(odometerReading.getText().toString())>
                                    Integer.parseInt(getIntent().getStringExtra("odometer")))
                            {
                                db.collection("vehicles").document(getIntent().getStringExtra("vehicleId")).update("odometer",odometerReading.getText().toString())
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if(task.isSuccessful())
                                                {
                                                    Toast.makeText(AddService.this,"Odometer updated",Toast.LENGTH_SHORT).show();

                                                }

                                            }
                                        });
                            }
                            db.collection("vehicles").document(getIntent().getStringExtra("vehicleId")).get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if(task.isSuccessful())
                                            {

                                                DocumentSnapshot d = task.getResult();
                                                if(checkLatestDate(dateEditText.getText().toString(),d.getString("lastServiced")))
                                                {
                                                    db.collection("vehicles").document(getIntent().getStringExtra("vehicleId")).update("lastServiced",dateEditText.getText().toString())
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if(task.isComplete())
                                                                    {

                                                                    }
                                                                    else {
                                                                        Toast.makeText(AddService.this, "Failed",
                                                                                Toast.LENGTH_LONG).show();
                                                                    }
                                                                }
                                                            });
                                                }

                                                Toast.makeText(AddService.this, "Sucessfully Added",
                                                        Toast.LENGTH_SHORT).show();

                                                finish();
                                            }

                                        }
                                    });

                        }
                        else
                        {
                            Toast.makeText(AddService.this, "Task failed",
                                    Toast.LENGTH_LONG).show();

                        }
                    }
                });

    }
    private String addString(String a,String b)
    {
        double c=Double.parseDouble(a);
        double d=Double.parseDouble(b);
        return c+d+"";
    }


}
