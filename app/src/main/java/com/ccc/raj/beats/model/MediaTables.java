package com.ccc.raj.beats.model;

import android.provider.MediaStore;

/**
 * Created by Raj on 1/27/2018.
 */

public class MediaTables {

    public static class Album {
        public static final String ALBUM = MediaStore.Audio.AlbumColumns.ALBUM;
        public static final String ALBUM_ART = MediaStore.Audio.AlbumColumns.ALBUM_ART;
        public static final String ALBUM_ID = MediaStore.Audio.AlbumColumns.ALBUM_ID;
        public static final String ALBUM_KEY = MediaStore.Audio.AlbumColumns.ALBUM_KEY;
        public static final String ARTIST = MediaStore.Audio.AlbumColumns.ARTIST;
        public static final String FIRST_YEAR = MediaStore.Audio.AlbumColumns.FIRST_YEAR;
        public static final String LAST_YEAR = MediaStore.Audio.AlbumColumns.LAST_YEAR;
        public static final String NUMBER_OF_SONGS = MediaStore.Audio.AlbumColumns.NUMBER_OF_SONGS;
        public static final String NUMBER_OF_SONGS_FOR_ARTIST = MediaStore.Audio.AlbumColumns.NUMBER_OF_SONGS_FOR_ARTIST;
        public static final String ID = MediaStore.Audio.Albums._ID;

        public static void getAllAlbums() {

        }

        public static void getAlbumByAlbumId() {

        }

        public static void getAlbumsByArtistId() {

        }

        public static void addAlbumToResent() {

        }

        public static void getRecentAlbums() {

        }

    }

    public static class Song {
        public static final String ALBUM = MediaStore.Audio.AudioColumns.ALBUM;
        public static final String ALBUM_ID = MediaStore.Audio.AudioColumns.ALBUM_ID;
        public static final String ALBUM_KEY = MediaStore.Audio.AudioColumns.ALBUM_KEY;
        public static final String ARTIST = MediaStore.Audio.AudioColumns.ARTIST;
        public static final String ARTIST_ID = MediaStore.Audio.AudioColumns.ARTIST_ID;
        public static final String ARTIST_KEY = MediaStore.Audio.AudioColumns.ARTIST_KEY;
        public static final String BOOKMARK = MediaStore.Audio.AudioColumns.BOOKMARK;
        public static final String COMPOSER = MediaStore.Audio.AudioColumns.COMPOSER;
        public static final String DURATION = MediaStore.Audio.AudioColumns.DURATION;
        public static final String IS_ALARM = MediaStore.Audio.AudioColumns.IS_ALARM;
        public static final String IS_MUSIC = MediaStore.Audio.AudioColumns.IS_MUSIC;
        public static final String IS_NOTIFICATION = MediaStore.Audio.AudioColumns.IS_NOTIFICATION;
        public static final String IS_PODCAST = MediaStore.Audio.AudioColumns.IS_PODCAST;
        public static final String IS_RINGTONE = MediaStore.Audio.AudioColumns.IS_RINGTONE;
        public static final String TITLE_KEY = MediaStore.Audio.AudioColumns.TITLE_KEY;
        public static final String TITLE = MediaStore.Audio.AudioColumns.TITLE;
        public static final String TRACK = MediaStore.Audio.AudioColumns.TRACK;
        public static final String YEAR = MediaStore.Audio.AudioColumns.YEAR;
        public static final String DATA = MediaStore.Audio.AudioColumns.DATA;
        public static final String DATE_MODIFIED = MediaStore.Audio.AudioColumns.DATE_MODIFIED;
        public static final String DATE_ADDED = MediaStore.Audio.AudioColumns.DATE_ADDED;
        public static final String DISPLAY_NAME = MediaStore.Audio.AudioColumns.DISPLAY_NAME;
        public static final String HEIGHT = MediaStore.Audio.AudioColumns.HEIGHT;
        public static final String MIME_TYPE = MediaStore.Audio.AudioColumns.MIME_TYPE;
        public static final String SIZE = MediaStore.Audio.AudioColumns.SIZE;
        public static final String WIDTH = MediaStore.Audio.AudioColumns.WIDTH;
        public static final String ID = MediaStore.Audio.Media._ID;

        public static void getAllSongs() {

        }

        public static void getSongsByAlbumId() {

        }

        public static void getSongsByArtistId() {

        }

        public static void getSongsByPlaylistId() {

        }

        public static void getSongsByGenresId() {

        }

    }

    public static class Artist {
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

    public static class Playlists {
        public static final String DATA = MediaStore.Audio.PlaylistsColumns.DATA;
        public static final String DATE_ADDED = MediaStore.Audio.PlaylistsColumns.DATE_ADDED;
        public static final String DATE_MODIFIED = MediaStore.Audio.PlaylistsColumns.DATE_MODIFIED;
        public static final String NAME = MediaStore.Audio.PlaylistsColumns.NAME;
        public static final String ID = MediaStore.Audio.Playlists._ID;
    }
}
