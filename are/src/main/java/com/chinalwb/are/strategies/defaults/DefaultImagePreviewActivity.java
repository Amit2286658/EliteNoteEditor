package com.chinalwb.are.strategies.defaults;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.style.ImageSpan;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
//todo:enable it back again when the bug is solved
/*import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;*/
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.chinalwb.are.R;
import com.chinalwb.are.Util;
import com.chinalwb.are.spans.AreImageSpan;
//todo: enable this when the bug is gone
/*import com.chinalwb.are.glidesupport.GlideApp;
import com.chinalwb.are.glidesupport.GlideRequests;*/

import java.io.File;
import java.io.IOException;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import static com.chinalwb.are.spans.AreImageSpan.ImageType;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class DefaultImagePreviewActivity extends AppCompatActivity {

    int state_type = 0;
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
//todo: enable this too
   /* private static GlideRequests sGlideRequests;*/
    //private Picasso picasso = Picasso.get();

    private static int sWidth;

    private ImageView mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;


    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (state_type == 0) {
                if (AUTO_HIDE) {
                    delayedHide(AUTO_HIDE_DELAY_MILLIS);
                }
                return false;
            }else  {

                return true;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_default_image_preview);
//todo: enable this too
        /*sGlideRequests = GlideApp.with(this);*/
        sWidth = Util.getScreenWidthAndHeight(this)[0];
        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.default_image_preview);

        try{
            state_type = getIntent().getExtras().getInt("default_image_preview_state_key");
        }catch (NullPointerException e){
            //empty
        }

        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        if (state_type != 0) {
            Button button = (Button) findViewById(R.id.default_button_save);
            button.setText("Save and share");
        }
        findViewById(R.id.default_button_save).setOnTouchListener(mDelayHideTouchListener);
        findViewById(R.id.default_button_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    Bitmap bitmap;
                    mContentView.invalidate();
                    bitmap = ((BitmapDrawable)mContentView.getDrawable()).getBitmap();
                    /*if (mContentView.getDrawable() instanceof BitmapDrawable){
                        bitmap = ((BitmapDrawable)mContentView.getDrawable()).getBitmap();
                    }else {
                        Drawable d = mContentView.getDrawable();
                        bitmap = Bitmap.createBitmap(d.getIntrinsicWidth(), d.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                        Canvas c = new Canvas(bitmap);
                        d.draw(c);
                    }*/
                    File file = Util.saveBitmap(bitmap, DefaultImagePreviewActivity.this,
                            Util.getNoteSavedImagesDirectory());
                    if (file != null){
                        Toast.makeText(DefaultImagePreviewActivity.this, "Image saved to : "+file.getAbsolutePath(),
                                Toast.LENGTH_LONG).show();
                    }
                }catch (Exception e){
                    //empty
                }
            }
        });

        init();
    }

    private void init() {

        //todo:enable it back again when the bug is solved
        /*SimpleTarget myTarget = new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
                if (bitmap == null) { return; }

                itmap = Util.scaleBitmapToFitWidth(bitmap, sWidth);
                mContentView.setImageBitmap(bitmap);
            }
        };*/


        Bundle extras = this.getIntent().getExtras();
        ImageType imageType = (ImageType) extras.get("imageType");
        if (imageType == ImageType.URI) {
            Uri uri = (Uri) extras.get("uri");
            //todo:enable it back again when the bug is solved
            /*sGlideRequests.asBitmap().load(uri).centerCrop().into(myTarget);*/
            /*Bitmap bitmap;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                bitmap = Util.scaleBitmapToFitWidth(bitmap, sWidth);
                mContentView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }*/
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
            boolean isCacheEnabled = sp.getBoolean("cache_preference_key", true);

            /*RequestCreator requestCreator = picasso.load(uri);
            if (!isCacheEnabled){
                requestCreator.memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE);
                requestCreator.networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE);
            }
            requestCreator.placeholder(R.drawable.image_place_holder);
            requestCreator.into(mContentView);*/
            Glide.with(this)
                    .load(uri)
                    .placeholder(R.drawable.image_place_holder)
                    .into(mContentView);
            /*final Util util = new Util();
            util.setmContext(this);
            util.loadImageInBackground(uri, util);
            util.setOnImageLoadListener(new Util.onImageSuccessfulLoad() {
                @Override
                public void onImageLoad(Bitmap bitmap) {
                    bitmap = Util.scaleBitmapToFitWidth(bitmap, sWidth);
                    mContentView.setImageBitmap(bitmap);
                }
            });*/
        } else if (imageType == ImageType.URL) {
            String url = extras.getString("url");
            //todo:enable this too
            /*sGlideRequests.asBitmap().load(url).centerCrop().into(myTarget);*/
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
            boolean isCacheEnabled = sp.getBoolean("cache_preference_key", true);

            /*RequestCreator requestCreator = picasso.load(url);
            if (!isCacheEnabled){
                requestCreator.memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE);
                requestCreator.networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE);
            }
            requestCreator.placeholder(R.drawable.image_place_holder);
            requestCreator.error(R.drawable.image_place_holder);
            requestCreator.into(mContentView);*/
            Glide.with(this)
                    .load(url)
                    .placeholder(R.drawable.image_place_holder)
                    .into(mContentView);
            /*final Util util = new Util();
            util.setmContext(this);
            util.loadImageInBackground(url, util);
            util.setOnImageLoadListener(new Util.onImageSuccessfulLoad() {
                @Override
                public void onImageLoad(Bitmap bitmap) {
                    if (bitmap != null) {
                        bitmap = Util.scaleBitmapToFitWidth(bitmap, sWidth);
                        mContentView.setImageBitmap(bitmap);
                    }else {
                        Drawable drawable = ContextCompat.
                                getDrawable(DefaultImagePreviewActivity.this, R.drawable.image_place_holder);
                        mContentView.setImageDrawable(drawable);
                    }
                }
            });*/
        } else if (imageType == ImageType.RES) {
            int resId = extras.getInt("resId");
            mContentView.setImageResource(resId);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}
