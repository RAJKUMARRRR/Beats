package com.ccc.raj.beats.model;

import android.graphics.Bitmap;

/**
 * Created by Raj on 2/3/2018.
 */

public class GenresAlbum implements Album {
    private String genresName;
    private int genresId;
    private Bitmap genresBitmap;

    public void setGenresName(String genresName) {
        this.genresName = genresName;
    }

    public void setGenresId(int genresId) {
        this.genresId = genresId;
    }

    public void setGenresBitmap(Bitmap genresBitmap) {
        this.genresBitmap = genresBitmap;
    }

    @Override
    public int getAlbumId() {
        return genresId;
    }

    @Override
    public String getAlbumTitle() {
        return genresName;
    }

    public Bitmap getGenresBitmap() {
        return genresBitmap;
    }

    @Override
    public String getArtist() {
        return "";
    }

    @Override
    public String getAlbumArt() {
        return "";
    }
}
