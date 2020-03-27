package com.me.android.noteeditor.Recyclerview_adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.chinalwb.are.AREditText;
import com.chinalwb.are.Util;
import com.me.android.noteeditor.R;
import com.me.android.noteeditor.Recyclerview_adapter.AddActivityDataSet.dataPool;
import com.me.android.noteeditor.Recyclerview_adapter.commonDataModel.EditingContentModelData;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

import static com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade;

/**
 * Created by Amit kumar on 1/17/2020.
 */

public class AddingContentAdapter extends RecyclerView.Adapter<AddingContentViewHolder> {

    ArrayList<EditingContentModelData> dataList = new ArrayList<>();

    public AddingContentAdapter(ArrayList<EditingContentModelData> list){
        this.dataList = list;
    }

    @Override
    public AddingContentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.are_edit_text, parent, false);
        return new AddingContentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AddingContentViewHolder holder, int position) {

        if (dataList.get(position).getType() == EditingContentModelData.EditingContentType.TEXT){
            if (dataPool.containsData(holder.editText)){
                holder.editText.fromHtml(dataPool.getData(holder.editText));
            }
        }

    }

    @Override
    public void onViewRecycled(AddingContentViewHolder holder) {
        super.onViewRecycled(holder);
        int position = holder.getAdapterPosition();
        if (dataList.get(position).getType() == EditingContentModelData.EditingContentType.TEXT){
            dataPool.putData(holder.editText, holder.editText.getHtml());
        }
    }

    public void refresh(ArrayList<EditingContentModelData> list){
        this.dataList.clear();
        this.dataList.addAll(list);
        notifyDataSetChanged();
    }

    public void insertItem(EditingContentModelData data, int index){
        dataList.add(index, data);
        notifyItemInserted(index);
    }

    public void removeItem(int index){
        dataList.remove(index);
        notifyItemRemoved(index);
    }

    private void loadUri(final Context context, Uri uri, final ImageView imageView){
        int maxWidth = Util.getScreenWidthAndHeight(context)[0];
        Glide.with(context)
                .asBitmap()
                .load(uri)
                .centerCrop()
                .override(maxWidth, Target.SIZE_ORIGINAL)
                .transition(withCrossFade())
                .placeholder(R.drawable.image_place_holder)
                .error(R.drawable.ic_warning_black_24dp)
                .into(imageView);
    }

    private void loadUrl(final Context context, String link, final ImageView imageView){
        int maxWidth = Util.getScreenWidthAndHeight(context)[0];
        Glide.with(context)
                .asBitmap()
                .load(link)
                .centerCrop()
                .override(maxWidth, Target.SIZE_ORIGINAL)
                .transition(withCrossFade())
                .placeholder(R.drawable.image_place_holder)
                .error(R.drawable.ic_warning_black_24dp)
                .into(imageView);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

}

class AddingContentViewHolder extends RecyclerView.ViewHolder{

    AREditText editText;
    ImageView image, videoIcon;
    TextView closeButton;
    Context context;

    public AddingContentViewHolder(View itemView) {
        super(itemView);
        editText = (AREditText) itemView.findViewById(R.id.addActivityRecyclerContent_editText);
        image = (ImageView) itemView.findViewById(R.id.addActivityRecyclerContent_image);
        videoIcon = (ImageView) itemView.findViewById(R.id.addActivityRecyclerContent_videoIcon);
        closeButton =(TextView) itemView.findViewById(R.id.addActivityRecyclerContent_clearButton);

        context = itemView.getContext();

    }
}
