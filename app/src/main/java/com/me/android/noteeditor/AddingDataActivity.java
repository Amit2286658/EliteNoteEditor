package com.me.android.noteeditor;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.chinalwb.are.AREditText;
import com.chinalwb.are.styles.IARE_Style;
import com.chinalwb.are.styles.toolbar.ARE_ToolbarDefault;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_Abstract;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_AlignmentCenter;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_AlignmentLeft;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_AlignmentRight;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_At;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_BackgroundColor;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_Bold;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_FontColor;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_FontSize;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_Hr;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_Image;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_Italic;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_Link;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_ListBullet;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_ListNumber;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_Quote;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_Strikethrough;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_Subscript;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_Superscript;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_Underline;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_Video;
import com.chinalwb.are.styles.toolitems.IARE_ToolItem;
import com.chinalwb.are.styles.toolitems.IARE_ToolItem_Updater;
import com.chinalwb.are.styles.windows.FontsizePickerWindow;
import com.me.android.noteeditor.CommonUtility.utilityClass;
import com.me.android.noteeditor.MainActivity;
import com.me.android.noteeditor.contract.DataBaseManager;
import com.me.android.noteeditor.fontManager.fontManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.jaredrummler.cyanea.app.CyaneaAppCompatActivity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NavUtils;
import androidx.core.app.TaskStackBuilder;

import static com.me.android.noteeditor.CommonUtility.customSaveDialogUtility.setFolderSelectionAdapter;
import static com.me.android.noteeditor.CommonUtility.utilityClass.setTint;
import static com.me.android.noteeditor.CommonUtility.utilityClass.textSize;
import static com.me.android.noteeditor.CommonUtility.utilityClass.updateWorkaround;

public class AddingDataActivity extends CyaneaAppCompatActivity {

    private DataBaseManager dataBaseManager = new DataBaseManager(this);

    private final utilityClass utilityClass = new utilityClass();

    AREditText content;

    private ARE_ToolbarDefault areToolbar;


    /*TextInputEditText title;*/
    private String editTextTitle, editTextContent, tempTitle, tempContent, tempFolder;
    private int activityIdentifier = -1;
    int id = -1;

    private static Date date;

    @Deprecated
    private static Thread postTime;

    private static boolean posttimeBoolean = false;

    private static String fontPath;
    static Typeface typeface;

    private SimpleDateFormat sdf = new SimpleDateFormat("MMM/dd/yyyy hh:mm aa", Locale.getDefault());
    private SimpleDateFormat simpleDateFormat =
            new SimpleDateFormat("MMM/dd/yyyy hh:mm:ss aa", Locale.getDefault());

    /**
     * Dispatch onPause() to fragments.
     */
    @Override
    protected void onPause() {
        super.onPause();
        /*if (postTime.isAlive() || !postTime.isInterrupted()){
            if (posttimeBoolean){
                posttimeBoolean = false;
            }
            postTime.interrupt();
        }*/
    }

    /**
     * Dispatch onResume() to fragments.  Note that for better inter-operation
     * with older versions of the platform, at the point of this call the
     * fragments attached to the activity are <em>not</em> resumed.  This means
     * that in some cases the previous state may still be saved, not allowing
     * fragment transactions that modify the state.  To correctly interact
     * with fragments in their proper state, you should instead override
     * {@link #onResumeFragments()}.
     */
    @Override
    protected void onResume() {
        super.onResume();
        /*if (postTime != null || postTime.isInterrupted()){
            posttime();
        }*/
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        content.clearComposingText();
        finish();
        Runtime.getRuntime().gc();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_adding_data);
        if (getSupportActionBar()!=null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        try {
            activityIdentifier = getIntent().getExtras().getInt(getString(R.string.key_activity_identifier));
            id = getIntent().getExtras().getInt(getString(R.string.key_data_id));
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        setUp();
        /*posttime();*/
        initToolbar();

        content.setBackground(null);
        content.clearComposingText();

    }

    private void initToolbar(){

        areToolbar = findViewById(R.id.areToolbar);

        IARE_ToolItem bold = new ARE_ToolItem_Bold();
        IARE_ToolItem italic = new ARE_ToolItem_Italic();
        IARE_ToolItem underline = new ARE_ToolItem_Underline();
        IARE_ToolItem strikethrough = new ARE_ToolItem_Strikethrough();
        IARE_ToolItem fontsize = new ARE_ToolItem_FontSize();
        IARE_ToolItem fontColor = new ARE_ToolItem_FontColor();
        IARE_ToolItem backgroundColor = new ARE_ToolItem_BackgroundColor();
        IARE_ToolItem quote = new ARE_ToolItem_Quote();
        IARE_ToolItem listNumber = new ARE_ToolItem_ListNumber();
        IARE_ToolItem listBullet = new ARE_ToolItem_ListBullet();
        IARE_ToolItem hr = new ARE_ToolItem_Hr();
        IARE_ToolItem link = new ARE_ToolItem_Link();
        IARE_ToolItem subScript = new ARE_ToolItem_Subscript();
        IARE_ToolItem superScript = new ARE_ToolItem_Superscript();
        IARE_ToolItem left = new ARE_ToolItem_AlignmentLeft();
        IARE_ToolItem right = new ARE_ToolItem_AlignmentRight();
        IARE_ToolItem center = new ARE_ToolItem_AlignmentCenter();
        IARE_ToolItem image = new ARE_ToolItem_Image();
        IARE_ToolItem video = new ARE_ToolItem_Video();
        IARE_ToolItem at = new ARE_ToolItem_At();
        IARE_ToolItem typeface = new ARE_ToolItem_Abstract() {
            @Override
            public IARE_Style getStyle() {
                return null;
            }

            @Override
            public View getView(Context context) {
                return null;
            }

            @Override
            public void onSelectionChanged(int selStart, int selEnd) {

            }

            @Override
            public IARE_ToolItem_Updater getToolItemUpdater() {
                return null;
            }

            @Override
            public void onActivityResult(int requestCode, int resultCode, Intent data) {
                super.onActivityResult(requestCode, resultCode, data);
            }
        };

        areToolbar.addToolbarItem(bold);
        areToolbar.addToolbarItem(underline);
        areToolbar.addToolbarItem(italic);
        areToolbar.addToolbarItem(strikethrough);
        areToolbar.addToolbarItem(fontsize);
        areToolbar.addToolbarItem(fontColor);
        areToolbar.addToolbarItem(backgroundColor);
        areToolbar.addToolbarItem(quote);
        areToolbar.addToolbarItem(listNumber);
        areToolbar.addToolbarItem(listBullet);
        areToolbar.addToolbarItem(hr);
        areToolbar.addToolbarItem(link);
        areToolbar.addToolbarItem(subScript);
        areToolbar.addToolbarItem(superScript);
        areToolbar.addToolbarItem(left);
        areToolbar.addToolbarItem(right);
        areToolbar.addToolbarItem(center);
        areToolbar.addToolbarItem(image);
        areToolbar.addToolbarItem(video);
        //areToolbar.addToolbarItem(at);

        content.setToolbar(areToolbar);

        ARE_ToolbarDefault.setOnTextSizeWindowShowListener(new ARE_ToolbarDefault.TextSizeWindowDisplay() {
            @Override
            public void onTextSizeDisplay(final FontsizePickerWindow ARE_FontSize,
                                          View anchor, int x_offset, int y_offset, boolean isWindowDisplaying) {
                if (isWindowDisplaying) {
                    ARE_FontSize.getContentView().setBackgroundColor(getCyanea().getBackgroundColor());
                    /*ApplyBlurOnPopupWindow applyBlurOnPopupWindow = new ApplyBlurOnPopupWindow(AddingDataActivity.this);
                    final Dialog fakeDialog = applyBlurOnPopupWindow.getFakeDialog();
                    applyBlurOnPopupWindow.setBlurredBackground(fakeDialog);
                    fakeDialog.show();

                    if (fakeDialog.isShowing()) ARE_FontSize.showAsDropDown(areToolbar, 0, 0, Gravity.TOP);
                    ARE_FontSize.setOnDismissListener(new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            fakeDialog.dismiss();
                            ARE_FontSize.dismiss();
                        }
                    });*/
                }
            }
        });

    }

    @Deprecated
    private void posttime(){
        final TextView editingActivityTime = (TextView) findViewById(R.id.editingActivityTime);
        editingActivityTime.setTextColor(fetchAccentColor());

        postTime = new Thread(new Runnable() {
            @Override
            public void run() {
                posttimeBoolean = true;
                while (posttimeBoolean){
                    date = new Date();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            editingActivityTime.setText(simpleDateFormat.format(date));
                        }
                    });
                    try {
                        Thread.sleep(1000);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        });
        postTime.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.addingactivitymenu, menu);
        /*int theme_identifier = PreferenceManager.getDefaultSharedPreferences(this)
                .getInt(
                        getString(R.string.customThemeChangekey),
                        1
                );
        if (theme_identifier == 2) {
            menu.findItem(R.id.menu_add).setIcon(R.drawable.ic_check_black_24dp_black);
        }*/
        return super.onCreateOptionsMenu(menu);
    }

    private void setUp(){

        fontManager fontManager = new fontManager(this);
        fontManager.initializeFontManager();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        /*todo: titleBar1 title = (TextInputEditText)findViewById(R.id.EditTextTitle);*/
        content = (AREditText) findViewById(R.id.EditTextContent);

        int textSize = sharedPreferences.getInt(
                getString(R.string.custom_text_size_key),
                1
        );
        String fontType = sharedPreferences.getString(
                getString(R.string.font_preference_key),
                "Aller_Lt"
        );

        try {
            fontPath = fontManager.getFontPath(fontType);
        } catch (IOException e) {
            e.printStackTrace();
        }

        typeface = Typeface.createFromAsset(this.getAssets(), fontPath);

        /*TextInputLayout textInputLayout = findViewById(R.id.AddingDataActivit_textInputLayout);
        textInputLayout.setTypeface(typeface);*/

        /*todo : titleBar2 title.setTextSize(textSize(textSize));
        title.setTypeface(typeface);*/
        content.setTextSize(textSize(textSize));
        content.setTypeface(typeface);

        try {
            if (getIntent().getExtras().getString(getString(R.string.key_data_title)) != null) {
                /*todo : titleBar3 title.setText(getIntent().getExtras().getString(getString(R.string.key_data_title)));*/
                tempTitle = getIntent().getExtras().getString(getString(R.string.key_data_title));
            }
            if (getIntent().getExtras().getString(getString(R.string.key_data_content)) != null) {
                content.fromHtml(getIntent().getExtras().getString(getString(R.string.key_data_content)));
                tempContent = content.getHtml()/*getIntent().getExtras().getString(getString(R.string.key_data_content))*/;
            }
            if (getIntent().getExtras().getString(getString(R.string.key_data_folderName)) != null){
                tempFolder = getIntent().getExtras().getString(getString(R.string.key_data_folderName));
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int position = item.getItemId();
        switch (position){
            case R.id.menu_add :
                /*todo : titleBar7 editTextTitle = title.getText().toString();*/
                editTextContent = content.getHtml();

                View titleView = getLayoutInflater().inflate(R.layout.edit_title_with_folder, null, false);
                final TextInputLayout textInputLayout = titleView.findViewById(R.id.editTitle_inputLayout_withFolder);
                final TextInputEditText textInputEditText = textInputLayout.findViewById(R.id.editTitle_inputEditText_withFolder);

                final Spinner spinner = titleView.findViewById(R.id.folderSelector);

                textInputLayout.setTypeface(typeface);
                textInputEditText.setTypeface(typeface);
                if (activityIdentifier == 1){
                    textInputEditText.setText(tempTitle);
                    /*spinner.setSelection(getSpinnerDataSet().indexOf(tempFolder));*/
                }

                titleView.setBackgroundColor(getCyanea().getBackgroundColor());

                setFolderSelectionAdapter(this, spinner);

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setView(titleView);
                /*final Dialog dialog = new Dialog(this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(titleView);
                dialog.setCanceledOnTouchOutside(true);*/

                /*Window window = dialog.getWindow();
                int w = LinearLayout.LayoutParams.MATCH_PARENT;
                int h = LinearLayout.LayoutParams.WRAP_CONTENT;
                window.setLayout(w, h);*/

                AlertDialog alertDialog = utilityClass.DialogBlur(this, builder);

                MaterialButton positiveButton, negativeButton;
                positiveButton = titleView.findViewById(R.id.custom_dialog_positive_button);
                setTint(positiveButton);
                negativeButton = titleView.findViewById(R.id.custom_dialog_negative_button);

                alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        utilityClass.cancelDialog();
                    }
                });

                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (TextUtils.isEmpty(textInputEditText.getText())) {
                            textInputLayout.setError(getString(R.string.warning_empty_title));
                        }
                        else {
                            if (activityIdentifier == -1) {
                                /*boolean fileNameExists = false;
                                for (content_class item : dataBaseManager.getAll()){
                                    if (item.getTitle().equalsIgnoreCase(textInputEditText.getText().toString()))
                                        fileNameExists = true;
                                }*/
                                //if (!fileNameExists) {
                                Date date = new Date();
                                String currentTime = sdf.format(date);
                                dataBaseManager.InsertData(textInputEditText.getText().toString(), content.getHtml(), currentTime,
                                        spinner.getSelectedItem().toString());
                                NavUtils.navigateUpFromSameTask(AddingDataActivity.this);
                                Toast.makeText(getApplicationContext(), getString(R.string.success_add), Toast.LENGTH_SHORT).show();
                                finish();
                                //}else
                                  //  textInputLayout.setError(getString(R.string.file_already_exists_with_the_same_name));
                            }
                            else {
                                /*boolean filaNameExists = false;
                                content_class dataItem = dataBaseManager.getSingleData(id);
                                String initialTitle = dataItem.getTitle();
                                if (!textInputEditText.getText().toString().equals(initialTitle)) {
                                    for (content_class item : dataBaseManager.getAll()) {
                                        if (item.getTitle().equalsIgnoreCase(textInputEditText.getText().toString()))
                                            filaNameExists = true;
                                    }
                                }*/
                                /*if (!filaNameExists) {*/
                                Date date = new Date();
                                String currentTime = sdf.format(date);
                                updateWorkaround(AddingDataActivity.this, id, textInputEditText.getText().toString(), content.getHtml(),
                                        currentTime, spinner.getSelectedItem().toString());
                                /*dataBaseManager.updateEntry(id, textInputEditText.getText().toString(), content.getHtml(),
                                        currentTime, spinner.getSelectedItem().toString());*/
                                NavUtils.navigateUpFromSameTask(AddingDataActivity.this);
                                Toast.makeText(getApplicationContext(), getString(R.string.success_update), Toast.LENGTH_SHORT).show();
                                finish();
                                //}else textInputLayout.setError(getString(R.string.file_already_exists_with_the_same_name));
                            }
                        }
                    }
                });
                negativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        utilityClass.cancelDialog();
                        finish();
                    }
                });


               /* saveDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        closeSaveDialogs();
                    }
                });
                saveDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (TextUtils.isEmpty(textInputEditText.getText())) {
                            textInputLayout.setError(getString(R.string.warning_empty_title));
                        }
                        else {
                            if (activityIdentifier == -1) {
                                Date date = new Date();
                                String currentTime = sdf.format(date);
                                dataBaseManager.InsertData(textInputEditText.getText().toString(), editTextContent, currentTime,
                                        spinner.getSelectedItem().toString());
                                NavUtils.navigateUpFromSameTask(AddingDataActivity.this);
                                Toast.makeText(getApplicationContext(), getString(R.string.success_add), Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Date date = new Date();
                                String currentTime = sdf.format(date);
                                dataBaseManager.updateEntry(id, textInputEditText.getText().toString(), editTextContent,
                                        currentTime, spinner.getSelectedItem().toString());
                                NavUtils.navigateUpFromSameTask(AddingDataActivity.this);
                                Toast.makeText(getApplicationContext(), getString(R.string.success_update), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
                saveDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        closeSaveDialogs();
                    }
                });*/
                return true;

                /*AlertDialog.Builder editTitleDialogBuilder =
                        new AlertDialog.Builder(this);
                editTitleDialogBuilder.setMessage(getString(R.string.edit_title_in_dialog_builder));
                editTitleDialogBuilder.setView(titleView);
                editTitleDialogBuilder.setPositiveButton(getString(R.string.save), null);
                editTitleDialogBuilder.setNegativeButton(getString(R.string.Discard), null);*/

                /*final AlertDialog alertDialog = apr.getRealDialog(editTitleDialogBuilder);
                final Dialog fakeAlertDialog = apr.getFakeDialog();
                apr.setBackgroundDrawable(fakeAlertDialog);
                fakeAlertDialog.show();
                alertDialog.show();
                apr.clearDialogDimming(new Dialog[]{fakeAlertDialog, alertDialog});
                alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        alertDialog.dismiss();
                        fakeAlertDialog.dismiss();
                    }
                });*/

                /*if (editTextTitle == null || editTextTitle.isEmpty()){
                    Toast.makeText(this, getString(R.string.warning_empty_title), Toast.LENGTH_SHORT).show();
                    return false;
                }
                if (activityIdentifier == -1) {
                    Date date = new Date();
                    String currentTime = sdf.format(date);
                    dataBaseManager.InsertData(editTextTitle, editTextContent, currentTime);
                    NavUtils.navigateUpFromSameTask(this);
                    Toast.makeText(getApplicationContext(),getString(R.string.success_add), Toast.LENGTH_SHORT).show();
                    return true;
                }else {
                    Date date = new Date();
                    String currentTime = sdf.format(date);
                    dataBaseManager.updateEntry(id, editTextTitle, editTextContent, currentTime);
                    NavUtils.navigateUpFromSameTask(this);
                    Toast.makeText(getApplicationContext(),getString(R.string.success_update), Toast.LENGTH_SHORT).show();
                    return true;
                }*/
                default:
                    return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Obtain an {@link Intent} that will launch an explicit target activity
     * specified by sourceActivity's {@link NavUtils#PARENT_ACTIVITY} &lt;meta-data&gt;
     * element in the application's manifest. If the device is running
     * Jellybean or newer, the android:parentActivityName attribute will be preferred
     * if it is present.
     *
     * @return a new Intent targeting the defined parent activity of sourceActivity
     */
    @Nullable
    @Override
    public Intent getSupportParentActivityIntent() {
        return new Intent(this, MainActivity.class);
    }

    /**
     * This method is called whenever the user chooses to navigate Up within your application's
     * activity hierarchy from the action bar.
     *
     * <p>If a parent was specified in the manifest for this activity or an activity-alias to it,
     * default Up navigation will be handled automatically. See
     * {@link #getSupportParentActivityIntent()} for how to specify the parent. If any activity
     * along the parent chain requires extra Intent arguments, the Activity subclass
     * should override the method {@link #onPrepareSupportNavigateUpTaskStack(TaskStackBuilder)}
     * to supply those arguments.</p>
     *
     * <p>See <a href="{@docRoot}guide/topics/fundamentals/tasks-and-back-stack.html">Tasks and
     * Back Stack</a> from the developer guide and
     * <a href="{@docRoot}design/patterns/navigation.html">Navigation</a> from the design guide
     * for more information about navigating within your app.</p>
     *
     * <p>See the {@link TaskStackBuilder} class and the Activity methods
     * {@link #getSupportParentActivityIntent()}, {@link #supportShouldUpRecreateTask(Intent)}, and
     * {@link #supportNavigateUpTo(Intent)} for help implementing custom Up navigation.</p>
     *
     * @return true if Up navigation completed successfully and this Activity was finished,
     * false otherwise.
     */
    @Override
    public boolean onSupportNavigateUp() {
        /*if (activityIdentifier == 1){*/
            onBackPressed();
        /*}else
            NavUtils.navigateUpFromSameTask(this);*/
        return true;
    }

    /**
     * Take care of popping the fragment back stack or finishing the activity
     * as appropriate.
     */
    @Override
    public void onBackPressed() {

        if (activityIdentifier == 1) {
            if (tempContent != null) {
                if (/*todo: titleBar3 t!tempTitle.equalsIgnoreCase(title.getText().toString())
                    || */!tempContent.equals(content.getHtml())) {
                    /*
                     * this "if" section evaluates if the title is empty or not
                     * it does not check whether the title has changed partially or entirely
                     * it just evaluates if it is empty or not
                     * and in case of no, the "else if" block will get the control
                     * which subsequently will either give the control to the "else" block
                     * or will finish the activity as appropriate*/
                /*todo : titleBar4 if (TextUtils.isEmpty(title.getText().toString())) {

                    final TextInputLayout editTitle =
                            (TextInputLayout) getLayoutInflater().inflate(R.layout.title_edittext, null);
                    *//*final TextInputEditText editTitle = view.findViewById(R.id.editTitle_inputEditText);*//*
                    final TextInputEditText textInputEditText = editTitle.findViewById(R.id.editTitle_inputEditText);

                    editTitle.setTypeface(typeface);
                    editTitle.setErrorEnabled(true);
                    editTitle.setHintEnabled(true);
                    editTitle.setElevation(6);
                    editTitle.setHint("Title");

                    FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.WRAP_CONTENT
                    );
                    lp.setMargins(getDipValue(this, 8f)
                            , 0
                            , getDipValue(this, 8f)
                            , 0);
                    textInputEditText.setLayoutParams(lp);

                    AlertDialog.Builder editTitleDialogBuilder =
                            new AlertDialog.Builder(this);
                    editTitleDialogBuilder.setMessage(getString(R.string.edit_title_in_dialog_builder));
                    editTitleDialogBuilder.setView(editTitle);
                    editTitleDialogBuilder.setPositiveButton(getString(R.string.save), null);
                    editTitleDialogBuilder.setNegativeButton(getString(R.string.Discard), null);

                    final AlertDialog alertDialog = apr.getRealDialog(editTitleDialogBuilder);
                    final Dialog fakeAlertDialog = apr.getFakeDialog();
                    apr.setBackgroundDrawable(fakeAlertDialog);
                    fakeAlertDialog.show();
                    alertDialog.show();
                    apr.clearDialogDimming(new Dialog[]{fakeAlertDialog, alertDialog});
                    alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            alertDialog.dismiss();
                            fakeAlertDialog.dismiss();
                        }
                    });
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (TextUtils.isEmpty(textInputEditText.getText().toString())) {
                                editTitle.setError(getString(R.string.warning_empty_title));
                            }
                            else if (TextUtils.isEmpty(content.getText().toString())){
                                checkEmptyContent(fakeAlertDialog, alertDialog);
                            }
                            else {
                                String currentTime = sdf.format(new Date());
                                editTextContent = content.getText().toString();
                                editTextTitle = textInputEditText.getText().toString();
                                dataBaseManager.updateEntry(id, editTextTitle, editTextContent, currentTime);
                                Toast.makeText(getApplicationContext()
                                        , getString(R.string.success_add)
                                        , Toast.LENGTH_SHORT
                                ).show();
                                alertDialog.dismiss();
                                fakeAlertDialog.dismiss();
                                NavUtils.navigateUpFromSameTask(AddingDataActivity.this);
                                finish();
                            }
                        }
                    });
                    alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                            fakeAlertDialog.dismiss();
                            finish();
                        }
                    });
                }*/
                    /*
                     * this else if section gets the control,
                     * when the check evaluates that the content is empty,
                     * it doesn't check whether the content has changed partially or entirely,
                     * it just evaluates if it is empty or not
                     * and in case of no, the else section will get the control*/
                    if (TextUtils.isEmpty(content.getText().toString())) {
                        checkEmptyContent();
                    }
                    /*
                     * this else section gets the control when none of the title or the content is empty
                     * and simply implies that either the title or the content has changed
                     * from it's initial state/value, but are not empty*/
                    else {
                        AlertDialog.Builder onTextChanged = new AlertDialog.Builder(AddingDataActivity.this);
                        onTextChanged.setMessage(getString(R.string.on_text_changed));
                        onTextChanged.setPositiveButton(getString(R.string.save), null);
                        onTextChanged.setNegativeButton(getString(R.string.Discard), null);

                        final AlertDialog onTextChangedRealALertDialog = utilityClass.DialogBlur(this, onTextChanged);

                        onTextChangedRealALertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                utilityClass.cancelDialog();
                            }
                        });
                        onTextChangedRealALertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String currentTime = sdf.format(new Date());
                                        /*todo: titleBar8 editTextTitle = title.getText().toString();*/
                                        editTextContent = content.getHtml();
                                        updateWorkaround(AddingDataActivity.this,
                                                id, tempTitle, content.getHtml(), currentTime, tempFolder);
                                        /*dataBaseManager.updateEntry(id, tempTitle, content.getHtml(), currentTime, tempFolder);*/
                                        Toast.makeText(getApplicationContext(), getString(R.string.success_update)
                                                , Toast.LENGTH_SHORT).show();
                                        NavUtils.navigateUpFromSameTask(AddingDataActivity.this);
                                        finish();
                                        Runtime.getRuntime().gc();
                                    }
                                }
                        );
                        onTextChangedRealALertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        utilityClass.cancelDialog();
                                        finish();
                                        Runtime.getRuntime().gc();
                                    }
                                }
                        );
                    }
                } else {
                    finish();
                    Runtime.getRuntime().gc();
                }
            } else {
                finish();
                Runtime.getRuntime().gc();
            }
        }else {
            checkContent();
        }
    }

    /*checkContent method will get called only if the activity is not being called to modify an existing data,
    * but to create and add a new data into the database*/

    private void checkContent(){
        if (!TextUtils.isEmpty(content.getText().toString())) {
            AlertDialog.Builder onNoteChanged = new AlertDialog.Builder(this);
            onNoteChanged.setMessage(getString(R.string.on_note_changed));
            onNoteChanged.setPositiveButton(getString(R.string.save), null);
            onNoteChanged.setNegativeButton(getString(R.string.Discard), null);

            final AlertDialog onNoteChangedrealAlertDialog = utilityClass.DialogBlur(this, onNoteChanged);

            onNoteChangedrealAlertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    utilityClass.cancelDialog();
                }
            });

            onNoteChangedrealAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            utilityClass.cancelDialog();

                            View titleView = getLayoutInflater().inflate(R.layout.edit_title_with_folder, null, false);
                            final TextInputLayout textInputLayout = titleView.findViewById(R.id.editTitle_inputLayout_withFolder);
                            final TextInputEditText textInputEditText = textInputLayout.findViewById(R.id.editTitle_inputEditText_withFolder);
                            final Spinner spinner = titleView.findViewById(R.id.folderSelector);

                            textInputLayout.setTypeface(typeface);
                            textInputEditText.setTypeface(typeface);
                            if (activityIdentifier == 1){
                                textInputEditText.setText(tempTitle);
                            }
                            setFolderSelectionAdapter(AddingDataActivity.this, spinner);

                            titleView.setBackgroundColor(getCyanea().getBackgroundColor());

                            final AlertDialog.Builder dialog = new AlertDialog.Builder(AddingDataActivity.this);
                            dialog.setView(titleView);

                            final AlertDialog realDialog = utilityClass.DialogBlur(AddingDataActivity.this, dialog);

                            MaterialButton positiveButton, negativeButton;
                            positiveButton = titleView.findViewById(R.id.custom_dialog_positive_button);
                            setTint(positiveButton);
                            negativeButton = titleView.findViewById(R.id.custom_dialog_negative_button);

                            realDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    utilityClass.cancelDialog();
                                }
                            });

                            positiveButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (TextUtils.isEmpty(textInputEditText.getText())) {
                                        textInputLayout.setError(getString(R.string.warning_empty_title));
                                    }
                                    else {
                                        if (activityIdentifier == -1) {
                                            /*boolean fileNameExists = false;
                                            for (content_class item : dataBaseManager.getAll()){
                                                if (item.getTitle().equalsIgnoreCase(textInputEditText.getText().toString()))
                                                    fileNameExists = true;
                                            }*/
                                            /*if (!fileNameExists) {*/
                                            Date date = new Date();
                                            String currentTime = sdf.format(date);
                                            editTextContent = content.getHtml();
                                            dataBaseManager.InsertData(textInputEditText.getText().toString(), content.getHtml(), currentTime,
                                                    spinner.getSelectedItem().toString());
                                            NavUtils.navigateUpFromSameTask(AddingDataActivity.this);
                                            Toast.makeText(getApplicationContext(), getString(R.string.success_add), Toast.LENGTH_SHORT).show();
                                            finish();
                                            Runtime.getRuntime().gc();
                                            /*}else textInputLayout.setError(getString(R.string.file_already_exists_with_the_same_name));*/
                                        }
                                        //this else section will never be called,
                                        else {
                                            Date date = new Date();
                                            String currentTime = sdf.format(date);
                                            editTextContent = content.getHtml();
                                            dataBaseManager.updateEntry(id, textInputEditText.getText().toString(), content.getHtml(),
                                                    currentTime, spinner.getSelectedItem().toString());
                                            NavUtils.navigateUpFromSameTask(AddingDataActivity.this);
                                            Toast.makeText(getApplicationContext(), getString(R.string.success_update), Toast.LENGTH_SHORT).show();
                                            finish();
                                            Runtime.getRuntime().gc();
                                        }
                                    }
                                }
                            });
                            negativeButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    utilityClass.cancelDialog();
                                    finish();
                                    Runtime.getRuntime().gc();
                                }
                            });
                        }
                    }
            );

            onNoteChangedrealAlertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finish();
                            Runtime.getRuntime().gc();
                        }
                    }
            );

        }
        else {
            finish();
            Runtime.getRuntime().gc();
        }
    }

    private void checkEmptyContent(){
        final AlertDialog realEmptyContentAlertDialog = utilityClass.simpleDialogBlur(
                this,
                getString(R.string.the_content_is_empty_confirmation),
                getString(R.string.save),
                getString(R.string.Discard),
                null
        );

        realEmptyContentAlertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                utilityClass.cancelDialog();
            }
        });

        realEmptyContentAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentTime = sdf.format(new Date());
                /*todo : titleBar5 editTextTitle = title.getText().toString();*/
                editTextContent = "";
                dataBaseManager.updateEntry(id, tempTitle, editTextContent, currentTime, tempFolder);
                Toast.makeText(getApplicationContext()
                        , getString(R.string.success_add)
                        , Toast.LENGTH_SHORT
                ).show();
                utilityClass.cancelDialog();
                NavUtils.navigateUpFromSameTask(AddingDataActivity.this);
                finish();
                Runtime.getRuntime().gc();
            }
        });
        realEmptyContentAlertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                utilityClass.cancelDialog();
                finish();
                Runtime.getRuntime().gc();
            }
        });
    }

    private int fetchAccentColor(){
        /*TypedValue typedValue = new TypedValue();
        TypedArray typedArray = this.obtainStyledAttributes(typedValue.data, new int[]{R.attr.colorAccent});
        int colorAccent = typedArray.getColor(0, ContextCompat.getColor(this, R.color.editorColorAccent));
        typedArray.recycle();*/
        return getCyanea().getAccent();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*Uri uri = data.getData();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            if (uri != null) {
                getContentResolver().
                        takePersistableUriPermission(uri,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION |
                                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
        }*/
        areToolbar.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Callback for the result from requesting permissions. This method
     * is invoked for every call on {@link #requestPermissions(String[], int)}.
     * <p>
     * <strong>Note:</strong> It is possible that the permissions request interaction
     * with the user is interrupted. In this case you will receive empty permissions
     * and results arrays which should be treated as a cancellation.
     * </p>
     *
     * @param requestCode  The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either {@link PackageManager#PERMISSION_GRANTED}
     *                     or {@link PackageManager#PERMISSION_DENIED}. Never null.
     * @see #requestPermissions(String[], int)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
