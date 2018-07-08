package com.example.parjanya.thedeathlyhallows;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;


public class HomeActivity extends Activity {

    private PopupWindow popupWindow;
    Intent clipService;
    private int gesture_count=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gesture_count=0;
        setContentView(R.layout.activity_home);
        clipService = new Intent(HomeActivity.this,ClipService.class);
        startService(clipService);
        final SharedPreferences sp = getSharedPreferences("Hallows",MODE_PRIVATE);
        final SharedPreferences.Editor editor = sp.edit();
        File gesture_file = new File(getFilesDir(), "user_gesture");
        final GestureLibrary gestureLibrary = GestureLibraries.fromFile(gesture_file);
        TextView name = (TextView)findViewById(R.id.name_home);
        name.setText(sp.getString("Name",""));
        Button lg_out = (Button)findViewById(R.id.log_out);
        lg_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
        final Button add_new_gesture = (Button)findViewById(R.id.add_gesture_button);
        if (sp.getInt("gesture_registered",-1)!=-1){
            add_new_gesture.setText("Delete Gesture");
        }
        add_new_gesture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText password_again = new EditText(HomeActivity.this);
                password_again.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
                AlertDialog.Builder password_dialog = new AlertDialog.Builder(HomeActivity.this);
                password_dialog.setView(password_again);
                password_dialog.setTitle("Authentication");
                password_dialog.setMessage("Enter your Password");
                password_dialog.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                password_dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String uid = sp.getString("UID", "");
                        String encrypted_pass = sp.getString(uid,"");
                        String stored_password = "";
                        try {
                            stored_password = SimpleCrypto.decrypt("FORTHEGREATERGOOD",encrypted_pass);
                        }catch (Exception e){
                            Log.d("Password Exception",e.getMessage());
                        }
                        if((!stored_password.isEmpty())&&(stored_password.equals(password_again.getText().toString()))){
                            if(sp.getInt("gesture_registered",-1)!=-1) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                                builder.setTitle("Confirm Delete");
                                builder.setMessage("Are you sure you want to delete the current login gesture?");
                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                builder.setPositiveButton("Delete",new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        editor.remove("gesture_registered");
                                        editor.apply();
                                        gestureLibrary.removeEntry("login_gesture");
                                        gestureLibrary.save();
                                        add_new_gesture.setText("Add Gesture");
                                    }
                                });
                                builder.show();
                            }
                            else{
                                GestureOverlayView gestureOverlayView = new GestureOverlayView(HomeActivity.this);
                                GestureOverlayView.LayoutParams layoutParams = new GestureOverlayView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                                gestureOverlayView.setLayoutParams(layoutParams);
                                gestureOverlayView.setGestureColor(Color.BLUE);
                                gestureOverlayView.setUncertainGestureColor(Color.GRAY);
                                gestureOverlayView.setBackgroundColor(Color.RED);
                                gestureOverlayView.addOnGesturePerformedListener(new GestureOverlayView.OnGesturePerformedListener() {
                                    @Override
                                    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
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
                                                        Toast.makeText(HomeActivity.this, "Please do it again to increase accuracy", Toast.LENGTH_SHORT).show();
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
                                                            Toast.makeText(HomeActivity.this, "You cannot add different Gestures", Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            if (gesture_count == 1)
                                                                Toast.makeText(HomeActivity.this, "Please do it again to increase accuracy", Toast.LENGTH_SHORT).show();
                                                            else {
                                                                editor.putInt("gesture_registered", 1);
                                                                editor.apply();
                                                                popupWindow.dismiss();
                                                                add_new_gesture.setText("Delete Gesture");
                                                                Toast.makeText(HomeActivity.this, "You are good to go!", Toast.LENGTH_SHORT).show();
                                                            }
                                                            gesture_count++;
                                                        }
                                                    }
                                                } else
                                                    Toast.makeText(HomeActivity.this, "Gesture already added!", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                        builder.show();
                                    }
                                });
                                popupWindow = new PopupWindow(gestureOverlayView,400,400);
                                LinearLayout parent_gesture = (LinearLayout)findViewById(R.id.parent_buttons_home);
                                popupWindow.showAtLocation(parent_gesture, Gravity.CENTER,0,0);
                            }
                        }
                        else
                            Toast.makeText(HomeActivity.this,"Password Mismatch!",Toast.LENGTH_SHORT).show();
                    }
                });
                password_dialog.show();
            }
        });
        Button start_new_quiz = (Button)findViewById(R.id.new_quiz_button);
        start_new_quiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent new_quiz = new Intent(HomeActivity.this,NewQuiz.class);
                startActivity(new_quiz);
                HomeActivity.this.finish();
            }
        });
        Button score_card = (Button)findViewById(R.id.view_score);
        score_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent score_card_act = new Intent(HomeActivity.this,ScoreActivity.class);
                score_card_act.putExtra("Marks",""+sp.getInt("Marks",0));
                score_card_act.putExtra("Correct",""+sp.getInt("Correct",0));
                score_card_act.putExtra("Wrong",""+sp.getInt("Wrong",0));
                score_card_act.putExtra("Hint",""+sp.getInt("Hints",0));
                score_card_act.putExtra("Time",""+sp.getInt("Best_time",-1));
                score_card_act.putExtra("From_WHERE",2);
                startActivity(score_card_act);
                HomeActivity.this.finish();
            }
        });
        Button rules_button = (Button)findViewById(R.id.rules_again);
        rules_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent rule_act = new Intent(HomeActivity.this,RuleActivity.class);
                startActivity(rule_act);
                HomeActivity.this.finish();
            }
        });
        Button clip_button = (Button)findViewById(R.id.show_clip);
        clip_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent clip_intent = new Intent(HomeActivity.this,ClipActivity.class);
                startActivity(clip_intent);
            }
        });
        Button to_website = (Button)findViewById(R.id.to_website_button);
        to_website.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent website_intent = new Intent(HomeActivity.this,WebActivity.class);
                startActivity(website_intent);
            }
        });
    }

    public void logout(){
        SharedPreferences sp = getSharedPreferences("Hallows",MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove("logged_in");
        editor.apply();
        Intent login_again = new Intent(HomeActivity.this, LoginActivity.class);
        startActivity(login_again);
        HomeActivity.this.finish();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Log Out");
        builder.setMessage("Are you sure you want to log out?");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("Log Out",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                logout();
            }
        });
        builder.show();
    }

    @Override
    protected void onDestroy() {
        ClipService.service_running = false;
        stopService(clipService);
        super.onDestroy();
    }
}