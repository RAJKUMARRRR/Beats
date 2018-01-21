package com.ccc.raj.beats.model;

import android.graphics.Bitmap;

/**
 * Created by Raj on 1/19/2018.
 */

public interface Album {
    public int getAlbumId();
    public String getAlbumTitle();
    public String getAlbumPath();
    public String getComposer();
    public void setComposer(String composer);
    public void setArtist(String artist);
    public String getArtist();
}
