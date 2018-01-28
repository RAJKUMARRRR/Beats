package com.ccc.raj.beats.model;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Raj on 1/27/2018.
 */

public class MediaTables {

    public static class AlbumTable {
    }

    public static class SongTable {
    }

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

    public static class PlaylistTable {
        public static final String DATA = MediaStore.Audio.PlaylistsColumns.DATA;
        public static final String DATE_ADDED = MediaStore.Audio.PlaylistsColumns.DATE_ADDED;
        public static final String DATE_MODIFIED = MediaStore.Audio.PlaylistsColumns.DATE_MODIFIED;
        public static final String NAME = MediaStore.Audio.PlaylistsColumns.NAME;
        public static final String ID = MediaStore.Audio.Playlists._ID;
    }
}
