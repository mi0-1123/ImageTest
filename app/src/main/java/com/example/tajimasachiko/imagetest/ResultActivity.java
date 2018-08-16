package com.example.tajimasachiko.imagetest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.view.View;

public class ResultActivity extends AppCompatActivity {

    private Button button0;
    private Button button1;
    private Button button2;

    private Button backBottun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        button0 = (Button)findViewById(R.id.button0);
        button1 = (Button)findViewById(R.id.button1);
        button2 = (Button)findViewById(R.id.button2);

        backBottun = (Button)findViewById(R.id.backBottun);

    }

    public void onClick(View view) {

        ImageView itemImage = (ImageView) findViewById(R.id.items);
        if (view.equals(button0)) {
            itemImage.setImageResource(R.drawable.coin);
        } else if (view.equals(button1)) {
            itemImage.setImageResource(R.drawable.star);
        } else if (view.equals(button2)) {
            itemImage.setImageResource(R.drawable.mash);
        } else if(view.equals(backBottun)){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }else {
            itemImage.setImageResource(R.drawable.ic_launcher_background);
        }

    }
}
