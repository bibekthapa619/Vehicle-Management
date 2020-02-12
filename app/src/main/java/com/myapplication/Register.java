package com.myapplication;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    EditText email;
    EditText firstName;
    EditText lastName;
    EditText password;
    EditText confirmPassword;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        email =(EditText) findViewById(R.id.email);
        password=(EditText) findViewById(R.id.password);
        firstName=(EditText)findViewById(R.id.firstName);
        lastName=(EditText) findViewById(R.id.lastName);
        confirmPassword=(EditText)findViewById(R.id.confirmPassword);
        progressDialog = new ProgressDialog(this,ProgressDialog.THEME_DEVICE_DEFAULT_DARK);
        progressDialog.setCanceledOnTouchOutside(false);
        firebaseAuth = FirebaseAuth.getInstance();
        db= FirebaseFirestore.getInstance();
    }
    public void registerUser(View target)
    {

        if(firstName.getText().toString().isEmpty())
        {
            firstName.setError("First Name is empty");
            firstName.requestFocus();
            return ;
        }
        if(lastName.getText().toString().isEmpty())
        {
            lastName.setError("Last name is empty");
            lastName.requestFocus();
            return ;
        }
        if(email.getText().toString().isEmpty())
        {
            email.setError("Email is empty");
            email.requestFocus();
            return ;
        }


        if(!email.getText().toString().matches(emailPattern))
        {
            email.setError("Invalid email format");
            email.requestFocus();
            return ;
        }
        if(password.getText().toString().length()<6)
        {
            password.setError("Password must contain atleast 6 characters");
            password.requestFocus();
            return ;
        }


        if(!password.getText().toString().equals(confirmPassword.getText().toString()))
        {
            confirmPassword.setError("Password donot match");
            confirmPassword.requestFocus();
            return ;
        }
        progressDialog.setMessage("Registering User");
        progressDialog.show();
       firebaseAuth.createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        progressDialog.dismiss();
                        if(task.isSuccessful())
                        {
//                            Toast.makeText(Register.this, "Registered Sucessfully",
//                                    Toast.LENGTH_LONG).show();
//                            finish();
                            String uid;
                            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            Map<String,Object> data =new HashMap<>();

                            data.put("firstName",firstName.getText().toString());
                            data.put("lastName",lastName.getText().toString());

                            db.collection("users").document(uid).set(data)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {

                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            progressDialog.dismiss();
                                            Toast.makeText(Register.this, "Registered Sucessfully",
                                                    Toast.LENGTH_LONG).show();
                                            finish();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progressDialog.dismiss();
                                            Toast.makeText(Register.this, "Registration failed",
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }
                        else
                        {
                            progressDialog.dismiss();
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(Register.this,
                                        "User with this email already exist.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            Toast.makeText(Register.this, "Registration failed",
                                    Toast.LENGTH_LONG).show();

                        }
                    }
                });



    }

}
