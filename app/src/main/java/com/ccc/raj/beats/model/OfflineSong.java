package com.ccc.raj.beats.model;

import android.graphics.Bitmap;
import android.provider.MediaStore;

/**
 * Created by Raj on 1/19/2018.
 */

public class OfflineSong extends Song{


    private int albumId;
    private String album;
    private String albumKey;
    private String artist;
    private long artistId;
    private String artistKey;
    private long id;
    private int bookmark;
    private String title;
    private String titleKey;
    private String dateModified;
    private int year;
    private long size;


    private String albumFullPath;
    private long duratio;
    private String displayName;
    private String date;
    private int trackNumber;
    private String composer;

    private int frequency = 0;
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

    public int getAlbumId() {
        return albumId;
    }

    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getAlbumKey() {
        return albumKey;
    }

    public void setAlbumKey(String albumKey) {
        this.albumKey = albumKey;
    }


    public void setArtist(String artist) {
        this.artist = artist;
    }

    public long getArtistId() {
        return artistId;
    }

    public void setArtistId(long artistId) {
        this.artistId = artistId;
    }

    public String getArtistKey() {
        return artistKey;
    }

    public void setArtistKey(String artistKey) {
        this.artistKey = artistKey;
    }


    public void setId(long id) {
        this.id = id;
    }

    public int getBookmark() {
        return bookmark;
    }

    public void setBookmark(int bookmark) {
        this.bookmark = bookmark;
    }


    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitleKey() {
        return titleKey;
    }

    public void setTitleKey(String titleKey) {
        this.titleKey = titleKey;
    }

    public String getDateModified() {
        return dateModified;
    }

    public void setDateModified(String dateModified) {
        this.dateModified = dateModified;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }
}
