package com.chinalwb.are.styles.toolitems.styles;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AlignmentSpan;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
//todo:enablee this whent he bug is no longer present
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
import com.chinalwb.are.spans.AreImageSpan;
import com.chinalwb.are.styles.IARE_Image;
import com.chinalwb.are.styles.IARE_Style;
import com.chinalwb.are.styles.windows.ImageSelectDialog;
//todo: enable this when the bug is gone
/*import com.chinalwb.are.glidesupport.GlideApp;
import com.chinalwb.are.glidesupport.GlideRequests;*/

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade;
import static com.chinalwb.are.spans.AreImageSpan.ImageType;

public class ARE_Style_Image implements IARE_Style, IARE_Image {

	public static int REQUEST_CODE = 1001;

	public static int DRAWING_CODE = 289;

	private ImageView mInsertImageView;

	private AREditText mEditText;

	private Context mContext;
//todo: enable this too
    /*private static GlideRequests sGlideRequests;*/

    private static int sWidth = 0;

    /**
	 *
	 * @param emojiImageView the emoji image view
	 */
	public ARE_Style_Image(AREditText editText, ImageView emojiImageView) {
		this.mEditText = editText;
		this.mInsertImageView = emojiImageView;
		this.mContext = emojiImageView.getContext();
		//todo: enable this too
		/*sGlideRequests = GlideApp.with(mContext);*/
        sWidth = Util.getScreenWidthAndHeight(mContext)[0];
		setListenerForImageView(this.mInsertImageView);
	}

	@Override
	public void setListenerForImageView(ImageView imageView) {
		imageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				openImageChooser();
			}
		});
	} // #End of setListenerForImageView(..)

	/**
	 * Open system image chooser page.
	 */
	private void openImageChooser() {
		ImageSelectDialog dialog = new ImageSelectDialog(mContext, this, REQUEST_CODE, DRAWING_CODE);
		dialog.show();
	}

	/**
	 *
	 */
	public void insertImage(final Object src, final ImageType type) {
		// Note for a possible bug:
		// There may be a possible bug here, it is related to:
		//   https://issuetracker.google.com/issues/67102093
		// But I forget what the real use case is, just remember that
		// When working on the feature, there was a crash bug
		//
		// That's why I introduce the method below:
	    // this.mEditText.useSoftwareLayerOnAndroid8();
		//
		// However, with this setting software layer, there is another
		// bug which is when inserting a few (2~3) images, there will
		// be a warning:
		//
		// AREditText not displayed because it is too large to fit into a software layer (or drawing cache), needs 17940960 bytes, only 8294400 available
		//
		// And then the EditText becomes an entire white nothing displayed
		//
		// So in temporary, I commented out this method invoke to prevent this known issue
		// When someone run into the crash bug caused by this on Android 8
		// I can then find out a solution to cover both cases
		//todo: enable this too
		/*SimpleTarget myTarget = new SimpleTarget<Bitmap>() {
			@Override
			public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
				if (bitmap == null) { return; }

                bitmap = Util.scaleBitmapToFitWidth(bitmap, sWidth);
                ImageSpan imageSpan = null;
                if (type == ImageType.URI) {
                    imageSpan = new AreImageSpan(mContext, bitmap, ((Uri) src));
				} else if (type == ImageType.URL) {
					imageSpan = new AreImageSpan(mContext, bitmap, ((String) src));
				}
				if (imageSpan == null) { return; }
				insertSpan(imageSpan);
			}
		};*/

        if (type == ImageType.URI) {
        	//todo    do:enable this too
            /*sGlideRequests.asBitmap().load((Uri) src).centerCrop().into(myTarget);*/
            /*Bitmap bitmap;*/
            /*try {
            	Util


                bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), (Uri)src);
				bitmap = Util.scaleBitmapToFitWidth(bitmap, sWidth);
				ImageSpan imageSpan = new AreImageSpan(mContext, bitmap, (Uri) src);
				insertSpan(imageSpan);
            } catch (IOException e) {
                e.printStackTrace();
            }*/

            //int pixel = Util.getPixelByDp(mContext, 200);
			Glide.with(mContext)
					.asBitmap()
					.load((Uri)src)
					.placeholder(R.drawable.image_place_holder)
					.error(R.drawable.image_place_holder)
					.transition(withCrossFade())
					.into(new CustomTarget<Bitmap>() {
						@Override
						public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
							resource = Util.scaleBitmapToFitWidth(resource, Util.getScreenWidthAndHeight(mContext)[0]);
							ImageSpan imageSpan = new AreImageSpan(mContext, resource, (Uri) src);
							insertSpan(imageSpan);
						}

						@Override
						public void onLoadCleared(Drawable placeholder) {

						}
					});
			/*if (isDownscaledEnabled){
				requestCreator.resize(pixel, pixel);
				requestCreator.centerCrop();
			}*/

            /*final Util util = new Util();
            util.setmContext(mContext);
            util.loadImageInBackground((Uri) src, util);
            util.setOnImageLoadListener(new Util.onImageSuccessfulLoad() {
				@Override
				public void onImageLoad(Bitmap bitmap) {
					if (bitmap != null){
						bitmap = Util.scaleBitmapToFitWidth(bitmap, sWidth);
						ImageSpan imageSpan = new AreImageSpan(mContext, bitmap, (Uri) src);
						insertSpan(imageSpan);
					}
				}
			});*/

        } else if (type == ImageType.URL) {
        	//todo:enable this too
        	    /*sGlideRequests.asBitmap().load((String) src).centerCrop().into(myTarget);*/
			Glide.with(mContext)
					.asBitmap()
					.load((String) src)
					.placeholder(R.drawable.image_place_holder)
					.error(R.drawable.image_place_holder)
					.transition(withCrossFade())
					.into(new CustomTarget<Bitmap>() {
						@Override
						public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
							resource = Util.scaleBitmapToFitWidth(resource, Util.getScreenWidthAndHeight(mContext)[0]);
							ImageSpan imageSpan = new AreImageSpan(mContext, resource, (String) src);
							insertSpan(imageSpan);
						}

						@Override
						public void onLoadCleared(Drawable placeholder) {

						}
					});
			/*if (isDownscaledEnabled){
				requestCreator.resize(pixel, pixel);
				requestCreator.centerCrop();
			}*/
        	    /*final Util util = new Util();
        	    util.setmContext(mContext);
        	    util.loadImageInBackground((String) src, util);
        	    util.setOnImageLoadListener(new Util.onImageSuccessfulLoad() {
					@Override
					public void onImageLoad(Bitmap bitmap) {
						if (bitmap != null) {
							bitmap = Util.scaleBitmapToFitWidth(bitmap, sWidth);
							ImageSpan imageSpan = new AreImageSpan(mContext, bitmap, (String) src);
							insertSpan(imageSpan);
						}else {
							bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.image_place_holder);
							ImageSpan imageSpan = new AreImageSpan(mContext, bitmap, (String) src);
							insertSpan(imageSpan);
						}
					}
				});*/
		} else if (type == ImageType.RES) {
            ImageSpan imageSpan = new AreImageSpan(mContext, ((int) src));
            insertSpan(imageSpan);
        }
	}

	private void insertSpan(ImageSpan imageSpan) {
		Editable editable = this.mEditText.getEditableText();
		int start = this.mEditText.getSelectionStart();
		int end = this.mEditText.getSelectionEnd();

		AlignmentSpan centerSpan = new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER);
		SpannableStringBuilder ssb = new SpannableStringBuilder();
		ssb.append(Constants.CHAR_NEW_LINE);
		ssb.append(Constants.ZERO_WIDTH_SPACE_STR);
		ssb.append(Constants.CHAR_NEW_LINE);
		ssb.append(Constants.ZERO_WIDTH_SPACE_STR);
		ssb.setSpan(imageSpan, 1, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		ssb.setSpan(centerSpan, 1, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		AlignmentSpan leftSpan = new AlignmentSpan.Standard(Layout.Alignment.ALIGN_NORMAL);
		ssb.setSpan(leftSpan, 3,4, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

		editable.replace(start, end, ssb);
	}

	@Override
	public void applyStyle(Editable editable, int start, int end) {
		// Do nothing
	}

	@Override
	public ImageView getImageView() {
		return this.mInsertImageView;
	}

	@Override
	public void setChecked(boolean isChecked) {
		// Do nothing
	}

	@Override
	public boolean getIsChecked() {
		return false;
	}

	@Override
	public EditText getEditText() {
		return this.mEditText;
	}
}
