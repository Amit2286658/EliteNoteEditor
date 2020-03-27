package com.me.android.noteeditor.CommonUtility;

import android.content.Context;
import android.util.AttributeSet;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Amit on 10/23/2019.
 */

public class custom_recycle_view extends RecyclerView {
    Context context;
    public custom_recycle_view(Context context) {
        super(context);
        this.context = context;
    }

    public custom_recycle_view(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public custom_recycle_view(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        //heightSpec = MeasureSpec.makeMeasureSpec(utilityClass.getDipValue(context, 300), MeasureSpec.AT_MOST);
        super.onMeasure(widthSpec, heightSpec);
    }
}
