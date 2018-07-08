package com.example.parjanya.thedeathlyhallows;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class RuleActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rule);
        Button new_quiz_button = (Button)findViewById(R.id.start_quiz_from_rules);
        new_quiz_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent start_quiz = new Intent(RuleActivity.this,NewQuiz.class);
                startActivity(start_quiz);
                RuleActivity.this.finish();
            }
        });
        Button home_button = (Button)findViewById(R.id.home_from_rules);
        home_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent home_act = new Intent(RuleActivity.this,HomeActivity.class);
                startActivity(home_act);
                RuleActivity.this.finish();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_rule, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
