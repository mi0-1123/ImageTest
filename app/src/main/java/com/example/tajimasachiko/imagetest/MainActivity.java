package com.example.tajimasachiko.imagetest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.view.View;

import java.sql.BatchUpdateException;


public class MainActivity extends AppCompatActivity {

    private Button connectButon;
    private Button nextBotton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connectButon = (Button)findViewById(R.id.connectButton);
        nextBotton = (Button)findViewById(R.id.nextBotton);
    }

    //connection botton
    public void onClick(View view){
        if(view.equals(connectButon)){
            //hogehoge
        } else if(view.equals(nextBotton)){
            Intent intent = new Intent(this, ResultActivity.class);
            startActivity(intent);
        }


    }


}
