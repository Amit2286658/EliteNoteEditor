package com.chinalwb.are.styles.toolitems;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.text.Editable;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.Toast;
import com.chinalwb.are.AREditText;
import com.chinalwb.are.Constants;
import com.chinalwb.are.R;
import com.chinalwb.are.Util;
import com.chinalwb.are.activities.Are_VideoPlayerActivity;
import com.chinalwb.are.models.AtItem;
import com.chinalwb.are.spans.AreImageSpan;
import com.chinalwb.are.strategies.ImageStrategy;
import com.chinalwb.are.styles.ARE_At;
import com.chinalwb.are.styles.IARE_Style;
import com.chinalwb.are.styles.toolbar.ARE_Toolbar;
import com.chinalwb.are.styles.toolitems.styles.ARE_Style_Bold;
import com.chinalwb.are.styles.toolitems.styles.ARE_Style_Image;

import java.io.File;

/**
 * Created by wliu on 13/08/2018.
 */

public class ARE_ToolItem_Image extends ARE_ToolItem_Abstract {



    @Override
    public IARE_ToolItem_Updater getToolItemUpdater() {
        return null;
    }

    @Override
    public IARE_Style getStyle() {
        if (mStyle == null) {
            AREditText editText = this.getEditText();
            mStyle = new ARE_Style_Image(editText, (ImageView) mToolItemView);
        }
        return mStyle;
    }

    @Override
    public View getView(Context context) {
        if (null == context) {
            return mToolItemView;
        }
        if (mToolItemView == null) {
            ImageView imageView = new ImageView(context);
            int size = Util.getPixelByDp(context, 40);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            int pad = Util.getPixelByDp(context, 6);
            imageView.setPadding(pad, pad, pad, pad);
            imageView.setLayoutParams(params);
            imageView.setImageResource(R.drawable.ic_picture);
            imageView.bringToFront();
            mToolItemView = imageView;
        }

        return mToolItemView;
    }

    @Override
    public void onSelectionChanged(int selStart, int selEnd) {
        return;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (ARE_Style_Image.REQUEST_CODE == requestCode) {
                ARE_Style_Image imageStyle = (ARE_Style_Image) getStyle();
                Uri uri = data.getData();

                setImageStyle(uri, imageStyle);

                /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
                    if (uri != null) {
                        this.getEditText().getContext().getContentResolver().
                                takePersistableUriPermission(uri,
                                        Intent.FLAG_GRANT_READ_URI_PERMISSION |
                                                Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    }
                }
                ImageStrategy imageStrategy = this.getEditText().getImageStrategy();
                if (imageStrategy != null) {
                    imageStrategy.uploadAndInsertImage(uri, imageStyle);
                    return;
                }
                imageStyle.insertImage(uri, AreImageSpan.ImageType.URI);*/
            }

            if (requestCode == ARE_Style_Image.DRAWING_CODE){
                byte[] result= data.getByteArrayExtra("bitmap");
                Bitmap bitmap = BitmapFactory.decodeByteArray(result, 0, result.length);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                        && ((Activity) this.getEditText().getContext()).checkSelfPermission
                        (Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED
                        && ((Activity) this.getEditText().getContext()).checkSelfPermission
                        (Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    //申请授权
                    ((Activity) this.getEditText().getContext()).requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, ARE_Toolbar.REQ_DOODLE_IMAGE);
                    return;
                }
                File file = Util.saveBitmap(bitmap, this.getEditText().getContext(), Util.getNoteDoodleDirectory());
                if (file == null){
                    Toast.makeText(this.getEditText().getContext(), "file is null", Toast.LENGTH_LONG).show();
                    return;
                }
                Util.toast(this.getEditText().getContext(), "Image saved to : "+file.getAbsolutePath());

                Intent intent = new Intent();
                intent.setType("image/*");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                }else intent.setAction(Intent.ACTION_GET_CONTENT);
                ((Activity)this.getEditText().getContext()).startActivityForResult(intent, ARE_Style_Image.REQUEST_CODE);
            }
        }
    }

    private void setImageStyle(Uri uri, ARE_Style_Image imageStyle) throws SecurityException{
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            if (uri != null) {
                this.getEditText().getContext().getContentResolver().
                        takePersistableUriPermission(uri,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION |
                                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
        }
        ImageStrategy imageStrategy = this.getEditText().getImageStrategy();
        if (imageStrategy != null) {
            imageStrategy.uploadAndInsertImage(uri, imageStyle);
            return;
        }
        imageStyle.insertImage(uri, AreImageSpan.ImageType.URI);
    }

}
