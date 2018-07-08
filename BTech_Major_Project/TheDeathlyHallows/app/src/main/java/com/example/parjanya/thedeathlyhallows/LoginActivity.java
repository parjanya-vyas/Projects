package com.example.parjanya.thedeathlyhallows;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;


public class LoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        final SharedPreferences sp = getSharedPreferences("Hallows", Context.MODE_PRIVATE);
        File gesture_file = new File(getFilesDir(),"user_gesture");
        final GestureLibrary gestureLibrary = GestureLibraries.fromFile(gesture_file);
        GestureOverlayView gestureOverlayView = new GestureOverlayView(LoginActivity.this);
        final LinearLayout parent = (LinearLayout)findViewById(R.id.parent_login);
        GestureOverlayView.LayoutParams layoutParams = new GestureOverlayView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        gestureOverlayView.setLayoutParams(layoutParams);
        gestureOverlayView.setGestureColor(Color.BLUE);
        gestureOverlayView.setUncertainGestureColor(Color.GRAY);
        gestureOverlayView.setBackgroundColor(Color.RED);
        gestureOverlayView.addOnGesturePerformedListener(new GestureOverlayView.OnGesturePerformedListener() {
            @Override
            public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
                Intent home_act = new Intent(LoginActivity.this, HomeActivity.class);
                ArrayList<Prediction> predictions = gestureLibrary.recognize(gesture);
                for (Prediction prediction : predictions) {
                    if (prediction.score > 3.0) {
                        startActivity(home_act);
                        LoginActivity.this.finish();
                        break;
                    }
                }
            }
        });
        if((!gestureLibrary.load())&&(sp.getInt("gesture_registered",-1)!=-1)){
            LoginActivity.this.finish();
        }
        if(sp.getInt("gesture_registered",-1)!=-1)
            parent.addView(gestureOverlayView);
        else
            parent.setVisibility(View.GONE);
        Button login_button = (Button)findViewById(R.id.login_button);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText uid_edt = (EditText)findViewById(R.id.uid_login);
                EditText pass_edt = (EditText)findViewById(R.id.password_login);
                String uid = uid_edt.getText().toString();
                String password = pass_edt.getText().toString();
                if(uid.isEmpty()&&password.isEmpty())
                    Toast.makeText(LoginActivity.this,"Write at least something!",Toast.LENGTH_SHORT).show();
                else if (uid.isEmpty()||password.isEmpty())
                    Toast.makeText(LoginActivity.this,"No empty fields are permissible!",Toast.LENGTH_SHORT).show();
                else{
                    String stored_password = sp.getString(uid,"");
                    try {
                        stored_password = SimpleCrypto.decrypt("FORTHEGREATERGOOD",stored_password);
                    }catch (Exception e){
                        Log.d("Encryption",e.getMessage());
                    }
                    Log.d("Password",stored_password);
                    if(!stored_password.equals(password)){
                        Toast.makeText(LoginActivity.this,"Awww!Login Failure!",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Intent home_activity = new Intent(LoginActivity.this,HomeActivity.class);
                        startActivity(home_activity);
                        LoginActivity.this.finish();
                    }
                }
            }
        });
    }
}
