package com.me.android.noteeditor;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chinalwb.are.render.AreTextView;
import com.me.android.noteeditor.CommonUtility.utilityClass;
import com.me.android.noteeditor.fontManager.fontManager;
import com.me.android.noteeditor.permissionUtility.permissionUtility;
import com.jaredrummler.cyanea.app.CyaneaAppCompatActivity;

import java.io.File;
import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;

import static com.chinalwb.are.Util.getContentUri;
import static com.me.android.noteeditor.CommonUtility.utilityClass.getNotePictureDirectory;
import static com.me.android.noteeditor.CommonUtility.utilityClass.getNoteShareDirectory;
import static com.me.android.noteeditor.CommonUtility.utilityClass.getViewSnap;
import static com.me.android.noteeditor.CommonUtility.utilityClass.saveBitmap;
import static com.me.android.noteeditor.CommonUtility.utilityClass.textSize;
import static com.me.android.noteeditor.permissionUtility.permissionUtility.checkAllPermissions;

/**
 * Created by Amit on 9/23/2019.
 */

public class snip_preview extends CyaneaAppCompatActivity {

    private NestedScrollView nestedScrollView;

    private utilityClass utilityClass = new utilityClass();

    private fontManager fontManager;

    private SharedPreferences sharedPrefs;

    private Typeface typeface;

    private static int textsize, isSharable = -1;

    public static final int IMAGE_PREVIEW_PERMISSION_CODE = 74, TYPE_IMAGE = 1, TYPE_TEXT = 2, SHARABLE = 1, NOT_SHARABLE = 2;

    /**
     * Take care of popping the fragment back stack or finishing the activity
     * as appropriate.
     */
    @Override
    public void onBackPressed() {
        if (getIntent().getExtras().getInt("snip_type") == TYPE_IMAGE){
            Uri uri = Uri.parse(getIntent().getExtras().getString("snip_content"));
            getContentResolver().delete(uri, null, null);
        }
        finish();
        Runtime.getRuntime().gc();
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
       /* return super.onSupportNavigateUp();*/
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.snip_preview);

        Toolbar toolbar = findViewById(R.id.snip_layout_toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        @NonNull
                int type = getIntent().getExtras().getInt("snip_type");

        try{
            isSharable = getIntent().getExtras().getInt("can_share");
        }catch (Exception e){
            //empty
        }

        switch (type){
            case TYPE_TEXT :
                textPreview();
                break;
            case TYPE_IMAGE :
                imagePreview();
                break;
                default: break;
        }

        setUpSaveButton();

    }

    private void imagePreview(){
        findViewById(R.id.snip_preview_container).setVisibility(View.GONE);
        ImageView imageView = findViewById(R.id.snip_preview_image);

        @NonNull
        String s = getIntent().getExtras().getString("snip_content");

        Uri uri = Uri.parse(s);

        Glide
                .with(this)
                .load(uri)
                .placeholder(R.drawable.image_place_holder)
                .into(imageView);

    }

    private void textPreview(){

        ImageView snip_image = findViewById(R.id.snip_preview_image);
        snip_image.setVisibility(View.GONE);
        findViewById(R.id.snip_preview_image_container).setVisibility(View.GONE);

        @NonNull
        String content = getIntent().getExtras().getString("snip_content");

        AreTextView areTextView = findViewById(R.id.snip_preview_textView);
        nestedScrollView = findViewById(R.id.snip_preview_container);
        areTextView.setTypeface(typeface);
        areTextView.fromHtml(content);
        areTextView.setTextSize(textSize(textsize));

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == IMAGE_PREVIEW_PERMISSION_CODE){
            if (checkAllPermissions(grantResults)){
                saveAndShare();
            }
            else {
                if (permissionUtility.CheckRuntimePermission(this, permissions)
                        == permissionUtility.FLAG_RUNTIME_PERMISSION_DENIED_ONCE) {

                    final AlertDialog permissionDeniedRealRequest = utilityClass.simpleDialogBlur(this,
                            getString(R.string.permission_denied_upon_request_in_snip_preview),
                            getString(R.string.ask_again),
                            getString(R.string.exit),
                            null);

                    permissionDeniedRealRequest.setCancelable(false);

                    permissionDeniedRealRequest.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ActivityCompat.requestPermissions(snip_preview.this
                                            , new String[]{Manifest.permission.READ_EXTERNAL_STORAGE
                                                    , Manifest.permission.WRITE_EXTERNAL_STORAGE}, IMAGE_PREVIEW_PERMISSION_CODE);
                                    utilityClass.cancelDialog();
                                }
                            }
                    );
                    permissionDeniedRealRequest.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    utilityClass.cancelDialog();
                                    finish();
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
                                    finish();
                                }
                            }
                    );
                }
            }
        }
    }

    private void setUpSaveButton(){
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        String fonttype = sharedPrefs.getString(
                "font_preference_key",
                "Aller_Lt"
        );
        textsize = sharedPrefs.getInt(
                getString(R.string.custom_text_size_key),
                1
        );

        fontManager = new fontManager(this);
        fontManager.initializeFontManager();

        String pathToFont = null;
        try {
            pathToFont = fontManager.getFontPath(fonttype);
        } catch (IOException e) {
            e.printStackTrace();
        }
        typeface = Typeface.createFromAsset(getAssets(), pathToFont);

        TextView textView = findViewById(R.id.snip_button);
        textView.setBackgroundResource(R.drawable.scrim);
        if (isSharable == SHARABLE ) textView.setText(getString(R.string.save_and_share));
        else if (isSharable == NOT_SHARABLE)textView.setText(getString(R.string.save));
        textView.setTypeface(typeface);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23){
                    if (ContextCompat.checkSelfPermission(snip_preview.this
                            , Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                            || ContextCompat.checkSelfPermission(snip_preview.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {

                        if (permissionUtility.ShowPermissionRationale(snip_preview.this, Manifest.permission.READ_EXTERNAL_STORAGE
                                , Manifest.permission.WRITE_EXTERNAL_STORAGE)){

                            final AlertDialog requestRealDialog = utilityClass.simpleDialogBlur(snip_preview.this,
                                    getString(R.string.permission_denied),
                                    getString(R.string.ask),
                                    getString(R.string.exit),
                                    null);
                            requestRealDialog.setCancelable(false);

                            requestRealDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            ActivityCompat.requestPermissions(snip_preview.this
                                                    , new String[]{Manifest.permission.READ_EXTERNAL_STORAGE
                                                            , Manifest.permission.WRITE_EXTERNAL_STORAGE}
                                                    , IMAGE_PREVIEW_PERMISSION_CODE);
                                            utilityClass.cancelDialog();
                                        }
                                    }
                            );
                            requestRealDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            utilityClass.cancelDialog();
                                            finish();
                                        }
                                    }
                            );
                        }else {
                            if (permissionUtility.CheckPermissionFlag(snip_preview.this)
                                    == permissionUtility.FLAG_FIRST_TIME_PERMISSION_REQUEST){
                                ActivityCompat.requestPermissions(snip_preview.this,
                                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE
                                                , Manifest.permission.WRITE_EXTERNAL_STORAGE}, IMAGE_PREVIEW_PERMISSION_CODE);
                            }else {
                                final AlertDialog realNeverAskDialog = utilityClass.simpleDialogBlur(snip_preview.this,
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
                                                finish();
                                            }
                                        }
                                );
                            }
                        }
                    } else {
                        saveAndShare();
                    }
                }else {
                    saveAndShare();
                }
            }
        });

    }

    private void saveAndShare(){

        //ever since the requirement has changed, this if block will not be called by any class,
        // and only the else statement will be executed
        if (getIntent().getExtras().getInt("snip_type") == TYPE_IMAGE){

            Uri uri = Uri.parse(getIntent().getExtras().getString("snip_content"));
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            File file = saveBitmap(bitmap, snip_preview.this, getNoteShareDirectory());
            Uri uri1 = getContentUri(snip_preview.this, file);

            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("image/jpeg");
            share.putExtra(Intent.EXTRA_STREAM, uri1);
            startActivity(Intent.createChooser(share,
                    getString(R.string.shareImages)));
            getContentResolver().delete(uri, null, null);
            finish();
            Runtime.getRuntime().gc();
        }else {
            Bitmap bitmap = getViewSnap(nestedScrollView);
            File file = saveBitmap(bitmap, snip_preview.this,
                    isSharable == SHARABLE ? getNoteShareDirectory() : getNotePictureDirectory());
            if (isSharable == SHARABLE) {
                Uri uri = getContentUri(snip_preview.this, file);
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("image/jpeg");
                share.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(Intent.createChooser(share,
                        getString(R.string.shareImages)));
            }
            finish();
            Runtime.getRuntime().gc();
        }
    }
}
