package com.myapplication;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Expenses extends AppCompatActivity {

    TextView service,fuel,insurance,permit,accident;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses);

        setTitle("Expenses");

        service =(TextView) findViewById(R.id.service);
        fuel =(TextView) findViewById(R.id.fuel);
        insurance=(TextView) findViewById(R.id.insurance);
        permit=(TextView) findViewById(R.id.permit);
        accident=(TextView) findViewById(R.id.accident);
        progressDialog=new ProgressDialog(this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
        progressDialog.setCanceledOnTouchOutside(false);

        init();
    }
    private void init()
    {
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        progressDialog.setMessage("loading...");
        progressDialog.show();
        db.collection("expenses").document(getIntent().getStringExtra("vehicleId")).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        progressDialog.dismiss();
                        if(task.isSuccessful())
                        {
                            DocumentSnapshot d=task.getResult();
                            service.setText(d.getString("service"));
                            fuel.setText(d.getString("fuel"));
                            insurance.setText(d.getString("insurance"));
                            permit.setText(d.getString("permit"));
                            accident.setText(d.getString("accident"));
                        }
                        else {
                            Toast.makeText(Expenses.this,"Failed to load",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
