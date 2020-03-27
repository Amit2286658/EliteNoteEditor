package com.me.android.noteeditor.fontManager;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class fontManager {

    private Context mContext;
    private AssetManager assetManager;

    public static final String ext = ".ttf";
    /*public static final String path = "PetDbHelper2/app/src/main/assets/fonts";*/
    private static ArrayList<String> arrayList = new ArrayList<>();

    public fontManager(Context context){
        this.mContext = context;
    }

    public void initializeFontManager(){
        assetManager = mContext.getAssets();
    }

    public ArrayList<String> listAllFileNames() throws IOException, IllegalStateException {

        if (assetManager == null){
            throw new IllegalStateException("The font manager is not initialized");
        }

        String [] fileList = assetManager.list("fonts");

        if (!arrayList.isEmpty()) arrayList.clear();

        arrayList.addAll(Arrays.asList(fileList));

        //Total file count by hand 179

        /*SearchFiles searchFiles = new SearchFiles(ext);

        if (!file.isDirectory()){
            throw new FileNotFoundException("the path to the font folder is not correct");
        }

        *//*File[] files = file.listFiles();

        if (files != null){
            for (File file1 : files){
                if (file1.isDirectory()){
                    listAllFileNames(file1);
                }
            }
        }*//*
        String[] temp = file.list(searchFiles);

        if (temp.length != 0 ){
            for (String str : temp){
                String[] str1 = str.split(".");
                arrayList.add(str1[0]);
            }
        }*/

        return arrayList;
    }

    public String getFontPath(String fontPreferenceString) throws IOException, IllegalStateException{
        fontManager fontManager = new fontManager(mContext);
        fontManager.initializeFontManager();
        ArrayList<String> list = new ArrayList<>(fontManager.listAllFileNames());
        String pathToFont = "";
        for (String str : list){
            if (str.endsWith(".ttf") && str.replace(".ttf", "").equalsIgnoreCase(fontPreferenceString))
                pathToFont = "fonts/"+str;
            if (str.endsWith(".otf") && str.replace(".otf", "").equalsIgnoreCase(fontPreferenceString))
                pathToFont = "fonts/"+str;
            /*if (str.contains(fontPreferenceString)){
                pathToFont = "fonts/"+str;
            }*/
        }
        return pathToFont;
    }

}

class SearchFiles implements FilenameFilter{

    private String extension;

    SearchFiles(String str){
        this.extension = str;
    }

    @Override
    public boolean accept(File file, String s) {
        if (s.lastIndexOf('.') > 0){
            int lastIndex = s.lastIndexOf('.');
            String ext = s.substring(lastIndex);
            return ext.equalsIgnoreCase(extension);
        }
        return false;
    }
}
