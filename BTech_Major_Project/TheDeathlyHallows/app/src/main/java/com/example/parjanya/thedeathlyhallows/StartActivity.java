package com.example.parjanya.thedeathlyhallows;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;


public class StartActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                ProgressBar load = (ProgressBar)findViewById(R.id.loading);
                load.setVisibility(View.GONE);
                SharedPreferences sp = getSharedPreferences("Hallows", Context.MODE_PRIVATE);
                if(sp.getInt("just_installed",-1)==-1){
                    Intent signup = new Intent(StartActivity.this,SignupActivity.class);
                    startActivity(signup);
                    StartActivity.this.finish();
                }
                else{
                    if(sp.getInt("logged_in",-1)==-1){
                        Intent login_act = new Intent(StartActivity.this,LoginActivity.class);
                        startActivity(login_act);
                        StartActivity.this.finish();
                    }
                    else{
                        Intent home_act = new Intent(StartActivity.this,HomeActivity.class);
                        startActivity(home_act);
                        StartActivity.this.finish();
                    }
                }
                return false;
            }
        });
        handler.sendMessageDelayed(new Message(), 2000);
    }
}
