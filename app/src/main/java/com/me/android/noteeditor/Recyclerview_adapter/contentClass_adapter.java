package com.me.android.noteeditor.Recyclerview_adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.me.android.noteeditor.AddingDataActivity;
import com.me.android.noteeditor.CommonUtility.utilityClass;
import com.me.android.noteeditor.R;
import com.me.android.noteeditor.Recyclerview_adapter.itemTouchHelper.itemTouchFeedback;
import com.me.android.noteeditor.contract.DataBaseManager;
import com.me.android.noteeditor.contract.content_class;
import com.me.android.noteeditor.customListener.Adapter_refresh_listener;
import com.me.android.noteeditor.customListener.EmptyData_listener;
import com.me.android.noteeditor.customListener.EmptySuggestionListener;
import com.me.android.noteeditor.customListener.FolderCountChangeListener;
import com.me.android.noteeditor.customSettingsScreen;
import com.me.android.noteeditor.editingActivity;
import com.me.android.noteeditor.fontManager.fontManager;
import com.me.android.noteeditor.snip_preview;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.jaredrummler.cyanea.Cyanea;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.recyclerview.widget.RecyclerView;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.me.android.noteeditor.CommonUtility.utilityClass.TYPE_IMAGE;
import static com.me.android.noteeditor.CommonUtility.utilityClass.TYPE_VIDEO;
import static com.me.android.noteeditor.CommonUtility.utilityClass.getDipValue;
import static com.me.android.noteeditor.CommonUtility.utilityClass.getFirstImageType;
import static com.me.android.noteeditor.CommonUtility.utilityClass.setFrameLayoutParams;

/*import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;*/

public class contentClass_adapter extends RecyclerView.Adapter<viewHolder> implements itemTouchFeedback {

    private List<content_class> data_list;
    private HashMap<ImageView, Bitmap> video_thumbnails = new HashMap<>();
    private HashMap<ImageView, RoundedBitmapDrawable> rounded_video_thumbnails = new HashMap<>();

    private fontManager fontManager;
    private static Typeface typeface;
    private SharedPreferences sharedPrefs;
    private DataBaseManager dataBaseManager;

    private String fonttype;
    private static boolean transparencyOnListItems;

    private utilityClass utilityClass = new utilityClass();

    private PopupWindow popupWindow = null;

    private static EditTitleButtonClickListener mEditTitleButtonClickListener;

    private static float x = -1f;
    private static float y = -1f;

    public contentClass_adapter(){
        //empty constructor
    }



    /**
     * Called when a view created by this adapter has been recycled.
     *
     * <p>A view is recycled when a {@link new LayoutManager} decides that it no longer
     * needs to be attached to its parent {@link RecyclerView}. This can be because it has
     * fallen out of visibility or a set of cached views represented by views still
     * attached to the parent RecyclerView. If an item view has large or expensive data
     * bound to it such as large bitmaps, this may be a good place to release those
     * resources.</p>
     * <p>
     * RecyclerView calls this method right before clearing ViewHolder's internal data and
     * sending it to RecycledViewPool. This way, if ViewHolder was holding valid information
     * before being recycled, you can call {@link viewHolder#viewHolder(View)} .getAdapterPosition()} to get
     * its adapter position.
     *
     * @param holder The ViewHolder for the view being recycled
     */
    @Override
    public void onViewRecycled(viewHolder holder) {
        super.onViewRecycled(holder);
        //Toast.makeText(holder.context, "view is recycled in content class", Toast.LENGTH_SHORT).show();
    }

    public contentClass_adapter(List<content_class> dataList, Context context){

        data_list = dataList;
        fontManager = new fontManager(context);
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        dataBaseManager = new DataBaseManager(context);

        fontManager.initializeFontManager();

        fonttype = sharedPrefs.getString(
                "font_preference_key",
                "Aller_Lt"
        );

        String pathToFont = null;
        try {
            pathToFont = fontManager.getFontPath(fonttype);
        } catch (IOException e) {
            e.printStackTrace();
        }
        typeface = Typeface.createFromAsset(context.getAssets(), pathToFont);

        /*transparencyOnListItems = sharedPrefs.getInt(
                context.getString(R.string.cardView_transparency_key),
                9
        );*/

        final Context finalContext = context;
        Adapter_refresh_listener.setOnAdapterRefreshListener(new Adapter_refresh_listener.AdapterRefreshListener() {
            @Override
            public void shouldRefreshAdapter(boolean should_adapter_refresh) {
                if (should_adapter_refresh)
                    refresh(finalContext);
            }
        });

    }

    public static void setTransparencyOnListItems(boolean transparencyOnListItems) {
        contentClass_adapter.transparencyOnListItems = transparencyOnListItems;
    }

    /**
     * Called when RecyclerView needs a new {@link viewHolder} of the given type to represent
     * an item.
     * <p>
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     * <p>
     * The new ViewHolder will be used to display items of the adapter using
     * . Since it will be re-used to display
     * different items in the data set, it is a good idea to cache references to sub views of
     * the View to avoid unnecessary {@link View#findViewById(int)} calls.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     * @see #getItemViewType(int)
     * @see #onBindViewHolder(viewHolder, int)
     */
    @Override
    public viewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View listItemView = layoutInflater.inflate(R.layout.content_class_item_layout, parent, false);

        return new viewHolder(listItemView);
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the {@link viewHolder#itemView} to reflect the item at the given
     * position.
     * <p>
     * Note that unlike {@link ListView}, RecyclerView will not call this method
     * again if the position of the item changes in the data set unless the item itself is
     * invalidated or the new position cannot be determined. For this reason, you should only
     * use the <code>position</code> parameter while acquiring the related data item inside
     * this method and should not keep a copy of it. If you need the position of an item later
     * on (e.g. in a click listener), use {@link viewHolder#getAdapterPosition()} which will
     * have the updated adapter position.
     * <p>
     * Override  instead if Adapter can
     * handle efficient partial bind.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(final viewHolder holder, final int position) {

        final content_class titleList = data_list.get(position);
        final content_class getPosition = dataBaseManager.getSingleData(titleList.getId());

        /*final ImageUtility imageUtility = new ImageUtility(holder.context, getPosition.getContent());
        imageUtility.loadInBackground(holder.noteImage, imageUtility);
        imageUtility.setOnSuccessfulImageLoadListener(new ImageUtility.onSuccessfulImageLoadListener() {
            @Override
            public void onSuccessfulImageLoad(Bitmap bitmap, String uri_or_url) {
                if (ImageUtility.shouldSetImageNow(getPosition.getContent()).equals(uri_or_url)){
                    holder.noteImage.setImageBitmap(bitmap);
                    holder.noteImage.setVisibility(View.VISIBLE);
                }
            }
        });*/

        final int isRoundThumbnailEnabled = sharedPrefs
                .getInt(holder.context.getString(R.string.round_thumbnail_preference_key), customSettingsScreen.SQUARE_THUMBNAIL);

        Object[] objects = getFirstImageType(getPosition.getContent());
        int type = (int)objects[1];
        final String path = (String) objects[0];
        if (!path.isEmpty() && type == TYPE_IMAGE) {
            holder.noteImage.setVisibility(View.VISIBLE);
            holder.contentView.setVisibility(View.VISIBLE);
            holder.playView.setVisibility(View.GONE);
            holder.contentView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            final int pix = getDipValue(holder.context, 48);
            boolean isCacheEnabled = sharedPrefs.getBoolean(holder.context.getString(R.string.cache_preference_key), true);

            /*Target target = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    *//*bitmap = ThumbnailUtils.extractThumbnail(bitmap, pix, pix);*//*
                    if (isRoundThumbnailEnabled == customSettingsScreen.ROUNDD_THUMBNAIL){
                        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory
                                .create(holder.context.getResources(), bitmap);
                        roundedBitmapDrawable.setCircular(true);
                        roundedBitmapDrawable.setAntiAlias(true);
                        holder.contentView.setImageDrawable(roundedBitmapDrawable);
                    }else {
                        holder.contentView.setImageBitmap(bitmap);
                    }
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                    holder.contentView.setImageDrawable(placeHolderDrawable);
                }
            };
            holder.contentView.setTag(target);*/

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
            );
            layoutParams.setMargins(
                    getDipValue(holder.context, 76),
                    0,
                    0,
                    0
            );
            holder.divider.setLayoutParams(layoutParams);

            //fresco library implementation
            /*Uri imageUri = Uri.parse(path);
            ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(imageUri)
                    .setResizeOptions(new ResizeOptions(getDipValue(holder.context, 48)
                            , getDipValue(holder.context, 48)))
                    .build();

            GenericDraweeHierarchy genericDraweeHierarchy = GenericDraweeHierarchyBuilder
                    .newInstance(holder.context.getResources())
                    .setRetryImage(R.drawable.ic_refresh_black_24dp)
                    .setFailureImage(R.drawable.ic_warning_black_24dp)
                    .setFailureImageScaleType(ScalingUtils.ScaleType.CENTER_CROP)
                    .setPlaceholderImage(R.drawable.image_placeholder)
                    .setPlaceholderImageScaleType(ScalingUtils.ScaleType.CENTER_CROP)
                    .setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP)
                    .setFadeDuration(300)
                    .setRoundingParams(
                            isRoundThumbnailEnabled == customSettingsScreen.ROUNDD_THUMBNAIL ?
                                    new RoundingParams().setRoundAsCircle(true)
                                    : new RoundingParams()
                                    *//*.setCornersRadii(getDipValue(holder.context, 4)
                                            , getDipValue(holder.context, 4)
                                            , getDipValue(holder.context, 4)
                                            , getDipValue(holder.context, 4)
                                    )*//*
                    )
                    .build();

            holder.contentView
                    .setHierarchy(genericDraweeHierarchy);
            holder.contentView
                    .setController(
                    Fresco.newDraweeControllerBuilder()
                    .setOldController(holder.contentView.getController())
                    .setTapToRetryEnabled(true)
                    .setImageRequest(imageRequest)
                    .build()
            );*/
            //up until here

            /*Picasso.get().load(path).into(holder.contentView);*/

            /*Picasso picasso = Picasso.get();
            RequestCreator requestCreator = picasso.load(path);
            requestCreator.placeholder(R.drawable.image_placeholder);
            requestCreator.resize(pix, pix);
            requestCreator.centerCrop();
            if (!isCacheEnabled){
                requestCreator.memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE);
                requestCreator.networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE);
            }
            requestCreator.into(target);*/

            DrawableCrossFadeFactory drawableCrossFadeFactory = new DrawableCrossFadeFactory.Builder()
                    .setCrossFadeEnabled(true).build();

            RequestManager requestManager = Glide.with(holder.context);
            RequestBuilder requestBuilder = requestManager.load(path);
            requestBuilder.error(R.drawable.ic_warning_black_24dp);
            requestBuilder.placeholder(R.drawable.image_placeholder);
            requestBuilder.transition(withCrossFade(drawableCrossFadeFactory));
            requestBuilder.override(pix, pix);
            requestBuilder.centerCrop();
            if (isRoundThumbnailEnabled == customSettingsScreen.ROUNDD_THUMBNAIL){
                requestBuilder.apply(RequestOptions.circleCropTransform());
            }
            requestBuilder.into(holder.contentView);

        }else if (!path.isEmpty() && type == TYPE_VIDEO){

            holder.noteImage.setVisibility(View.VISIBLE);
            holder.contentView.setVisibility(View.VISIBLE);
            holder.playView.setVisibility(View.VISIBLE);

            DrawableCrossFadeFactory drawableCrossFadeFactory = new DrawableCrossFadeFactory.Builder()
                    .setCrossFadeEnabled(true).build();
            final int pix = getDipValue(holder.context, 48);

            RequestManager requestManager = Glide.with(holder.context);
            RequestBuilder requestBuilder = requestManager.load(path);
            requestBuilder.error(R.drawable.ic_warning_black_24dp);
            requestBuilder.placeholder(R.drawable.image_placeholder);
            requestBuilder.transition(withCrossFade(drawableCrossFadeFactory));
            requestBuilder.override(pix, pix);
            requestBuilder.centerCrop();
            if (isRoundThumbnailEnabled == customSettingsScreen.ROUNDD_THUMBNAIL){
                requestBuilder.apply(RequestOptions.circleCropTransform());
            }
            requestBuilder.into(holder.contentView);

            /*holder.contentView.setImageDrawable(ContextCompat.getDrawable(holder.context, R.drawable.image_placeholder));
            holder.contentView.post(new Runnable() {
                @Override
                public void run() {
                    if (video_thumbnails.get(holder.contentView) == null && rounded_video_thumbnails.get(holder.contentView)
                            == null) {
                        Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Video.Thumbnails.MINI_KIND);
                        Bitmap resizedBitmap = ThumbnailUtils.extractThumbnail(bitmap
                                , getDipValue(holder.context, 48)
                                , getDipValue(holder.context, 48)
                        );
                        bitmap.recycle();

                        holder.contentView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        if (isRoundThumbnailEnabled == customSettingsScreen.ROUNDD_THUMBNAIL) {
                            final RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory
                                    .create(holder.context.getResources(), resizedBitmap);
                            roundedBitmapDrawable.setCircular(true);
                            roundedBitmapDrawable.setAntiAlias(true);
                            holder.contentView.setImageDrawable(roundedBitmapDrawable);
                            rounded_video_thumbnails.put(holder.contentView, roundedBitmapDrawable);
                        } else {
                            holder.contentView.setImageBitmap(resizedBitmap);
                            video_thumbnails.put(holder.contentView, resizedBitmap);
                        }
                    }else {
                        if (isRoundThumbnailEnabled == customSettingsScreen.ROUNDD_THUMBNAIL) {
                            holder.contentView.setImageDrawable(rounded_video_thumbnails.get(holder.contentView));
                        }
                        else {
                            holder.contentView.setImageBitmap(video_thumbnails.get(holder.contentView));
                        }
                    }
                }
            });*/

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
            );
            layoutParams.setMargins(
                    getDipValue(holder.context, 76),
                    0,
                    0,
                    0
            );
            holder.divider.setLayoutParams(layoutParams);

        }
        else {
            LinearLayout.LayoutParams layoutParams =  new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
            );
            holder.divider.setLayoutParams(layoutParams);
            holder.noteImage.setVisibility(View.GONE);
        }

        holder.title.setText(titleList.getTitle());
        holder.title.setTypeface(typeface);
        holder.dateAndTime.setText(titleList.getTime());
        holder.folderName.setText(titleList.getFolderName());
        Drawable drawable = ContextCompat.getDrawable(holder.context, R.drawable.ic_folder);
        holder.folderIcon.setImageDrawable(drawable);

        if (titleList.getFolderName().equals("None")){
            holder.folderName.setText("");
            holder.folderIcon.setImageDrawable(null);
        }

        holder.container.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.context, editingActivity.class);
                intent.putExtra(holder.context.getString(R.string.key_value_title), getPosition.getTitle());
                intent.putExtra(holder.context.getString(R.string.key_value_content), getPosition.getContent());
                intent.putExtra(holder.context.getString(R.string.key_value_id), getPosition.getId());
                intent.putExtra(holder.context.getString(R.string.key_value_time), getPosition.getTime());
                intent.putExtra(holder.context.getString(R.string.key_value_folderName), getPosition.getFolderName());
                holder.context.startActivity(intent);
                //Toast.makeText(holder.context, ImageUtility.getFirstImageSource(getPosition.getContent()), Toast.LENGTH_SHORT).show();
            }
        });

        /*holder.container.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
               *//* PopupMenu popupMenu = new PopupMenu(holder.context, holder.container);
                popupMenu.inflate(R.menu.popup_menu);
                popupMenu.setGravity(Gravity.START);
                popupMenu.show();*//*

                popupWindow.setFocusable(true);
                popupWindow.setOutsideTouchable(true);
                popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                if (x == -1 && y == -1)
                    popupWindow.showAsDropDown(holder.dateAndTime);
                else {
                    holder.container.measure(
                            View.MeasureSpec.makeMeasureSpec(
                                    0, View.MeasureSpec.UNSPECIFIED),
                            View.MeasureSpec.makeMeasureSpec(
                                    0, View.MeasureSpec.UNSPECIFIED
                            ));
                    *//*popupWindow.showAsDropDown(holder.container, (int) x, (int) y);*//*
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        popupWindow.setElevation(getDipValue(holder.context, 6));
                    }
                    popupWindow.showAtLocation(mActivity.getWindow().getDecorView(), Gravity.NO_GRAVITY, (int) x,
                            (int) y - holder.container.getMeasuredHeight());
                }

                return true;
            }
        });
        holder.container.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                x = event.getX();
                y = event.getY();
                return false;
            }
        });*/
        holder.itemMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View view = LayoutInflater.from(holder.context).inflate(R.layout.popup_menu, null);
                final int height = getDipValue(holder.context, 196);

                popupWindow = new PopupWindow(view,
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        height
                );

                MaterialCardView materialCardView = (MaterialCardView) view;
                materialCardView.setBackgroundColor(Cyanea.getInstance().getBackgroundColor());

                popupWindow.setFocusable(true);
                popupWindow.setOutsideTouchable(true);
                popupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    popupWindow.setElevation(getDipValue(holder.context, 10));
                }
                popupWindow.showAsDropDown(v);

                //setting the click actions below
                TextView editButton = (TextView)view.findViewById(R.id.popupMenu_layout_edit);
                editButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        content_class getPosition = dataBaseManager.getSingleData(titleList.getId());
                        Intent intent = new Intent(holder.context, AddingDataActivity.class);
                        intent.putExtra(holder.context.getString(R.string.key_data_title), getPosition.getTitle());
                        intent.putExtra(holder.context.getString(R.string.key_data_content), getPosition.getContent());
                        intent.putExtra(holder.context.getString(R.string.key_data_id), getPosition.getId());
                        intent.putExtra(holder.context.getString(R.string.key_data_folderName), getPosition.getFolderName());
                        intent.putExtra(holder.context.getString(R.string.key_activity_identifier), 1);
                        holder.context.startActivity(intent);
                    }
                });
                TextView deleteButton = (TextView)view.findViewById(R.id.popupMenu_layout_delete);
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        content_class getPosition = dataBaseManager.getSingleData(titleList.getId());
                        final int _id = getPosition.getId();
                        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(holder.context);
                        alertDialog.setMessage(holder.context.getString(R.string.confirm_delete_message));
                        alertDialog.setPositiveButton(holder.context.getString(R.string.confirm_delete), null);
                        alertDialog.setNegativeButton(holder.context.getString(R.string.confirm_cancel), null);

                        final AlertDialog deleteDialog = utilityClass.DialogBlur(holder.context, alertDialog);
                        if (popupWindow.isShowing()) popupWindow.dismiss();

                        deleteDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                utilityClass.cancelDialog();
                            }
                        });

                        deleteDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dataBaseManager.deleteEntry(_id);
                                remove(holder.getAdapterPosition());

                                try {
                                    FolderCountChangeListener.confirmFolderCountChangeListner(true);
                                }catch (NullPointerException e){
                                    //empty
                                }

                                if (popupWindow.isShowing()) popupWindow.dismiss();

                                Toast.makeText(holder.context,
                                        holder.context.getString(R.string.success_delete),
                                        Toast.LENGTH_SHORT
                                ).show();
                                utilityClass.cancelDialog();

                                if (getData().isEmpty() || getData() == null){
                                    EmptyData_listener.confirmDataEmpty(true);
                                }else EmptyData_listener.confirmDataEmpty(false);
                            }
                        });
                        deleteDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                utilityClass.cancelDialog();
                                /*if (popupWindow.isShowing()) popupWindow.dismiss();*/
                            }
                        });
                    }
                });

                TextView shareButton = view.findViewById(R.id.popupMenu_layout_share);
                shareButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        popupWindow.dismiss();

                        LayoutInflater layoutInflater = (LayoutInflater)
                                holder.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        final View view1 = layoutInflater.inflate(R.layout.share_layout, null);
                        View share_as_html, share_as_picture, divider;
                        share_as_html = view1.findViewById(R.id.share_as_html_text);
                        share_as_picture = view1.findViewById(R.id.share_as_picture);
                        divider = view1.findViewById(R.id.share_layout_divider);

                        share_as_html.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                                intent.setType("text/plain");
                                String shareNoteTitle = getPosition.getTitle();
                                String shareNoteContent = getPosition.getContent();
                                intent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareNoteTitle );
                                intent.putExtra(android.content.Intent.EXTRA_TEXT,  shareNoteContent );
                                holder.context.startActivity(Intent.createChooser(intent, "share via" ));
                                utilityClass.cancelDialog();
                            }
                        });

                        share_as_picture.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i = new Intent();
                                i.setClass(holder.context, snip_preview.class);
                                i.putExtra("snip_content", getPosition.getContent());
                                i.putExtra("snip_type", snip_preview.TYPE_TEXT);
                                i.putExtra("can_share", snip_preview.SHARABLE);
                                holder.context.startActivity(i);
                                /*NestedScrollView nestedScrollView = new NestedScrollView(holder.context);
                                AreTextView areTextView = new AreTextView(holder.context);
                                nestedScrollView.addView(areTextView);
                                areTextView.fromHtml(getPosition.getContent());

                                Bitmap bitmap = getViewSnap(nestedScrollView);
                                File file = saveBitmap(bitmap, holder.context, getNoteShareDirectory());

                                Uri uri = getContentUri(holder.context, file);
                                Intent share = new Intent(Intent.ACTION_SEND);
                                share.setType("image/jpeg");
                                share.putExtra(Intent.EXTRA_STREAM, uri);
                                holder.context.startActivity(Intent.createChooser(share,
                                        holder.context.getString(R.string.shareImages)));*/
                                utilityClass.cancelDialog();
                            }
                        });

                        if (getPosition.getContent().contains("<img src") || getPosition.getContent().contains("<video src")){
                            share_as_html.setVisibility(View.GONE);
                            share_as_picture.setVisibility(View.VISIBLE);
                            divider.setVisibility(View.GONE);
                        }else {
                            share_as_html.setVisibility(View.VISIBLE);
                            share_as_picture.setVisibility(View.VISIBLE);
                            divider.setVisibility(View.VISIBLE);
                        }

                        AlertDialog.Builder builder = new AlertDialog.Builder(holder.context);
                        builder.setView(view1);

                        AlertDialog alertDialog = utilityClass.DialogBlur(holder.context, builder);
                        alertDialog.getWindow().setGravity(Gravity.BOTTOM);
                        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                utilityClass.cancelDialog();
                            }
                        });

                        /*content_class getPosition = dataBaseManager.getSingleData(titleList.getId());
                        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        String shareNoteTitle = getPosition.getTitle();
                        String shareNoteContent = getPosition.getContent();
                        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareNoteTitle );
                        intent.putExtra(android.content.Intent.EXTRA_TEXT,  shareNoteContent );
                        holder.context.startActivity(Intent.createChooser(intent, "share via" ));*/
                    }
                });

                TextView editTitleButton = view.findViewById(R.id.popupMenu_layout_editTitle);
                editTitleButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final content_class getPosition = dataBaseManager.getSingleData(titleList.getId());
                        final int position = holder.getAdapterPosition();
                        //confirmEditTitleButtonClicked(getPosition, position);

                        ///////////////////////////////////////////////////////////////
                        /*= Typeface.createFromAsset(this.getAssets(),"fonts/roboto_mono.ttf");*/

                        final TextInputLayout editTitle =
                                (TextInputLayout) LayoutInflater.from(holder.context).inflate(R.layout.title_edittext, null);
                        final TextInputEditText textInputEditText = editTitle.findViewById(R.id.editTitle_inputEditText);

                        editTitle.setTypeface(typeface);
                        editTitle.setErrorEnabled(true);
                        editTitle.setHintEnabled(true);
                        editTitle.setElevation(6);
                        editTitle.setHint("Title");

                        setFrameLayoutParams(holder.context, textInputEditText, 8f);

                        textInputEditText.setText(getPosition.getTitle());
                        textInputEditText.setTypeface(typeface);


                        AlertDialog.Builder editTitleDialog = new AlertDialog.Builder(holder.context);
                        editTitleDialog.setView(editTitle);
                        editTitleDialog.setMessage(holder.context.getString(R.string.edit_title_in_dialog_builder));
                        editTitleDialog.setPositiveButton(holder.context.getString(R.string.save), null);
                        editTitleDialog.setNegativeButton(holder.context.getString(R.string.Discard), null);

                        final AlertDialog editTitleAlert_dialog = utilityClass.DialogBlur(holder.context, editTitleDialog);
                        if (popupWindow.isShowing()) popupWindow.dismiss();

                        editTitleAlert_dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                /*final Thread thread = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        KeyboardHiding(editTitle);
                                    }
                                });
                                thread.start();
                                keyboardHiddenListener(new NotesClass.interruptThread() {
                                    @Override
                                    public void isKeyboardHidden(boolean value) {
                                        if (value) thread.interrupt();
                                    }
                                });*/
                                /*arrayAdapter.closePopUpWindow();*/
                                utilityClass.cancelDialog();
                            }
                        });

                        editTitleAlert_dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (TextUtils.isEmpty(textInputEditText.getText().toString())){
                                    editTitle.setError(holder.context.getString(R.string.warning_empty_title));
                                }else {
                                    /*boolean fileNameExists = false;
                                    String initialTitle = dataBaseManager
                                            .getSingleData(data_list.get(position).getId()).getTitle();
                                    if (!initialTitle.equals(textInputEditText.getText().toString())) {
                                        for (content_class item : dataBaseManager.getAll()) {
                                            if (item.getTitle().equals(textInputEditText.getText().toString()))
                                                fileNameExists = true;
                                        }
                                    }*/
                                    //if (!fileNameExists) {
                                    SimpleDateFormat sdf = new SimpleDateFormat("MMM/dd/yyyy hh:mm aa", Locale.getDefault());
                                    String currentTime = sdf.format(new Date());
                                    String newTitle = textInputEditText.getText().toString();
                                    dataBaseManager.updateEntry(getPosition.getId(), newTitle, getPosition.getContent(), currentTime,
                                            getPosition.getFolderName());
                                    content_class updatedItem = dataBaseManager.getSingleData(getPosition.getId());
                                    replace(updatedItem, position);

                                        //KeyboardHiding(editTitle);

                                    Toast.makeText(holder.context,
                                            holder.context.getString(R.string.success_update), Toast.LENGTH_SHORT)
                                            .show();
                                    utilityClass.cancelDialog();
                                    /*}else {
                                        editTitle.setError(holder.context.getString(R.string.file_already_exists_with_the_same_name));
                                    }*/
                                }
                            }
                        });
                        editTitleAlert_dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //KeyboardHiding(editTitle);
                                /*arrayAdapter.closePopUpWindow();*/
                                utilityClass.cancelDialog();
                            }
                        });

                        ///////////////////////////////////////////////////////////////
                    }
                });
            }
        });

        /*holder.cardView.getBackground().setAlpha(alphaProvider(transparencyOnListItems));*/
    }

    //this interface is just useless now, remove it later on
    private static void confirmEditTitleButtonClicked(content_class item, int position){
        mEditTitleButtonClickListener.onEditTitleButtonClicked(item, position);
    }

    public static void setOnEditTitleButtonClickListener(EditTitleButtonClickListener editTitleButtonClickListener){
        mEditTitleButtonClickListener = editTitleButtonClickListener;
    }

    public interface EditTitleButtonClickListener{
        void onEditTitleButtonClicked(content_class dataItem,int position);
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return data_list.size();
    }

    @Override
    public void remove(final int position) {
        data_list.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public void restore(content_class data, int position) {
        data_list.add(position, data);
        notifyItemInserted(position);
    }

    @Override
    public void replace(content_class data, int position) {
        data_list.set(position, data);
        notifyItemChanged(position);
        closePopUpWindow();
    }

    public void closePopUpWindow(){
        if (popupWindow.isShowing()) popupWindow.dismiss();
    }

    public List<content_class> getData(){
        return data_list;
    }

    public void refresh(Context context){
        dataBaseManager = new DataBaseManager(context);
        data_list.clear();
        data_list.addAll(dataBaseManager.getAll());
        if (dataBaseManager.getAll().isEmpty()) EmptyData_listener.confirmDataEmpty(true);
        notifyDataSetChanged();
    }

    public void refresh(Context context, List<content_class> list){
        data_list.clear();
        data_list.addAll(list);
        if (list.isEmpty()) EmptySuggestionListener.confirmEmptySuggestionListner(true);
        notifyDataSetChanged();
    }

    public HashMap<ImageView, RoundedBitmapDrawable> getRounded_video_thumbnails() {
        return rounded_video_thumbnails;
    }

    public HashMap<ImageView, Bitmap> getVideo_thumbnails() {
        return video_thumbnails;
    }
}

class viewHolder extends RecyclerView.ViewHolder{

    TextView title, dateAndTime, folderName;
    LinearLayout container;
    Context context;
    ImageView itemMenu, folderIcon, playView, contentView;
    /*SimpleDraweeView contentView;*/
    FrameLayout noteImage;
    View divider;

    viewHolder(View itemView) {
        super(itemView);
        title = (TextView) itemView.findViewById(R.id.listViewTitle);
        dateAndTime = (TextView) itemView.findViewById(R.id.listViewTime);
        container = (LinearLayout) itemView.findViewById(R.id.listItemViewContainer);
        itemMenu = (ImageView)itemView.findViewById(R.id.item_menu);
        folderName = (TextView)itemView.findViewById(R.id.listViewFolder);
        folderIcon = (ImageView)itemView.findViewById(R.id.content_class_holder_folder_icon);
        noteImage = (FrameLayout) itemView.findViewById(R.id.content_class_note_image_view);
        playView = (ImageView) itemView.findViewById(R.id.content_class_note_play_view);
        contentView = (ImageView) itemView.findViewById(R.id.content_class_note_content_view);
        divider = itemView.findViewById(R.id.list_divider);

        context = itemView.getContext();
    }
}
