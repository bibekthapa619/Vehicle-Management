package com.myapplication;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

public class AddRefuel extends AppCompatActivity {
    EditText dateEditText;
    final Calendar myCalendar = Calendar.getInstance();

    EditText fuelStation;
    EditText totalAmount;
    EditText rate;
    EditText quantity;
    EditText odometerReading;
    ProgressDialog progressDialog;
    FloatingActionButton save;
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_refuel);
        setTitle("Refuel");
        save= (FloatingActionButton) findViewById(R.id.save);

        progressDialog=new ProgressDialog(this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
        progressDialog.setCanceledOnTouchOutside(false);
        dateEditText= (EditText) findViewById(R.id.date);
        db=FirebaseFirestore.getInstance();
        fuelStation = (EditText)findViewById(R.id.fuelStation);
        totalAmount = (EditText)findViewById(R.id.totalAmount);
        rate = (EditText)findViewById(R.id.rate);
        quantity=(EditText) findViewById(R.id.quantity);
        odometerReading=(EditText)findViewById(R.id.odometerReading);

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
                new DatePickerDialog(AddRefuel.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        totalAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(totalAmount.getText().toString().isEmpty() || rate.getText().toString().isEmpty())
                {
                    quantity.setText("null");
                }
                else {
                    int amt=Integer.parseInt(totalAmount.getText().toString());
                    float rat= Float.parseFloat(rate.getText().toString());
                    float q= (float) (amt*1.0/rat);
                    quantity.setText(q+"");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        rate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(totalAmount.getText().toString().isEmpty() || rate.getText().toString().isEmpty())
                {
                    quantity.setText("null");
                }
                else {
                    int amt=Integer.parseInt(totalAmount.getText().toString());
                    float rat= Float.parseFloat(rate.getText().toString());
                    float q= (float) (amt*1.0/rat);
                    quantity.setText(q+"");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(dateEditText.getText().toString().isEmpty())
                    {
                        dateEditText.setError("Select date");
                        dateEditText.requestFocus();
                        return;
                    }
                    if(totalAmount.getText().toString().isEmpty())
                    {
                        totalAmount.setError("Enter amount");
                        totalAmount.requestFocus();
                        return;
                    }
                    if(odometerReading.getText().toString().isEmpty())
                    {
                        odometerReading.setError("Enter current reading");
                        odometerReading.requestFocus();
                        return;
                    }
                    progressDialog.setMessage("Adding data..");
                    progressDialog.show();

                    Map<String,Object> data = new HashMap<>();
                    data.put("vehicleId",getIntent().getStringExtra("vehicleId"));
                    data.put("date",dateEditText.getText().toString());
                    data.put("fuelStation",fuelStation.getText().toString());
                    data.put("totalAmount",totalAmount.getText().toString());
                    data.put("rate",rate.getText().toString());
                    data.put("quantity",quantity.getText().toString());
                    data.put("odometerReading",odometerReading.getText().toString());

                    db.collection("refuel").add(data).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if(task.isComplete())
                            {
//                                Toast.makeText(AddRefuel.this, "Sucessfully Added",
//                                        Toast.LENGTH_LONG).show();
                                db.collection("expenses").document(getIntent().getStringExtra("vehicleId")).get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if(task.isSuccessful())
                                                {
                                                    DocumentSnapshot d= task.getResult();
                                                    String current=d.getString("fuel");
                                                    db.collection("expenses").document(getIntent().getStringExtra("vehicleId"))
                                                            .update("fuel",addString(current,totalAmount.getText().toString()));

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
                                                        Toast.makeText(AddRefuel.this,"Odometer updated",Toast.LENGTH_SHORT).show();

                                                    }

                                                }
                                            });
                                }


                            }
                            else
                            {
                                Toast.makeText(AddRefuel.this, "Task failed",
                                        Toast.LENGTH_LONG).show();

                            }
                        }
                    });
                    db.collection("vehicles").document(getIntent().getStringExtra("vehicleId")).get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    progressDialog.dismiss();
                                    if(task.isSuccessful())
                                    {


                                        DocumentSnapshot d = task.getResult();
                                        if(checkLatestDate(dateEditText.getText().toString(),d.getString("lastFueled")))
                                        {
                                            db.collection("vehicles").document(getIntent().getStringExtra("vehicleId")).update("lastFueled",dateEditText.getText().toString())
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isComplete())
                                                            {
                                                                Toast.makeText(AddRefuel.this, "Sucessfully Added",
                                                                        Toast.LENGTH_LONG).show();

                                                                finish();
                                                            }
                                                            else {
                                                                Toast.makeText(AddRefuel.this, "Failed",
                                                                        Toast.LENGTH_LONG).show();
                                                            }
                                                        }
                                                    });
                                        }
                                        finish();
                                    }

                                }
                            });

                }
            });

    }

    private String addString(String a,String b)
    {
        double c=Double.parseDouble(a);
        double d=Double.parseDouble(b);
        return c+d+"";
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
}
