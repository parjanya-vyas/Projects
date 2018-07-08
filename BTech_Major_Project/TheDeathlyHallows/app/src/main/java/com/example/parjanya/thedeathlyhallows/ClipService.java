package com.example.parjanya.thedeathlyhallows;

import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.sql.SQLException;

public class ClipService extends Service {

    private ClipsDataSource dataSource;
    public static int clip_cnt;
    private String prev_clip;
    public static boolean service_running;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        final ClipboardManager clipboardManager = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
        clip_cnt = 0;
        prev_clip = "";
        service_running = true;

        dataSource = new ClipsDataSource(ClipService.this);
        try{
            dataSource.open();
            dataSource.deleteAllClips();
            dataSource.close();
        }
        catch (SQLException e){
            Log.i("SQLException","Error with opening");
        }
        final Handler clip_hand = new Handler();
        Runnable clip_run = new Runnable() {
            @Override
            public void run() {
                ClipData.Item data = clipboardManager.getPrimaryClip().getItemAt(0);
                String clipDataCaught = data.getText().toString();
                if (!(clipDataCaught.equals(prev_clip))&&!(clipDataCaught.equals(""))) {
                    try {
                        dataSource.open();
                        dataSource.createClip(clip_cnt++,clipDataCaught);
                        prev_clip=clipDataCaught;
                    } catch (SQLException e) {
                        Log.i("SQLException", "SQL Exception Occurred!");
                    }
                }
                if(service_running)
                    clip_hand.postDelayed(this,2000);
            }
        };
        clip_hand.post(clip_run);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
