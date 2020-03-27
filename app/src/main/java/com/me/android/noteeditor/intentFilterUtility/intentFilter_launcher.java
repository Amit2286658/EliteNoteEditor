package com.me.android.noteeditor.intentFilterUtility;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.me.android.noteeditor.BlurUtility.ApplyBlurOnDialog;
import com.me.android.noteeditor.R;
import com.me.android.noteeditor.editingActivity;
import com.me.android.noteeditor.fileProviderUtility.FileProvider;
import com.me.android.noteeditor.permissionUtility.permissionUtility;
import com.jaredrummler.cyanea.app.CyaneaAppCompatActivity;

import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import static com.me.android.noteeditor.permissionUtility.permissionUtility.checkAllPermissions;

public class intentFilter_launcher extends CyaneaAppCompatActivity {

    private ApplyBlurOnDialog apr = new ApplyBlurOnDialog(this);

    private static final int EXTERNAL_FILE_ID = -1;
    private FileProvider fileProvider = new FileProvider();
    private static final int PERMISSION_CODE = 234;

    private static Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fileProvider.initiate(this);

        @NonNull
        Intent intent = getIntent();
        uri = intent.getData();

        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this
                    , Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                if (permissionUtility.ShowPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE
                        , Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                    /*shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
                                    || shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)*/){
                    AlertDialog.Builder requestRationaleDialog = new AlertDialog.Builder(this);
                    requestRationaleDialog.setMessage(getString(R.string.permission_denied));
                    requestRationaleDialog.setPositiveButton(getString(R.string.ask), null);
                    requestRationaleDialog.setNegativeButton(getString(R.string.exit), null);


                    final AlertDialog requestRealDialog = apr.getRealDialog(requestRationaleDialog);
                    requestRealDialog.show();
                    requestRealDialog.setCancelable(false);

                    requestRealDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ActivityCompat.requestPermissions(intentFilter_launcher.this
                                            , new String[]{Manifest.permission.READ_EXTERNAL_STORAGE
                                                    , Manifest.permission.WRITE_EXTERNAL_STORAGE}
                                            , PERMISSION_CODE);
                                    requestRealDialog.dismiss();
                                }
                            }
                    );
                    requestRealDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    requestRealDialog.dismiss();
                                    finish();
                                }
                            }
                    );
                }else {
                    if (permissionUtility.CheckPermissionFlag(this)
                            == permissionUtility.FLAG_FIRST_TIME_PERMISSION_REQUEST
                        /*check == 0*/){
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE
                                        , Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_CODE);
                    }else {
                        AlertDialog.Builder neverAskAgainDialgBuilder = new AlertDialog.Builder(this);
                        neverAskAgainDialgBuilder.setMessage(getString(R.string.permanently_disabled_permissions));
                        neverAskAgainDialgBuilder.setPositiveButton(getString(R.string.OK), null);

                        final AlertDialog realNeverAskDialog = neverAskAgainDialgBuilder.create();
                        realNeverAskDialog.show();
                        realNeverAskDialog.setCancelable(false);

                        realNeverAskDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        realNeverAskDialog.dismiss();
                                        finish();
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

        if (requestCode == PERMISSION_CODE){
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

                    final AlertDialog permissionDeniedRealRequest = apr.getRealDialog(permissionDeniedUponRequestDialog);
                    permissionDeniedRealRequest.show();
                    permissionDeniedRealRequest.setCancelable(false);

                    permissionDeniedRealRequest.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ActivityCompat.requestPermissions(intentFilter_launcher.this
                                            , new String[]{Manifest.permission.READ_EXTERNAL_STORAGE
                                                    , Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_CODE);
                                    permissionDeniedRealRequest.dismiss();
                                }
                            }
                    );
                    permissionDeniedRealRequest.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    permissionDeniedRealRequest.dismiss();
                                    finish();
                                }
                            }
                    );
                } else {
                    AlertDialog.Builder neverAskAgainDialgBuilder = new AlertDialog.Builder(this);
                    neverAskAgainDialgBuilder.setMessage(getString(R.string.permanently_disabled_permissions));
                    neverAskAgainDialgBuilder.setPositiveButton(getString(R.string.OK), null);

                    final AlertDialog realNeverAskDialog = apr.getRealDialog(neverAskAgainDialgBuilder);
                    realNeverAskDialog.show();
                    realNeverAskDialog.setCancelable(false);

                    realNeverAskDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    realNeverAskDialog.dismiss();
                                    finish();
                                }
                            }
                    );
                }
            }
        }
    }

    private int checkReadRationalePermission(){
        int requestRationale = -1;
        if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)){
            androidx.appcompat.app.AlertDialog.Builder permissionDialog = new androidx.appcompat.app.AlertDialog.Builder(this);
            permissionDialog.setMessage(getString(R.string.permission_denied));
            permissionDialog.setPositiveButton(getString(R.string.permission_allow_text)
                    , new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(intentFilter_launcher.this
                                    , new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_CODE);
                        }
                    });
            permissionDialog.show();
            requestRationale = 1;
        } else if (!shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)){
            final androidx.appcompat.app.AlertDialog.Builder permissionDialog = new androidx.appcompat.app.AlertDialog.Builder(this);
            permissionDialog.setMessage(getString(R.string.permission_denied_upon_request));
            permissionDialog.setPositiveButton(getString(R.string.OK), null);
            permissionDialog.show();
            requestRationale = 0;
        }
        return requestRationale;
    }

    private void launchFinalIntent(){
        String title = fileProvider.readExternalTextFileDisplayName(uri);
        String content = null;
        try {
            content = fileProvider.readExternalTextFileContent(uri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Intent launchEditingActivity_intent = new Intent(this, editingActivity.class);
        launchEditingActivity_intent.putExtra(getString(R.string.key_value_title), title);
        launchEditingActivity_intent.putExtra(getString(R.string.key_value_content), content);
        launchEditingActivity_intent.putExtra(getString(R.string.key_value_time), "");
        launchEditingActivity_intent.putExtra(getString(R.string.key_value_id), EXTERNAL_FILE_ID);
        startActivity(launchEditingActivity_intent);
        finish();
    }
}
