package com.chinalwb.are.styles.toolbar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import android.widget.Toast;
import com.chinalwb.are.AREditText;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_Image;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_Video;
import com.chinalwb.are.styles.toolitems.IARE_ToolItem;
import com.chinalwb.are.styles.windows.FontsizePickerWindow;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wliu on 13/08/2018.
 */
public class ARE_ToolbarDefault extends HorizontalScrollView implements IARE_Toolbar {

    private Context mContext;

    private LinearLayout mContainer;

    private List<IARE_ToolItem> mToolItems = new ArrayList<>();

    private AREditText mAREditText;

    private static TextSizeWindowDisplay mTextSizeDisplayListener;

    public ARE_ToolbarDefault(Context context) {
        this(context, null);
    }

    public ARE_ToolbarDefault(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ARE_ToolbarDefault(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = (Activity) context;
        initSelf();
    }

    @Override
    public void addToolbarItem(IARE_ToolItem toolbarItem) {
        toolbarItem.setToolbar(this);
        mToolItems.add(toolbarItem);
        View view = toolbarItem.getView(mContext);
        if (view != null) {
            mContainer.addView(view);
        }
    }

    @Override
    public List<IARE_ToolItem> getToolItems() {
        return mToolItems;
    }

    @Override
    public void setEditText(AREditText editText) {
        this.mAREditText = editText;
    }

    @Override
    public AREditText getEditText() {
        return mAREditText;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        for (IARE_ToolItem toolItem : this.mToolItems) {
            toolItem.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void initSelf() {
        mContainer = new LinearLayout(mContext);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mContainer.setGravity(Gravity.CENTER_VERTICAL);
        mContainer.setLayoutParams(params);
        this.addView(mContainer);
    }


    //todo: Adding a custom interface here, it is not factory included and is created to tailor the blur effect available in the main app
    public static void confirmTextSizeWindowDisplay(
            FontsizePickerWindow fontsizePickerWindow, View anchor, int x_offset, int y_offset, boolean value){
        mTextSizeDisplayListener.onTextSizeDisplay(fontsizePickerWindow, anchor, x_offset, y_offset, value);
    }

    public static void setOnTextSizeWindowShowListener(TextSizeWindowDisplay textSizeDisplayListener){
        mTextSizeDisplayListener = textSizeDisplayListener;
    }

    public interface TextSizeWindowDisplay {
        void onTextSizeDisplay(FontsizePickerWindow ARE_FontSize, View anchor, int x_offset, int y_offset, boolean isWindowDisplaying);
    }

}
