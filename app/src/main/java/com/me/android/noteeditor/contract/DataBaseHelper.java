package com.me.android.noteeditor.contract;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Note_Editor.db";
    static final String TABLE_NOTES = "notes";
    static final String KEY_ID = "id";
    static final String KEY_TITLE = "Title";
    static final String KEY_CONTENT = "content";
    static final String KEY_TIME = "time";
    static final String KEY_FOLDER = "folder";
    private static final String CREATE_NOTES_TABLE = "CREATE TABLE "
            + TABLE_NOTES + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_TITLE + " TEXT NOT NULL,"
            + KEY_CONTENT + " TEXT,"
            + KEY_FOLDER + " TEXT,"
            + KEY_TIME+" TEXT NOT NULL );";

    DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_NOTES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        onCreate(sqLiteDatabase);
    }
}
