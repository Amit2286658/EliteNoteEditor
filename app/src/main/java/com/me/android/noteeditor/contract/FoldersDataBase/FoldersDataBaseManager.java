package com.me.android.noteeditor.contract.FoldersDataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.me.android.noteeditor.contract.FoldersDataBase.Folder_content_class;
import com.me.android.noteeditor.contract.FoldersDataBase.FoldersDataBaseHelper;

import java.util.ArrayList;
import java.util.List;

public class FoldersDataBaseManager {

    private FoldersDataBaseHelper foldersDataBaseHelper;
    private SQLiteDatabase sqLiteDatabase;
    private Context context;

    public FoldersDataBaseManager(Context context){
        this.context = context;
    }

    public void insertFolderData(String folderName){
        foldersDataBaseHelper = new FoldersDataBaseHelper(context);
        sqLiteDatabase = foldersDataBaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(FoldersDataBaseHelper.KEY_FOLDERNAME, folderName);
        sqLiteDatabase.insert(FoldersDataBaseHelper.KEY_TABLE, null, contentValues);
        sqLiteDatabase.close();
        foldersDataBaseHelper.close();
    }

    public void deleteFolderEntry(int id){
        foldersDataBaseHelper = new FoldersDataBaseHelper(context);
        sqLiteDatabase = foldersDataBaseHelper.getWritableDatabase();
        sqLiteDatabase.delete(FoldersDataBaseHelper.KEY_TABLE
                , FoldersDataBaseHelper.KEY_FOLDERID+" = ?"
                , new String[]{String.valueOf(id)});
        sqLiteDatabase.close();
        foldersDataBaseHelper.close();
    }

    public void updateEntry(int id, String newFolderName){
        foldersDataBaseHelper = new FoldersDataBaseHelper(context);
        sqLiteDatabase = foldersDataBaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(FoldersDataBaseHelper.KEY_FOLDERNAME, newFolderName);
        sqLiteDatabase.update(FoldersDataBaseHelper.KEY_TABLE,
                contentValues,
                FoldersDataBaseHelper.KEY_FOLDERID+" = ?",
                new String[]{String.valueOf(id)});
        sqLiteDatabase.close();
        foldersDataBaseHelper.close();
    }

    public Folder_content_class getSingleData(int id){
        foldersDataBaseHelper = new FoldersDataBaseHelper(context);
        sqLiteDatabase = foldersDataBaseHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery
                ("SELECT * FROM "+FoldersDataBaseHelper.KEY_TABLE+" WHERE "+FoldersDataBaseHelper.KEY_FOLDERID+" = "+String.valueOf(id)
                        , null);
        cursor.moveToFirst();
        Folder_content_class folder_content_class = new Folder_content_class(
                cursor.getString(cursor.getColumnIndex(FoldersDataBaseHelper.KEY_FOLDERNAME))
                , cursor.getInt(cursor.getColumnIndex(FoldersDataBaseHelper.KEY_FOLDERID)));
        cursor.close();
        sqLiteDatabase.close();
        foldersDataBaseHelper.close();
        return folder_content_class;
    }

    public List<Folder_content_class> getAll(){
        ArrayList<Folder_content_class> list = new ArrayList<>();
        foldersDataBaseHelper = new FoldersDataBaseHelper(context);
        sqLiteDatabase = foldersDataBaseHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM "+FoldersDataBaseHelper.KEY_TABLE, null);
        if (cursor.moveToFirst()){
            cursor.moveToFirst();
            do {
                list.add(new Folder_content_class(
                        cursor.getString(cursor.getColumnIndex(FoldersDataBaseHelper.KEY_FOLDERNAME)),
                        cursor.getInt(cursor.getColumnIndex(FoldersDataBaseHelper.KEY_FOLDERID))
                ));
            }while (cursor.moveToNext());
        }
        cursor.close();
        sqLiteDatabase.close();
        foldersDataBaseHelper.close();

        return list;
    }

    public void deleteAll(){
        foldersDataBaseHelper = new FoldersDataBaseHelper(context);
        sqLiteDatabase = foldersDataBaseHelper.getWritableDatabase();
        sqLiteDatabase.execSQL("DELETE FROM "+FoldersDataBaseHelper.KEY_TABLE);
        sqLiteDatabase.close();
        foldersDataBaseHelper.close();
    }

    public void close(){
        try {
            foldersDataBaseHelper.close();
            sqLiteDatabase.close();
        }catch (Exception e){
            //no need to do anything, if the objects can't be shut down, they're probably already dead,
        }
    }

}
