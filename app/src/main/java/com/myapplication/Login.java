package com.myapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class Login extends AppCompatActivity {
    EditText email;
    EditText password;
    Button login;
    TextView register;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email = (EditText)findViewById(R.id.email);
        password = (EditText)findViewById(R.id.password);
        login = (Button)findViewById(R.id.login);
        register=(TextView) findViewById(R.id.register);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        firebaseAuth = FirebaseAuth.getInstance();
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    public void validate()
    {

        progressDialog.setMessage("Logging in");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email.getText().toString(),password.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if(task.isSuccessful())
                        {
                            Intent intent =new Intent(Login.this,Drawer.class);
                            startActivity(intent);
                            finish();
                        }
                        else
                        {
                            if(task.getException() instanceof FirebaseAuthInvalidUserException)
                            {
                                Toast.makeText(Login.this, "User not registered",
                                        Toast.LENGTH_SHORT).show();
                                return;

                            }
                            else if(task.getException() instanceof FirebaseAuthInvalidCredentialsException)
                            {
                                Toast.makeText(Login.this, "Invalid password",
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }

                            Toast.makeText(Login.this, "Authentication Failed",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });


    }
    public void loginHandle(View target)
    {
        if(email.getText().toString().isEmpty())
        {
            Toast.makeText(Login.this, "Enter email",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if(password.getText().toString().isEmpty())
        {
            Toast.makeText(Login.this, "Enter password",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if(!isNetworkAvailable())
        {
            Toast.makeText(Login.this, "Cannot connect to internet",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        validate();
    }
    public void registerHandle(View target)
    {

            Intent intent =new Intent(Login.this,Register.class);
            startActivity(intent);


    }

    @Override
    protected void onResume() {
        super.onResume();
        email.setText("");
        password.setText("");
    }
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, 2000);
        }
    }


}
