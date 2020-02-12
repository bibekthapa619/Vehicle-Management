package com.myapplication;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class VehiclePanel extends AppCompatActivity {


    TextView vehicleModel;
    TextView vehicleNum;
    TextView odometer;
    TextView actualReading;
    TextView service;
    TextView refuel;
    TextView accident;
    TextView permit;
    TextView insurance;
    TextView expenses;
    String text;
    ProgressDialog progressDialog;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_panel);
        vehicleModel=(TextView) findViewById(R.id.vehicleModel);
        vehicleNum=(TextView) findViewById(R.id.vehicleNum);
        vehicleModel.setText(getIntent().getStringExtra("model"));
        vehicleNum.setText(getIntent().getStringExtra("regdnum"));
        text="";
        odometer=(TextView) findViewById(R.id.odometer);
        actualReading=(TextView) findViewById(R.id.actualReading);
        service =(TextView) findViewById(R.id.serviceEntry);
        refuel=(TextView) findViewById(R.id.refuel);
        accident=(TextView) findViewById(R.id.accident);
        permit=(TextView)findViewById(R.id.permitRenewal);
        insurance=(TextView) findViewById(R.id.insurance);
        expenses=(TextView) findViewById(R.id.expense);
        progressDialog = new ProgressDialog(this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
        progressDialog.setCanceledOnTouchOutside(false);

        load();

    }
    private void load()
    {
        FirebaseFirestore db =FirebaseFirestore.getInstance();
        progressDialog.show();
        db.collection("vehicles").document(getIntent().getStringExtra("vehicleId")).get()
                .addOnCompleteListener(
                        new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                progressDialog.dismiss();
                                if(task.isSuccessful())
                                {
                                    DocumentSnapshot d= task.getResult();
                                    text= new String(d.getString("odometer"));

                                    actualReading.setText(text);
                                }
                            }
                        }
                );

    }

    public void onClick(View target)
    {
        Intent intent=new Intent();
        if(target.equals(odometer) || target.equals(actualReading))
        {
            intent =new Intent(VehiclePanel.this,OdometerUpdate.class);

        }
        else if(target.equals(service))
        {
            intent =new Intent(VehiclePanel.this,ServiceDetails.class);

        }
        else if(target.equals(refuel))
        {
            intent =new Intent(VehiclePanel.this,FuelDetails.class);

        }
        else if(target.equals(accident)){

            intent =new Intent(VehiclePanel.this,AccidentDetails.class);
        }
        else if(target.equals(permit))
        {

            intent =new Intent(VehiclePanel.this,PermitDetails.class);
        }
        else if(target.equals(insurance))
        {

            intent =new Intent(VehiclePanel.this,InsuranceDetails.class);
        }
        else if(target.equals(expenses))
        {
            intent =new Intent(VehiclePanel.this,Expenses.class);
        }
        intent.putExtra("vehicleId",getIntent().getStringExtra("vehicleId"));
        intent.putExtra("odometer",text);
        intent.putExtra("model",getIntent().getStringExtra("model"));
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        load();
    }
}
