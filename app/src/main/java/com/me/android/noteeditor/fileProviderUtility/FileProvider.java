package com.me.android.noteeditor.fileProviderUtility;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileProvider {

    private static final String CACHEFILENAME = "cacheFile.txt";

    private Context context = null;
    public void initiate(Context context){
        this.context = context;
    }

    public String readExternalTextFileContent(Uri uri) throws IOException {

        if(context == null)
            throw new NullPointerException("The FileProvider class is not initialized, please initialize it first");

        StringBuilder stringBuilder = new StringBuilder();

        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(context.getContentResolver().openInputStream(uri)));

        String line = "";
        while ((line = bufferedReader.readLine()) != null){
            stringBuilder.append(line);
            stringBuilder.append("\n");
        }

        bufferedReader.close();

        return stringBuilder.toString();
    }
    public String readExternalTextFileDisplayName(Uri uri){

        if (context == null)
            throw new NullPointerException("FileProvider class is not initialized, please initialize it first");

        String fileName = "";
        String displayName;

        File externalFile = new File(uri.toString());
        if (uri.toString().startsWith("content://")){
            Cursor cursor = context.getContentResolver().query(
                    uri
                    , null
                    , null
                    , null
                    , null);
            if (cursor != null) {
                cursor.moveToFirst();
                displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                fileName += displayName;
                cursor.close();
            }
        }else if (uri.toString().startsWith("file://")){
            displayName = externalFile.getName();
            fileName += displayName.contains("%20") ? displayName.replaceAll("%20", " ")
                    : displayName;
        }

        return fileName;
    }

    @Deprecated
    public Date getLastModifiedDate(Uri uri){
        File file = new File(uri.toString());
        return new Date(file.lastModified());
    }

    // TODO: this methods are in beta ands should not be used.
    public String getLastModifiedDate(Uri uri, SimpleDateFormat sdf) throws IOException {
        /*File file = new File(uri.getScheme());
        Log.e("external_file_name", String.valueOf(file.exists())+String.valueOf(uri.getPathSegments()));
        if (!file.exists()) throw new  RuntimeException("file not found");
        long modifiedDateLongValue = file.lastModified();
        Date modifiedDate = new Date();
        modifiedDate.setTime(modifiedDateLongValue);
        return sdf.format(modifiedDate);*/
        File file = createTempFile(uri);
        long modifiedDateInLong = file.lastModified();
        Date lastmodifiedDate = new Date();
        lastmodifiedDate.setTime(modifiedDateInLong);
        return sdf.format(lastmodifiedDate);
    }

    private File createTempFile(Uri uri) throws FileNotFoundException, IOException, NullPointerException{
        if (context == null) throw new RuntimeException("FileProvider is not initialized");
        File file = new File(context.getCacheDir(), CACHEFILENAME);
        try (InputStream ip = context.getContentResolver().openInputStream(uri);
             OutputStream outputStream = new FileOutputStream(file)) {
            byte[] buffer = new byte[4 * 1024];
            int read;
            while ((read = ip.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            outputStream.flush();
        }
        return file;
    }

    public String readAssetFileContent(String path) throws IOException {
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = assetManager.open(path);
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        StringBuilder stringBuilder = new StringBuilder();
        String line = "";
        while ((line = bufferedReader.readLine()) != null){
            stringBuilder.append(line);
            stringBuilder.append("\n");
        }
        bufferedReader.close();
        inputStreamReader.close();
        inputStream.close();

        return stringBuilder.toString();
    }
}
