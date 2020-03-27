package com.me.android.noteeditor;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.me.android.noteeditor.BlurUtility.ApplyBlurOnDialog;
import com.me.android.noteeditor.CommonUtility.utilityClass;
import com.me.android.noteeditor.Recyclerview_adapter.folders_adapter;
import com.me.android.noteeditor.contract.DataBaseManager;
import com.me.android.noteeditor.contract.FoldersDataBase.Folder_content_class;
import com.me.android.noteeditor.contract.FoldersDataBase.FoldersDataBaseManager;
import com.me.android.noteeditor.contract.content_class;
import com.me.android.noteeditor.customListener.Adapter_refresh_listener;
import com.me.android.noteeditor.customListener.EmptyFolder_listener;
import com.me.android.noteeditor.fontManager.fontManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.jaredrummler.cyanea.Cyanea;

import java.io.IOException;
import java.util.ArrayList;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.me.android.noteeditor.CommonUtility.utilityClass.setFrameLayoutParams;
import static com.me.android.noteeditor.CommonUtility.utilityClass.setTint;

public class FolderClass extends Fragment {

    private View mainView;
    private folders_adapter adapter;
    private ArrayList<Folder_content_class> datalist = new ArrayList<>();
    private final utilityClass utilityClass = new utilityClass();

    private FoldersDataBaseManager dbManager;

    static Typeface typeface;

    ApplyBlurOnDialog apr;

    public FolderClass(){
        //requires public empty constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        mainView = inflater.inflate(R.layout.folders_main_layout, container, false);

        setHasOptionsMenu(true);

        dbManager = new FoldersDataBaseManager(getActivity());
        apr = new ApplyBlurOnDialog(getActivity());

        TextView emptyFoldersWarning = mainView.findViewById(R.id.empty_folders_text);
        emptyFoldersWarning.setVisibility(View.GONE);

        RecyclerView foldersList = mainView.findViewById(R.id.folders_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        foldersList.setLayoutManager(linearLayoutManager);

        if (dbManager.getAll() != null){
            datalist.addAll(dbManager.getAll());
        }

        if (dbManager.getAll().isEmpty() || dbManager.getAll() == null){
            emptyFoldersWarning.setVisibility(View.VISIBLE);
        }

        adapter = new folders_adapter(datalist, getActivity());
        foldersList.setAdapter(adapter);

        FloatingActionButton floatingActionButton = mainView.findViewById(R.id.FabCreateNewFolder);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NameField();
            }
        });
        setTint(getContext(), R.drawable.ic_create_new_folder_black_24dp, floatingActionButton);

        EmptyFolder_listener.setOnEmptyFolderListener(new EmptyFolder_listener.EmptyFolderListener() {
            @Override
            public void onFolderEmpty(boolean isFolderListEmpty) {
                if (isFolderListEmpty) mainView.findViewById(R.id.empty_folders_text).setVisibility(View.VISIBLE);
                else mainView.findViewById(R.id.empty_folders_text).setVisibility(View.GONE);
            }
        });

        return mainView;
    }

    private void NameField(){

        fontManager fontManager = new fontManager(getActivity());
        fontManager.initializeFontManager();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        String fontType = sharedPreferences.getString(
                getString(R.string.font_preference_key),
                "Aller_Lt"
        );

        String fontpath = null;
        try {
            fontpath = fontManager.getFontPath(fontType);
        } catch (IOException e) {
            e.printStackTrace();
        }

        typeface = Typeface.createFromAsset(getActivity().getAssets(), fontpath);


        final TextInputLayout editTitle =
                (TextInputLayout) getLayoutInflater().inflate(R.layout.title_edittext, null);
        final TextInputEditText textInputEditText = editTitle.findViewById(R.id.editTitle_inputEditText);

        editTitle.setTypeface(typeface);
        editTitle.setErrorEnabled(true);
        editTitle.setHintEnabled(true);
        editTitle.setElevation(6);
        editTitle.setHint("Folder name");

        setFrameLayoutParams(getActivity(), textInputEditText, 8f);

        textInputEditText.setTypeface(typeface);

        AlertDialog.Builder editTitleDialogBuilder =
                new AlertDialog.Builder(getActivity());
        editTitleDialogBuilder.setMessage(getString(R.string.create_new_folder));
        editTitleDialogBuilder.setView(editTitle);
        editTitleDialogBuilder.setPositiveButton(getString(R.string.save), null);
        editTitleDialogBuilder.setNegativeButton(getString(R.string.Discard), null);

        final AlertDialog alertDialog = utilityClass.DialogBlur(getContext(), editTitleDialogBuilder);

        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                utilityClass.cancelDialog();
            }
        });

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean fileNameExists = false;
                for (Folder_content_class item : dbManager.getAll()){
                    if (item.getFolderName().equals(textInputEditText.getText().toString())){
                        editTitle.setError("Folder exists with the same name");
                        fileNameExists = true;
                    }
                }
                if (!fileNameExists){
                    dbManager.insertFolderData(textInputEditText.getText().toString());
                    adapter.refresh(getActivity());
                    utilityClass.cancelDialog();
                    EmptyFolder_listener.confirmEmptyLFolderListner(false);
                }
            }
        });
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                utilityClass.cancelDialog();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.folders_menu, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        getActivity().invalidateOptionsMenu();
        if (datalist.isEmpty()){
            menu.findItem(R.id.folder_menu_deleteAll).setEnabled(false);
        }
        else
            super.onPrepareOptionsMenu(menu);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId){
            case R.id.folder_menu_deleteAll :

                final DataBaseManager ccDBManager = new DataBaseManager(getContext());

                View deleteCheckBox = getLayoutInflater().inflate(R.layout.folder_delete_info, null, false);
                final CheckBox Checkbox = deleteCheckBox.findViewById(R.id.folder_delete_item_checkbox);
                TextView message = deleteCheckBox.findViewById(R.id.folderDeleteInfo_message);
                message.setText(getString(R.string.confirm_deleteAll_message));

                deleteCheckBox.setBackgroundColor(Cyanea.getInstance().getBackgroundColor());

                final AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                dialog.setView(deleteCheckBox);

                final AlertDialog confirmFakeDialog = utilityClass.DialogBlur(getContext(), dialog);
                confirmFakeDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        utilityClass.cancelDialog();
                    }
                });

                MaterialButton DeleteButton,DeleteCancelButton;
                DeleteButton = deleteCheckBox.findViewById(R.id.folder_item_delete_button);
                setTint(DeleteButton);
                DeleteCancelButton = deleteCheckBox.findViewById(R.id.folder_item_delete_cancel_button);

                DeleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Checkbox.isChecked()){
                            for (Folder_content_class item : dbManager.getAll()){
                                String folderName = item.getFolderName();
                                for (content_class item1 : ccDBManager.getAll()){
                                    if (item1.getFolderName().equals(folderName))
                                        ccDBManager.deleteEntry(item1.getId());
                                }
                                dbManager.deleteFolderEntry(item.getId());
                            }
                            EmptyFolder_listener.confirmEmptyLFolderListner(true);
                            Adapter_refresh_listener.confirmAdapterRefresh(true);
                        }else {
                            for (Folder_content_class item : dbManager.getAll()){
                                String folderName = item.getFolderName();
                                for (content_class item1 : ccDBManager.getAll()){
                                    if (item1.getFolderName().equals(folderName)){
                                        content_class singleItem1 = ccDBManager.getSingleData(item1.getId());
                                        ccDBManager.updateEntry(singleItem1.getId(),
                                                singleItem1.getTitle(),
                                                singleItem1.getContent(),
                                                singleItem1.getTime(),
                                                "None"
                                        );
                                    }
                                }
                                dbManager.deleteFolderEntry(item.getId());
                            }
                            /*just in case if it throws certain that scary null pointer exception,
                             * that shit has really given me a very tough time, and i don't wanna deal with this anytime soon*/
                            try {
                                EmptyFolder_listener.confirmEmptyLFolderListner(true);
                                Adapter_refresh_listener.confirmAdapterRefresh(true);
                            }catch (NullPointerException e){
                                //empty
                            }
                        }
                        adapter.refresh(getContext());
                        utilityClass.cancelDialog();
                    }
                });
                DeleteCancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        utilityClass.cancelDialog();
                    }
                });


                /*final AlertDialog.Builder deleteAllDialog = new AlertDialog.Builder(getContext());
                deleteAllDialog.setMessage(getString(R.string.confirm_deleteAll_message));
                deleteAllDialog.setPositiveButton(getString(R.string.confirm_delete), null);
                deleteAllDialog.setNegativeButton(getString(R.string.confirm_cancel), null);

                final AlertDialog deleteAll = apr.getRealDialog(deleteAllDialog);
                final Dialog fakeDialog = apr.getFakeDialog();
                apr.setBackgroundDrawable(fakeDialog);
                fakeDialog.show();
                deleteAll.show();
                apr.clearDialogDimming(new Dialog[]{fakeDialog, deleteAll});

                deleteAll.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        deleteAll.dismiss();
                        fakeDialog.dismiss();
                    }
                });


                deleteAll.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (datalist.isEmpty()){
                            Toast.makeText(getContext(), getString(R.string.nothing_to_delete), Toast.LENGTH_SHORT)
                                    .show();
                            return;
                        }else {
                            dbManager.deleteAll();
                            Toast.makeText(getContext(), getString(R.string.success_delete), Toast.LENGTH_SHORT).show();
                            adapter.refresh(getContext());
                            EmptyFolder_listener.confirmEmptyLFolderListner(true);
                            Adapter_refresh_listener.confirmAdapterRefresh(true);
                        }
                        deleteAll.dismiss();
                        fakeDialog.dismiss();
                    }
                });

                deleteAll.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteAll.dismiss();
                        fakeDialog.dismiss();
                    }
                });*/

                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
