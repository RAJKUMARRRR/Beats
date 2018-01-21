package com.ccc.raj.beats.model;

import android.graphics.Bitmap;

/**
 * Created by Raj on 1/19/2018.
 */

public class OfflineSong extends Song{
    private String albumFullPath;
    private long duratio;
    private String displayName;
    private String date;
    private int trackNumber;
    private String composer;
    OfflineSong(long id, String title, String artist,String albumFullPath) {
        super(id, title, artist);
        this.albumFullPath = albumFullPath;
    }
    public  String getAlbumFullPath(){
        return this.albumFullPath;
    }

    public void setDuratio(long duratio) {
        this.duratio = duratio;
    }


    public long getDuratio(){
        return this.duratio;
    }

    public void setAlbumFullPath(String albumFullPath) {
        this.albumFullPath = albumFullPath;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTrackNumber(int trackNumber) {
        this.trackNumber = trackNumber;
    }

    public void setComposer(String composer) {
        this.composer = composer;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDate() {
        return date;
    }

    public int getTrackNumber() {
        return trackNumber;
    }

    public String getComposer() {
        return composer;
    }
}
