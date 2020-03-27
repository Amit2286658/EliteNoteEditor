package com.me.android.noteeditor.CommonUtility;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Spanned;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.chinalwb.are.AREditText;
import com.chinalwb.are.Util;
import com.chinalwb.are.android.inner.Html;
import com.chinalwb.are.spans.AreVideoSpan;
import com.chinalwb.are.styles.toolitems.IARE_ToolItem;
import com.chinalwb.are.styles.toolitems.styles.ARE_Style_Video;
import com.me.android.noteeditor.Annotatons.Dip;
import com.me.android.noteeditor.BlurUtility.ApplyBlurOnDialog;
import com.me.android.noteeditor.MainActivity;
import com.me.android.noteeditor.R;
import com.me.android.noteeditor.Recyclerview_adapter.commonDataModel.EditingContentModelData;
import com.me.android.noteeditor.contract.DataBaseManager;
import com.me.android.noteeditor.permissionUtility.permissionUtility;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jaredrummler.cyanea.Cyanea;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.core.widget.NestedScrollView;

public class utilityClass {

    private static AlertDialog realDialog;
    private ApplyBlurOnDialog apr;

    private static final String RES_NAME = "status_bar_height", DEF_TYPE = "dimen", DEF_PACKAGE = "android",
    NAV_BAR_NAME = "config_showNavigationBar", NAV_BAR_DEF_TYPE = "bool", NAV_BAR_HEIGHT = "navigation_bar_height";

    public static final int TYPE_IMAGE = 1, TYPE_VIDEO = 2;

    public static final int ATTRIBUTE_INT = 1, ATTRIBUTE_STRING = 2, ATTRIBUTE_BOOLEAN = 3, ATTRIBUTE_FLOAT = 4,
            ATTRIBUTE_DRAWABLE = 5, ATTRIBUTE_COLOR = 6, ATTRIBUTE_FONT = 7;

    public static final int SKIP_STATUS_BAR = 1;

    public static final float IDEAL_LIGHTEN_FACTOR = 0.15f;

    private static String storage_link;

    public static void setStorage_link(String storagee_link) {
        utilityClass.storage_link = storagee_link;
    }
    public enum ContentType {
        IMAGE, VIDEO
    }

    public static String getStorage_link() {
        return storage_link;
    }

    public static int getDipValue(Context context, @Dip float dipValue){
        Resources res = context.getResources();
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dipValue,
                res.getDisplayMetrics()
        );
    }

    public static float textSize(int textSize){
        switch (textSize){
            case 0:
                return 14;
            case 1:
                return 16;
            case 2:
                return 18;
            case 3:
                return 20;
            case 4:
                return 22;
            case 5:
                return 24;
            case 6:
                return 25;
            case 7:
                return 26;
            case 8:
                return 27;
            default: return -1;
        }
    }

    public static int alphaProvider(int defValue){
        int baseDefValue = 51/2;
        switch (defValue){
            case 0 :
                return baseDefValue * 10;
            case 1 :
                return baseDefValue * 9;
            case 2 :
                return baseDefValue * 8;
            case 3 :
                return baseDefValue * 7;
            case 4 :
                return baseDefValue * 6;
            case 5 :
                return baseDefValue * 5;
            case 6 :
                return baseDefValue * 4;
            case 7 :
                return baseDefValue * 3;
            case 8 :
                return baseDefValue * 2;
            case 9 :
                return baseDefValue;
            default: return -1;
        }
    }

    public static void setFrameLayoutParams(Context context, View view, float margin){
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(getDipValue(context, margin),
                getDipValue(context, margin),
                getDipValue(context, margin),
                0);
        view.setLayoutParams(layoutParams);
    }

    public static double similarTo(int c1, int c2){
        double l1, a1, b1, l2, a2, b2;
        double[] c1Outlab = new double[3];
        double[] c2Outlab = new double[3];
        ColorUtils.colorToLAB(c1, c1Outlab);
        ColorUtils.colorToLAB(c2, c2Outlab);
        l1 = c1Outlab[0];
        a1 = c1Outlab[1];
        b1 = c1Outlab[2];
        l2 = c2Outlab[0];
        a2 = c2Outlab[1];
        b2 = c2Outlab[2];
        //get delta e
        return Math.sqrt(((l2-l1)*(l2-l1)) + ((a2-a1)*(a2-a1)) + ((b2-b1)*(b2-b1)));
    }

    public static boolean colorSimilarity(int c1, int c2){
        float threshold = 24f;
        return similarTo(c1, c2) <= threshold;
    }

    public static void restartApplication(Context context){
        Intent intent = new Intent(context, MainActivity.class);
        int mPendingIntentId = 123;
        PendingIntent pendingIntent =
                PendingIntent.getActivity(context, mPendingIntentId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis()+50, pendingIntent);
        //android.os.Process.killProcess(mPendingIntentId);
        System.exit(0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setTint(Context context, int resId, FloatingActionButton fabButton){
        Drawable drawable = ContextCompat.getDrawable(context, resId);
        int c1 = Cyanea.getInstance().getAccent();
        int c2 = Color.parseColor("#FFFFFFFF");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            if (colorSimilarity(c1, c2)){
                drawable.setTint(Color.BLACK);
                fabButton.setImageDrawable(drawable);
            }
        }
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setTint(MaterialButton button){
        int c1 = Cyanea.getInstance().getAccent();
        int c2 = Color.parseColor("#FFFFFFFF");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            if (colorSimilarity(c1, c2)){
                button.setTextColor(Color.BLACK);
            }
        }
    }

    public static int lightenTheColor(int color, float factor){
        int red = (int)((Color.red(color) * ( 1 - factor ) / 255 + factor ) * 255);
        int green = (int)((Color.green(color) * ( 1 - factor ) / 255 + factor ) * 255);
        int blue = (int)((Color.blue(color) * ( 1 - factor ) / 255 + factor ) * 255);
        return Color.argb(Color.alpha(color), red, green, blue);
    }

    public static String getNotePictureDirectory(){
        return
                Environment.getExternalStorageDirectory()
                        + File.separator
                        + "NOTE_EDITOR"
                        + File.separator
                        + "SAVED_NOTES_AS_PICTURES"
                        + File.separator;
    }

    public static String getNoteHtmlDirectory(){
        return
                Environment.getExternalStorageDirectory()
                        + File.separator
                        + "NOTE_EDITOR"
                        + File.separator
                        + "SAVED_NOTES_AS_HTML"
                        + File.separator;
    }

    public static String getNoteTextDirectory(){
        return
                Environment.getExternalStorageDirectory()
                        + File.separator
                        + "NOTE_EDITOR"
                        + File.separator
                        + "SAVED_NOTES_AS_TEXT"
                        + File.separator;
    }

    public static String getNoteShareDirectory(){
        return
                Environment.getExternalStorageDirectory()
                        + File.separator
                        + "NOTE_EDITOR"
                        + File.separator
                        + "SHARED_NOTES"
                        + File.separator;
    }

    public static String getNoteTempDirectory(){
        return
                Environment.getExternalStorageDirectory()
                        + File.separator
                        + "NOTE_EDITOR"
                        + File.separator
                        + "TEMPORARY_DIRECTORY"
                        + File.separator;
    }

    public static Bitmap getViewSnap(View view){
        /*view.setDrawingCacheEnabled(true);*/
        NestedScrollView nestedScrollView = (NestedScrollView) view;
        /*int h = nestedScrollView.getChildAt(0).getHeight();
        int w = nestedScrollView.getChildAt(0).getWidth();
        view.layout(0, 0, w, h);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);*/
        int h = nestedScrollView.getChildAt(0).getHeight();
        int w = nestedScrollView.getChildAt(0).getWidth();
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Drawable drawable = nestedScrollView.getBackground();
        if (drawable != null){
            drawable.draw(canvas);
        }else {
            canvas.drawColor(Cyanea.getInstance().getBackgroundColor());
        }
        nestedScrollView.getChildAt(0).draw(canvas);
        /*Bitmap bitmap = Bitmap.createBitmap(
                nestedScrollView.getChildAt(0).getWidth(),
                nestedScrollView.getChildAt(0).getHeight(),
                Bitmap.Config.ARGB_8888
        );
        Canvas c = new Canvas(bitmap);
        nestedScrollView.getChildAt(0).draw(c);*/
        return bitmap;
    }

    public static File saveBitmap(Bitmap bm, Context context, String directoryPath){
        /*String directoryPath =
                Environment.getExternalStorageDirectory()
                        + File.separator
                        + "ELITE_NOTE_EDITOR"
                        + File.separator
                        + "DOODLE_IMAGES"
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
            Toast.makeText(context, "Image saved to : "+directoryPath+fname, Toast.LENGTH_LONG).show();
            bm.recycle();
            return  file;

        } catch (Exception e) {
            e.printStackTrace();
            return  null;
        }
    }



    public static boolean writeToFile(Context context, String dataToWrite, String directoryPath, String fileName, String ext) {
        /*SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        boolean html = sp.getBoolean(context.getString(R.string.html_selector_preference_key), false);*/

        /*String directoryPath =
                Environment.getExternalStorageDirectory()
                        + File.separator
                        + "ELITE_NOTE_EDITOR"
                        + File.separator;*/

        // Create the fileDirectory.
        File fileDirectory = new File(directoryPath);

        // Make sure the directoryPath directory exists.
        if (!fileDirectory.exists()) {
            fileDirectory.mkdirs();
        }

        try {
            // Create FIle Objec which I need to write
            /*String ext = html ? ".html" : ".txt";*/
            File fileToWrite = new File(directoryPath, fileName + ext);

            // ry to create FIle on card
            if (fileToWrite.createNewFile()) {
                //Create a stream to file path
                FileOutputStream outPutStream = new FileOutputStream(fileToWrite);
                //Create Writer to write Stream to file Path
                OutputStreamWriter outPutStreamWriter = new OutputStreamWriter(outPutStream);
                // Stream Byte Data to the file
                outPutStreamWriter.append(dataToWrite);
                //Close Writer
                outPutStreamWriter.close();
                //Clear Stream
                outPutStream.flush();
                //Terminate STream
                outPutStream.close();

                Toast.makeText(context, context.getString(R.string.file_saved_to_)
                        +directoryPath+fileName+ext, Toast.LENGTH_LONG).show();
                return true;
            } else {
                return false;
            }

        } catch (IOException e) {
            e.fillInStackTrace();
            return false;
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

    public AlertDialog DialogBlur(Context context, AlertDialog.Builder alertDialog){
        apr = new ApplyBlurOnDialog((Activity)context);
        realDialog = apr.getRealDialog(alertDialog);

        WindowManager.LayoutParams windowManager = realDialog.getWindow().getAttributes();
        windowManager.windowAnimations = R.style.dialog_alpha_animation;
        realDialog.getWindow().setAttributes(windowManager);

        realDialog.show();
        apr.setBackgroundDrawable(apr);
        apr.clearDialogDimming(new Dialog[]{realDialog});
        return realDialog;
    }

    @Deprecated
    public void DialogBlur(Context context, Dialog dialog){
        apr = new ApplyBlurOnDialog((Activity) context);
        realDialog = (AlertDialog) dialog;
        realDialog.show();
        apr.setBackgroundDrawable(apr);
        apr.clearDialogDimming(new Dialog[]{realDialog});
    }

    public AlertDialog simpleDialogBlur(Context context, String s){
        apr = new ApplyBlurOnDialog((Activity)context);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(s);
        builder.setPositiveButton(context.getString(R.string.save), null);
        builder.setNegativeButton(context.getString(R.string.Discard), null);

        realDialog = apr.getRealDialog(builder);

        apr.setBackgroundDrawable(apr);

        /*realDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;*/
        WindowManager.LayoutParams windowManager = realDialog.getWindow().getAttributes();
        windowManager.windowAnimations = R.style.dialog_alpha_animation;
        realDialog.getWindow().setAttributes(windowManager);

        realDialog.show();
        apr.clearDialogDimming(new Dialog[]{realDialog});
        return realDialog;
    }

    public AlertDialog simpleDialogBlur(Context context, String content, String positive, String negative, String neutral){
        apr = new ApplyBlurOnDialog((Activity)context);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (content != null)
            builder.setMessage(content);
        if (positive != null)
            builder.setPositiveButton(positive, null);
        if (negative != null)
            builder.setNegativeButton(negative, null);
        if (neutral != null)
            builder.setNeutralButton(neutral, null);

        realDialog = apr.getRealDialog(builder);

        apr.setBackgroundDrawable(apr);

        WindowManager.LayoutParams windowManager = realDialog.getWindow().getAttributes();
        windowManager.windowAnimations = R.style.dialog_alpha_animation;
        realDialog.getWindow().setAttributes(windowManager);

        realDialog.show();
        apr.clearDialogDimming(new Dialog[]{realDialog});
        return realDialog;
    }

    /*public AlertDialog simpleDialogBlur(Context context, int resId, String positive, String negative, String neutral){
        apr = new ApplyBlurOnDialog((Activity)context);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (resId != 0)
            builder.setItems(resId, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
        if (positive != null)
            builder.setPositiveButton(positive, null);
        if (negative != null)
            builder.setNegativeButton(negative, null);
        if (neutral != null)
            builder.setNeutralButton(neutral, null);

        realDialog = apr.getRealDialog(builder);

        apr.setBackgroundDrawable(apr);

        WindowManager.LayoutParams windowManager = realDialog.getWindow().getAttributes();
        windowManager.windowAnimations = R.style.dialog_alpha_animation;
        realDialog.getWindow().setAttributes(windowManager);

        realDialog.show();
        apr.clearDialogDimming(new Dialog[]{realDialog});
        return realDialog;
    }*/

    public void cancelDialog(){
        apr.removeView();
        if (realDialog.isShowing()) realDialog.dismiss();

    }

    public static void cancelDialog(AlertDialog alertDialog){
        if (alertDialog.isShowing()) alertDialog.dismiss();
    }

    /*
    * this is an additional helper method that does the workaround
    * and saves updates of the data to notes database 2 times,
    * saving the 2 times, with even a slight change the 2nd time reflects the changes done the first time
    * it's due to a bug and this helper method is just a workaround, and not a permanent solution*/
    public static void updateWorkaround(Context context,
                                      int id, String title, String htmlContent, String currentTime, String folderName){
        DataBaseManager dbManager = new DataBaseManager(context);
        dbManager.updateEntry(id, title, htmlContent, currentTime, folderName);

        AREditText arEditText = new AREditText(context);
        arEditText.fromHtml(htmlContent);
        String newHtmlContent = arEditText.getHtml();
        dbManager.updateEntry(id, title, newHtmlContent, currentTime, folderName);
        arEditText = null;
    }

    /*below are permission related helper methods to reduce the amount of code,
    * and bring in the consistency across all the classes that uses such function or feature*/
    /*
    * INCOMPLETE
    */
    public static boolean checkBuildVersion(){
        return Build.VERSION.SDK_INT >= 23;
    }

    public static void PermissionCheck(Context context, String... permissions){
        if (checkBuildVersion()) {
            for (String permission : permissions){
                if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED){
                    if (permissionUtility.ShowPermissionRationale((Activity) context, permission)){

                    }
                }
            }
        }
    }


    /*
    * INCOMPLETE
    */
    public static void setMediaAddingListener(Context context, int mediaType){

    }

    public static void checkInitialPermission(Context context, String message){
        if (checkBuildVersion()){

        }
    }

    public static void AddVideo(Context context, final Uri uri, final String videoUrl, IARE_ToolItem are_style_video){
        String path = Util.GetPathFromUri4kitkat.getPath(context, uri);
        Bitmap thumb = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Images.Thumbnails.MINI_KIND);

        Bitmap play = BitmapFactory.decodeResource(context.getResources(), com.chinalwb.are.R.drawable.play);
        Bitmap video = Util.mergeBitmaps(thumb, play);
        AreVideoSpan videoSpan = new AreVideoSpan(context, video, path, videoUrl);
        ((ARE_Style_Video)are_style_video).insertSpan(videoSpan);
    }


    public static Spanned parseHtml(String htmlContent){
        return Html.fromHtml(htmlContent, Html.FROM_HTML_MODE_COMPACT);
    }

    public static void fixDimension(Context context, View view, int argument){
        /*int resId = ((Activity)context).getResources().getIdentifier(RES_NAME, DEF_TYPE, DEF_PACKAGE);
        if (resId > 0){
            int systemStatusBarHeight = context.getResources().getDimensionPixelOffset(resId);*/
        int systemStatusBarHeight = getStatusBarHeight(context);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        );
        if (argument == SKIP_STATUS_BAR) {
            params.setMargins(0, 0, 0, getNavBarHeight(context));
            view.setLayoutParams(params);
        }else {
            params.setMargins(0, systemStatusBarHeight, 0, getNavBarHeight(context));
            view.setLayoutParams(params);
        }
        //}
    }

    public static boolean checkHasNavBar(Context context){
        int resId = context.getResources().getIdentifier(NAV_BAR_NAME, NAV_BAR_DEF_TYPE, DEF_PACKAGE);
        return resId > 0 && context.getResources().getBoolean(resId);
    }

    public static int getNavBarHeight(Context context){
        if (checkHasNavBar(context)){
            int resId = context.getResources().getIdentifier(NAV_BAR_HEIGHT, DEF_TYPE, DEF_PACKAGE);
            if (resId > 0){
                return context.getResources().getDimensionPixelOffset(resId);
            }return 0;
        }else return 0;
    }

    public static Object[] getFirstImageType(String htmlContent){
        Document document = Jsoup.parse(htmlContent);
        Element element = document.select("img").first();
        if (htmlContent.contains("<img src")) {
            String s = element.attr("src");
            return new Object[]{s, TYPE_IMAGE};
        }else if (htmlContent.contains("<video src")){
            Element element1 = document.select("video").first();
            String s = element1.attr("src");
            return new Object[]{s, TYPE_VIDEO};
        } else return new Object[]{"", 0};
    }

    public static String getFirstVideoLink(String content){
        Document document = Jsoup.parse(content);
        Element element = document.select("video").first();
        return element.attr("src");
    }

    public static Bitmap resize(Context context, Bitmap bitmap, float height, float width){
        int h = getDipValue(context, height);
        int w = getDipValue(context, width);
        int bitmapHeight = bitmap.getHeight();
        int bitmapWidth = bitmap.getWidth();

        float scaleWidth = ((float) w)/bitmapWidth;
        float scaleHeight = ((float) h)/bitmapHeight;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap resizedBitmap = Bitmap.createBitmap(
                bitmap, 0, 0, bitmapWidth, bitmapHeight, matrix, false
        );
        bitmap.recycle();
        return resizedBitmap;
    }

    public static BitmapDrawable getBitmapDrawable(Context context, Bitmap bitmap){
        return new BitmapDrawable(context.getResources(), bitmap);
    }

    @NonNull
    public static ArrayList<String> cutContent(@NonNull String htmlContent){
//        String alignment_start = "start", alignment_center = "center";

        ArrayList<String> arrayList = new ArrayList<>();

        String prefix = "<html><body>";
        String suffix = "</body></html>";
        String imageTag_center = "<p style=\"text-align:center;\"><img src";
        String imageTag_start = "<p style=\"text-align:start;\"><img src";
        String videoTag_center = "<p style=\"text-align:center;\"><video src";
        String videoTag_start = "<p style=\"text-align:start;\"><video src";

        if (!htmlContent.contains(imageTag_center) && !htmlContent.contains(imageTag_start)
                /*&& !htmlContent.contains(videoTag_center) && !htmlContent.contains(videoTag_start)*/) {
            ArrayList<String> str = new ArrayList<>();
            str.add(htmlContent);
            return str;
        }

        if (htmlContent.startsWith(prefix))
            htmlContent = htmlContent.replace(prefix, "");
        if (htmlContent.endsWith(suffix))
            htmlContent = htmlContent.replace(suffix, "");

        String[] cutList;
        /*if (htmlContent.contains(imageTag_center)) {
            cutList = htmlContent.split(imageTag_center);
        }else {*/
            cutList = htmlContent.split(imageTag_center+"|"+imageTag_start);
        //}

        for (int i = 0; i < cutList.length; i++){
            /*if (isImageStart){
                if (i == 0) continue;
                if (i == 1) {
                    cutList[i] = imageTag_center + cutList[i];
                }
                else {
                    cutList[i] = imageTag_start+cutList[i];
                }
            }else {*/
                if (i == 0) continue;
                cutList[i] = imageTag_center+cutList[i];
            //}
        }

        /*for (int i = 1; i < cutList.length; i++){
            if (cutList[i].contains(imageTag_start)){

            }
        }
        for(int i = 1; i == cutList.length; i++){
            cutList[i] = imageTag+cutList[i];
        }*/
        arrayList.addAll(Arrays.asList(cutList));
        return /*arrayList*/getImageAndHtmlTextSeperately(AddHtmlSuffixAndPrefix(cutList));
    }

    public static ArrayList<String> getImageAndHtmlTextSeperately(String[] list){
        String prefix = "<html><body>";
        String suffix = "</body></html>";
        String imageTag_center = "<p style=\"text-align:center;\"><img src";
        String imageTag_start = "<p style=\"text-align:start;\"><img src";
        ArrayList<String> arrayList = new ArrayList<>();

        for (int i = 0; i < list.length; i++){
            if (list[i].contains(imageTag_center) || list[i].contains(imageTag_start)){
                String image_uri = (String) getFirstImageType(list[i])[0];
                arrayList.add(image_uri);

                if (list[i].startsWith(prefix)) list[i] = list[i].replace(prefix, "");
                if (list[i].endsWith(suffix)) list[i] = list[i].replace(suffix, "");
                String[] cutList = list[i].split("</p>", 2);
                cutList = AddHtmlSuffixAndPrefix(cutList);
                arrayList.add(cutList[1]);
            }else{
                arrayList.add(list[i]);
            }
        }
        return arrayList;

        /*String imageTag_center = "<p style=\"text-align:center;\"><img src";
        String imageTag_start = "<p style=\"text-align:start;\"><img src";
        ArrayList<String> list1 = new ArrayList<>();
        for (int i = 0; i < list.length; i++){
            if (list[i].contains(imageTag_center) || list[i].contains(imageTag_start)) {

                String[] cutList = list[i].split(imageTag_center+"|"+imageTag_start);
                for (String str : cutList){
                    if ();
                }
            }else {
                list1.add(list[i]);
            }
        }
        return list1;*/
    }

    public static ArrayList<EditingContentModelData> getVideoAndImageAndHtmlTextSeperately(ArrayList<String> list){
        String prefix = "<html><body>";
        String suffix = "</body></html>";
        String videoTag_center = "<p style=\"text-align:center;\"><video src";
        String videoTag_start = "<p style=\"text-align:start;\"><video src";
        String delimiter = "</video></p>";
        ArrayList<EditingContentModelData> datalist = new ArrayList<>();
        for (String item : list){
            if (item.startsWith("http")){
                EditingContentModelData model = new EditingContentModelData();
                model.setImage_link(item);
                model.setType(EditingContentModelData.EditingContentType.IMAGE);
                model.setLinkType(EditingContentModelData.EditingContentLinkType.URL);
                datalist.add(model);
            }else if (item.startsWith("content")){
                EditingContentModelData modelData = new EditingContentModelData();
                modelData.setImage_uri(Uri.parse(item));
                modelData.setType(EditingContentModelData.EditingContentType.IMAGE);
                modelData.setLinkType(EditingContentModelData.EditingContentLinkType.URI);
                datalist.add(modelData);
            }else {
                if (item.contains(videoTag_start) || item.contains(videoTag_center)){

                    if (item.startsWith(prefix)) item = item.replace(prefix, "");
                    if (item.endsWith(suffix)) item = item.replace(suffix, "");

                    String[] cutList = item.split(videoTag_center+"|"+videoTag_start);
                    for (int i = 0; i < cutList.length; i++){
                        if (i == 0) continue;
                        cutList[i] = videoTag_center + cutList[i];
                    }
                    for (int i = 0; i < cutList.length; i++){
                        if (i == 0){
                            String[] str = new String[]{cutList[0]};
                            str = AddHtmlSuffixAndPrefix(str);
                            EditingContentModelData modelData = new EditingContentModelData();
                            modelData.setHtml_text(str[0]);
                            modelData.setType(EditingContentModelData.EditingContentType.TEXT);
                            datalist.add(modelData);
                            continue;
                        }

                        String video_link = getFirstVideoLink(cutList[i]);

                        EditingContentModelData modelData = new EditingContentModelData();
                        modelData.setVideo_link(video_link);
                        modelData.setType(EditingContentModelData.EditingContentType.VIDEO);
                        modelData.setLinkType(EditingContentModelData.EditingContentLinkType.URI);
                        datalist.add(modelData);

                        String[] newSplittedString = cutList[i].split(delimiter, 2);
                        String[] str1 = new String[]{newSplittedString[newSplittedString.length - 1]};
                        str1 = AddHtmlSuffixAndPrefix(str1);

                        EditingContentModelData modelData1 = new EditingContentModelData();
                        modelData1.setHtml_text(str1[0]);
                        modelData1.setType(EditingContentModelData.EditingContentType.TEXT);
                        datalist.add(modelData1);
                    }
                }else {
                    EditingContentModelData modelData = new EditingContentModelData();
                    modelData.setHtml_text(item);
                    modelData.setType(EditingContentModelData.EditingContentType.TEXT);
                    datalist.add(modelData);
                }
            }
        }
        return datalist;
    }

    public static String[] AddHtmlSuffixAndPrefix(String[] array){
        String[] list = new String[array.length];
        String prefix = "<html><body>";
        String suffix = "</body></html>";
        /*for (String str : array){
            if (str.startsWith(prefix)) {
                str.replace(prefix, "");
            } else if (str.endsWith(suffix)) {
                str.replace(suffix, "");
            }
        }*/
        for (int i = 0; i < list.length; i++){
            list[i] = prefix+array[i]+suffix;
        }
        return list;
    }

    /*
    * Most of the images are in the bitmap type already,
    * and therefore does not require any sampling,
    * which is required during the decoding when the image
    * is being decoded from  file, which is not a case here,
    * and getting path using {@Link new Uri()#getPath()} , causes the application to force close
    * therefore, i'm deprecating this method. however, the helper methods can come in handy in future
    * therefore, they've public modifier and are static in nature
    * see {@Link utilityClass#calculateInSampleSize()}
    */
    @Deprecated
    public static Bitmap getImageThumbnail(Context context, String path, @Dip int dipsize) throws IOException {
        int pixel = getDipValue(context, dipsize);

        if (path.startsWith("http")) {
            URL url = new URL(path);

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(url.openConnection().getInputStream(), null, options);

            options.inSampleSize = calculateInSampleSize(options, pixel, pixel);

            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeStream(url.openConnection().getInputStream(), null, options);
        }else {
            Uri uri = Uri.parse(path);
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;

            BitmapFactory.decodeStream(inputStream, null, options);

            options.inSampleSize = calculateInSampleSize(options, pixel, pixel);

            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeStream(inputStream, null, options);
        }
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        if (!options.inJustDecodeBounds) throw new IllegalArgumentException("please set the inJustDeccodeBounds to be true" +
                "in the BitmapOptions that's being passed as the parameter to the method call");
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static boolean checkIfHostContainsSubstring(String host, String substring){
        boolean booleanToReturn;

        String lowerCaseHost = host.toLowerCase();
        String lowerCaseSubstring = substring.toLowerCase();

        booleanToReturn = lowerCaseHost.contains(lowerCaseSubstring);

        //doing word by word check if the whole substring is not a part of the host string
        if (!booleanToReturn){
            char[] lowerCaseSubstringArray = lowerCaseSubstring.toCharArray();
            for (char c : lowerCaseSubstringArray){
                booleanToReturn = lowerCaseHost.indexOf(c) >= 0;
            }
        }
        return booleanToReturn;
    }

    public static int getStatusBarHeight(Context context){
        int height = 0;
        int resId = context.getResources().getIdentifier(RES_NAME, DEF_TYPE, DEF_PACKAGE);
        if (resId > 0){
            height = context.getResources().getDimensionPixelSize(resId);
        }
        return height;
    }

    /*public static Object getAttributeValueRuntime(Context context, int[] id, int type){
        TypedValue typedValue = new TypedValue();
        TypedArray typedArray = context.obtainStyledAttributes(typedValue.data, id);
        switch (type) {
            case ATTRIBUTE_COLOR :
            int color = typedArray.getColor(0, ContextCompat.getColor(context, android.R.attr.popupBackground));
            typedArray.recycle();
            return (Object) color;

            default: return new Object();
        }
    }*/
}
