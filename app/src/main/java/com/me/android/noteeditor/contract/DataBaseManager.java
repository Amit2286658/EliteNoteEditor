package com.me.android.noteeditor.contract;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class DataBaseManager {

    private DataBaseHelper DBHelper;
    private SQLiteDatabase SQLDatabase;
    private Context context;

    public DataBaseManager(Context context){
        this.context = context;
    }

    public void InsertData(String title, String content, String time, String folderName){
        DBHelper = new DataBaseHelper(context);
        SQLDatabase = DBHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DataBaseHelper.KEY_TITLE, title);
        contentValues.put(DataBaseHelper.KEY_CONTENT, content);
        contentValues.put(DataBaseHelper.KEY_TIME, time);
        contentValues.put(DataBaseHelper.KEY_FOLDER, folderName);
        SQLDatabase.insert(DataBaseHelper.TABLE_NOTES, null, contentValues);
        SQLDatabase.close();
        DBHelper.close();
    }
    public content_class getSingleData(int id){
        DBHelper = new DataBaseHelper(context);
        SQLDatabase = DBHelper.getReadableDatabase();
        Cursor cursor = SQLDatabase.rawQuery
                ("SELECT * FROM "+DataBaseHelper.TABLE_NOTES+" WHERE "+DataBaseHelper.KEY_ID+" = "+String.valueOf(id)
                        , null);
        cursor.moveToFirst();
        content_class content_class = new content_class(
                cursor.getInt(cursor.getColumnIndex(DataBaseHelper.KEY_ID))
                , cursor.getString(cursor.getColumnIndex(DataBaseHelper.KEY_TITLE))
                , cursor.getString(cursor.getColumnIndex(DataBaseHelper.KEY_CONTENT))
                , cursor.getString(cursor.getColumnIndex(DataBaseHelper.KEY_TIME))
                , cursor.getString(cursor.getColumnIndex(DataBaseHelper.KEY_FOLDER))
        );
        cursor.close();
        SQLDatabase.close();
        DBHelper.close();
        return content_class;
    }
    public void deleteEntry(int id){
        DBHelper = new DataBaseHelper(context);
        SQLDatabase = DBHelper.getWritableDatabase();
        SQLDatabase.delete(DataBaseHelper.TABLE_NOTES
                , DataBaseHelper.KEY_ID+" = ?"
                , new String[]{String.valueOf(id)});
        SQLDatabase.close();
        DBHelper.close();
    }
    public void updateEntry(int id, String newTitle, String newContent, String newTime, String folderName){
        DBHelper = new DataBaseHelper(context);
        SQLDatabase = DBHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DataBaseHelper.KEY_TITLE, newTitle);
        contentValues.put(DataBaseHelper.KEY_CONTENT, newContent);
        contentValues.put(DataBaseHelper.KEY_TIME, newTime);
        contentValues.put(DataBaseHelper.KEY_FOLDER, folderName);
        SQLDatabase.update(DataBaseHelper.TABLE_NOTES
                , contentValues
                , DataBaseHelper.KEY_ID+" = ?"
                , new String[]{String.valueOf(id)});

        SQLDatabase.close();
        DBHelper.close();
    }
    public ArrayList<content_class> getAll(){
        ArrayList<content_class> arrayList = new ArrayList<>();
        DBHelper = new DataBaseHelper(context);
        SQLDatabase = DBHelper.getReadableDatabase();
        Cursor cursor = SQLDatabase.rawQuery("SELECT * FROM "+DataBaseHelper.TABLE_NOTES, null);
        if (cursor.moveToFirst()){
            cursor.moveToFirst();
            do {
                arrayList.add(new content_class(cursor.getInt(cursor.getColumnIndex(DataBaseHelper.KEY_ID)),
                        cursor.getString(cursor.getColumnIndex(DataBaseHelper.KEY_TITLE)),
                        cursor.getString(cursor.getColumnIndex(DataBaseHelper.KEY_TIME)),
                        cursor.getString(cursor.getColumnIndex(DataBaseHelper.KEY_FOLDER))));
            }while (cursor.moveToNext());
        }
        cursor.close();
        SQLDatabase.close();
        DBHelper.close();
        return arrayList;
    }
    public void deleteAll(){
        DBHelper = new DataBaseHelper(context);
        SQLDatabase = DBHelper.getWritableDatabase();
        SQLDatabase.execSQL("DELETE FROM "+DataBaseHelper.TABLE_NOTES);
        SQLDatabase.close();
        DBHelper.close();
    }
}
