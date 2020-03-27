package com.me.android.noteeditor.BlurUtility;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.me.android.noteeditor.CommonUtility.utilityClass;
import com.me.android.noteeditor.R;

import androidx.appcompat.app.AlertDialog;

import static com.me.android.noteeditor.BlurUtility.renderScriptBlur.blurBitmap;
import static com.me.android.noteeditor.BlurUtility.takeScreenShot.ScreenShot;

public class ApplyBlurOnDialog {
    private Activity mActivity;
    private AlertDialog.Builder alertDialog;
    /*static boolean arbitraryDrawable = false;*/
    private View view;

    protected static final String PARAMETER = "alpha";
    protected static final float END_VALUE = 1.0f;

    ImageView imageView;
    ViewGroup decorView;

    @Deprecated
    public ApplyBlurOnDialog(Activity activity, AlertDialog.Builder alertDialog){
        mActivity = activity;
        this.alertDialog = alertDialog;
    }

    public ApplyBlurOnDialog(Activity activity){
        mActivity = activity;
    }

   /* public Dialog getFakeDialog(){
        if (mActivity == null) throw new NullPointerException("Please pass the Activity's instance to the constructor");
        final Dialog fakeDialog = new Dialog(mActivity);
        fakeDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        fakeDialog.getWindow().setGravity(Gravity.CENTER);
        return fakeDialog;
    }*/

    public AlertDialog getRealDialog(AlertDialog.Builder builder){
        final AlertDialog realDialog = builder.create();
        WindowManager.LayoutParams lp = realDialog.getWindow().getAttributes();
        lp.gravity = Gravity.CENTER;
        return realDialog;
    }

    @Deprecated
    public AlertDialog getRealDialog(){
        if (alertDialog == null) throw new NullPointerException("please" +
                " pass the AlertDialog.Builder() \"identifier\" to the constructor");
        final AlertDialog realDialog = alertDialog.create();
        WindowManager.LayoutParams lp = realDialog.getWindow().getAttributes();
        lp.gravity = Gravity.CENTER;
        return realDialog;
    }

    @Deprecated
    public void setBackgroundDrawable(Dialog fake_dialog){
        Object[] objects = new Object[]{mActivity, fake_dialog};
        applyBlurInBackground setBlurDrawableInBackground = new applyBlurInBackground();
        setBlurDrawableInBackground.execute(objects);
    }

    public void setBackgroundDrawable(ApplyBlurOnDialog apr){
        imageView = (ImageView) mActivity.getLayoutInflater().
                inflate(R.layout.custom_blur_image_raw_file, null, false);
        decorView = (ViewGroup) mActivity.getWindow().getDecorView();

        utilityClass.fixDimension(mActivity, imageView, 0);

        Object[] objects = new Object[]{mActivity, apr};
        applyBlurInBackground applyBlurOnPopupInBackground = new applyBlurInBackground();
        applyBlurOnPopupInBackground.execute(objects);
    }

    public void removeView(){

        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(
                imageView, PARAMETER, 1.0f, 0.0f
        );
        objectAnimator.setDuration(300);
        objectAnimator.start();
        objectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                decorView.removeView(imageView);
            }
        });
        /*decorView.removeView(imageView);*/
    }
    /*public void setBackgroundDrawable(View view, Bitmap bitmap, ApplyBlurOnDialog applyBlurOnDialog){
        arbitraryDrawable = true;
        this.view = view;
        Object[] objects = new Object[]{bitmap, applyBlurOnDialog};
        applyBlurInBackground applyBlurInBackground = new applyBlurInBackground();
        applyBlurInBackground.execute(objects);
    }*/

    public View getView() {
        return view;
    }

    /*@Deprecated
    public void setBackgroundDrawable(){
        final Dialog fakeDialog = getFakeDialog();
        Object[] objects = new Object[]{mActivity, fakeDialog};
        applyBlurInBackground applyBlurInBackground = new applyBlurInBackground();
        applyBlurInBackground.execute(objects);
    }*/

    public void clearDialogDimming(Dialog[] dialogs){
        for (Dialog d : dialogs){
            if (!d.isShowing()){
                throw new IllegalStateException("Please show the dialog first" +
                        ", only then the dim reducing effect could take place");
            }
            WindowManager.LayoutParams lp = d.getWindow().getAttributes();
            lp.dimAmount = 0.00f;
            d.getWindow().setAttributes(lp);
        }
    }
}

class applyBlurInBackground extends AsyncTask<Object, Integer, Object[]>{

    //private Dialog fakeDialog;

    /**
     * Override this method to perform a computation on a background thread. The
     * specified parameters are the parameters passed to {@link #execute}
     * by the caller of this task.
     * <p>
     * This method can call {@link #publishProgress} to publish updates
     * on the UI thread.
     *
     * @param objects The parameters of the task.
     * @return A result, defined by the subclass of this task.
     * @see #onPreExecute()
     * @see #onPostExecute
     * @see #publishProgress
     */
    @Override
    protected Object[] doInBackground(Object... objects) {
        /*if (arbitraryDrawable) {
            applyBlurOnDialog = (ApplyBlurOnDialog) objects[1];
            Bitmap bitmap = (Bitmap)objects[0];
            Bitmap blurredBitmap = renderScriptBlur.blurBitmap(applyBlurOnDialog.getView().getContext(), 12, bitmap);
            return  new BitmapDrawable(applyBlurOnDialog.getView().getContext().getResources(), blurredBitmap);
        }
        else {*/
            Activity mActivity = (Activity) objects[0];
            Bitmap plainBitmap = ScreenShot(mActivity);
            Bitmap bitmap = blurBitmap(mActivity, 16, plainBitmap);
            /*int h = bitmap.getHeight();
            int w = bitmap.getWidth();
            int hh = h/2;
            int hw = w/2;
            Bitmap bitmap1 = Bitmap.createScaledBitmap(bitmap, hw, hh, false);
            return new BitmapDrawable(mActivity.getResources(), bitmap1);*/
            BitmapDrawable bitmapDrawable = new BitmapDrawable(mActivity.getResources(), bitmap);

            ApplyBlurOnDialog applyBlurOnDialog = (ApplyBlurOnDialog) objects[1];
        return new Object[]{bitmapDrawable, applyBlurOnDialog};
        //}


        /*Bitmap bitmap = new fastBlur().fastblur(takeScreenShot.ScreenShot(mActivity), 10);*/
        /*Paint paint = new Paint();
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        ColorFilter colorFilter = new LightingColorFilter(0xFFFFFFFF, 0x00222222);
        paint.setColorFilter(colorFilter);
        new Canvas(bitmap).drawBitmap(bitmap, 0, 0, paint);*/
    }

    /**
     * <p>Runs on the UI thread after {@link #doInBackground}. The
     * specified result is the value returned by {@link #doInBackground}.</p>
     *
     * <p>This method won't be invoked if the task was cancelled.</p>
     *
     * @param objects The result of the operation computed by {@link #doInBackground}.
     * @see #onPreExecute
     * @see #doInBackground
     * @see #onCancelled(Object)
     */
    @Override
    protected void onPostExecute(Object[] objects) {
        /*if (arbitraryDrawable){
            applyBlurOnDialog.getView().setBackground(drawable);
        }else*/
        BitmapDrawable bitmapDrawable = (BitmapDrawable) objects[0];

        ApplyBlurOnDialog apr = (ApplyBlurOnDialog) objects[1];

        ViewGroup decorview = apr.decorView;
        ImageView imageView = apr.imageView;

        decorview.addView(imageView);

        Bitmap bitmap = bitmapDrawable.getBitmap();
        imageView.setImageBitmap(bitmap);
        ObjectAnimator objectAnimator = ObjectAnimator.
                ofFloat(imageView, ApplyBlurOnDialog.PARAMETER, ApplyBlurOnDialog.END_VALUE);
        objectAnimator.setDuration(300);
        objectAnimator.start();

        /*Animation animation = AnimationUtils.loadAnimation(context, R.anim.fade_in);

        *//*fakeDialog.getWindow().setBackgroundDrawable(bitmapDrawable);
        View view = fakeDialog.getWindow().getDecorView();
        view.startAnimation(animation);*/
    }

    /**
     * Runs on the UI thread after {@link #publishProgress} is invoked.
     * The specified values are the values passed to {@link #publishProgress}.
     *
     * @param values The values indicating progress.
     * @see #publishProgress
     * @see #doInBackground
     */
    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }
}
