package com.ccc.raj.beats.model;

import android.graphics.Bitmap;

/**
 * Created by Raj on 2/4/2018.
 */

public class ArtistAlbum implements Album {
    private int artistId;
    private String artistName;
    private Bitmap artistBitmap;
    private String albumArt;
    @Override
    public int getAlbumId() {
        return artistId;
    }

    public Bitmap getArtistBitmap() {
        return artistBitmap;
    }

    public void setArtistId(int artistId) {
        this.artistId = artistId;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public void setArtistBitmap(Bitmap artistBitmap) {
        this.artistBitmap = artistBitmap;
    }

    public void setAlbumArt(String albumArt) {
        this.albumArt = albumArt;
    }

    @Override
    public String getAlbumTitle() {
        return artistName;

    }

    @Override
    public String getArtist() {
        return "";
    }

    @Override
    public String getAlbumArt() {
        return albumArt;
    }
}
