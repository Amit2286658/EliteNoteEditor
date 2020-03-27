package com.me.android.noteeditor.contract.searchQueryDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by Amit on 10/20/2019.
 */

public class searchQueryDatabaseManager {

    private Context context;
    private SQLiteDatabase queryDatabase;
    private searchQueryDatabaseHelper queryDBHelper;

    public searchQueryDatabaseManager(Context context){
        this.context = context;
    }

    public void insertQueryEntry(String query_name, int associated_id, int type, int isHistory){
        queryDBHelper = new searchQueryDatabaseHelper(context);
        queryDatabase = queryDBHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(searchQueryDatabaseHelper.KEY_SEARCH_NAME, query_name);
        contentValues.put(searchQueryDatabaseHelper.KEY_ASSOCIATEDID, associated_id);
        contentValues.put(searchQueryDatabaseHelper.KEY_QUERY_TYPE, type);
        contentValues.put(searchQueryDatabaseHelper.KEY_QUERY_IS_HISTORY, isHistory);
        queryDatabase.insert(searchQueryDatabaseHelper.KEY_TABLE, null, contentValues);
        queryDatabase.close();
        queryDBHelper.close();
    }

    public void deleteQueryEntry(int id){
        queryDBHelper = new searchQueryDatabaseHelper(context);
        queryDatabase = queryDBHelper.getWritableDatabase();
        queryDatabase.delete(searchQueryDatabaseHelper.KEY_TABLE
                , searchQueryDatabaseHelper.KEY_SEARCHID+" = ?"
                , new String[]{String.valueOf(id)});
        queryDatabase.close();
        queryDBHelper.close();
    }

    public void updateQueryEntry(int id, String newQueryName, int isHistory){
        queryDBHelper = new searchQueryDatabaseHelper(context);
        queryDatabase = queryDBHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(searchQueryDatabaseHelper.KEY_SEARCH_NAME, newQueryName);
        contentValues.put(searchQueryDatabaseHelper.KEY_QUERY_IS_HISTORY, isHistory);
        queryDatabase.update(searchQueryDatabaseHelper.KEY_TABLE,
                contentValues,
                searchQueryDatabaseHelper.KEY_SEARCHID + " = ?",
                new String[]{String.valueOf(id)});
        queryDatabase.close();
        queryDBHelper.close();
    }

    public suggestion_model getQueryEntry(int id){
        queryDBHelper = new searchQueryDatabaseHelper(context);
        queryDatabase = queryDBHelper.getReadableDatabase();
        Cursor cursor = queryDatabase.rawQuery
                ("SELECT * FROM "+searchQueryDatabaseHelper.KEY_TABLE+" WHERE "+searchQueryDatabaseHelper.KEY_SEARCHID+" = "+String.valueOf(id)
                        , null);
        cursor.moveToFirst();
        suggestion_model suggestion_model = new suggestion_model(
                cursor.getInt(cursor.getColumnIndex(searchQueryDatabaseHelper.KEY_SEARCHID))
                , cursor.getString(cursor.getColumnIndex(searchQueryDatabaseHelper.KEY_SEARCH_NAME))
                , cursor.getInt(cursor.getColumnIndex(searchQueryDatabaseHelper.KEY_ASSOCIATEDID))
                , cursor.getInt(cursor.getColumnIndex(searchQueryDatabaseHelper.KEY_QUERY_TYPE))
                , cursor.getInt(cursor.getColumnIndex(searchQueryDatabaseHelper.KEY_QUERY_IS_HISTORY)));
        cursor.close();
        queryDatabase.close();
        queryDBHelper.close();
        return suggestion_model;
    }

    public ArrayList<suggestion_model> getAll(){
        ArrayList<suggestion_model> list = new ArrayList<>();
        queryDBHelper = new searchQueryDatabaseHelper(context);
        queryDatabase = queryDBHelper.getReadableDatabase();
        Cursor cursor = queryDatabase.rawQuery("SELECT * FROM "+searchQueryDatabaseHelper.KEY_TABLE, null);
        if (cursor.moveToFirst()){
            cursor.moveToFirst();
            do {
                list.add(new suggestion_model(
                        cursor.getInt(cursor.getColumnIndex(searchQueryDatabaseHelper.KEY_SEARCHID)),
                        cursor.getString(cursor.getColumnIndex(searchQueryDatabaseHelper.KEY_SEARCH_NAME)),
                        cursor.getInt(cursor.getColumnIndex(searchQueryDatabaseHelper.KEY_ASSOCIATEDID)),
                        cursor.getInt(cursor.getColumnIndex(searchQueryDatabaseHelper.KEY_QUERY_TYPE)),
                        cursor.getInt(cursor.getColumnIndex(searchQueryDatabaseHelper.KEY_QUERY_IS_HISTORY)))
                );
            }while (cursor.moveToNext());
        }
        cursor.close();
        queryDatabase.close();
        queryDBHelper.close();

        return list;
    }

    public void deleteAll(){
        queryDBHelper = new searchQueryDatabaseHelper(context);
        queryDatabase = queryDBHelper.getWritableDatabase();
        queryDatabase.execSQL("DELETE FROM "+searchQueryDatabaseHelper.KEY_TABLE);
        queryDatabase.close();
        queryDBHelper.close();
    }

}
