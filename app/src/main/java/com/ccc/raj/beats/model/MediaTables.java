package com.ccc.raj.beats.model;

import android.provider.MediaStore;

/**
 * Created by Raj on 1/27/2018.
 */

public class MediaTables {
    public static class ArtistTable {
        public static final String ARTIST = MediaStore.Audio.ArtistColumns.ARTIST;
        public static final String ARTIST_KEY = MediaStore.Audio.ArtistColumns.ARTIST_KEY;
        public static final String NUMBER_OF_ALBUMS = MediaStore.Audio.ArtistColumns.NUMBER_OF_ALBUMS;
        public static final String NUMBER_OF_TRACKS = MediaStore.Audio.ArtistColumns.NUMBER_OF_TRACKS;
        public static final String ID = MediaStore.Audio.Artists._ID;

        public static void getAllArtists() {
        }

        public static void getArtistByArtistId() {

        }

    }
}
