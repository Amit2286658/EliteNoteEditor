package com.chinalwb.are;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.*;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.*;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.text.PrecomputedTextCompat;
import androidx.core.widget.TextViewCompat;
import com.chinalwb.are.render.AreTextView;
import com.chinalwb.are.render.AreUrlDrawable;
import com.chinalwb.are.spans.AreImageSpan;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Ref;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.Executor;

/**
 * All Rights Reserved.
 *
 * @author Wenbin Liu
 */
public class Util {

    public enum link_type{
        URI, URL
    }

    private Context mContext;

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    public Context getmContext() {
        if (mContext == null) throw new IllegalStateException("mContext is null");
        return mContext;
    }

    /**
     * Toast message.
     */
    public static void toast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    /**
     * @param s
     */
    public static void log(String s) {
        Log.d("CAKE", s);
    }

    /**
     * Returns the line number of current cursor.
     *
     * @param editText
     * @return
     */
    public static int getCurrentCursorLine(EditText editText) {
        int selectionStart = Selection.getSelectionStart(editText.getText());
        Layout layout = editText.getLayout();

        if (null == layout) {
            return -1;
        }
        if (selectionStart != -1) {
            return layout.getLineForOffset(selectionStart);
        }

        return -1;
    }

    /**
     * Returns the selected area line numbers.
     *
     * @param editText
     * @return
     */
    public static int[] getCurrentSelectionLines(EditText editText) {
        Editable editable = editText.getText();
        int selectionStart = Selection.getSelectionStart(editable);
        int selectionEnd = Selection.getSelectionEnd(editable);
        Layout layout = editText.getLayout();

        int[] lines = new int[2];
        if (selectionStart != -1) {
            int startLine = layout.getLineForOffset(selectionStart);
            lines[0] = startLine;
        }

        if (selectionEnd != -1) {
            int endLine = layout.getLineForOffset(selectionEnd);
            lines[1] = endLine;
        }

        return lines;
    }

    /**
     * Returns the line start position of the current line (which cursor is focusing now).
     *
     * @param editText
     * @return
     */
    public static int getThisLineStart(EditText editText, int currentLine) {
        Layout layout = editText.getLayout();
        int start = 0;
        if (currentLine > 0) {
            start = layout.getLineStart(currentLine);
            if (start > 0) {
                String text = editText.getText().toString();
                char lastChar = text.charAt(start - 1);
                while (lastChar != '\n') {
                    if (currentLine > 0) {
                        currentLine--;
                        start = layout.getLineStart(currentLine);
                        if (start > 1) {
                            start--;
                            lastChar = text.charAt(start);
                        } else {
                            break;
                        }
                    }
                }
            }
        }
        return start;
    }

    /**
     * Returns the line end position of the current line (which cursor is focusing now).
     *
     * @param editText
     * @return
     */
    public static int getThisLineEnd(EditText editText, int currentLine) {
        Layout layout = editText.getLayout();
        if (-1 != currentLine) {
            return layout.getLineEnd(currentLine);
        }
        return -1;
    }

    /**
     * Gets the pixels by the given number of dp.
     *
     * @param context
     * @param dp
     * @return
     */
    public static int getPixelByDp(Context context, int dp) {
        int pixels = dp;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        pixels = (int) (displayMetrics.density * dp + 0.5);
        return pixels;
    }

    /**
     * Returns the screen width and height.
     *
     * @param context
     * @return
     */
    public static int[] getScreenWidthAndHeight(Context context) {
        Point outSize = new Point();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        display.getSize(outSize);

        int[] widthAndHeight = new int[2];
        widthAndHeight[0] = outSize.x;
        widthAndHeight[1] = outSize.y;
        return widthAndHeight;
    }

    /**
     * Returns the color in string format.
     *
     * @param intColor
     * @param containsAlphaChannel
     * @param removeAlphaFromResult
     * @return
     */
    public static String colorToString(int intColor, boolean containsAlphaChannel, boolean removeAlphaFromResult) {
        String strColor = String.format("#%06X", 0xFFFFFF & intColor);
        if (containsAlphaChannel) {
            strColor = String.format("#%06X", 0xFFFFFFFF & intColor);
            if (removeAlphaFromResult) {
                StringBuffer buffer = new StringBuffer(strColor);
                buffer.delete(1, 3);
                strColor = buffer.toString();
            }
        }

        return strColor;
    }

    public static Bitmap scaleBitmapToFitWidth(Bitmap bitmap, int maxWidth) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int newWidth = maxWidth;
        int newHeight = maxWidth * h / w;
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) newWidth / w);
        float scaleHeight = ((float) newHeight / h);
        if (w < maxWidth * 0.2) {
            return bitmap;
        }
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
    }

    public static Bitmap fitWidthAndMaintainRatio(Bitmap bitmap, int maxWidth){

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int ratio = h/w;

        int maintainedHeight = maxWidth*ratio;

        Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, maxWidth, maintainedHeight);
        bitmap.recycle();
        return newBitmap;
    }

    public static Bitmap mergeBitmaps(Bitmap background, Bitmap foreground) {
        if( background == null ) {
            return null;
        }

        int bgWidth = background.getWidth();
        int bgHeight = background.getHeight();

        //create the new blank bitmap
        Bitmap newBitmap = Bitmap.createBitmap(bgWidth, bgHeight, Bitmap.Config.ARGB_8888);
        Canvas cv = new Canvas(newBitmap);
        //draw bg into
        cv.drawBitmap(background, 0, 0, null);

        int fgWidth = foreground.getWidth();
        int fgHeight = foreground.getHeight();
        int fgLeft = (bgWidth - fgWidth) / 2;
        int fgTop = (bgHeight - fgHeight) / 2;

        //draw fg into
        cv.drawBitmap(foreground, fgLeft, fgTop, null);
        //save all clip
        cv.save(/*Canvas.ALL_SAVE_FLAG*/);
        //store
        cv.restore();
        return newBitmap;
    }

    public static class GetPathFromUri4kitkat {

        /**
         * For Android 4.4
         */
        @SuppressLint("NewApi")
        public static String getPath(final Context context, final Uri uri) {

            final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

            // DocumentProvider
            if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    if ("primary".equalsIgnoreCase(type)) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    }

                    // TODO handle non-primary volumes
                }
                // DownloadsProvider
                else if (isDownloadsDocument(uri)) {

                    final String id = DocumentsContract.getDocumentId(uri);
                    if (id.startsWith("raw:")) {
                        return id.substring(4);
                    }
                    final Uri contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                    return getDataColumn(context, contentUri, null, null);
                }
                // MediaProvider
                else if (isMediaDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }

                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[] { split[1] };

                    return getDataColumn(context, contentUri, selection, selectionArgs);
                }
            }
            // MediaStore (and general)
            else if ("content".equalsIgnoreCase(uri.getScheme())) {
                return getDataColumn(context, uri, null, null);
            }
            // File
            else if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }

            return null;
        }

        /**
         * Get the value of the data column for this Uri. This is useful for
         * MediaStore Uris, and other file-based ContentProviders.
         *
         * @param context
         *            The context.
         * @param uri
         *            The Uri to query.
         * @param selection
         *            (Optional) Filter used in the query.
         * @param selectionArgs
         *            (Optional) Selection arguments used in the query.
         * @return The value of the _data column, which is typically a file path.
         */
        public static String getDataColumn(Context context, Uri uri, String selection,
                                           String[] selectionArgs) {

            Cursor cursor = null;
            final String column = "_data";
            final String[] projection = { column };

            try {
                cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                        null);
                if (cursor != null && cursor.moveToFirst()) {
                    final int column_index = cursor.getColumnIndexOrThrow(column);
                    return cursor.getString(column_index);
                }
            } finally {
                if (cursor != null)
                    cursor.close();
            }
            return null;
        }

        /**
         * @param uri
         *            The Uri to check.
         * @return Whether the Uri authority is ExternalStorageProvider.
         */
        public static boolean isExternalStorageDocument(Uri uri) {
            return "com.android.externalstorage.documents".equals(uri.getAuthority());
        }

        /**
         * @param uri
         *            The Uri to check.
         * @return Whether the Uri authority is DownloadsProvider.
         */
        public static boolean isDownloadsDocument(Uri uri) {
            return "com.android.providers.downloads.documents".equals(uri.getAuthority());
        }

        /**
         * @param uri
         *            The Uri to check.
         * @return Whether the Uri authority is MediaProvider.
         */
        public static boolean isMediaDocument(Uri uri) {
            return "com.android.providers.media.documents".equals(uri.getAuthority());
        }
    }

    public static void hideKeyboard(View view, Context context) {
        if (view != null && context != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        int w = drawable.getBounds().width();
        int h = drawable.getBounds().height();
        Bitmap bitmap = Bitmap.createBitmap(
                w,
                h,
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565);

        Canvas canvas = new Canvas(bitmap);
        //canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }


    /*public setImageSpan mSetImageSpan;

    public void setSpanReadyListener(setImageSpan spanReadyListener){
        mSetImageSpan = spanReadyListener;
    }

    public void confirmSpanReady(Bitmap bitmap_, Uri uri_){
        mSetImageSpan.onSpanReady(bitmap_, uri_);
    }

    public interface setImageSpan{
        void onSpanReady(Bitmap bitmap, Uri uri);
    }

    Util htmlutil;

    public void initHtml(Util util){
        htmlutil = util;
    }*/

    public static String getNoteShareDirectory(){
        return
                Environment.getExternalStorageDirectory()
                        + File.separator
                        + "ELITE_NOTE_EDITOR"
                        + File.separator
                        + "SHARED_NOTES"
                        + File.separator;
    }

    public static String getNoteDoodleDirectory(){
        return
                Environment.getExternalStorageDirectory()
                        + File.separator
                        + "NOTE_EDITOR"
                        + File.separator
                        + "DOODLE_IMAGES"
                        + File.separator;
    }

    public static String getNoteSavedImagesDirectory(){
        return
                Environment.getExternalStorageDirectory()
                        + File.separator
                        + "NOTE_EDITOR"
                        + File.separator
                        + "SAVED_IMAGES"
                        + File.separator;
    }

    public static File saveBitmap(Bitmap bm, Context context, String directoryPath){
        /*String directoryPath =
                Environment.getExternalStorageDirectory()
                        + File.separator
                        + "NOTE_EDITOR"
                        + File.separator
                        + "SAVED_IMAGES"
                        + File.separator;*/
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-" + n + ".jpg";
        File dir = new File(directoryPath);
        if (!dir.exists())
            dir.mkdirs();
        try {
            File file = new File(directoryPath, fname);
            FileOutputStream out = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(),
                    file.getName(), file.getName());
            /*Uri uri = getContentUri(context, file);
            toast(context, "Image saved to : "+directoryPath+fname);
            return uri;*/
            return  file;

        } catch (Exception e) {
            return null;
        }
    }

    public static Uri getContentUri(Context context, File file){
        String filepath = file.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filepath},
                null
        );
        if (cursor != null && cursor.moveToFirst()){
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            cursor.close();
            return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + id);
        } else {
            if (file.exists()){
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.Images.Media.DATA, filepath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues
                );
            }else return null;
        }
    }

    public boolean checkNetworkStatus(){
        ConnectivityManager cm = (ConnectivityManager)
                mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null){
            if (Build.VERSION.SDK_INT < 23){
                final NetworkInfo networkInfo = cm.getActiveNetworkInfo();
                if (networkInfo != null){
                    return (networkInfo.isConnected() && (networkInfo.getType()
                            == ConnectivityManager.TYPE_WIFI ||
                            networkInfo.getType() == ConnectivityManager.TYPE_MOBILE));
                }
            }else {
                final Network network = cm.getActiveNetwork();
                if (network != null){
                    final NetworkCapabilities networkCapabilities = cm.getNetworkCapabilities(network);
                    return (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                            || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI));
                }
            }
        }
        return false;
    }

    /*public boolean checkActiveInternetConnectionStatus(){
        if (checkNetworkStatus()){
            try {
                HttpURLConnection httpURLConnection = (HttpURLConnection)(new URL("http://clients3.google.com/generate_204")
                .openConnection());
                httpURLConnection.setRequestProperty("User-Agent", "Test");
                httpURLConnection.setRequestProperty("Connection", "close");
                httpURLConnection.setConnectTimeout(1500);
                httpURLConnection.connect();

                return (httpURLConnection.getResponseCode() == 204 && httpURLConnection.getContentLength() == 0);
            }catch (IOException e){
                e.printStackTrace();
                return false;
            }
        } else return false;
    }*/

    public static Executor getExecutor(){
        return new Executor() {
            @Override
            public void execute(@NonNull Runnable command) {

            }
        };
    }

    public static void asyncSetText(final TextView textView, final Spanned spanned, Executor bgExecutor){
        final PrecomputedTextCompat.Params params = TextViewCompat.getTextMetricsParams(textView);
        final Reference textViewReference = new WeakReference<>(textView);
        bgExecutor.execute(new Runnable() {
            @Override
            public void run() {
                TextView textView1 = (TextView) textViewReference.get();
                if (textView1 != null){
                    final PrecomputedTextCompat prec = PrecomputedTextCompat.create(spanned, params);
                    textView1.post(new Runnable() {
                        @Override
                        public void run() {
                            TextView textView2 = (TextView) textViewReference.get();
                            if (textView2 != null){
                                textView2.setText(prec);
                            }
                        }
                    });
                }
            }
        });
    }

    public static void AreTextviewImageDrawable(Context context, Bitmap bitmap, AreUrlDrawable areUrlDrawable,
                                                TextView textView){
        if (bitmap == null) return;
        /*bitmap = scaleBitmapToFitWidth(bitmap, Constants.SCREEN_WIDTH);*/
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Rect rect = new Rect(0, 0, w, h);

        BitmapDrawable bitmapDrawable = new BitmapDrawable(context.getResources(), bitmap);
        bitmapDrawable.setBounds(rect);

        areUrlDrawable.setBounds(rect);
        areUrlDrawable.setDrawable(bitmapDrawable);

        AREditText.stopMonitor();

        textView.setText(textView.getText());
        textView.invalidate();

        AREditText.startMonitor();
    }
    public static void AreTextviewImageDrawable(Drawable bitmap, AreUrlDrawable areUrlDrawable,
                                                TextView textView){
        /*bitmap = scaleBitmapToFitWidth(bitmap, Constants.SCREEN_WIDTH);*/
        /*int w = bitmap.getWidth();
        int h = bitmap.getHeight();*/
        int w = bitmap.getIntrinsicWidth();
        int h = bitmap.getIntrinsicHeight();

        Rect rect = new Rect(0, 0, w, h);

        /*BitmapDrawable bitmapDrawable = new BitmapDrawable(context.getResources(), bitmap);*/
        bitmap.setBounds(rect);

        areUrlDrawable.setBounds(rect);
        areUrlDrawable.setDrawable(bitmap);

        AREditText.stopMonitor();

        textView.setText(textView.getText());
        textView.invalidate();

        AREditText.startMonitor();
    }

    public void loadImageInBackground(String url, Util util){
        loadInBackground loadInBackground = new loadInBackground();
        Object[] objects = new Object[]{url, util, link_type.URL};
        loadInBackground.execute(objects);
    }

    public void loadImageInBackground(Uri uri, Util util){
        loadInBackground loadInBackground = new loadInBackground();
        Object[] objects = new Object[]{uri, util, link_type.URI};
        loadInBackground.execute(objects);
    }

    public void clearUp(Bitmap bitmap, Util util){
        bitmap.recycle();
        util = null;
    }

    private static RecycleCall mRecycleCall;

    public static void setOnRecycleCallListener(RecycleCall recycleCallListener){
        mRecycleCall = recycleCallListener;
    }

    public static void confirmRecycleCall(boolean should){
        mRecycleCall.onRecycleCall(should);
    }

    public interface RecycleCall{
        void onRecycleCall(boolean should);
    }

    public static int getPrimaryTextColor(Context context){
        TypedValue typedValue = new TypedValue();
        TypedArray typedArray = context.obtainStyledAttributes(typedValue.data, new int[]{android.R.attr.textColorPrimary});
        int textColorPrimary = typedArray.getColor(0, 0);
        typedArray.recycle();
        return textColorPrimary;
    }

    /*setting up the listener*/

    private onImageSuccessfulLoad mOnImageSuccesssfulLoad;

    void confirmOnImageLoad(Bitmap bitmap){
        mOnImageSuccesssfulLoad.onImageLoad(bitmap);
    }

    public void setOnImageLoadListener(onImageSuccessfulLoad onImageLoadListener){
        mOnImageSuccesssfulLoad = onImageLoadListener;
    }

    public interface onImageSuccessfulLoad{
        void onImageLoad(Bitmap bitmap);
    }
}

class loadInBackground extends AsyncTask<Object, Integer, Object[]>{

    @Override
    protected Object[] doInBackground(Object... objects) {
        Util util = (Util) objects[1];
        Util.link_type link_type = (Util.link_type) objects[2];
        Bitmap bitmap = null;
        if (link_type == Util.link_type.URL){
            String s = (String) objects[0];
            try {
                URL url = new URL(s);
                if (util.checkNetworkStatus()) {
                    try {
                        bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return new Object[]{bitmap, util};
                } else {
                    return new Object[]{null, util};
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        if (link_type == Util.link_type.URI){
            try {
                Uri uri = (Uri) objects[0];
                if (util.getmContext() == null) throw new IllegalStateException("please pass the context using the setter method");
                bitmap = MediaStore.Images.Media.getBitmap(util.getmContext().getContentResolver(), uri);

                return new Object[]{bitmap, util};
            } catch (IOException | ClassCastException e1) {
                Log.e("CAKE", e1.toString());
            }
        }
        /*if (_url.contains("http")) {

        }*/

        return new Object[]{null, util};
    }

    @Override
    protected void onPostExecute(Object[] objects) {
        super.onPostExecute(objects);
        Bitmap bitmap = (Bitmap) objects[0];
        Util util = (Util) objects[1];
        util.confirmOnImageLoad(bitmap);
    }
}
