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

public class AddInsurance extends AppCompatActivity {
    EditText issuedDate;
    EditText expiryDate;

    EditText insuranceType;
    EditText insuranceNum;
    EditText insuranceCost;
    EditText description;
    FirebaseFirestore db;
    ProgressDialog progressDialog;

    final Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_insurance);
        setTitle("Insurance Details");
        issuedDate= (EditText) findViewById(R.id.dateRenewed);
        expiryDate =(EditText) findViewById(R.id.expiryDate);
        db=FirebaseFirestore.getInstance();
        progressDialog=new ProgressDialog(this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
        progressDialog.setCanceledOnTouchOutside(false);

        insuranceType=(EditText) findViewById(R.id.insuranceType);
        insuranceNum=(EditText) findViewById(R.id.insuranceNum);
        insuranceCost=(EditText) findViewById(R.id.cost);
        description=(EditText) findViewById(R.id.description);

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateIssuedDate();
            }

        };

        final DatePickerDialog.OnDateSetListener date1 = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateExpiryDate();
            }

        };
        issuedDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(AddInsurance.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        expiryDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(AddInsurance.this, date1, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();

            }
        });

    }
    private void updateIssuedDate() {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        issuedDate.setText(sdf.format(myCalendar.getTime()));
    }

    private void updateExpiryDate() {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        expiryDate.setText(sdf.format(myCalendar.getTime()));
    }
    public void addInsurance(View target)
    {
        if(issuedDate.getText().toString().isEmpty())
        {
            issuedDate.setError("Select Date");
            issuedDate.requestFocus();
            return;
        }
        if(expiryDate.getText().toString().isEmpty())
        {
            expiryDate.setError("Select date");
            expiryDate.requestFocus();
            return;
        }

        if(insuranceType.getText().toString().isEmpty())
        {
            insuranceType.setError("enter insurance type");
            insuranceType.requestFocus();
            return;
        }
        if(insuranceNum.getText().toString().isEmpty())
        {
            insuranceNum.setError("enter insurance num");
            insuranceNum.requestFocus();
            return;
        }
        if(insuranceCost.getText().toString().isEmpty())
        {
            insuranceCost.setError("enter cost");
            insuranceCost.requestFocus();
            return;
        }
        progressDialog.setMessage("Adding..");
        progressDialog.show();
        Map<String,Object> data= new HashMap<>();
        data.put("vehicleId",getIntent().getStringExtra("vehicleId"));
        data.put("issuedDate",issuedDate.getText().toString());
        data.put("expiryDate",expiryDate.getText().toString());
        data.put("insuranceType",insuranceType.getText().toString());
        data.put("insuranceNum",insuranceNum.getText().toString());
        data.put("insuranceCost",insuranceCost.getText().toString());
        data.put("description",description.getText().toString());

        db.collection("insurance").add(data).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                progressDialog.dismiss();
                if(task.isComplete())
                {
                    Toast.makeText(AddInsurance.this, "Sucessfully Added",
                            Toast.LENGTH_LONG).show();
                    db.collection("expenses").document(getIntent().getStringExtra("vehicleId")).get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful())
                                    {
                                        DocumentSnapshot d= task.getResult();
                                        String current=d.getString("insurance");
                                        db.collection("expenses").document(getIntent().getStringExtra("vehicleId"))
                                                .update("insurance",addString(current,insuranceCost.getText().toString()));
                                        finish();
                                    }
                                }
                            });
                }
                else
                {
                    Toast.makeText(AddInsurance.this, "Task failed",
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
