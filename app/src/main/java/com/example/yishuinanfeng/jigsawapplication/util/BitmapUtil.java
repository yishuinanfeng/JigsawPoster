package com.example.yishuinanfeng.jigsawapplication.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Hendricks on 2017/8/31.
 */

public class BitmapUtil {
    /**
     * 由View得到对应的指定尺寸的Bitmap
     *
     * @param view
     * @return
     */
    public static Bitmap getBitmapFromView(View view, int bitmapWidth, int bitmapHeight) {
        //Define a bitmap with the same size as the view
        Bitmap tempBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(tempBitmap);
        //Get the view's background
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null) {
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        } else {
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.TRANSPARENT);
        }
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        Bitmap resultBitmap = Bitmap.createScaledBitmap(tempBitmap, bitmapWidth, bitmapHeight, true);
        tempBitmap.recycle();
        return resultBitmap;
    }

    public static boolean saveBitmap(Bitmap bm) {
        File f = new File(FileUtil.getSDPath(), "jigsaw" + System.currentTimeMillis());
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
