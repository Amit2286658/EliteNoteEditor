package com.me.android.noteeditor.CommonUtility;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.me.android.noteeditor.BlurUtility.ApplyBlurOnDialog;
import com.me.android.noteeditor.R;
import com.me.android.noteeditor.contract.FoldersDataBase.Folder_content_class;
import com.me.android.noteeditor.contract.FoldersDataBase.FoldersDataBaseManager;

import java.util.ArrayList;

import androidx.appcompat.app.AlertDialog;

import static com.me.android.noteeditor.CommonUtility.utilityClass.getDipValue;

public class customSaveDialogUtility {
    private static AlertDialog saveDialog;
    private static Dialog fakeDialog;
    private static ArrayList<String> arrayList;
    private final utilityClass utilityClass = new utilityClass();

    public AlertDialog setSaveDialog(Activity activity, Context context, View view, String message){
        ApplyBlurOnDialog apr = new ApplyBlurOnDialog(activity);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        alertDialog.setMessage(message);
        alertDialog.setView(view);
        alertDialog.setPositiveButton(context.getString(R.string.save), null);
        alertDialog.setNegativeButton(context.getString(R.string.Discard), null);

        saveDialog = utilityClass.DialogBlur(context, alertDialog);
        return saveDialog;
    }

    public static void setFolderSelectionAdapter(Context context, Spinner spinner){
        FoldersDataBaseManager foldersDataBaseManager = new FoldersDataBaseManager(context);
        arrayList = new ArrayList<>();
        arrayList.add("None");
        for (Folder_content_class item : foldersDataBaseManager.getAll()){
            arrayList.add(item.getFolderName());
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, R.layout.spinner_item_1, arrayList);
        arrayAdapter.setDropDownViewResource(R.layout.spinner_item);

        spinner.setAdapter(arrayAdapter);

    }

    public static ArrayList<String> getSpinnerDataSet(){
        return arrayList;
    }

    public static void closeSaveDialogs(){
        if (saveDialog.isShowing()) saveDialog.dismiss();
        if (fakeDialog.isShowing()) fakeDialog.dismiss();
    }

    public static void setLinearLayoutParams(Context context, View spinner, float f){
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(getDipValue(context, f),
                0,
                getDipValue(context, f),
                0);
        spinner.setLayoutParams(layoutParams);
    }


}

