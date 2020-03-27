package com.chinalwb.are.strategies.defaults;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.style.URLSpan;

import com.chinalwb.are.Util;
import com.chinalwb.are.activities.Are_VideoPlayerActivity;
import com.chinalwb.are.spans.AreAtSpan;
import com.chinalwb.are.spans.AreImageSpan;
import com.chinalwb.are.spans.AreVideoSpan;
import com.chinalwb.are.strategies.AreClickStrategy;
import com.chinalwb.are.strategies.VideoStrategy;
import com.chinalwb.are.styles.toolitems.styles.ARE_Style_Video;

import static com.chinalwb.are.spans.AreImageSpan.ImageType;

/**
 * Created by wliu on 30/06/2018.
 */

public class DefaultClickStrategy implements AreClickStrategy {
    @Override
    public boolean onClickAt(Context context, AreAtSpan atSpan) {
        Intent intent = new Intent();
        intent.setClass(context, DefaultProfileActivity.class);
        intent.putExtra("userKey", atSpan.getUserKey());
        intent.putExtra("userName", atSpan.getUserName());
        context.startActivity(intent);
        return true;
    }

    @Override
    public boolean onClickImage(Context context, AreImageSpan imageSpan) {
        Intent intent = new Intent();
        ImageType imageType = imageSpan.getImageType();
        intent.putExtra("imageType", imageType);
        if (imageType == ImageType.URI) {
            intent.putExtra("uri", imageSpan.getUri());
        } else if (imageType == ImageType.URL) {
            intent.putExtra("url", imageSpan.getURL());
        } else {
            intent.putExtra("resId", imageSpan.getResId());
        }
        intent.setClass(context, DefaultImagePreviewActivity.class);
        context.startActivity(intent);
        return true;
    }

    @Override
    public boolean onClickVideo(Context context, AreVideoSpan videoSpan) {
        Intent intent = new Intent();
        intent.setClass(context, Are_VideoPlayerActivity.class);
        Uri uri = Uri.parse(videoSpan.getVideoPath());
        intent.setData(uri);
        context.startActivity(intent);
        return true;
    }

    @Override
    public boolean onClickUrl(Context context, URLSpan urlSpan) {
        // Use default behavior
        return false;
    }
}
