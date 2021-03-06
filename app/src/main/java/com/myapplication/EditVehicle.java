package com.myapplication;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import dataTypes.Vehicle;

public class EditVehicle extends AppCompatActivity {

    EditText vehicleType;
    EditText model;
    EditText manufacturer;
    EditText regd;
    EditText regdState;
    EditText color;
    FloatingActionButton save;
    FirebaseFirestore db;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vehicle);
        vehicleType = (EditText) findViewById(R.id.vehicleType);
        model = (EditText) findViewById(R.id.model);
        manufacturer = (EditText) findViewById(R.id.manufacturer);
        regd = (EditText) findViewById(R.id.registrationNum);
        regdState =(EditText) findViewById(R.id.registrationState);
        color = (EditText) findViewById(R.id.color);
        save = (FloatingActionButton) findViewById(R.id.save);
        progressDialog = new ProgressDialog(this,ProgressDialog.THEME_DEVICE_DEFAULT_DARK);
        progressDialog.setCanceledOnTouchOutside(false);

        db = FirebaseFirestore.getInstance();
        init();
    }
    public void init()
    {
        progressDialog.setMessage("Loading..");
        progressDialog.show();
        DocumentReference documentReference=db.collection("vehicles").document(getIntent().getStringExtra("documentId"));
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                progressDialog.dismiss();
                if(task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    vehicleType.setText(document.getString("vehicleType"));
                    model.setText(document.getString("model"));
                    manufacturer.setText(document.getString("manufacturer"));
                    regd.setText(document.getString("regdNum"));
                    regdState.setText(document.getString("regdState"));
                    color.setText(document.getString("color"));
                }
                else {
                    Toast.makeText(EditVehicle.this, "Failed to retreive",
                            Toast.LENGTH_LONG).show();

                }
            }
        });

    }
    public  void registerVehicle(View target) {
        if (regd.getText().toString().isEmpty()) {
            regd.setError("Empty registration number");
            return;
        }
        progressDialog.setMessage("saving vehicle details");
        progressDialog.show();
        Map<String, Object> data = new HashMap<>();
        data.put("ownerId", FirebaseAuth.getInstance().getCurrentUser().getUid());
        data.put("vehicleType", vehicleType.getText().toString());
        data.put("model", model.getText().toString());
        data.put("regdNum", regd.getText().toString());
        data.put("regdState", regdState.getText().toString());
        data.put("color", color.getText().toString());
        db.collection("vehicles").document(getIntent().getStringExtra("documentId")).update(data)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isComplete())
                        {

                            Toast.makeText(EditVehicle.this, "Updated Sucessfully",
                                    Toast.LENGTH_LONG).show();
                            finish();
                        }
                        else {

                            Toast.makeText(EditVehicle.this, "Task failed",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
