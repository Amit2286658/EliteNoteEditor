package com.me.android.noteeditor;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.me.android.noteeditor.CommonUtility.searchViewUtility;
import com.me.android.noteeditor.CommonUtility.utilityClass;
import com.me.android.noteeditor.FragmentAdapter.fragment_adapter;
import com.me.android.noteeditor.customListener.Open_external_file_listener;
import com.me.android.noteeditor.fileProviderUtility.FileProvider;
import com.me.android.noteeditor.permissionUtility.permissionUtility;
import com.google.android.material.tabs.TabLayout;
import com.jaredrummler.cyanea.Cyanea;
import com.jaredrummler.cyanea.app.CyaneaAppCompatActivity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import static com.me.android.noteeditor.CommonUtility.utilityClass.colorSimilarity;
import static com.me.android.noteeditor.permissionUtility.permissionUtility.checkAllPermissions;

public class MainActivity extends CyaneaAppCompatActivity implements Cyanea.ThemeModifiedListener {

    private static themeModifedListener mThemeModifiedListener;
    private static final int REQUEST_EXTERNAL_FILE_CODE = 1;
    private static final int PERMISSIONS_REQUEST_CODE = 23;
    private static final int EXTERNAL_FILE_ID = -1;
    private Uri uri;
    private FileProvider fileProvider = new FileProvider();
    private final utilityClass utilityClass = new utilityClass();

    @Override
    public void onBackPressed() {
        if (searchViewUtility.isSearchViewAdded()){
            searchViewUtility.onBackPressed(this, false);
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(getCyanea().getPrimary());

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        if (getCyanea().getAccent() == getCyanea().getPrimary()){
            int c1 = getCyanea().getPrimary();
            int c2 = Color.parseColor("#FFFFFFFF");
            if (colorSimilarity(c1, c2)){
                tabLayout.setSelectedTabIndicatorColor(Color.BLACK);
                tabLayout.setTabTextColors(Color.BLACK, Color.BLACK);
            }else {
                tabLayout.setSelectedTabIndicatorColor(Color.WHITE);
                tabLayout.setTabTextColors(Color.WHITE, Color.WHITE);
            }
        }
        int c1 = getCyanea().getAccent();
        int c2 = getCyanea().getPrimary();
        if (colorSimilarity(c1, c2)){
            int c2_1 = Color.parseColor("#FFFFFFFF");
            if (colorSimilarity(c2, c2_1)){
                tabLayout.setSelectedTabIndicatorColor(Color.BLACK);
                tabLayout.setTabTextColors(Color.BLACK, Color.BLACK);
            }else {
                tabLayout.setSelectedTabIndicatorColor(Color.WHITE);
                tabLayout.setTabTextColors(Color.WHITE, Color.WHITE);
            }
        }

        ViewPager viewPager = findViewById(R.id.main_viewpager_id);
        fragment_adapter fragment_adapter = new fragment_adapter(getSupportFragmentManager());
        viewPager.setAdapter(fragment_adapter);

        tabLayout.setupWithViewPager(viewPager);

        /*if (getCyanea().isThemeModified()){
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(getString(R.string.customThemeChangekey), 0).apply();
        }*/

        open_file();
        fileProvider.initiate(this);

    }

    private void open_file(){
        Open_external_file_listener.setOpenFileListener(new Open_external_file_listener.openFile() {
            @Override
            public void onOpenFIle(boolean b) {
                if (b) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("text/plain");
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(intent, REQUEST_EXTERNAL_FILE_CODE);
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case REQUEST_EXTERNAL_FILE_CODE :
                if (resultCode == Activity.RESULT_OK){
                    uri = data.getData();
                    if (Build.VERSION.SDK_INT >= 23) {
                        if (ContextCompat.checkSelfPermission(this
                                , Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {

                            if (permissionUtility.ShowPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE
                                    , Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                                AlertDialog.Builder requestRationaleDialog = new AlertDialog.Builder(this);
                                requestRationaleDialog.setMessage( getString(R.string.permission_denied));
                                requestRationaleDialog.setPositiveButton(getString(R.string.ask), null);
                                requestRationaleDialog.setNegativeButton(getString(R.string.exit), null);


                                final AlertDialog requestRealDialog = utilityClass.DialogBlur(this, requestRationaleDialog);
                                requestRealDialog.setCancelable(false);
                                requestRealDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                ActivityCompat.requestPermissions(MainActivity.this
                                                        , new String[]{Manifest.permission.READ_EXTERNAL_STORAGE
                                                                , Manifest.permission.WRITE_EXTERNAL_STORAGE}
                                                        , PERMISSIONS_REQUEST_CODE);
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
                                if (permissionUtility.CheckPermissionFlag(this)
                                        == permissionUtility.FLAG_FIRST_TIME_PERMISSION_REQUEST
                                    /*check == 0*/){
                                    ActivityCompat.requestPermissions(this,
                                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE
                                                    , Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE);
                                }else {
                                    AlertDialog.Builder neverAskAgainDialgBuilder = new AlertDialog.Builder(this);
                                    neverAskAgainDialgBuilder.setMessage(getString(R.string.message_to_ask_to_allow_permission_from_settings));
                                    neverAskAgainDialgBuilder.setPositiveButton(getString(R.string.ok), null);

                                    final AlertDialog realNeverAskDialog = utilityClass.DialogBlur(this, neverAskAgainDialgBuilder);

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
                            launchFinalIntent();
                        }
                    }else launchFinalIntent();
                }
                break;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (checkAllPermissions(grantResults)){
                launchFinalIntent();
                finish();
            }
            else {
                if (permissionUtility.CheckRuntimePermission(this, permissions)
                        == permissionUtility.FLAG_RUNTIME_PERMISSION_DENIED_ONCE
                        /*shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) ||
                        shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)*/) {

                    AlertDialog.Builder permissionDeniedUponRequestDialog = new AlertDialog.Builder(this);
                    permissionDeniedUponRequestDialog.setMessage(getString(R.string.permission_denied_upon_request));
                    permissionDeniedUponRequestDialog.setPositiveButton(getString(R.string.ask_again), null);
                    permissionDeniedUponRequestDialog.setNegativeButton(getString(R.string.exit), null);

                    final AlertDialog permissionDeniedRealRequest = utilityClass.DialogBlur(this,
                            permissionDeniedUponRequestDialog);
                    permissionDeniedRealRequest.setCancelable(false);

                    permissionDeniedRealRequest.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ActivityCompat.requestPermissions(MainActivity.this
                                            , new String[]{Manifest.permission.READ_EXTERNAL_STORAGE
                                                    , Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE);
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
                    AlertDialog.Builder neverAskAgainDialgBuilder = new AlertDialog.Builder(this);
                    neverAskAgainDialgBuilder.setMessage(getString(R.string.message_to_ask_to_allow_permission_from_settings));
                    neverAskAgainDialgBuilder.setPositiveButton(getString(R.string.ok), null);

                    final AlertDialog realNeverAskDialog = utilityClass.DialogBlur(this, neverAskAgainDialgBuilder);
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

    private void launchFinalIntent(){
        SimpleDateFormat sdf = new SimpleDateFormat("MMM/dd/yyyy hh:mm aa", Locale.getDefault());
        String title = fileProvider.readExternalTextFileDisplayName(uri);
        String time = null;
        String content = null;
        try {
            time = fileProvider.getLastModifiedDate(uri, sdf);
            content = fileProvider.readExternalTextFileContent(uri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Intent launchEditingActivity_intent = new Intent(this, editingActivity.class);
        launchEditingActivity_intent.putExtra(getString(R.string.key_value_title), title);
        launchEditingActivity_intent.putExtra(getString(R.string.key_value_content), content);
        launchEditingActivity_intent.putExtra(getString(R.string.key_value_id), EXTERNAL_FILE_ID);
        launchEditingActivity_intent.putExtra(getString(R.string.key_value_time), "");
        startActivity(launchEditingActivity_intent);
        finish();
    }

    @Override
    public void onThemeModified() {
        mThemeModifiedListener.on_theme_modified(true);
    }

    public static void setOnThemeModifiedListener(themeModifedListener themeModifiedListener){
        mThemeModifiedListener = themeModifiedListener;
    }

    public interface themeModifedListener{
        void on_theme_modified(boolean isThemeModified);
    }

}
