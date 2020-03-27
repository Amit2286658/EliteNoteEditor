package com.me.android.noteeditor;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.me.android.noteeditor.snip_preview;
import com.me.android.noteeditor.BlurUtility.ApplyBlurOnDialog;
import com.me.android.noteeditor.CommonUtility.utilityClass;
import com.me.android.noteeditor.Recyclerview_adapter.EditingContentAdapter;
import com.me.android.noteeditor.Recyclerview_adapter.commonDataModel.EditingContentModelData;
import com.me.android.noteeditor.contract.DataBaseManager;
import com.me.android.noteeditor.fontManager.fontManager;
import com.me.android.noteeditor.permissionUtility.permissionUtility;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.jaredrummler.cyanea.Cyanea;
import com.jaredrummler.cyanea.app.CyaneaAppCompatActivity;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NavUtils;
import androidx.core.app.TaskStackBuilder;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.me.android.noteeditor.CommonUtility.utilityClass.cutContent;
import static com.me.android.noteeditor.CommonUtility.utilityClass.getNoteHtmlDirectory;
import static com.me.android.noteeditor.CommonUtility.utilityClass.getNoteTextDirectory;
import static com.me.android.noteeditor.CommonUtility.utilityClass.getVideoAndImageAndHtmlTextSeperately;
import static com.me.android.noteeditor.CommonUtility.utilityClass.setFrameLayoutParams;
import static com.me.android.noteeditor.CommonUtility.utilityClass.setTint;
import static com.me.android.noteeditor.CommonUtility.utilityClass.writeToFile;
import static com.me.android.noteeditor.permissionUtility.permissionUtility.checkAllPermissions;

public class editingActivity extends CyaneaAppCompatActivity {

    final private ApplyBlurOnDialog apr = new ApplyBlurOnDialog(this);
    private final utilityClass utilityClass =new utilityClass();

    String title, content, time, foldername;
    int id, PERMISSIONS_REQUEST_CODE = 18032001;

    /*private AreTextView TextViewContent;*/
    private RecyclerView recyclerView;

    private static boolean isTitleChanged = false;

    private static final int WRITE_TO_FILE_PERMISSION_CODE = 1001, SHARE_FILE_PERMISSION_CODE = 1208;

    private Toolbar toolbar;

    Typeface typeface;

    private DataBaseManager dataBaseManager = new DataBaseManager(this);

    /**
     * Take care of popping the fragment back stack or finishing the activity
     * as appropriate.
     */
    @Override
    public void onBackPressed() {
        if (isTitleChanged){
            NavUtils.navigateUpFromSameTask(this);
            finish();
            Runtime.getRuntime().gc();
        }else {
            finish();
            Runtime.getRuntime().gc();
        }
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
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editing);

        toolbar = findViewById(R.id.editingActivityToolBar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        title = getIntent().getExtras().getString(getString(R.string.key_value_title));
        content = getIntent().getExtras().getString(getString(R.string.key_value_content));
        id = getIntent().getExtras().getInt(getString(R.string.key_value_id));
        foldername = getIntent().getExtras().getString(getString(R.string.key_value_folderName));
        try {
            time = getFormattedDate();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        final AdView adView = (AdView) findViewById(R.id.adView);
        adView.post(new Runnable() {
            @Override
            public void run() {
                setUpAds(adView);
            }
        });
        setUp();
        setUpMenuImageButtons();

        if (id != -1) {
            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(editingActivity.this, AddingDataActivity.class);
                    intent.putExtra(getString(R.string.key_data_id), id);
                    intent.putExtra(getString(R.string.key_data_title), title);
                    intent.putExtra(getString(R.string.key_data_content), content);
                    intent.putExtra(getString(R.string.key_data_folderName), foldername);
                    intent.putExtra(getString(R.string.key_activity_identifier), 1);
                    startActivity(intent);
                }
            });
            setTint(this, R.drawable.ic_edit_black_24dp, fab);
        }else findViewById(R.id.fab).setVisibility(View.GONE);
    }

    private void setUpAds(AdView adView){

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {

            }
        });
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    private void setUp(){

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        fontManager fontManager = new fontManager(this);
        fontManager.initializeFontManager();

        int textSize = sharedPreferences.getInt(
                getString(R.string.custom_text_size_key),
                1
        );
        String fontType = sharedPreferences.getString(
                getString(R.string.font_preference_key),
                "Aller_Lt"
        );
        int transparency = sharedPreferences.getInt(
                getString(R.string.cardView_transparency_key), 0
        );

        String path = null;
        try {
            path = fontManager.getFontPath(fontType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*= Typeface.createFromAsset(this.getAssets(),"fonts/roboto_mono.ttf");*/
        typeface = Typeface.createFromAsset(getAssets(), path);

        /*Toolbar toolbar = findViewById(R.id.editingActivityToolBar);
        toolbar.setTitle(title);*/

        final CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.editingActivityCollapsingToolbar);
        collapsingToolbarLayout.setTitle(title);
        collapsingToolbarLayout.setContentScrimColor(Cyanea.getInstance().getPrimary());
        collapsingToolbarLayout.setBackgroundColor(Cyanea.getInstance().getPrimary());
        collapsingToolbarLayout.setExpandedTitleTextAppearance(
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ?
                android.R.style.TextAppearance_Material_Large :
                R.style.TextAppearanceLarge
        );

        final TextView collapsingToolbarTime = findViewById(R.id.editingActivityCollapsingToolbarTextView);
        collapsingToolbarTime.setText(time);
        //final NestedScrollView nestedScrollView = findViewById(R.id.editingNestedScrollView);


        /*TextViewContent = (AreTextView) findViewById(R.id.editActivityContent);
        TextViewContent.setTypeface(typeface);
        TextViewContent.fromHtml(content);
        TextViewContent.setTextSize(textSize(textSize));*/
        final ProgressBar progressBar = findViewById(R.id.editing_activity_loader_progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        recyclerView = findViewById(R.id.editRecyclerView);
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                ArrayList<EditingContentModelData> stringList = getVideoAndImageAndHtmlTextSeperately(cutContent(content));

                EditingContentAdapter editingContentAdapter = new EditingContentAdapter(stringList, editingActivity.this);
                //editingContentAdapter.setHasStableIds(true);

                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(editingActivity.this);/*{

                    private static final float SPEED = 300f;

                    @Override
                    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
                        LinearSmoothScroller linearSmoothScroller = new LinearSmoothScroller(editingActivity.this){
                            @Override
                            protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                                return SPEED/displayMetrics.densityDpi;
                            }
                        };
                        linearSmoothScroller.setTargetPosition(position);
                        startSmoothScroll(linearSmoothScroller);
                    }
                }*/

                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerView.setAdapter(editingContentAdapter);
                //recyclerView.setHasFixedSize(true);
                //recyclerView.setNestedScrollingEnabled(false);
                progressBar.setVisibility(View.GONE);
            }
        });

        AppBarLayout appBarLayout = findViewById(R.id.editingActivityAppBar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.BaseOnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
                if(Math.abs(i) == appBarLayout.getTotalScrollRange()){
                    recyclerView.setPadding(0,0,0,0);
                }else if (i == 0){
                    recyclerView.setPadding(0,8,0,0);
                    appBarLayout.setElevation(6);
                }else {
                    recyclerView.setPadding(0,8,0,0);
                    appBarLayout.setElevation(6);

                }
            }
        });

        /*CardView cardView = findViewById(R.id.editing_activity_card_view);
        cardView.getBackground().setAlpha(alphaProvider(transparency));*/
    }

    private void setUpMenuImageButtons(){
        final MaterialButton delete, editTitle, shareNote, write_to_file;
        delete = findViewById(R.id.delete_editingActivity_image);
        editTitle = findViewById(R.id.editTitle_editingActivity_image);
        shareNote = findViewById(R.id.shareNote_editingActivity_image);
        write_to_file = findViewById(R.id.write_to_file_editingActivity_image);

        if (id == -1){
            delete.setVisibility(View.GONE);
            editTitle.setVisibility(View.GONE);
            write_to_file.setVisibility(View.GONE );
        }

        /*onClick listeners for the buttons above*/
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog deleteDialog = utilityClass.simpleDialogBlur(editingActivity.this,
                        getString(R.string.confirm_delete_message),
                        getString(R.string.confirm_delete),
                        getString(R.string.confirm_cancel),
                        null);

                deleteDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        utilityClass.cancelDialog();
                    }
                });

                deleteDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dataBaseManager.deleteEntry(id);
                        Toast.makeText(editingActivity.this, getString(R.string.success_delete), Toast.LENGTH_SHORT).show();
                        NavUtils.navigateUpFromSameTask(editingActivity.this);
                        utilityClass.cancelDialog();
                        finish();
                    }
                });
                deleteDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        utilityClass.cancelDialog();
                    }
                });
            }
        });

        editTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final TextInputLayout editTitle =
                        (TextInputLayout) getLayoutInflater().inflate(R.layout.title_edittext, null);
                final TextInputEditText textInputEditText = editTitle.findViewById(R.id.editTitle_inputEditText);

                editTitle.setTypeface(typeface);
                editTitle.setErrorEnabled(true);
                editTitle.setHintEnabled(true);
                editTitle.setElevation(6);
                editTitle.setHint("Title");

                setFrameLayoutParams(editingActivity.this, textInputEditText, 8f);
                textInputEditText.setText(title);
                textInputEditText.setTypeface(typeface);


                AlertDialog.Builder editTitleDialog = new AlertDialog.Builder(editingActivity.this);
                editTitleDialog.setView(editTitle);
                editTitleDialog.setMessage(getString(R.string.edit_title_in_dialog_builder));
                editTitleDialog.setPositiveButton(getString(R.string.save), null);
                editTitleDialog.setNegativeButton(getString(R.string.Discard), null);

                final AlertDialog editTitleAlert_dialog = utilityClass.DialogBlur(editingActivity.this, editTitleDialog);

                editTitleAlert_dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        utilityClass.cancelDialog();
                    }
                });

                editTitleAlert_dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (TextUtils.isEmpty(textInputEditText.getText().toString())){
                            editTitle.setError(getString(R.string.warning_empty_title));
                        }else {
                            /*boolean fileNameExists = false;
                            String initialTitle = dataBaseManager.getSingleData(id).getTitle();
                            if (!initialTitle.equals(textInputEditText.getText().toString())) {
                                for (content_class item : dataBaseManager.getAll()) {
                                    if (item.getTitle().equalsIgnoreCase(textInputEditText.getText().toString()))
                                        fileNameExists = true;
                                }
                            }*/
                            //if (!fileNameExists) {
                            SimpleDateFormat sdf = new SimpleDateFormat("MMM/dd/yyyy hh:mm aa", Locale.getDefault());
                            String currentTime = sdf.format(new Date());
                            String newTitle = textInputEditText.getText().toString();
                            dataBaseManager.updateEntry(id, newTitle, content, currentTime, foldername);
                            Toast.makeText(getApplicationContext(), getString(R.string.success_update), Toast.LENGTH_SHORT)
                                    .show();
                            utilityClass.cancelDialog();
                            isTitleChanged = true;
                            finish();

                            Intent intent = new Intent(editingActivity.this, editingActivity.class);
                            intent.putExtra(getString(R.string.key_value_id), id);
                            intent.putExtra(getString(R.string.key_value_title), newTitle);
                            intent.putExtra(getString(R.string.key_value_content), content);
                            intent.putExtra(getString(R.string.key_value_time),
                                    getIntent().getExtras().getString(getString(R.string.key_value_time)));
                            startActivity(intent);
                            //}else {
                              //  editTitle.setError(getString(R.string.file_already_exists_with_the_same_name));
                            //}
                        }
                    }
                });
                editTitleAlert_dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        utilityClass.cancelDialog();
                    }
                });
            }
        });

        shareNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= 23){

                    if (ContextCompat.checkSelfPermission(editingActivity.this
                            , Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                            || ContextCompat.checkSelfPermission(editingActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {

                        if (permissionUtility.ShowPermissionRationale(editingActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE
                                , Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                    /*shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
                                    || shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)*/){

                            final AlertDialog requestRealDialog = utilityClass.simpleDialogBlur(editingActivity.this,
                                    getString(R.string.permission_denied),
                                    getString(R.string.ask),
                                    getString(R.string.exit),
                                    null);
                            requestRealDialog.setCancelable(false);

                            requestRealDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            ActivityCompat.requestPermissions(editingActivity.this
                                                    , new String[]{Manifest.permission.READ_EXTERNAL_STORAGE
                                                            , Manifest.permission.WRITE_EXTERNAL_STORAGE}
                                                    , SHARE_FILE_PERMISSION_CODE);
                                            utilityClass.cancelDialog();
                                        }
                                    }
                            );
                            requestRealDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            utilityClass.cancelDialog();
                                        }
                                    }
                            );
                        }else {
                            if (permissionUtility.CheckPermissionFlag(editingActivity.this)
                                    == permissionUtility.FLAG_FIRST_TIME_PERMISSION_REQUEST
                                /*check == 0*/){
                                ActivityCompat.requestPermissions(editingActivity.this,
                                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE
                                                , Manifest.permission.WRITE_EXTERNAL_STORAGE}, SHARE_FILE_PERMISSION_CODE);
                            }else {
                                final AlertDialog realNeverAskDialog = utilityClass.simpleDialogBlur(editingActivity.this,
                                        getString(R.string.message_to_ask_to_allow_permission_from_settings),
                                        getString(R.string.OK),
                                        null,
                                        null);
                                realNeverAskDialog.setCancelable(false);

                                realNeverAskDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                utilityClass.cancelDialog();
                                            }
                                        }
                                );
                            }
                        }
                    } else {
                        shareFiles();
                    }
                }else {
                    shareFiles();
                }

            }
        });

        write_to_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23){

                    if (ContextCompat.checkSelfPermission(editingActivity.this
                            , Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                            || ContextCompat.checkSelfPermission(editingActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {

                        if (permissionUtility.ShowPermissionRationale(editingActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE
                                , Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                    /*shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
                                    || shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)*/){

                            final AlertDialog requestRealDialog = utilityClass.simpleDialogBlur(editingActivity.this,
                                    getString(R.string.permission_denied),
                                    getString(R.string.ask),
                                    getString(R.string.exit),
                                    null);
                            requestRealDialog.setCancelable(false);

                            requestRealDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            ActivityCompat.requestPermissions(editingActivity.this
                                                    , new String[]{Manifest.permission.READ_EXTERNAL_STORAGE
                                                            , Manifest.permission.WRITE_EXTERNAL_STORAGE}
                                                    , WRITE_TO_FILE_PERMISSION_CODE);
                                            utilityClass.cancelDialog();
                                        }
                                    }
                            );
                            requestRealDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            utilityClass.cancelDialog();
                                        }
                                    }
                            );
                        }else {
                            if (permissionUtility.CheckPermissionFlag(editingActivity.this)
                                    == permissionUtility.FLAG_FIRST_TIME_PERMISSION_REQUEST
                                /*check == 0*/){
                                ActivityCompat.requestPermissions(editingActivity.this,
                                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE
                                                , Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_TO_FILE_PERMISSION_CODE);
                            }else {
                                final AlertDialog realNeverAskDialog = utilityClass.simpleDialogBlur(editingActivity.this,
                                        getString(R.string.message_to_ask_to_allow_permission_from_settings),
                                        getString(R.string.OK),
                                        null,
                                        null);
                                realNeverAskDialog.setCancelable(false);

                                realNeverAskDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                utilityClass.cancelDialog();
                                            }
                                        }
                                );
                            }
                        }
                    } else {
                        saveFiles();
                    }
                }else {
                    saveFiles();
                }
            }
        });
    }

    private void shareFiles(){

        View view = getLayoutInflater().inflate(R.layout.share_layout, null, false);
        View share_as_picture, share_as_html_text, divider;
        share_as_picture = view.findViewById(R.id.share_as_picture);
        share_as_html_text = view.findViewById(R.id.share_as_html_text);
        divider = view.findViewById(R.id.share_layout_divider);

        share_as_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*NestedScrollView nestedScrollView = findViewById(R.id.editingNestedScrollView);

                String s = getContentUri(
                        editingActivity.this,
                        saveBitmap(
                                getViewSnap(nestedScrollView),
                                editingActivity.this,
                                getNoteTempDirectory())).
                        toString();

                Intent i = new Intent();
                i.setClass(editingActivity.this, snip_preview.class);
                i.putExtra("snip_type", snip_preview.TYPE_IMAGE);
                i.putExtra("snip_content", s);
                editingActivity.this.startActivity(i);*/
                        /*NestedScrollView nestedScrollView = findViewById(R.id.editingNestedScrollView);
                        Bitmap bitmap = getViewSnap(nestedScrollView);
                        File file = saveBitmap(bitmap, editingActivity.this, getNoteShareDirectory());
                        Uri uri = getContentUri(editingActivity.this, file);
                        Intent share = new Intent(Intent.ACTION_SEND);
                        share.setType("image/jpeg");
                        share.putExtra(Intent.EXTRA_STREAM, uri);
                        startActivity(Intent.createChooser(share, getString(R.string.shareImages)));
                        utilityClass.cancelDialog();*/

                Intent i = new Intent();
                i.setClass(editingActivity.this, snip_preview.class);
                i.putExtra("snip_content", content);
                i.putExtra("snip_type", snip_preview.TYPE_TEXT);
                i.putExtra("can_share", snip_preview.SHARABLE);
                editingActivity.this.startActivity(i);

                utilityClass.cancelDialog();
            }
        });
        share_as_html_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                intent.setType("text/plain");
                String shareNoteTitle = title;
                String shareNoteContent = content;
                intent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareNoteTitle );
                intent.putExtra(android.content.Intent.EXTRA_TEXT,  shareNoteContent );
                startActivity(Intent.createChooser(intent, "share via" ));
                utilityClass.cancelDialog();
            }
        });
        if (content.contains("<img src") || content.contains("<video src")){
            share_as_html_text.setVisibility(View.GONE);
            share_as_picture.setVisibility(View.VISIBLE);
            divider.setVisibility(View.GONE);
        }else{
            share_as_html_text.setVisibility(View.VISIBLE);
            share_as_picture.setVisibility(View.VISIBLE);
            divider.setVisibility(View.VISIBLE);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(editingActivity.this);
        builder.setView(view);

        AlertDialog alertDialog = utilityClass.DialogBlur(editingActivity.this, builder);
        alertDialog.getWindow().setGravity(Gravity.BOTTOM);

        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                utilityClass.cancelDialog();
            }
        });

    }

    private void saveFiles(){
        View view = getLayoutInflater().inflate(R.layout.save, null, false);
        View l1, l2, l3;

        l1 = view.findViewById(R.id.save_as_picture);
        l2 = view.findViewById(R.id.save_as_html);
        l3 = view.findViewById(R.id.save_as_text);

        l1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setClass(editingActivity.this, snip_preview.class);
                i.putExtra("snip_content", content);
                i.putExtra("snip_type", snip_preview.TYPE_TEXT);
                i.putExtra("can_share", snip_preview.NOT_SHARABLE);
                editingActivity.this.startActivity(i);
                utilityClass.cancelDialog();
                /*NestedScrollView nestedScrollView = findViewById(R.id.editingNestedScrollView);
                Bitmap bitmap = getViewSnap(nestedScrollView);
                saveBitmap(bitmap, editingActivity.this, getNotePictureDirectory());
                utilityClass.cancelDialog();*/
            }
        });
        l2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeToFile(editingActivity.this, content, getNoteHtmlDirectory(), title, ".html");
                utilityClass.cancelDialog();
            }
        });
        l3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder s = new StringBuilder();
                if (cutContent(content).size() != 0){
                    ArrayList<EditingContentModelData> modelData = getVideoAndImageAndHtmlTextSeperately(cutContent(content));
                    /*ArrayList<String> str = cutContent(content);
                    for (String st : str){
                        s.append(st).append("\n");
                    }*/
                    for (EditingContentModelData item : modelData){
                        if (item.getHtml_text() != null) s.append(item.getHtml_text()).append("\n");
                        if (item.getImage_link() != null) s.append(item.getImage_link()).append("\n");
                        if (item.getImage_uri() != null) s.append(item.getImage_uri().toString()).append("\n");
                        if (item.getVideo_link() != null) s.append(item.getVideo_link()).append("\n");
                    }
                }
                writeToFile(editingActivity.this, s.toString(), getNoteTextDirectory(), title, ".txt");
                utilityClass.cancelDialog();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(editingActivity.this);
        builder.setView(view);
        builder.setPositiveButton(getString(R.string.confirm_cancel), null);
        builder.setTitle(getString(R.string.saveAs));

        AlertDialog alertDialog = utilityClass.DialogBlur(editingActivity.this, builder);

        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                utilityClass.cancelDialog();
            }
        });

        alertDialog.getButton(Dialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                utilityClass.cancelDialog();
            }
        });
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

        if (requestCode == WRITE_TO_FILE_PERMISSION_CODE){
            if (checkAllPermissions(grantResults)){
                saveFiles();
            }
            else {
                if (permissionUtility.CheckRuntimePermission(this, permissions)
                        == permissionUtility.FLAG_RUNTIME_PERMISSION_DENIED_ONCE) {

                    final AlertDialog permissionDeniedRealRequest = utilityClass.simpleDialogBlur(this,
                            getString(R.string.permission_denied_upon_request),
                            getString(R.string.ask_again),
                            getString(R.string.exit),
                            null);

                    permissionDeniedRealRequest.setCancelable(false);

                    permissionDeniedRealRequest.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ActivityCompat.requestPermissions(editingActivity.this
                                            , new String[]{Manifest.permission.READ_EXTERNAL_STORAGE
                                                    , Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_TO_FILE_PERMISSION_CODE);
                                    utilityClass.cancelDialog();
                                }
                            }
                    );
                    permissionDeniedRealRequest.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    utilityClass.cancelDialog();
                                }
                            }
                    );
                } else {
                    final AlertDialog realNeverAskDialog = utilityClass.simpleDialogBlur(this,
                            getString(R.string.permanently_disabled_permissions),
                            getString(R.string.OK),
                            null,
                            null);
                    realNeverAskDialog.setCancelable(false);

                    realNeverAskDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    utilityClass.cancelDialog();
                                }
                            }
                    );
                }
            }
        }

        if (requestCode == SHARE_FILE_PERMISSION_CODE){
            if (checkAllPermissions(grantResults)){
                shareFiles();
            }
            else {
                if (permissionUtility.CheckRuntimePermission(this, permissions)
                        == permissionUtility.FLAG_RUNTIME_PERMISSION_DENIED_ONCE) {

                    final AlertDialog permissionDeniedRealRequest = utilityClass.simpleDialogBlur(this,
                            getString(R.string.permission_denied_upon_request),
                            getString(R.string.ask_again),
                            getString(R.string.exit),
                            null);

                    permissionDeniedRealRequest.setCancelable(false);

                    permissionDeniedRealRequest.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ActivityCompat.requestPermissions(editingActivity.this
                                            , new String[]{Manifest.permission.READ_EXTERNAL_STORAGE
                                                    , Manifest.permission.WRITE_EXTERNAL_STORAGE}, SHARE_FILE_PERMISSION_CODE);
                                    utilityClass.cancelDialog();
                                }
                            }
                    );
                    permissionDeniedRealRequest.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    utilityClass.cancelDialog();
                                }
                            }
                    );
                } else {
                    final AlertDialog realNeverAskDialog = utilityClass.simpleDialogBlur(this,
                            getString(R.string.permanently_disabled_permissions),
                            getString(R.string.OK),
                            null,
                            null);
                    realNeverAskDialog.setCancelable(false);

                    realNeverAskDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    utilityClass.cancelDialog();
                                }
                            }
                    );
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.editingactivitymenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Prepare the Screen's standard options menu to be displayed.  This is
     * called right before the menu is shown, every time it is shown.  You can
     * use this method to efficiently enable/disable items or otherwise
     * dynamically modify the contents.
     *
     * <p>The default implementation updates the system menu items based on the
     * activity's state.  Deriving classes should always call through to the
     * base class implementation.
     *
     * @param menu The options menu as last shown or first initialized by
     *             onCreateOptionsMenu().
     * @return You must return true for the menu to be displayed;
     * if you return false it will not be shown.
     * @see #onCreateOptionsMenu
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        invalidateOptionsMenu();
        if (id == -1){
           /* menu.findItem(R.id.deleteAction).setEnabled(false);
            menu.findItem(R.id.EditTitleAction).setEnabled(false);*/
            return true;
        }else return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId){
            case R.id.deleteAction :

                return true;
            case R.id.EditTitleAction :
                /*View mainView = getLayoutInflater().inflate(R.layout.edit_title_with_folder, null, false);
                final TextInputLayout textInputLayout = mainView.findViewById(R.id.editTitle_inputLayout_withFolder);
                final TextInputEditText textInputEditText = textInputLayout.findViewById(R.id.editTitle_inputEditText_withFolder);

                textInputLayout.setTypeface(typeface);
                textInputEditText.setTypeface(typeface);

                final Spinner spinner = mainView.findViewById(R.id.folderSelector);

                setFrameLayoutParams(this, textInputEditText, 8f);

                setLinearLayoutParams(this, spinner, 8f);

                setFolderSelectionAdapter(this, spinner);

                AlertDialog saveDialog =
                        customSaveDialogUtility.setSaveDialog(this, this, mainView, getString(R.string.edit_title_in_dialog_builder));
                saveDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        customSaveDialogUtility.closeSaveDialogs();
                    }
                });
                saveDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (TextUtils.isEmpty(textInputEditText.getText().toString())){
                            textInputLayout.setError(getString(R.string.warning_empty_title));
                        }else {
                            SimpleDateFormat sdf = new SimpleDateFormat("MMM/dd/yyyy hh:mm aa", Locale.getDefault());
                            String currentTime = sdf.format(new Date());
                            String newTitle = textInputEditText.getText().toString();
                            dataBaseManager.updateEntry(id, newTitle, content, currentTime, spinner.getSelectedItem().toString());
                            Toast.makeText(getApplicationContext(), getString(R.string.success_update), Toast.LENGTH_SHORT)
                                    .show();
                            finish();

                            Intent intent = new Intent(editingActivity.this, editingActivity.class);
                            intent.putExtra(getString(R.string.key_value_id), id);
                            intent.putExtra(getString(R.string.key_value_title), newTitle);
                            intent.putExtra(getString(R.string.key_value_content), content);
                            intent.putExtra(getString(R.string.key_value_time),
                                    getIntent().getExtras().getString(getString(R.string.key_value_time)));
                            startActivity(intent);

                            isTitleChanged = true;
                        }
                    }
                });
                saveDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        customSaveDialogUtility.closeSaveDialogs();
                    }
                });*/



                return true;
            case R.id.share_note :

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private String getFormattedDate() throws ParseException, NullPointerException {
        String localTime;
        Date dt;
        SimpleDateFormat sdf = new SimpleDateFormat("MMM/dd/yyyy hh:mm aa", Locale.getDefault());

        dt = sdf.parse(getIntent().getExtras().getString(getString(R.string.key_value_time)));

        SimpleDateFormat sdf1 = new SimpleDateFormat("EEEE MMMM dd, yyyy hh:mm aa", Locale.getDefault());
        localTime = sdf1.format(dt);
        return localTime;
    }

    /*private int fetchPrimaryColor(){
        TypedValue typedValue = new TypedValue();
        TypedArray typedArray = this.obtainStyledAttributes(typedValue.data, new int[]{R.attr.colorPrimary});
        int colorAccent = typedArray.getColor(0, ContextCompat.getColor(this, R.color.colorPrimary));
        typedArray.recycle();

        return colorAccent;
    }*/
}

/**/
