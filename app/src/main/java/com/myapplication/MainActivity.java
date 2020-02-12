package com.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Thread myThread = new Thread(){
            @Override
            public void run()
            {
                try {
                    sleep(1200);

                    Intent intent;
                    if(FirebaseAuth.getInstance().getCurrentUser()==null) {
                        intent = new Intent(MainActivity.this, Login.class);
                    }
                    else {
                        intent = new Intent(MainActivity.this, Drawer.class);
                    }

                    startActivity(intent);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        };
        myThread.start();
    }
}
