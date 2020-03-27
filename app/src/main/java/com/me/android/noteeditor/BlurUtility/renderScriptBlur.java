package com.me.android.noteeditor.BlurUtility;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.renderscript.Type;

class renderScriptBlur {
    static Bitmap blurBitmap(Context context, float radius, Bitmap bitmap){
        //creating renderScript
        RenderScript rs = RenderScript.create(context);

        //Create allocation from bitmap
        Allocation allocation = Allocation.createFromBitmap(rs, bitmap);

        Type t = allocation.getType();

        //creating allocation with the same type
        Allocation blurredAllocation = Allocation.createTyped(rs, t);

        //create script
        ScriptIntrinsicBlur intrinsicBlur = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

        //set blur radius (Maximum 25.0)
        intrinsicBlur.setRadius(radius);

        //set input for script
        intrinsicBlur.setInput(allocation);

        //call script for output allocation
        intrinsicBlur.forEach(blurredAllocation);

        //copy script result into bitmap
        blurredAllocation.copyTo(bitmap);

        //destroy everything to free memory
        try {
            allocation.destroy();
            blurredAllocation.destroy();
            intrinsicBlur.destroy();
            t.destroy();
            rs.destroy();
        } catch (Exception e){
            e.printStackTrace();
        }

        return bitmap;
    }
}
