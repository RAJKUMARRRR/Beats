package com.ccc.raj.beats.model;

import android.graphics.Bitmap;
import android.provider.MediaStore;

/**
 * Created by Raj on 1/19/2018.
 */

public class OfflineAlbum implements Album {
    private int albumId;
    private String albumTitle;
    private String artist;
    private String albumArt;
    private String albumKey;
    private int firstYear;
    private int lastYear;
    private int numberOfSongs;
    private int numberOfSongsForArtist;
    private long id;

    public  OfflineAlbum(){
    }

    public int getAlbumId() {
        return albumId;
    }

    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }

    public String getAlbumTitle() {
        return albumTitle;
    }

    public void setAlbumTitle(String albumTitle) {
        this.albumTitle = albumTitle;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbumArt() {
        return albumArt;
    }

    public void setAlbumArt(String albumArt) {
        this.albumArt = albumArt;
    }

    public String getAlbumKey() {
        return albumKey;
    }

    public void setAlbumKey(String albumKey) {
        this.albumKey = albumKey;
    }

    public int getFirstYear() {
        return firstYear;
    }

    public void setFirstYear(int firstYear) {
        this.firstYear = firstYear;
    }

    public int getLastYear() {
        return lastYear;
    }

    public void setLastYear(int lastYear) {
        this.lastYear = lastYear;
    }

    public int getNumberOfSongs() {
        return numberOfSongs;
    }

    public void setNumberOfSongs(int numberOfSongs) {
        this.numberOfSongs = numberOfSongs;
    }

    public int getNumberOfSongsForArtist() {
        return numberOfSongsForArtist;
    }

    public void setNumberOfSongsForArtist(int numberOfSongsForArtist) {
        this.numberOfSongsForArtist = numberOfSongsForArtist;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
