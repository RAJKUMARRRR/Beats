package com.ccc.raj.beats;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Raj on 1/19/2018.
 */

public class Utitlity {
    private final static String TAG = "Beats";

    public static String formatString(String str, int limit) {
        if(str == null || str.isEmpty()){
            return "";
        }
        if (str.length() > limit) {
            str = str.substring(0, limit) + "...";
            return str;
        } else {
            return str;
        }
    }

    public static String converMillisecondsToMMSS(long milliseconds) {
        long minutes = (milliseconds / 1000) / 60;
        long seconds = (milliseconds / 1000) % 60;
        return minutes + ":" + seconds;
    }

    public static void Log(String string) {
        Log.i(TAG, string);
    }

    public static Bitmap mergeMultipleBitmaps(Bitmap[] parts) {
        int width = parts[0].getWidth();
        int height = parts[0].getHeight();
        for(int i=0;i<4;i++){
            parts[i] = getResizedBitmap(parts[i],width,height);
        }
        Bitmap result = Bitmap.createBitmap(parts[0].getWidth() * 2, parts[0].getHeight() * 2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        for (int i = 0; i < parts.length; i++) {
            canvas.drawBitmap(parts[i], parts[i].getWidth() * (i / 2), parts[i].getHeight() * (i % 2), paint);
        }
        return result;
    }

    private static Bitmap combineImages(Bitmap c, Bitmap s,boolean isVertical) {
        Bitmap cs = null;
        int width, height = 0;
        if(!isVertical) {
            if (c.getWidth() > s.getWidth()) {
                width = c.getWidth() + s.getWidth();
            } else {
                width = s.getWidth() + s.getWidth();
            }
            if (c.getHeight() < s.getHeight()) {
                height = c.getHeight();
            } else {
                height = s.getHeight();
            }
            cs = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas comboImage = new Canvas(cs);
            comboImage.drawBitmap(c, 0f, 0f, null);
            comboImage.drawBitmap(s, c.getWidth(), 0f, null);
        }else{
            if (c.getWidth() < s.getWidth()) {
                width = c.getWidth();
            } else {
                width = s.getWidth();
            }
            if (c.getHeight() > s.getHeight()) {
                height = c.getHeight() + s.getHeight();
            } else {
                height = s.getHeight() + s.getHeight();
            }
            cs = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas comboImage = new Canvas(cs);
            comboImage.drawBitmap(c, 0f, 0f, null);
            comboImage.drawBitmap(s, 0f, c.getHeight(), null);
        }
        return cs;
    }

    private static Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        //bm.recycle();
        return resizedBitmap;
    }
}
