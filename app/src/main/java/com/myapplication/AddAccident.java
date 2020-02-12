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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddAccident extends AppCompatActivity {

    EditText dateEditText;

    final Calendar myCalendar = Calendar.getInstance();

    EditText place;
    EditText driver;
    EditText odometerReading;
    EditText amount;
    EditText description;
    FirebaseFirestore db;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_accident);
        db=FirebaseFirestore.getInstance();
        progressDialog=new ProgressDialog(this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
        progressDialog.setCanceledOnTouchOutside(false);

        setTitle("Accident Details");
        dateEditText= (EditText) findViewById(R.id.date);
        place=(EditText)findViewById(R.id.place);
        driver=(EditText)findViewById(R.id.driver);
        odometerReading=(EditText)findViewById(R.id.odometerReading);
        amount = (EditText) findViewById(R.id.amount);
        description =(EditText) findViewById(R.id.description);

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
                new DatePickerDialog(AddAccident.this, date, myCalendar
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

    private String addString(String a,String b)
    {
        double c=Double.parseDouble(a);
        double d=Double.parseDouble(b);
        return c+d+"";
    }
    public void addAccident(View target)
    {
        if(dateEditText.getText().toString().isEmpty())
        {
            dateEditText.setError("Select Date");
            dateEditText.requestFocus();
            return;
        }
        if(place.getText().toString().isEmpty())
        {
            place.setError("Enter place");
            place.requestFocus();
            return;
        }
        if(amount.getText().toString().isEmpty())
        {
            amount.setError("Enter cost");
            amount.requestFocus();
            return;
        }
        progressDialog.setMessage("Adding data..");
        progressDialog.show();

        Map<String,Object> data= new HashMap<>();
        data.put("vehicleId",getIntent().getStringExtra("vehicleId"));
        data.put("date",dateEditText.getText().toString());
        data.put("place",place.getText().toString());
        data.put("driver",driver.getText().toString());
        data.put("odometerReading",odometerReading.getText().toString());
        data.put("amount",amount.getText().toString());
        data.put("description",description.getText().toString());

        db.collection("accident").add(data).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                progressDialog.dismiss();
                if(task.isComplete())
                {
                    db.collection("expenses").document(getIntent().getStringExtra("vehicleId")).get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful())
                                    {
                                        DocumentSnapshot d= task.getResult();
                                        String current=d.getString("accident");
                                        db.collection("expenses").document(getIntent().getStringExtra("vehicleId"))
                                                .update("accident",addString(current,amount.getText().toString()));

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
                                            Toast.makeText(AddAccident.this,"Odometer updated",Toast.LENGTH_SHORT).show();

                                        }

                                    }
                                });
                    }

                    Toast.makeText(AddAccident.this, "Sucessfully Added",
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
                else
                {
                    Toast.makeText(AddAccident.this, "Task failed",
                            Toast.LENGTH_LONG).show();

                }
            }
        });

    }

}
