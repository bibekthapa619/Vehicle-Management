package com.myapplication;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class OdometerUpdate extends AppCompatActivity {

    ImageView edit;
    EditText editText;
    Button save;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_odometer_update);
        editText = (EditText) findViewById(R.id.odometerReading);
        editText.setEnabled(false);
        progressDialog = new ProgressDialog(this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
        progressDialog.setCanceledOnTouchOutside(false);
        editText.setText(getIntent().getStringExtra("odometer"));
        edit=(ImageView) findViewById(R.id.edit);
        save=(Button) findViewById(R.id.save);
        save.setVisibility(View.GONE);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.setEnabled(true);
                editText.requestFocus();
                save.setVisibility(View.VISIBLE);
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editText.getText().toString().isEmpty())
                {
                    editText.setError("Enter current reading");
                    editText.requestFocus();
                    return;
                }
                FirebaseFirestore db=FirebaseFirestore.getInstance();
                progressDialog.show();
                db.collection("vehicles").document(getIntent().getStringExtra("vehicleId")).update("odometer",editText.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                progressDialog.dismiss();
                                        if(task.isSuccessful())
                                        {
                                            Toast.makeText(OdometerUpdate.this,"Sucessfully updated",Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                        else {
                                            Toast.makeText(OdometerUpdate.this,"Failed to update",Toast.LENGTH_SHORT).show();

                                        }

                            }
                        });


            }
        });
    }

}
