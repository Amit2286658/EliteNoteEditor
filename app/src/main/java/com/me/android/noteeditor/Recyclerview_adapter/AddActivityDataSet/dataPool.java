package com.me.android.noteeditor.Recyclerview_adapter.AddActivityDataSet;

import com.chinalwb.are.AREditText;

import java.util.HashMap;

/**
 * Created by Amit kumar on 1/18/2020.
 */

public class dataPool {

    private static HashMap<AREditText, String> datalist = new HashMap<>();

    public static void putData(AREditText arEditText, String str){
        datalist.put(arEditText, str);
    }

    public static void removeData(AREditText arEditText){
        datalist.remove(arEditText);
    }

    public static void addAllData(HashMap<AREditText, String> list){
        datalist.putAll(list);
    }

    public static void removeAll(){
        datalist.clear();
    }

    public static boolean containsData(AREditText arEditText){
        return datalist.containsKey(arEditText);
    }

    public static String getData(AREditText arEditText){
        return datalist.get(arEditText);
    }

    public static HashMap<AREditText,String> getAll(){
        return datalist;
    }

}
