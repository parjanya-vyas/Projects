package com.example.parjanya.thedeathlyhallows;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;


public class ClipActivity extends Activity {

    private ClipsDataSource clipsDataSource;
    private long clip_id;
    private String clip_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clip);
        LinearLayout parent_linear = (LinearLayout)findViewById(R.id.clip_parent);
        clipsDataSource = new ClipsDataSource(ClipActivity.this);
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        try {
            clipsDataSource.open();
            for (int i=0;i<ClipService.clip_cnt;i++){
                Clip cur_clip = clipsDataSource.getClip(i);
                clip_id = cur_clip.getCLIP_ID();
                clip_data = cur_clip.getCLIP_DATA();
                View new_row = inflater.inflate(R.layout.clip_layout, null);
                parent_linear.addView(new_row);
                final TextView id = (TextView)new_row.findViewById(R.id.clip_id);
                id.setText(Long.toString(clip_id+1));
                final TextView data = (TextView)new_row.findViewById(R.id.clip_data);
                data.setText(clip_data);
                id.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        showWarningDialog(id);
                        return true;
                    }
                });
                data.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        showWarningDialog(data);
                        return true;
                    }
                });
            }
            clipsDataSource.close();
            //Toast.makeText(ClipActivity.this,ClipService.clip_cnt+" "+Long.toString(clip_id)+ " "+clip_data,Toast.LENGTH_SHORT).show();
        }
        catch (SQLException e){
            Log.i("SQLException","SQL Exception occurred in Clip Activity");
        }
    }

    private void showWarningDialog(final TextView textView){
        AlertDialog.Builder warningDialog = new AlertDialog.Builder(ClipActivity.this);
        warningDialog.setTitle("Copy Warning");
        warningDialog.setMessage("If you copy this text then it may be visible to other applications and they may capture, upload or in any way use it. If you wish to continue, please press OK and select the text again");
        warningDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                textView.setTextIsSelectable(true);
                textView.setOnLongClickListener(null);
                dialog.dismiss();
            }
        });
        warningDialog.setNegativeButton("No! Save Me!",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                textView.setTextIsSelectable(false);
                dialog.cancel();
            }
        });
        warningDialog.show();
    }
}
