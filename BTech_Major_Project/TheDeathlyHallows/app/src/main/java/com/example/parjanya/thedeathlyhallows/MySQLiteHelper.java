package com.example.parjanya.thedeathlyhallows;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Parjanya on 2/26/2015.
 */
public class MySQLiteHelper extends SQLiteOpenHelper {
    public static final String TABLE_NAME = "questions";
    public static final String COLUMN_QID = "_id";
    public static final String COLUMN_QUESTION_TEXT = "question_text";
    public static final String COLUMN_OPTION_1 = "option_1";
    public static final String COLUMN_OPTION_2 = "option_2";
    public static final String COLUMN_OPTION_3 = "option_3";
    public static final String COLUMN_OPTION_4 = "option_4";
    public static final String COLUMN_ANSWER = "answer";
    public static final String COLUMN_HINT = "hint";

    public static final String TABLE_NAME_2 = "clipboard";
    public static final String COLUMN_CLIP_ID = "_id";
    public static final String COLUMN_CLIP_DATA = "clip_data";

    public static final long MAX_QUESTION = 5;

    private static final String DATABASE_NAME = "questions.db";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_CREATE = "create table "+TABLE_NAME+"("+COLUMN_QID+" integer primary key, "
            +COLUMN_QUESTION_TEXT+" text not null, "
            +COLUMN_OPTION_1+" text not null, "
            +COLUMN_OPTION_2+" text not null, "
            +COLUMN_OPTION_3+" text not null, "
            +COLUMN_OPTION_4+" text not null, "
            +COLUMN_ANSWER+" text not null, "
            +COLUMN_HINT+" text not null);";

    private static final String DATABASE_CREATE_2 = "create table "+TABLE_NAME_2+"("+COLUMN_CLIP_ID+" integer primary key, "
            +COLUMN_CLIP_DATA+" text);";

    public MySQLiteHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
        db.execSQL(DATABASE_CREATE_2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySQLiteHelper.class.getName(),"Upgrading database from version "+oldVersion+" to version "+newVersion+", which will destroy all data");
        db.execSQL("DROP TABLE IF EXIST "+TABLE_NAME);
        db.execSQL("DROP TABLE IF EXIST "+TABLE_NAME_2);
        onCreate(db);
    }
}
