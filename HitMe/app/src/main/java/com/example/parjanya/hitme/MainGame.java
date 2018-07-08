package com.example.parjanya.hitme;

import android.app.Activity;
import android.graphics.Color;
import android.os.Handler;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class MainGame extends Activity implements View.OnClickListener{

    TextView[] pads = new TextView[9];
    Handler newPad = new Handler();
    Runnable turnOn, turnOff;
    long highlightedPad = -1;
    boolean isRunning = false;
    Button startStopButton;
    int score=0;
    long time = 700;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_game);
        startStopButton = (Button)findViewById(R.id.start_stop_button);
        pads[0] = (TextView)findViewById(R.id.pad1);
        pads[1] = (TextView)findViewById(R.id.pad2);
        pads[2] = (TextView)findViewById(R.id.pad3);
        pads[3] = (TextView)findViewById(R.id.pad4);
        pads[4] = (TextView)findViewById(R.id.pad5);
        pads[5] = (TextView)findViewById(R.id.pad6);
        pads[6] = (TextView)findViewById(R.id.pad7);
        pads[7] = (TextView)findViewById(R.id.pad8);
        pads[8] = (TextView)findViewById(R.id.pad9);
        turnOn = new Runnable() {
            @Override
            public void run() {
                highlightedPad = (long)(Math.random()*(double)9);
                pads[(int)highlightedPad].setBackgroundColor(Color.BLACK);
                newPad.postDelayed(turnOff,time-200);
            }
        };
        turnOff = new Runnable() {
            @Override
            public void run() {
                pads[(int)highlightedPad].setBackgroundColor(Color.WHITE);
                if (isRunning){
                    newPad.postDelayed(turnOn,time);
                }
            }
        };
        startStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (startStopButton.getText().toString().equals("Start")){
                    startStopButton.setText("Stop");
                    isRunning = true;
                    newPad.postDelayed(turnOn,300);
                }
                else {
                    stopGame();
                }
            }
        });
        for (int i=0;i<9;i++){
            pads[i].setOnClickListener(this);
        }
    }

    void stopGame(){
        startStopButton.setText("Start");
        isRunning = false;
        time = 700;
        pads[(int)highlightedPad].setBackgroundColor(Color.WHITE);
        newPad.removeCallbacksAndMessages(null);
    }

    @Override
    public void onClick(View v) {
        if (v==pads[(int)highlightedPad])
            correctAnswer();
        else
            wrongAnswer();
    }

    void correctAnswer(){
        score++;
        if (time>300)
            time-=3;
    }

    void wrongAnswer(){
        showMessage("Oops! Score : "+score);
        stopGame();
    }

    void showMessage(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }
}
