package com.ccc.raj.beats.musiclibrary;

import android.graphics.Bitmap;

import com.ccc.raj.beats.model.Album;

/**
 * Created by Raj on 1/30/2018.
 */

public class PlayListAlbum implements Album{
    private int playListId;
    private String playListName;


    private Bitmap playListBitmap;

    @Override
    public int getAlbumId() {
        return playListId;
    }

    public void setPlayListId(int playListId) {
        this.playListId = playListId;
    }

    public void setPlayListName(String playListName) {
        this.playListName = playListName;
    }

    @Override
    public String getAlbumTitle() {
        return playListName;
    }

    @Override
    public String getArtist() {
        return "";
    }

    @Override
    public String getAlbumArt() {
        return "";
    }
    public Bitmap getPlayListBitmap() {
        return playListBitmap;
    }

    public void setPlayListBitmap(Bitmap playListBitmap) {
        this.playListBitmap = playListBitmap;
    }
}
