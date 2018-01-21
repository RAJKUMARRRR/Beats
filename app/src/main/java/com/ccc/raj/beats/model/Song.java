package com.ccc.raj.beats.model;

import android.net.Uri;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Raj on 1/6/2018.
 */

public abstract class Song {
    public long id;
    public String title;
    public String artist;
    Song(long id,String title,String artist){
        this.id = id;
        this.title = title;
        this.artist = artist;
    }
    public long getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public String getArtist() {
        return artist;
    }
}
