package com.mushfiks_app.musfiq.dxball;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {


    private Button button,buttonExit,buttonHelp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.buttonStart);
        buttonExit = findViewById(R.id.buttonExit);
        buttonHelp = findViewById(R.id.buttonHelp);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGameCanvas();
            }
        });

    }
    public void openGameCanvas(){
        Intent intent = new Intent(this,GameCanvas.class);
        startActivity(intent);

    }

    public void exitGame(View view){
        System.exit(1);
    }

    public void helpUser(View view){
        Intent intent = new Intent(this,Help.class);
        startActivity(intent);
    }

}