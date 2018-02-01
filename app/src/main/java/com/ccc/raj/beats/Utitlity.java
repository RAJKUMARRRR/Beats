package com.ccc.raj.beats;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Raj on 1/19/2018.
 */

public class Utitlity {
    private final static String TAG = "Beats";
    public static String formatString(String str, int limit) {
      if(str.length()>limit){
            str = str.substring(0,limit)+"...";
            return str;
        }else{
            return str;
        }
    }

    public static String converMillisecondsToMMSS(long milliseconds) {
        long minutes = (milliseconds / 1000) / 60;
        long seconds = (milliseconds / 1000) % 60;
        return minutes + ":" + seconds;
    }

    public static void Log(String string){
        Log.i(TAG,string);
    }

    public static Bitmap mergeMultipleBitmaps(Bitmap[] parts){
        Bitmap result = Bitmap.createBitmap(parts[0].getWidth() * 2, parts[0].getHeight() * 2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        for (int i = 0; i < parts.length; i++) {
            canvas.drawBitmap(parts[i], parts[i].getWidth() * (i % 2), parts[i].getHeight() * (i / 2), paint);
        }
        return result;
    }
}
