package com.me.android.noteeditor.Recyclerview_adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.chinalwb.are.Util;
import com.chinalwb.are.activities.Are_VideoPlayerActivity;
import com.chinalwb.are.render.AreTextView;
import com.chinalwb.are.spans.AreImageSpan;
import com.chinalwb.are.strategies.defaults.DefaultImagePreviewActivity;
import com.me.android.noteeditor.R;
import com.me.android.noteeditor.Recyclerview_adapter.commonDataModel.EditingContentModelData;
import com.me.android.noteeditor.fontManager.fontManager;

import java.io.IOException;
import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

import static com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade;
import static com.me.android.noteeditor.CommonUtility.utilityClass.textSize;

/*import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;*/

public class EditingContentAdapter extends RecyclerView.Adapter<EditingContentViewHolder> {

    private ArrayList<EditingContentModelData> list = new ArrayList<>();

    private int textSize;
    private Typeface typeface;

    public EditingContentAdapter(ArrayList<EditingContentModelData> list, Context context){
        this.list = list;

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        fontManager fontManager = new fontManager(context);
        fontManager.initializeFontManager();

        textSize = sharedPreferences.getInt(
                context.getString(R.string.custom_text_size_key),
                1
        );
        String fontType = sharedPreferences.getString(
                context.getString(R.string.font_preference_key),
                "Aller_Lt"
        );

        String path = null;
        try {
            path = fontManager.getFontPath(fontType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*= Typeface.createFromAsset(this.getAssets(),"fonts/roboto_mono.ttf");*/
        typeface = Typeface.createFromAsset(context.getAssets(), path);

    }

    /**
     * Called when RecyclerView needs a new {@link EditingContentViewHolder} of the given type to represent
     * an item.
     * <p>
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     * <p>
     * The new ViewHolder will be used to display items of the adapter using
     * {@link #EditingContentAdapter(ArrayList)} onBindViewHolder(ViewHolder, int, List)}. Since it will be re-used to display
     * different items in the data set, it is a good idea to cache references to sub views of
     * the View to avoid unnecessary {@link View#findViewById(int)} calls.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     * @see #getItemViewType(int)
     * @see #EditingContentAdapter(ArrayList) onBindViewHolder(ViewHolder, int)
     */
    @Override
    public EditingContentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.are_text_view, parent, false);
        return new EditingContentViewHolder(view);
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the {@link EditingContentViewHolder#itemView} to reflect the item at the given
     * position.
     * <p>
     * Note that unlike {@link ListView}, RecyclerView will not call this method
     * again if the position of the item changes in the data set unless the item itself is
     * invalidated or the new position cannot be determined. For this reason, you should only
     * use the <code>position</code> parameter while acquiring the related data item inside
     * this method and should not keep a copy of it. If you need the position of an item later
     * on (e.g. in a click listener), use {@link EditingContentViewHolder#getAdapterPosition()} which will
     * have the updated adapter position.
     * <p>
     * Override {@link #EditingContentAdapter(ArrayList)} onBindViewHolder(ViewHolder, int, List)} instead if Adapter can
     * handle efficient partial bind.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(final EditingContentViewHolder holder, final int position) {

        //AreTextView areTextView = holder.areTextView;
        //holder.areTextView.setText("");
        //holder.imageView.setImageDrawable(null);

        if (list.get(position).getType() == EditingContentModelData.EditingContentType.TEXT){
            holder.imageView.setVisibility(View.GONE);
            holder.video_icon.setVisibility(View.GONE);
            holder.areTextView.setVisibility(View.VISIBLE);
            holder.areTextView.fromHtml(list.get(position).getHtml_text());
            holder.areTextView.setTypeface(typeface);
            holder.areTextView.setTextSize(textSize(textSize));
        }else if (list.get(position).getType() == EditingContentModelData.EditingContentType.IMAGE){
            holder.areTextView.setVisibility(View.GONE);
            holder.video_icon.setVisibility(View.GONE);
            holder.imageView.setVisibility(View.VISIBLE);
            if (list.get(position).getLinkType() == EditingContentModelData.EditingContentLinkType.URI){
                loadUri(holder.context, list.get(position).getImage_uri(), holder.imageView);
            }else{
                loadUrl(holder.context, list.get(position).getImage_link(), holder.imageView);
            }
        }else if (list.get(position).getType() == EditingContentModelData.EditingContentType.VIDEO){
            holder.video_icon.setVisibility(View.VISIBLE);
            holder.imageView.setVisibility(View.VISIBLE);
            holder.areTextView.setVisibility(View.GONE);
            loadUrl(holder.context, list.get(position).getVideo_link(), holder.imageView);
        }



        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                if (list.get(position).getType() == EditingContentModelData.EditingContentType.IMAGE) {
                    if (list.get(position).getLinkType() == EditingContentModelData.EditingContentLinkType.URI) {
                        intent.putExtra("imageType", AreImageSpan.ImageType.URI);
                        intent.putExtra("uri", list.get(position).getImage_uri());
                    } else {
                        intent.putExtra("imageType", AreImageSpan.ImageType.URL);
                        intent.putExtra("url", list.get(position).getImage_link());
                    }
                    intent.setClass(holder.context, DefaultImagePreviewActivity.class);
                    holder.context.startActivity(intent);
                }else {
                    intent.setClass(holder.context, Are_VideoPlayerActivity.class);
                    Uri uri = Uri.parse(list.get(position).getVideo_link());
                    intent.setData(uri);
                    holder.context.startActivity(intent);
                }
            }
        });

        /*final ImageView imageView = holder.imageView;
        imageView.setImageDrawable(null);
        imageView.setVisibility(View.GONE);*/

        /*if (list.get(position).contains("content") || list.get(position).contains("http")){
            Target target = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    int w = Util.getScreenWidthAndHeight(holder.context)[0];
                    Bitmap bitmap1 = Util.scaleBitmapToFitWidth(bitmap, getDipValue(holder.context, w));
                    imageView.setImageBitmap(bitmap1);
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                    imageView.setImageDrawable(errorDrawable);
                    Toast.makeText(holder.context, "Unable to load image at position : "+String.valueOf(position), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                    imageView.setImageDrawable(placeHolderDrawable);
                }
            };
            Picasso
                    .get()
                    .load(list.get(position))
                    .placeholder(R.drawable.image_place_holder)
                    .resize(getDipValue(holder.context, 200), getDipValue(holder.context, 200))
                    .error(R.drawable.image_place_holder)
                    .into(target);
            areTextView.setVisibility(View.GONE);
            imageView.setTag(target);
        }else {
            areTextView.fromHtml(list.get(position));
            imageView.setVisibility(View.GONE);
        }*/

    }



    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return list.size();
    }

    public int getSize(){
        return list.size();
    }

    private void loadUri(final Context context, Uri uri, final ImageView imageView){
        int maxWidth = Util.getScreenWidthAndHeight(context)[0];
        Glide.with(context)
                .asBitmap()
                .load(uri)
                .centerCrop()
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .transition(withCrossFade())
                .placeholder(R.drawable.image_place_holder)
                .error(R.drawable.ic_warning_black_24dp)
                .into(imageView/*new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        //int maxWidth = Util.getScreenWidthAndHeight(context)[0];
                        //resource = Util.scaleBitmapToFitWidth(resource, maxWidth);
                        imageView.setImageBitmap(resource);
                    }

                    @Override
                    public void onLoadCleared(Drawable placeholder) {
                        imageView.setImageDrawable(placeholder);
                    }

                    @Override
                    public void onLoadFailed(Drawable errorDrawable) {
                        imageView.setImageDrawable(errorDrawable);
                    }
                }*/);
    }

    private void loadUrl(final Context context, String link, final ImageView imageView){
        int maxWidth = Util.getScreenWidthAndHeight(context)[0];
        Glide.with(context)
                .asBitmap()
                .load(link)
                .centerCrop()
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .transition(withCrossFade())
                .placeholder(R.drawable.image_place_holder)
                .error(R.drawable.ic_warning_black_24dp)
                .into(imageView);/*new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        //int maxWidth = Util.getScreenWidthAndHeight(context)[0];
                        //resource = Util.scaleBitmapToFitWidth(resource, maxWidth);
                        imageView.setImageBitmap(resource);
                    }

                    @Override
                    public void onLoadCleared(Drawable placeholder) {
                        imageView.setImageDrawable(placeholder);
                    }

                    @Override
                    public void onLoadFailed(Drawable errorDrawable) {
                        imageView.setImageDrawable(errorDrawable);
                    }
                });*/
    }

}

class EditingContentViewHolder extends RecyclerView.ViewHolder{

    AreTextView areTextView;
    ImageView imageView, video_icon;
    Context context;

    public EditingContentViewHolder(View itemView) {
        super(itemView);
        areTextView = (AreTextView)itemView.findViewById(R.id.editActivityRecyclerContent);
        imageView = (ImageView) itemView.findViewById(R.id.editActivityRecyclerContent_image);
        video_icon = (ImageView) itemView.findViewById(R.id.editActivityRecyclerContent_video_icon);
        context = itemView.getContext();
    }
}
