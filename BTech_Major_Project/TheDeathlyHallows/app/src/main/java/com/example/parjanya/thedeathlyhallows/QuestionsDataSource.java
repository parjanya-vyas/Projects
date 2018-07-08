package com.example.parjanya.thedeathlyhallows;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;

/**
 * Created by Parjanya on 2/26/2015.
 */
public class QuestionsDataSource {

    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = {MySQLiteHelper.COLUMN_QID, MySQLiteHelper.COLUMN_QUESTION_TEXT,
    MySQLiteHelper.COLUMN_OPTION_1, MySQLiteHelper.COLUMN_OPTION_2,
    MySQLiteHelper.COLUMN_OPTION_3, MySQLiteHelper.COLUMN_OPTION_4,
    MySQLiteHelper.COLUMN_ANSWER, MySQLiteHelper.COLUMN_HINT};

    public QuestionsDataSource(Context context){
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException{
        database = dbHelper.getWritableDatabase();
    }

    public void close(){
        dbHelper.close();
    }

    public void createQuestion(String text, String op1, String op2, String op3, String op4, String ans, String hint, long id){
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_QID,id);
        values.put(MySQLiteHelper.COLUMN_QUESTION_TEXT,text);
        values.put(MySQLiteHelper.COLUMN_OPTION_1,op1);
        values.put(MySQLiteHelper.COLUMN_OPTION_2,op2);
        values.put(MySQLiteHelper.COLUMN_OPTION_3,op3);
        values.put(MySQLiteHelper.COLUMN_OPTION_4,op4);
        values.put(MySQLiteHelper.COLUMN_ANSWER,ans);
        values.put(MySQLiteHelper.COLUMN_HINT,hint);
        database.insert(MySQLiteHelper.TABLE_NAME,null,values);
    }

    public Question getQuestion(long qid){
        Question question;
        Cursor cursor = database.query(MySQLiteHelper.TABLE_NAME,allColumns,MySQLiteHelper.COLUMN_QID+" = ?",new String[]{Long.toString(qid)},null,null,null);
        cursor.moveToFirst();
        question = cursorToQuestion(cursor);
        cursor.close();
        return question;
    }

    private Question cursorToQuestion(Cursor cursor){
        Question question = new Question();
        question.setQ_ID(cursor.getLong(0));
        question.setQ_TEXT(cursor.getString(1));
        question.setQ_OPTION_1(cursor.getString(2));
        question.setQ_OPTION_2(cursor.getString(3));
        question.setQ_OPTION_3(cursor.getString(4));
        question.setQ_OPTION_4(cursor.getString(5));
        question.setQ_ANSWER(cursor.getString(6));
        question.setQ_HINT(cursor.getString(7));
        return question;
    }
}
