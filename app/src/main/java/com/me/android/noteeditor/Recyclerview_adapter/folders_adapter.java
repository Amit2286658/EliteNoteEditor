package com.me.android.noteeditor.Recyclerview_adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.me.android.noteeditor.CommonUtility.utilityClass;
import com.me.android.noteeditor.R;
import com.me.android.noteeditor.contract.DataBaseManager;
import com.me.android.noteeditor.contract.FoldersDataBase.Folder_content_class;
import com.me.android.noteeditor.contract.FoldersDataBase.FoldersDataBaseManager;
import com.me.android.noteeditor.contract.content_class;
import com.me.android.noteeditor.customListener.Adapter_refresh_listener;
import com.me.android.noteeditor.customListener.EmptyFolder_listener;
import com.me.android.noteeditor.customListener.FolderCountChangeListener;
import com.me.android.noteeditor.fontManager.fontManager;
import com.me.android.noteeditor.fragment_container;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.jaredrummler.cyanea.Cyanea;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import static com.me.android.noteeditor.CommonUtility.utilityClass.setFrameLayoutParams;
import static com.me.android.noteeditor.CommonUtility.utilityClass.setTint;

public class folders_adapter extends RecyclerView.Adapter<foldersViewHolder>  {

    private static List<Folder_content_class> foldersList;
    private Activity activity;
    private Typeface typeface;
    private fontManager fontManager;

    public folders_adapter(ArrayList<Folder_content_class> foldersList, Activity activity){
        folders_adapter.foldersList = foldersList;
        this.activity = activity;
        fontManager = new fontManager(activity);
        fontManager.initializeFontManager();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);

        String fontType = sharedPreferences.getString(
                activity.getString(R.string.font_preference_key),
                "Aller_Lt"
        );

        String path = null;
        try {
            path = fontManager.getFontPath(fontType);
        } catch (IOException e) {
            e.printStackTrace();
        }

        typeface = Typeface.createFromAsset(activity.getAssets(), path);

    }

    @Override
    public foldersViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View folderitemView = layoutInflater.inflate(R.layout.folders_adapter_layout, viewGroup, false);

        return new foldersViewHolder(folderitemView);
    }

    @Override
    public void onBindViewHolder(final foldersViewHolder foldersViewHolder, final int i) {

        FolderCountChangeListener.setOnFolderCountChangeListener(new FolderCountChangeListener.FolderCountChange_listener() {
            @Override
            public void onFolderCountChange(boolean value) {
                if (value) refresh(foldersViewHolder.context);
            }
        });

        final Folder_content_class position = foldersList.get(i);
        final String folderName = position.getFolderName();
        foldersViewHolder.foldersName.setText(folderName);
        foldersViewHolder.foldersName.setTypeface(typeface);

        final DataBaseManager contentClassDatabaseManager = new DataBaseManager(foldersViewHolder.context);

        int folder_count = 0;
        for (content_class item : contentClassDatabaseManager.getAll()){
            if (item.getFolderName().equalsIgnoreCase(folderName))
                folder_count++ ;
        }

        foldersViewHolder.folderCount.setText(String.valueOf(folder_count));

        foldersViewHolder.containerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(foldersViewHolder.context, fragment_container.class);
                intent.putExtra(
                        foldersViewHolder.context.getString(R.string.from_folder_to_fragment_container_intent_key), folderName);
                intent.putExtra(foldersViewHolder.context.getString(R.string.from_folder_to_fragment_container_identify_key),
                        1);
                foldersViewHolder.context.startActivity(intent);
            }
        });

        foldersViewHolder.containerView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                final utilityClass utilityClass = new utilityClass();

                final LayoutInflater layoutInflater =
                        (LayoutInflater) foldersViewHolder.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View containerView = layoutInflater.inflate(R.layout.custom_folder_toast_layout, null, false);


                final TextView folderNameTextView = containerView.findViewById(R.id.custom_popup_toast_folderName);
                folderNameTextView.setText(foldersList.get(i).getFolderName());

                final AlertDialog.Builder dialog = new AlertDialog.Builder(foldersViewHolder.context);
                dialog.setView(containerView);

                final AlertDialog fakeDialog = utilityClass.DialogBlur(foldersViewHolder.context, dialog);
                fakeDialog.getWindow().setGravity(Gravity.BOTTOM);

                fakeDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        utilityClass.cancelDialog();
                    }
                });

                TextView renameButton = containerView.findViewById(R.id.folder_popup_toast_rename);
                renameButton.setTypeface(typeface);
                renameButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        utilityClass.cancelDialog();

                        final TextInputLayout editTitle =
                                (TextInputLayout) layoutInflater.inflate(R.layout.title_edittext, null);
                        final TextInputEditText textInputEditText = editTitle.findViewById(R.id.editTitle_inputEditText);

                        editTitle.setTypeface(typeface);
                        editTitle.setErrorEnabled(true);
                        editTitle.setHintEnabled(true);
                        editTitle.setElevation(6);
                        editTitle.setHint("Folder");

                        setFrameLayoutParams(foldersViewHolder.context, textInputEditText, 8f);

                        textInputEditText.setText(foldersList.get(i).getFolderName());
                        textInputEditText.setTypeface(typeface);


                        androidx.appcompat.app.AlertDialog.Builder editTitleDialog =
                                new androidx.appcompat.app.AlertDialog.Builder(foldersViewHolder.context);
                        editTitleDialog.setView(editTitle);
                        editTitleDialog.setMessage("Rename folder");
                        editTitleDialog.setPositiveButton(foldersViewHolder.context.getString(R.string.save), null);
                        editTitleDialog.setNegativeButton(foldersViewHolder.context.getString(R.string.Discard), null);

                        final androidx.appcompat.app.AlertDialog renameFolderNameDialog = utilityClass.
                                DialogBlur(foldersViewHolder.context, editTitleDialog);

                        renameFolderNameDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                FoldersDataBaseManager foldersDataBaseManager =
                                        new FoldersDataBaseManager(foldersViewHolder.context);

                                if (TextUtils.isEmpty(textInputEditText.getText())){
                                    editTitle.setError(foldersViewHolder.context.getString(R.string.folder_name_cant_be_empty));
                                }else {
                                    boolean fileNameExists= false;
                                    String initialName = foldersDataBaseManager.getSingleData(foldersList.get(i).getId()).getFolderName();
                                    if (!initialName.equals(textInputEditText.getText().toString())) {
                                        for (Folder_content_class item : foldersDataBaseManager.getAll()) {
                                            if (item.getFolderName().equals(textInputEditText.getText().toString())) {
                                                editTitle.setError(foldersViewHolder.context.getString(
                                                        R.string.folder_name_exists_already));
                                                fileNameExists = true;
                                            }
                                        }
                                    }
                                    if (!fileNameExists) {
                                        for (content_class item1 : contentClassDatabaseManager.getAll()) {
                                            if (item1.getFolderName().equals(folderName)) {
                                                content_class singleItem = contentClassDatabaseManager.getSingleData(item1.getId());
                                                contentClassDatabaseManager.updateEntry(singleItem.getId()
                                                        , singleItem.getTitle()
                                                        , singleItem.getContent()
                                                        , singleItem.getTime()
                                                        , textInputEditText.getText().toString());
                                            }
                                        }

                                        Folder_content_class item = foldersList.get(i);

                                        foldersDataBaseManager.updateEntry(item.getId(), textInputEditText.getText().toString());
                                        foldersDataBaseManager.close();

                                        utilityClass.cancelDialog();

                                        refresh(foldersViewHolder.context);

                                        Adapter_refresh_listener.confirmAdapterRefresh(true);
                                    }
                                }
                            }
                        });


                        renameFolderNameDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {

                                utilityClass.cancelDialog();
                            }
                        });
                        renameFolderNameDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                utilityClass.cancelDialog();
                            }
                        });
                    }
                });
                final TextView deleteButton = containerView.findViewById(R.id.folder_popup_toast_delete);
                deleteButton.setTypeface(typeface);
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        utilityClass.cancelDialog();

                        View deleteCheckBox = layoutInflater.inflate(R.layout.folder_delete_info, null, false);
                        final CheckBox Checkbox = deleteCheckBox.findViewById(R.id.folder_delete_item_checkbox);

                        deleteCheckBox.setBackgroundColor(Cyanea.getInstance().getBackgroundColor());

                        final AlertDialog.Builder dialog = new AlertDialog.Builder(foldersViewHolder.context);
                        dialog.setView(deleteCheckBox);

                        final AlertDialog confirmFakeDialog = utilityClass.DialogBlur(foldersViewHolder.context, dialog);
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
                                    for (content_class item : contentClassDatabaseManager.getAll()){
                                        if (item.getFolderName().equals(folderName))
                                            contentClassDatabaseManager.deleteEntry(item.getId());
                                    }
                                }else {
                                    for (content_class item1 : contentClassDatabaseManager.getAll()){
                                        if (item1.getFolderName().equals(folderName)){
                                            content_class singleItem = contentClassDatabaseManager.getSingleData(item1.getId());
                                            contentClassDatabaseManager.updateEntry(singleItem.getId(),
                                                    singleItem.getTitle(),
                                                    singleItem.getContent(),
                                                    singleItem.getTime(),
                                                    "None"
                                            );
                                        }
                                    }
                                }
                                Folder_content_class item = foldersList.get(i);
                                FoldersDataBaseManager foldersDataBaseManager =
                                        new FoldersDataBaseManager(foldersViewHolder.context);
                                foldersDataBaseManager.deleteFolderEntry(item.getId());

                                utilityClass.cancelDialog();

                                refresh(foldersViewHolder.context);

                                Adapter_refresh_listener.confirmAdapterRefresh(true);
                                if (foldersDataBaseManager.getAll().isEmpty() || foldersDataBaseManager.getAll() == null)
                                    EmptyFolder_listener.confirmEmptyLFolderListner(true);
                                else
                                    EmptyFolder_listener.confirmEmptyLFolderListner(false);
                            }
                        });
                        DeleteCancelButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                utilityClass.cancelDialog();
                            }
                        });
                    }
                });

                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return foldersList.size();
    }

    public List<Folder_content_class> getData(){
        return foldersList;
    }

    public void refresh(Context context){
        FoldersDataBaseManager foldersDBManager = new FoldersDataBaseManager(context);
        foldersList.clear();
        foldersList.addAll(foldersDBManager.getAll());
        foldersDBManager.close();
        notifyDataSetChanged();
    }
}
class foldersViewHolder extends RecyclerView.ViewHolder{

    TextView foldersName, folderCount;
    Context context;
    View containerView;

    foldersViewHolder(View itemView) {
        super(itemView);
        foldersName = itemView.findViewById(R.id.sample_folder_item);
        folderCount = itemView.findViewById(R.id.folders_count);
        containerView = itemView.findViewById(R.id.folders_adapter_layout_container_view);
        context = itemView.getContext();
    }
}
