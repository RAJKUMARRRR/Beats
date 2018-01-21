package com.ccc.raj.beats.model;

import android.graphics.Bitmap;

/**
 * Created by Raj on 1/19/2018.
 */

public class OfflineAlbum implements Album {
    private String albumPath;
    private int albumId;
    private String albumTitle;

    public  OfflineAlbum(String albumPath,int albumId,String albumTitle){
        this.albumPath = albumPath;
        this.albumId = albumId;
        this.albumTitle = albumTitle;
    }

    public int getAlbumId() {
        return albumId;
    }

    public String getAlbumTitle() {
        return albumTitle;
    }


    public String getAlbumPath(){
        return this.albumPath;
    }
}
