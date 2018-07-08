package com.example.parjanya.thedeathlyhallows;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class ScoreActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);
        final Intent cur_act = getIntent();

        final String mark = cur_act.getStringExtra("Marks");
        String correct = cur_act.getStringExtra("Correct");
        String wrong = cur_act.getStringExtra("Wrong");
        String hint = cur_act.getStringExtra("Hint");
        String time = cur_act.getStringExtra("Time");

        TextView mark_tv = (TextView)findViewById(R.id.score_points);
        TextView correct_tv = (TextView)findViewById(R.id.score_correct);
        TextView wrong_tv = (TextView)findViewById(R.id.score_wrong);
        TextView hint_tv = (TextView)findViewById(R.id.score_hint);
        TextView time_tv = (TextView)findViewById(R.id.score_time);
        TextView title_tv = (TextView)findViewById(R.id.summary_title);

        mark_tv.setText(mark);
        correct_tv.setText(correct);
        wrong_tv.setText(wrong);
        hint_tv.setText(hint);

        if(time.equals(Integer.toString(-1)))
            time_tv.setText("Never played a full Session");
        else
            time_tv.setText(time);

        if(cur_act.getIntExtra("From_WHERE",-1)==2)
            title_tv.setText("Total Summary");
        else
            title_tv.setText("Current Session Summary");

        Button home = (Button)findViewById(R.id.score_home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start_home();
            }
        });

        Button share_button = (Button)findViewById(R.id.score_share);
        share_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ContentResolver contactsContent = getContentResolver();
                Cursor cursor = contactsContent.query(ContactsContract.Contacts.CONTENT_URI,null,null,null,ContactsContract.Contacts.DISPLAY_NAME+" ASC");
                cursor.moveToFirst();
                if(cursor.getCount()>0){
                    final String[] all_contacts = new String[cursor.getCount()];
                    int i=0;
                    while (cursor.moveToNext()){
                        //Log.i("Contacts",cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
                        all_contacts[i] = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        i++;
                    }
                    AlertDialog.Builder contacts_dialog = new AlertDialog.Builder(ScoreActivity.this);
                    contacts_dialog.setTitle("Select Contact");
                    contacts_dialog.setMessage("Choose the contact to share the score via text message");
                    final Spinner contacts_spinner = new Spinner(ScoreActivity.this);
                    ArrayAdapter contacts_adapter = new ArrayAdapter<>(ScoreActivity.this,R.layout.support_simple_spinner_dropdown_item,all_contacts);
                    contacts_spinner.setAdapter(contacts_adapter);
                    contacts_dialog.setView(contacts_spinner);
                    contacts_dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    contacts_dialog.setPositiveButton("Share",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            AlertDialog.Builder sure_sms_send = new AlertDialog.Builder(ScoreActivity.this);
                            sure_sms_send.setTitle("Share Confirm");
                            sure_sms_send.setMessage("You are about to share your score via text message which may result in charges as per the tariff plan of your service provider. Do you wish to continue?");
                            sure_sms_send.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            sure_sms_send.setPositiveButton("Send",new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    SmsManager smsManager = SmsManager.getDefault();
                                    Cursor phone_number = contactsContent.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" = ?",new String[]{contacts_spinner.getSelectedItem().toString()},null);
                                    phone_number.moveToFirst();
                                    String destination_message = phone_number.getString(phone_number.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                    phone_number.close();
                                    String message_body = "I just scored "+mark+" points in the ultimate Harry Potter quiz named The Deathly Hallows! What's your score?";
                                    smsManager.sendTextMessage(destination_message,null,message_body,null,null);
                                }
                            });
                            sure_sms_send.show();
                        }
                    });
                    contacts_dialog.show();
                }
                else
                    Toast.makeText(ScoreActivity.this,"No contacts found",Toast.LENGTH_SHORT).show();
                cursor.close();
            }
        });
    }

    @Override
    public void onBackPressed() {
        start_home();
    }

    public void start_home(){
        Intent new_int = new Intent(ScoreActivity.this,HomeActivity.class);
        startActivity(new_int);
        ScoreActivity.this.finish();
    }
}
