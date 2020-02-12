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

public class AddPermit extends AppCompatActivity {

    EditText issuedDate;
    EditText expiryDate;

    EditText permitType;
    EditText permitNum;
    EditText permitCost;
    EditText description;
    FirebaseFirestore db;
    ProgressDialog progressDialog;

    final Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_permit);

        setTitle("Accident Details");
        issuedDate= (EditText) findViewById(R.id.dateRenewed);
        expiryDate =(EditText) findViewById(R.id.expiryDate);
        db=FirebaseFirestore.getInstance();
        progressDialog=new ProgressDialog(this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
        progressDialog.setCanceledOnTouchOutside(false);
        permitType=(EditText) findViewById(R.id.permitType);
        permitNum=(EditText) findViewById(R.id.permitNum);
        permitCost=(EditText) findViewById(R.id.cost);
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
                new DatePickerDialog(AddPermit.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();

            }
        });
        expiryDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(AddPermit.this, date1, myCalendar
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
    public  void addPermit(View target)
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

        if(permitType.getText().toString().isEmpty())
        {
            permitType.setError("enter permit type");
            permitType.requestFocus();
            return;
        }
        if(permitNum.getText().toString().isEmpty())
        {
            permitNum.setError("enter permit num");
            permitNum.requestFocus();
            return;
        }
        if(permitCost.getText().toString().isEmpty())
        {
            permitCost.setError("enter cost");
            permitCost.requestFocus();
            return;
        }
        progressDialog.setMessage("Adding..");
        progressDialog.show();
        Map<String,Object> data= new HashMap<>();
        data.put("vehicleId",getIntent().getStringExtra("vehicleId"));
        data.put("issuedDate",issuedDate.getText().toString());
        data.put("expiryDate",expiryDate.getText().toString());
        data.put("permitType",permitType.getText().toString());
        data.put("permitNum",permitNum.getText().toString());
        data.put("permitCost",permitCost.getText().toString());
        data.put("description",description.getText().toString());

        db.collection("permit").add(data).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                progressDialog.dismiss();
                if(task.isComplete())
                {
                    Toast.makeText(AddPermit.this, "Sucessfully Added",
                            Toast.LENGTH_LONG).show();
                    db.collection("expenses").document(getIntent().getStringExtra("vehicleId")).get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful())
                                    {
                                        DocumentSnapshot d= task.getResult();
                                        String current=d.getString("permit");
                                        db.collection("expenses").document(getIntent().getStringExtra("vehicleId"))
                                                .update("permit",addString(current,permitCost.getText().toString()));
                                        finish();
                                    }
                                }
                            });


                }
                else
                {
                    Toast.makeText(AddPermit.this, "Task failed",
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
