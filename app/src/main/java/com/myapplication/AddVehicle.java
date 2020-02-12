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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddVehicle extends AppCompatActivity {

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
        progressDialog = new ProgressDialog(AddVehicle.this,ProgressDialog.THEME_DEVICE_DEFAULT_DARK);
        progressDialog.setCanceledOnTouchOutside(false);

        db = FirebaseFirestore.getInstance();
    }

    public  void registerVehicle(View target)
    {
        if(regd.getText().toString().isEmpty())
        {
            regd.setError("Empty registration number");
            regd.requestFocus();
            return;
        }
        progressDialog.setMessage("saving vehicle details");
        progressDialog.show();
        Map<String,Object> data = new HashMap<>();
        data.put("ownerId",FirebaseAuth.getInstance().getCurrentUser().getUid());
        data.put("vehicleType",vehicleType.getText().toString());
        data.put("model",model.getText().toString());
        data.put("regdNum",regd.getText().toString());
        data.put("regdState",regdState.getText().toString());
        data.put("color",color.getText().toString());
        data.put("lastServiced","");
        data.put("lastFueled","");
        data.put("odometer","0");
        db.collection("vehicles").add(data)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if(task.isSuccessful())
                        {
                            progressDialog.dismiss();
                            DocumentReference d=task.getResult();
                            Map<String,Object> data2=new HashMap<>();
                            data2.put("service","0");
                            data2.put("fuel","0");
                            data2.put("permit","0");
                            data2.put("insurance","0");
                            data2.put("accident","0");
                            db.collection("expenses").document(d.getId()).set(data2)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            Toast.makeText(AddVehicle.this, "Sucessfully registered",
                                             Toast.LENGTH_LONG).show();
                                        }
                                    });
                        finish();
                        }
                        else
                        {
                            progressDialog.dismiss();
//
                        Toast.makeText(AddVehicle.this, "Vehicle registration failed",
                                Toast.LENGTH_LONG).show();
                        }
                    }
                });
//                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                    @Override
//                    public void onSuccess(DocumentReference documentReference) {
//
//                        Map<String,Object> data2=new HashMap<>();
//                        data2.put("service","0");
//                        data2.put("fuel","0");
//                        data2.put("permit","0");
//                        data2.put("insurance","0");
//                        data2.put("accident","0");
//                        db.collection("expenses").add(data2).addOnCompleteListener(
//                                new OnCompleteListener<DocumentReference>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<DocumentReference> task) {
//                                        progressDialog.dismiss();
//
//                                        Toast.makeText(AddVehicle.this, "Vehicle added Sucessfully",
//                                                Toast.LENGTH_LONG).show();
//                                    }
//                                }
//                        );
//                        finish();
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        progressDialog.dismiss();
//
//                        Toast.makeText(AddVehicle.this, "Vehicle registration failed",
//                                Toast.LENGTH_LONG).show();
//                    }
//                });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
