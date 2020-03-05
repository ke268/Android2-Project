package com.example.android2_ereport;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    EditText ename, enumber;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    private static final String myinfo = "Information";
    private static final String myName = "Name";
    private static final String myNumber = "Number";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ename = findViewById(R.id.name);
        enumber = findViewById(R.id.number);


        sharedPreferences = getSharedPreferences(myinfo, MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }


    public void Register(View view) {


        String name = ename.getText().toString();
        String number = enumber.getText().toString();

        editor.putString(myName, name);
        editor.putString(myNumber, number);
        editor.commit();
        editor.clear();

        if ((ename.getText().toString().equals(""))&&(enumber.getText().toString().equals(""))){
            Toast.makeText(this, "Please provide the following information.", Toast.LENGTH_SHORT).show();
        }

        else{
            Toast.makeText(this, "Saved.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, Main2Activity.class));
        }

    }


}