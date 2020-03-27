package com.me.android.noteeditor.contract.searchQueryDatabase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Amit on 10/20/2019.
 */

public class searchQueryDatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABSE_NAME = "search_query.db";
    static final String KEY_TABLE = "search_history";
    static final String KEY_SEARCH_NAME = "search_name";
    static final String KEY_SEARCHID = "id";
    static final String KEY_ASSOCIATEDID = "associted_id";
    static final String KEY_QUERY_TYPE = "query_type";
    static final String KEY_QUERY_IS_HISTORY = "isQueryHistory";
    private static final String CREATE_QUERY_LIST = "CREATE TABLE "
            + KEY_TABLE + "("
            + KEY_SEARCHID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_SEARCH_NAME + " TEXT NOT NULL, "
            + KEY_ASSOCIATEDID + " INTEGER NOT NULL, "
            + KEY_QUERY_TYPE + " INTEGER, "
            + KEY_QUERY_IS_HISTORY + " INTEGER );";

    public searchQueryDatabaseHelper(Context context) {
        super(context, DATABSE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_QUERY_LIST);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + KEY_TABLE);
        onCreate(db);
    }
}
