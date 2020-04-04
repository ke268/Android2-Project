package com.example.android2_ereport;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

public class Splashscreen extends AppCompatActivity {

    private static final String myinfo = "Information";
    private static final String myName = "Name";
    private static final String myNumber = "Number";
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        sharedPreferences = getSharedPreferences(myinfo, MODE_PRIVATE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                String gName=sharedPreferences.getString(myName,"");
                String gNumber=sharedPreferences.getString(myNumber,"");

                if(gName.isEmpty() && gNumber.isEmpty()){
                    Intent i = new Intent(Splashscreen.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }else{
                    startActivity(new Intent(Splashscreen.this, Main2Activity.class));

                }


            }
        },3000);




    }
}

