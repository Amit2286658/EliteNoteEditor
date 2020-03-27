package com.chinalwb.are.styles.windows;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chinalwb.are.R;
import com.chinalwb.are.Util;
import com.chinalwb.are.spans.AreImageSpan;
import com.chinalwb.are.styles.IARE_Image;
import com.divyanshu.draw.activity.DrawingActivity;

public class ImageSelectDialog {

    private Context mContext;

    private View mRootView;

    private Dialog mDialog;

    private IARE_Image mAreImage;

    private int mRequestCode;

    private int mDrawaingRequestCode;

    public ImageSelectDialog(Context context, IARE_Image areImage, int requestCode, int drawingcode) {
        mContext = context;
        mAreImage = areImage;
        mRequestCode = requestCode;
        mDrawaingRequestCode = drawingcode;
        mRootView = initView();
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Insert Image");
        builder.setView(mRootView);
        mDialog = builder.create();
    }

    private View initView() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.are_image_select, null);
        final RelativeLayout insertInternetImageLayout = view.findViewById(R.id.are_image_select_from_internet_layout);

        RadioGroup radioGroup = view.findViewById(R.id.are_image_select_radio_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.are_image_select_from_local) {
                    openImagePicker();
                } else if (checkedId == R.id.are_image_select_from_doodle){
                    ((Activity)mContext).startActivityForResult(
                            new Intent().setClass(mContext, DrawingActivity.class), mDrawaingRequestCode);
                    mDialog.dismiss();
                }
                else {
                    insertInternetImageLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        final TextView insertInternetImage = view.findViewById(R.id.are_image_select_insert);
        insertInternetImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertInternetImage();
            }
        });
        return view;
    }

    public void show() {
        mDialog.show();
    }

    private void openImagePicker() {
        Intent intent = new Intent();
		intent.setType("image/*");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }else intent.setAction(Intent.ACTION_GET_CONTENT);
        ((Activity) this.mContext).startActivityForResult(intent, mRequestCode);
		mDialog.dismiss();
    }

    private void insertInternetImage() {
        EditText editText = mRootView.findViewById(R.id.are_image_select_internet_image_url);
        String imageUrl = editText.getText().toString();
        if (imageUrl.startsWith("http")
                &&  (imageUrl.endsWith("png") || imageUrl.endsWith("jpg") || imageUrl.endsWith("jpeg"))) {
            mAreImage.insertImage(imageUrl, AreImageSpan.ImageType.URL);
            mDialog.dismiss();
        } else {
            Util.toast(mContext, "Not a valid image");
        }
    }

}
