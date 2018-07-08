package com.example.parjanya.thedeathlyhallows;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;

/**
 * Created by Parjanya on 4/5/2015.
 */
public class ClipsDataSource {
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = {MySQLiteHelper.COLUMN_CLIP_ID, MySQLiteHelper.COLUMN_CLIP_DATA};

    public ClipsDataSource(Context context){
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException{
        database = dbHelper.getWritableDatabase();
    }

    public void close(){
        dbHelper.close();
    }

    public void createClip(long id, String data){
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_CLIP_ID,id);
        values.put(MySQLiteHelper.COLUMN_CLIP_DATA,data);
        database.insert(MySQLiteHelper.TABLE_NAME_2,null,values);
    }

    public Clip getClip(long cid){
        Clip clip;
        Cursor cursor = database.query(MySQLiteHelper.TABLE_NAME_2,allColumns,MySQLiteHelper.COLUMN_CLIP_ID+" = ?",new String[]{Long.toString(cid)},null,null,null);
        cursor.moveToFirst();
        clip = cursorToClip(cursor);
        cursor.close();
        return clip;
    }

    public void deleteAllClips(){
        database.delete(MySQLiteHelper.TABLE_NAME_2,null,null);
    }

    private Clip cursorToClip(Cursor cursor){
        Clip clip = new Clip();
        clip.setCLIP_ID(cursor.getLong(0));
        clip.setCLIP_DATA(cursor.getString(1));
        return clip;
    }
}
