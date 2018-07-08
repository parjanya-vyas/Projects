package com.example.parjanya.thedeathlyhallows;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;


public class NewQuiz extends Activity {

    private QuestionsDataSource dataSource;
    private int question_count=0;
    private boolean hint_used = false;
    private Handler handler, time_handler;
    private boolean[] occurrence;
    private int initial_score;
    private int initial_right;
    private int initial_wrong;
    private int initial_hint;
    private int time_var;
    private boolean time_running;
    private boolean paused;
    private Runnable question_runnable;
    private Runnable time_runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_quiz);

        final SharedPreferences sp = getSharedPreferences("Hallows", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sp.edit();
        question_count=0;
        dataSource = new QuestionsDataSource(this);
        initial_score = sp.getInt("Marks",0);
        initial_right = sp.getInt("Correct",0);
        initial_wrong = sp.getInt("Wrong",0);
        initial_hint = sp.getInt("Hints",0);
        time_var = 0;
        time_running = true;
        paused = false;
        occurrence = new boolean[(int)MySQLiteHelper.MAX_QUESTION];
        for(int i=0;i<MySQLiteHelper.MAX_QUESTION;i++)
            occurrence[i]=false;

        final TextView question_number_tv = (TextView)findViewById(R.id.question_number);
        final TextView question_text_tv = (TextView)findViewById(R.id.question_text);
        final TextView hint_tv = (TextView)findViewById(R.id.hint_text);
        final TextView time_tv = (TextView)findViewById(R.id.time);

        final Button opt1_button = (Button)findViewById(R.id.button_option_1);
        final Button opt2_button = (Button)findViewById(R.id.button_option_2);
        final Button opt3_button = (Button)findViewById(R.id.button_option_3);
        final Button opt4_button = (Button)findViewById(R.id.button_option_4);
        final Button hint_button = (Button)findViewById(R.id.display_hint);
        final Button pause_timer_button = (Button)findViewById(R.id.pause_timer);

        handler = new Handler();
        time_handler = new Handler();

        question_runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    dataSource.open();
                    if(!time_running)
                        start_timer();
                    long new_q_id = (long)(Math.random()*(double)MySQLiteHelper.MAX_QUESTION);
                    while(occurrence[(int)new_q_id])
                        new_q_id = (long)(Math.random()*(double)MySQLiteHelper.MAX_QUESTION);
                    occurrence[(int)new_q_id]=true;
                    Question cur_que = dataSource.getQuestion(new_q_id);

                    String text = cur_que.getQ_TEXT();
                    String opt1 = cur_que.getQ_OPTION_1();
                    String opt2 = cur_que.getQ_OPTION_2();
                    String opt3 = cur_que.getQ_OPTION_3();
                    String opt4 = cur_que.getQ_OPTION_4();
                    final String ans = cur_que.getQ_ANSWER();
                    String hint = cur_que.getQ_HINT();
                    hint_used = false;

                    question_number_tv.setText(++question_count+") ");
                    question_text_tv.setText(text);
                    opt1_button.setText(opt1);
                    opt2_button.setText(opt2);
                    opt3_button.setText(opt3);
                    opt4_button.setText(opt4);
                    hint_tv.setText(hint);
                    hint_tv.setVisibility(View.INVISIBLE);
                    hint_button.setClickable(true);

                    opt1_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int cur_marks = sp.getInt("Marks",0);
                            if (ans.equals("A")){
                                makeToast("Correct Answer!");
                                if(paused)
                                    stop_timer();
                                if(hint_used)
                                    editor.putInt("Marks",cur_marks+1);
                                else
                                    editor.putInt("Marks",cur_marks+2);
                                int cur_correct = sp.getInt("Correct",0);
                                editor.putInt("Correct",cur_correct+1);
                                if((question_count<5)&&time_running)
                                    handler.post(question_runnable);
                                else if (question_count>=5) {
                                    editor.apply();
                                    finish_this();
                                }
                            }
                            else {
                                makeToast("Try again!");
                                int cur_wrong = sp.getInt("Wrong",0);
                                editor.putInt("Wrong",cur_wrong+1);
                                editor.putInt("Marks",cur_marks-1);
                            }
                            editor.apply();
                        }
                    });
                    opt2_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int cur_marks = sp.getInt("Marks",0);
                            if (ans.equals("B")){
                                makeToast("Correct Answer!");
                                if(paused)
                                    stop_timer();
                                if(hint_used)
                                    editor.putInt("Marks",cur_marks+1);
                                else
                                    editor.putInt("Marks",cur_marks+2);
                                int cur_correct = sp.getInt("Correct",0);
                                editor.putInt("Correct",cur_correct+1);
                                if((question_count<5)&&time_running)
                                    handler.post(question_runnable);
                                else if (question_count>=5) {
                                    editor.apply();
                                    finish_this();
                                }
                            }
                            else {
                                makeToast("Try again!");
                                int cur_wrong = sp.getInt("Wrong",0);
                                editor.putInt("Wrong",cur_wrong+1);
                                editor.putInt("Marks",cur_marks-1);
                            }
                            editor.apply();
                        }
                    });
                    opt3_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int cur_marks = sp.getInt("Marks",0);
                            if (ans.equals("C")){
                                makeToast("Correct Answer!");
                                if(paused)
                                    stop_timer();
                                if(hint_used)
                                    editor.putInt("Marks",cur_marks+1);
                                else
                                    editor.putInt("Marks",cur_marks+2);
                                int cur_correct = sp.getInt("Correct",0);
                                editor.putInt("Correct",cur_correct+1);
                                if((question_count<5)&&time_running)
                                    handler.post(question_runnable);
                                else if (question_count>=5) {
                                    editor.apply();
                                    finish_this();
                                }
                            }
                            else {
                                makeToast("Try again!");
                                int cur_wrong = sp.getInt("Wrong",0);
                                editor.putInt("Wrong",cur_wrong+1);
                                editor.putInt("Marks",cur_marks-1);
                            }
                            editor.apply();
                        }
                    });
                    opt4_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int cur_marks = sp.getInt("Marks",0);
                            if (ans.equals("D")){
                                makeToast("Correct Answer!");
                                if(paused)
                                    stop_timer();
                                if(hint_used)
                                    editor.putInt("Marks",cur_marks+1);
                                else
                                    editor.putInt("Marks",cur_marks+2);
                                int cur_correct = sp.getInt("Correct",0);
                                editor.putInt("Correct",cur_correct+1);
                                if((question_count<5)&&time_running)
                                    handler.post(question_runnable);
                                else if (question_count>=5) {
                                    editor.apply();
                                    finish_this();
                                }
                            }
                            else {
                                makeToast("Try again!");
                                int cur_wrong = sp.getInt("Wrong",0);
                                editor.putInt("Wrong",cur_wrong+1);
                                editor.putInt("Marks",cur_marks-1);
                            }
                            editor.apply();
                        }
                    });
                    hint_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            editor.putInt("Hints",sp.getInt("Hints",0)+1);
                            editor.apply();
                            hint_tv.setVisibility(View.VISIBLE);
                            hint_used=true;
                            hint_button.setClickable(false);
                        }
                    });
                }catch (SQLException e) {
                    Log.i("SQL Exception", "Error in database!");
                }
            }
        };
        handler.post(question_runnable);

        time_runnable = new Runnable() {
            @Override
            public void run() {
                time_tv.setText("Time: "+time_var+" sec");
                time_var++;
                if(time_running)
                    time_handler.postDelayed(this,1000);
            }
        };
        time_handler.post(time_runnable);

        pause_timer_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!paused) {
                    paused = true;
                    pause_timer_button.setText("Resume");
                }
                else {
                    paused = false;
                    pause_timer_button.setText("Pause");
                    if(!time_running)
                        handler.post(question_runnable);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Exit");
        builder.setMessage("Are you sure you want to terminate the quiz?");
        builder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("Quit",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish_this();
            }
        });
        builder.show();
    }

    public void makeToast(String str){
        Toast.makeText(this,str,Toast.LENGTH_SHORT).show();
    }

    public void finish_this(){
        Intent score = new Intent(this,ScoreActivity.class);
        SharedPreferences sp = getSharedPreferences("Hallows",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        score.putExtra("Marks",""+(sp.getInt("Marks",0)-initial_score));
        score.putExtra("Wrong",""+(sp.getInt("Wrong",0)-initial_wrong));
        score.putExtra("Correct",""+(sp.getInt("Correct",0)-initial_right));
        score.putExtra("Hint",""+(sp.getInt("Hints",0)-initial_hint));
        score.putExtra("Time",""+time_var);
        score.putExtra("From_WHERE",1);
        if(question_count>=5) {
            int yet_best_time = sp.getInt("Best_time",-1);
            if (yet_best_time==-1){
                editor.putInt("Best_time",time_var);
                editor.apply();
            }
            else {
                if(yet_best_time>time_var){
                    editor.putInt("Best_time",time_var);
                    editor.apply();
                }
            }
        }
        startActivity(score);
        this.finish();
    }

    public void stop_timer(){
        time_running=false;
        time_handler.removeCallbacksAndMessages(null);
    }

    public void start_timer(){
        time_running=true;
        time_handler.post(time_runnable);
    }
}
