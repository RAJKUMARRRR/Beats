package com.ccc.raj.beats;

import android.support.annotation.Nullable;

/**
 * Created by Raj on 1/19/2018.
 */

public class Utitlity {
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
}
