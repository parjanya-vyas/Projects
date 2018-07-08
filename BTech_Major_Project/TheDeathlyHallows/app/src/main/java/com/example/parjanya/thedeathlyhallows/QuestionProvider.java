package com.example.parjanya.thedeathlyhallows;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.util.Arrays;
import java.util.HashSet;

public class QuestionProvider extends ContentProvider {

    private MySQLiteHelper database;

    private static final int QUESTIONS = 10;
    private static final int QUESTION_ID = 20;

    private static final String AUTHORITY = "com.example.parjanya.thedeathlyhallows.questionprovider";

    private static final String BASE_PATH = "questions";
    public static final Uri CONTENT_URI = Uri.parse("content://"+AUTHORITY+"/"+BASE_PATH);
    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE+"/questions";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE+"/question";

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sURIMatcher.addURI(AUTHORITY, BASE_PATH, QUESTIONS);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", QUESTION_ID);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsDeleted = 0;
        switch (uriType) {
            case QUESTIONS:
                rowsDeleted = sqlDB.delete(MySQLiteHelper.TABLE_NAME, selection,
                        selectionArgs);
                break;
            case QUESTION_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(MySQLiteHelper.TABLE_NAME,
                            MySQLiteHelper.COLUMN_QID + "=" + id,
                            null);
                } else {
                    rowsDeleted = sqlDB.delete(MySQLiteHelper.TABLE_NAME,
                            MySQLiteHelper.COLUMN_QID + "=" + id
                                    + " and " + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        long id = 0;
        switch (uriType) {
            case QUESTIONS:
                id = sqlDB.insert(MySQLiteHelper.TABLE_NAME, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public boolean onCreate() {
        database = new MySQLiteHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        checkColumns(projection);
        queryBuilder.setTables(MySQLiteHelper.TABLE_NAME);
        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case QUESTIONS:
                break;
            case QUESTION_ID:
                queryBuilder.appendWhere(MySQLiteHelper.COLUMN_QID + "="
                        + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = database.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection,
                selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsUpdated = 0;
        switch (uriType) {
            case QUESTIONS:
                rowsUpdated = sqlDB.update(MySQLiteHelper.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            case QUESTION_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(MySQLiteHelper.TABLE_NAME,
                            values,
                            MySQLiteHelper.COLUMN_QID + "=" + id,
                            null);
                } else {
                    rowsUpdated = sqlDB.update(MySQLiteHelper.TABLE_NAME,
                            values,
                            MySQLiteHelper.COLUMN_QID + "=" + id
                                    + " and "
                                    + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

    private void checkColumns(String[] projection) {
        String[] available = { MySQLiteHelper.COLUMN_QID,MySQLiteHelper.COLUMN_QUESTION_TEXT,
                MySQLiteHelper.COLUMN_OPTION_1, MySQLiteHelper.COLUMN_OPTION_2,
                MySQLiteHelper.COLUMN_OPTION_3, MySQLiteHelper.COLUMN_OPTION_4,
                MySQLiteHelper.COLUMN_ANSWER, MySQLiteHelper.COLUMN_HINT};
        if (projection != null) {
            HashSet<String> requestedColumns = new HashSet<>(Arrays.asList(projection));
            HashSet<String> availableColumns = new HashSet<>(Arrays.asList(available));
            if (!availableColumns.containsAll(requestedColumns)) {
                throw new IllegalArgumentException("Unknown columns in projection");
            }
        }
    }
}
