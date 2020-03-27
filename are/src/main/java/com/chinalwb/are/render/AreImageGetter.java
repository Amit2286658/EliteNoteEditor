package com.chinalwb.are.render;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;
//todo:enable it back again when the bug is solved
/*import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;*/
import android.widget.Toast;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.chinalwb.are.AREditText;
import com.chinalwb.are.Constants;
import com.chinalwb.are.R;
import com.chinalwb.are.Util;
import com.chinalwb.are.android.inner.Html;

import java.util.ArrayList;
import java.util.List;

import static com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade;
//todo: enable this when the bug is gone
/*import com.chinalwb.are.glidesupport.GlideApp;
import com.chinalwb.are.glidesupport.GlideRequests;*/

public class AreImageGetter implements Html.ImageGetter {

    private Context mContext;

    private TextView mTextView;

    //todo: enable this too
    /*private static GlideRequests sGlideRequests;*/


    public AreImageGetter(Context context, TextView textView) {
        mContext = context;
        mTextView = textView;
        //todo: enable this too
        /*sGlideRequests = GlideApp.with(mContext);*/
    }

    @Override
    public Drawable getDrawable(String source) {
        if (source.startsWith(Constants.EMOJI)) {
            String resIdStr = source.substring(6);
            int resId = Integer.parseInt(resIdStr);
            Drawable d = mContext.getResources().getDrawable(resId);
            d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            return d;
        } else if (source.startsWith("http")) {

            final AreUrlDrawable areUrlDrawable = new AreUrlDrawable(mContext);

            /*final Util util = new Util();
            util.setmContext(mContext);
            util.loadImageInBackground(source, util);
            util.setOnImageLoadListener(new Util.onImageSuccessfulLoad() {
                @Override
                public void onImageLoad(final Bitmap bitmap) {
                    mBitmap = bitmap;
                    bitmap.recycle();
                    if (mBitmap != null) {
                        int w = Util.getScreenWidthAndHeight(mContext)[0];
                        mBitmap = Util.scaleBitmapToFitWidth(mBitmap, w);
                        Util.AreTextviewImageDrawable(mContext, mBitmap, areUrlDrawable, mTextView);
                    }else {
                        Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.image_place_holder);
                        Util.AreTextviewImageDrawable(drawable, areUrlDrawable, mTextView);
                    }
                }
            });*/
            //todo: enable this too
            /*BitmapTarget bitmapTarget = new BitmapTarget(areUrlDrawable, mTextView);*/
            //todo: enable this too
            /*sGlideRequests.asBitmap().load(source).into(bitmapTarget);*/

            /*Target target = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    //if (!isDownscaledEnabled) {
                    int w = Util.getScreenWidthAndHeight(mContext)[0];
                    bitmap = Util.scaleBitmapToFitWidth(bitmap, isDownscaledEnabled ? Util.getPixelByDp(mContext, 200) : w);

                    //}
                    Util.AreTextviewImageDrawable(mContext, bitmap, areUrlDrawable, mTextView);
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                    Util.AreTextviewImageDrawable(errorDrawable, areUrlDrawable, mTextView);
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            };
            targets.add(target);*/

            /*int pixel = Util.getPixelByDp(mContext, 200);*/

            /*Picasso picasso = Picasso.get();
            RequestCreator requestCreator = picasso.load(source);
            requestCreator.error(R.drawable.image_place_holder);
            if (!isCacheEnabled) {
                requestCreator.memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE);
                requestCreator.networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE);
            }
            requestCreator.into(target);*/
            Glide.with(mContext)
                    .asBitmap()
                    .load(source)
                    .transition(withCrossFade())
                    .placeholder(R.drawable.image_place_holder)
                    .error(R.drawable.image_place_holder)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {

                            resource = Util.scaleBitmapToFitWidth(resource, Util.getScreenWidthAndHeight(mContext)[0]);
                            int bw = resource.getWidth();
                            int bh = resource.getHeight();
                            Rect rect = new Rect(0, 0, bw, bh);
                            BitmapDrawable bitmapDrawable = new BitmapDrawable(resource);
                            bitmapDrawable.setBounds(rect);
                            areUrlDrawable.setBounds(rect);
                            areUrlDrawable.setDrawable(bitmapDrawable);
                            AREditText.stopMonitor();
                            mTextView.setText(mTextView.getText());
                            mTextView.invalidate();
                            AREditText.startMonitor();

                            /*int w = Util.getScreenWidthAndHeight(mContext)[0];
                            //resource = Util.scaleBitmapToFitWidth(resource, Util.getPixelByDp(mContext, 360));

                            //}
                            Util.AreTextviewImageDrawable(mContext, resource, areUrlDrawable, mTextView);*/
                        }

                        @Override
                        public void onLoadCleared(Drawable placeholder) {
                            int w = placeholder.getIntrinsicWidth();
                            int h = placeholder.getIntrinsicHeight();

                            Rect rect = new Rect(0, 0, w, h);

                            areUrlDrawable.setBounds(rect);
                            areUrlDrawable.setDrawable(placeholder);

                            AREditText.stopMonitor();

                            mTextView.setText(mTextView.getText());
                            mTextView.invalidate();

                            AREditText.startMonitor();
                        }
                    });

                    /*.memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)*/
            return areUrlDrawable;
        } else if (source.startsWith("content")) {

            //   content://media/external/images/media/846589
            final AreUrlDrawable areUrlDrawable = new AreUrlDrawable(mContext);
            //todo: enable this too
            /*BitmapTarget bitmapTarget = new BitmapTarget(areUrlDrawable, mTextView);*/

                //todo: enable this too
                //*sGlideRequests.asBitmap().load(uri).into(bitmapTarget);*//*
            Uri uri = Uri.parse(source);

            /*Target target = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    //if (!isDownscaledEnabled) {
                    int w = Util.getScreenWidthAndHeight(mContext)[0];
                    bitmap = Util.scaleBitmapToFitWidth(bitmap, isDownscaledEnabled ? Util.getPixelByDp(mContext, 200) : w);
                    //}
                    Util.AreTextviewImageDrawable(mContext, bitmap, areUrlDrawable, mTextView);
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            };
            targets.add(target);*/

            /*Picasso picasso = Picasso.get();
            RequestCreator requestCreator = picasso.load(uri);
            if (!isCacheEnabled) {
                requestCreator.memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE);
                requestCreator.networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE);
            }
            requestCreator.into(target);*/
            Glide.with(mContext)
                    .asBitmap()
                    .load(uri)
                    .transition(withCrossFade())
                    .placeholder(R.drawable.image_place_holder)
                    .error(R.drawable.image_place_holder)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {

                            resource = Util.scaleBitmapToFitWidth(resource, Constants.SCREEN_WIDTH);
                            int bw = resource.getWidth();
                            int bh = resource.getHeight();
                            Rect rect = new Rect(0, 0, bw, bh);
                            BitmapDrawable bitmapDrawable = new BitmapDrawable(resource);
                            bitmapDrawable.setBounds(rect);
                            areUrlDrawable.setBounds(rect);
                            areUrlDrawable.setDrawable(bitmapDrawable);
                            AREditText.stopMonitor();
                            mTextView.setText(mTextView.getText());
                            mTextView.invalidate();
                            AREditText.startMonitor();

                            /*int w = Util.getScreenWidthAndHeight(mContext)[0];
                            //resource = Util.scaleBitmapToFitWidth(resource, Util.getPixelByDp(mContext, 360));

                            //}
                            Util.AreTextviewImageDrawable(mContext, resource, areUrlDrawable, mTextView);*/
                        }

                        @Override
                        public void onLoadCleared(Drawable placeholder) {
                            int w = placeholder.getIntrinsicWidth();
                            int h = placeholder.getIntrinsicHeight();

                            Rect rect = new Rect(0, 0, w, h);

                            areUrlDrawable.setBounds(rect);
                            areUrlDrawable.setDrawable(placeholder);

                            AREditText.stopMonitor();

                            mTextView.setText(mTextView.getText());
                            mTextView.invalidate();

                            AREditText.startMonitor();
                        }
                    });
            /*final Uri uri = Uri.parse(source);
            final Util util = new Util();
            util.setmContext(mContext);
            util.loadImageInBackground(uri, util);
            util.setOnImageLoadListener(new Util.onImageSuccessfulLoad() {
                @Override
                public void onImageLoad(final Bitmap bitmap) {
                    mBitmap = bitmap;
                    *//*bitmap.recycle();*//*
                    if (mBitmap != null) {
                        int w = Util.getScreenWidthAndHeight(mContext)[0];
                        mBitmap = Util.scaleBitmapToFitWidth(mBitmap, w);
                        Util.AreTextviewImageDrawable(mContext, mBitmap, areUrlDrawable, mTextView);
                    }
                }
            });*/
            return areUrlDrawable;
        }
        return null;
    }

    //todo: enable this too
    /*private static class BitmapTarget extends SimpleTarget<Bitmap> {
        private final AreUrlDrawable areUrlDrawable;
        private TextView textView;

        private BitmapTarget(AreUrlDrawable urlDrawable, TextView textView) {
            this.areUrlDrawable = urlDrawable;
            this.textView = textView;
        }

        @Override
        public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
            bitmap = Util.scaleBitmapToFitWidth(bitmap, Constants.SCREEN_WIDTH);
            int bw = bitmap.getWidth();
            int bh = bitmap.getHeight();
            Rect rect = new Rect(0, 0, bw, bh);
            BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmap);
            bitmapDrawable.setBounds(rect);
            areUrlDrawable.setBounds(rect);
            areUrlDrawable.setDrawable(bitmapDrawable);
            AREditText.stopMonitor();
            textView.setText(textView.getText());
            textView.invalidate();
            AREditText.startMonitor();
        }
    }*/
}


//package com.chinalwb.are.render;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Rect;
//import android.graphics.drawable.BitmapDrawable;
//import android.graphics.drawable.Drawable;
//import android.net.Uri;
//import android.text.style.ImageSpan;
//import android.util.Log;
//import android.widget.TextView;
//
//import com.bumptech.glide.load.resource.transcode.BitmapDrawableTranscoder;
//import com.bumptech.glide.request.target.SimpleTarget;
//import com.bumptech.glide.request.transition.Transition;
//import com.chinalwb.are.Constants;
//import com.chinalwb.are.Util;
//import com.chinalwb.are.android.inner.Html;
//import com.chinalwb.are.spans.AreImageSpan;
//import com.chinalwb.are.styles.ARE_Image;
//import com.rainliu.glidesupport.GlideApp;
//import com.rainliu.glidesupport.GlideRequests;
//
//import java.util.WeakHashMap;
//
//public class AreImageGetter implements Html.ImageGetter {
//
//    private Context mContext;
//
//    private TextView mTextView;
//
//    private static GlideRequests sGlideRequests;
//
//    private static WeakHashMap<String, Bitmap> bitmapWeakHashMap = new WeakHashMap<>();
//
//    public AreImageGetter(Context context, TextView textView) {
//        mContext = context;
//        mTextView = textView;
//        sGlideRequests = GlideApp.with(mContext);
//    }
//
//    @Override
//    public Drawable getDrawable(final String source) {
//        if (source.startsWith(Constants.EMOJI)) {
//            String resIdStr = source.substring(6);
//            int resId = Integer.parseInt(resIdStr);
//            Drawable d = mContext.getResources().getDrawable(resId);
//            d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
//            return d;
//        } else if (source.startsWith("http")) {
//            Bitmap cacheBitmap = bitmapWeakHashMap.get(source);
//            Util.log("cachebitmap is " + cacheBitmap);
//            if (cacheBitmap != null) {
//                cacheBitmap = Util.scaleBitmapToFitWidth(cacheBitmap, Constants.SCREEN_WIDTH);
//                int bw = cacheBitmap.getWidth();
//                int bh = cacheBitmap.getHeight();
//                Rect rect = new Rect(0, 0, bw, bh);
//                BitmapDrawable bitmapDrawable = new BitmapDrawable(cacheBitmap);
//                bitmapDrawable.setBounds(rect);
//                return bitmapDrawable;
//            } else {
//                final AreUrlDrawable areUrlDrawable = new AreUrlDrawable(mContext);
//                SimpleTarget myTarget = new SimpleTarget<Bitmap>() {
//                    @Override
//                    public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
//                         bitmapWeakHashMap.put(source, bitmap);
//                         mTextView.setText(mTextView.getText());
//                         mTextView.invalidate();
////                        bitmap = Util.scaleBitmapToFitWidth(bitmap, Constants.SCREEN_WIDTH);
////                        int bw = bitmap.getWidth();
////                        int bh = bitmap.getHeight();
////                        Rect rect = new Rect(0, 0, bw, bh);
////                        BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmap);
////                        bitmapDrawable.setBounds(rect);
////                        areUrlDrawable.setBounds(rect);
////                        areUrlDrawable.setDrawable(bitmapDrawable);
////                        mTextView.setText(mTextView.getText());
////                        mTextView.invalidateDrawable(areUrlDrawable);
//                    }
//                };
//                sGlideRequests.asBitmap().load(source).into(myTarget);
//                return areUrlDrawable;
//            }
//        }
//        return null;
//    }
//
////    private static class BitmapTarget extends SimpleTarget<Bitmap> {
////        private final AreUrlDrawable areUrlDrawable;
////        private TextView textView;
////
////        private BitmapTarget(AreUrlDrawable urlDrawable, TextView textView) {
////            this.areUrlDrawable = urlDrawable;
////            this.textView = textView;
////        }
////
////        @Override
////        public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
////            bitmap = Util.scaleBitmapToFitWidth(bitmap, Constants.SCREEN_WIDTH);
////            int bw = bitmap.getWidth();
////            int bh = bitmap.getHeight();
////            Rect rect = new Rect(0, 0, bw, bh);
////            BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmap);
////            bitmapDrawable.setBounds(rect);
////            areUrlDrawable.setBounds(rect);
////            areUrlDrawable.setDrawable(bitmapDrawable);
////            textView.setText(textView.getText());
////            textView.invalidate();
////        }
////    }
//}
