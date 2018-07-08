package com.example.parjanya.thedeathlyhallows;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import java.sql.SQLException;
import java.util.ArrayList;


public class SignupActivity extends Activity {

    private int gesture_count = 0;
    private QuestionsDataSource questionsDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        gesture_count = 0;
        File gesture_file = new File(getFilesDir(), "user_gesture");
        final GestureLibrary gestureLibrary = GestureLibraries.fromFile(gesture_file);
        GestureOverlayView gestureOverlayView = new GestureOverlayView(SignupActivity.this);
        LinearLayout parent = (LinearLayout) findViewById(R.id.parent_gesture);
        GestureOverlayView.LayoutParams layoutParams = new GestureOverlayView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        gestureOverlayView.setLayoutParams(layoutParams);
        gestureOverlayView.setGestureColor(Color.BLUE);
        gestureOverlayView.setUncertainGestureColor(Color.GRAY);
        gestureOverlayView.setBackgroundColor(Color.RED);
        gestureOverlayView.addOnGesturePerformedListener(new GestureOverlayView.OnGesturePerformedListener() {
            @Override
            public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
                builder.setMessage("Do you want to save this attempt?");
                builder.setTitle("Gesture Attempt");
                final Gesture new_gesture = gesture;
                builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boolean flag = true;
                        if (gesture_count < 3) {
                            if (gesture_count == 0) {
                                gestureLibrary.addGesture("login_gesture", new_gesture);
                                gestureLibrary.save();
                                gesture_count++;
                                Toast.makeText(SignupActivity.this, "Please do it again to increase accuracy", Toast.LENGTH_SHORT).show();
                            } else {
                                ArrayList<Prediction> predictions = gestureLibrary.recognize(new_gesture);
                                for (Prediction prediction : predictions) {
                                    if (prediction.score > 2.0) {
                                        gestureLibrary.addGesture("login_gesture", new_gesture);
                                        gestureLibrary.save();
                                        flag = false;
                                        break;
                                    }
                                }
                                if (flag) {
                                    Toast.makeText(SignupActivity.this, "You cannot add different Gestures", Toast.LENGTH_SHORT).show();
                                } else {
                                    if (gesture_count == 1)
                                        Toast.makeText(SignupActivity.this, "Please do it again to increase accuracy", Toast.LENGTH_SHORT).show();
                                    else {
                                        SharedPreferences sp = getSharedPreferences("Hallows", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sp.edit();
                                        editor.putInt("gesture_registered", 1);
                                        editor.apply();
                                        Toast.makeText(SignupActivity.this, "You are good to go!", Toast.LENGTH_SHORT).show();
                                    }
                                    gesture_count++;
                                }
                            }
                        } else
                            Toast.makeText(SignupActivity.this, "Gesture already added!", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });
        parent.addView(gestureOverlayView);
        Button delete_gesture = (Button) findViewById(R.id.clear_gesture);
        delete_gesture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gesture_count < 3) {
                    gestureLibrary.removeEntry("login_gesture");
                    gestureLibrary.save();
                    gesture_count = 0;
                    Toast.makeText(SignupActivity.this, "All attempts cleared!", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(SignupActivity.this, "Already recorded, cannot be undone!", Toast.LENGTH_SHORT).show();
            }
        });
        Button register = (Button) findViewById(R.id.register_user);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = ((EditText) findViewById(R.id.uname_signup)).getText().toString();
                String uid = ((EditText) findViewById(R.id.uid_signup)).getText().toString();
                String password = ((EditText) findViewById(R.id.password_signup)).getText().toString();
                if (name.isEmpty() && uid.isEmpty() && password.isEmpty()) {
                    Toast.makeText(SignupActivity.this, "Write at least SOMETHING!", Toast.LENGTH_SHORT).show();
                } else if (name.isEmpty() || uid.isEmpty() || password.isEmpty()) {
                    Toast.makeText(SignupActivity.this, "No empty fields allowed!", Toast.LENGTH_SHORT).show();
                } else if ((gesture_count > 0) && (gesture_count < 3)) {
                    Toast.makeText(SignupActivity.this, "First complete your Gesture!", Toast.LENGTH_SHORT).show();
                } else {
                    SharedPreferences sp = getSharedPreferences("Hallows", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putInt("just_installed", 1);
                    editor.putInt("logged_in", 1);
                    editor.putString("Name", name);
                    editor.putString("UID",uid);
                    try {
                        password = SimpleCrypto.encrypt("FORTHEGREATERGOOD",password);
                    }catch (Exception e){
                        Log.i("Encryption Problem",e.getMessage());
                    }
                    editor.putString(uid, password);
                    editor.putInt("Marks",0);
                    editor.putInt("Correct",0);
                    editor.putInt("Wrong",0);
                    editor.putInt("Hints",0);
                    editor.putInt("Best_time",-1);
                    editor.apply();
                    questionsDataSource = new QuestionsDataSource(SignupActivity.this);
                    try {
                        questionsDataSource.open();
                    }catch (SQLException e){
                        Log.i("SQL Exception","Something is wrong with the database!");
                    }
                    questionsDataSource.createQuestion("What are the correct initials of Albus Dumbledore?","APBWD","APWBD","ABPWD","ABWPD","B","His fathers name is Percival",0);
                    questionsDataSource.createQuestion("Whose pet is a cat?","Filtch","Hermoine","Both","None","C","Mrs Crookshanks",1);
                    questionsDataSource.createQuestion("Who was Harry's best friend?","Ronald","Snape","Malfoy","Neville","A","The Red Haired",2);
                    questionsDataSource.createQuestion("A position in Quidditch","Snatcher","Grabber","Bowler","Keeper","D","The position is also in football",3);
                    questionsDataSource.createQuestion("Who was the monster of Slytherine?","Aragog","Basilisk","Norbert","Fluffy","B","A giant snake",4);
                    questionsDataSource.close();
                    Intent rule_act = new Intent(SignupActivity.this, RuleActivity.class);
                    startActivity(rule_act);
                    SignupActivity.this.finish();
                }
            }
        });
    }
}