package com.ccc.raj.beats.model;

import android.graphics.Bitmap;

/**
 * Created by Raj on 1/19/2018.
 */

public class OfflineSong extends Song{
    private String albumFullPath;
    OfflineSong(long id, String title, String artist,String albumFullPath) {
        super(id, title, artist);
        this.albumFullPath = albumFullPath;
    }

    public  String getAlbumFullPath(){
        return this.albumFullPath;
    }
}
