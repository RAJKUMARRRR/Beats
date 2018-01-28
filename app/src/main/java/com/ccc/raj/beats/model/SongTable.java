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

public class SongTable {
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

    public static ArrayList<Song> getAllSongs(Context context) {
        ContentResolver musicResolver = context.getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
        ArrayList<Song> songList = getSongsFromCursor(musicCursor);
        return songList;
    }
    public static ArrayList<Song> getSongsFromColumn(Context context, String column, String columnValue) {
        ContentResolver musicResolver = context.getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String where = column + "=?";//MediaStore.Audio.Media.ALBUM
        String whereVal[] = {
                columnValue
        };
        String orderBy = MediaStore.Audio.Media.TRACK;
        Cursor musicCursor = musicResolver.query(musicUri, null, where, whereVal, orderBy);
        ArrayList<Song> songList = getSongsFromCursor(musicCursor);
        return songList;
    }

    public static ArrayList<Song> getSongsFromAlbum(Context context, String Album) {
        ContentResolver musicResolver = context.getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String where = MediaStore.Audio.Media.ALBUM + "=?";
        String whereVal[] = {
                Album
        };
        String orderBy = MediaStore.Audio.Media.TRACK;
        Cursor musicCursor = musicResolver.query(musicUri, null, where, whereVal, orderBy);
        ArrayList<Song> songList = getSongsFromCursor(musicCursor);
        return songList;
    }
    public static void getSongsByAlbumId() {

    }

    public static void getSongsByArtistId() {

    }

    public static void getSongsByPlaylistId() {

    }

    public static void getSongsByGenresId() {

    }
    public static ArrayList<Song> getSongsFromCursor(Cursor musicCursor) {
        ArrayList<Song> songList = new ArrayList<>();
        if (musicCursor != null && musicCursor.moveToFirst()) {
            int titleColumn = musicCursor.getColumnIndex(TITLE);
            int idColumn = musicCursor.getColumnIndex(ID);
            int artistColumn = musicCursor.getColumnIndex(ARTIST);
            int fullpathColumn = musicCursor.getColumnIndex(DATA);
            int durationColumn = musicCursor.getColumnIndex(DURATION);
            int displayNameColumn = musicCursor.getColumnIndex(DISPLAY_NAME);
            int dateAddedColumn = musicCursor.getColumnIndex(DATE_ADDED);
            int composorColumn = musicCursor.getColumnIndex(COMPOSER);
            int trackColumn = musicCursor.getColumnIndex(TRACK);
            int albumIdColumn = musicCursor.getColumnIndex(ALBUM_ID);
            int albumColumn = musicCursor.getColumnIndex(ALBUM);
            int albumKeyColumn = musicCursor.getColumnIndex(ALBUM_KEY);
            int artistIdColumn = musicCursor.getColumnIndex(ARTIST_ID);
            int artistKeyColumn = musicCursor.getColumnIndex(ARTIST_KEY);
            int bookmarkColumn = musicCursor.getColumnIndex(BOOKMARK);
            int titleKeyColumn = musicCursor.getColumnIndex(TITLE_KEY);
            int dateModifiedColumn = musicCursor.getColumnIndex(DATE_MODIFIED);
            int yearColumn = musicCursor.getColumnIndex(YEAR);
            int sizeColumn = musicCursor.getColumnIndex(SIZE);
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                String fullPath = musicCursor.getString(fullpathColumn);

                long duration = musicCursor.getLong(durationColumn);
                String displayName = musicCursor.getString(displayNameColumn);
                String date = musicCursor.getString(dateAddedColumn);
                String composor = musicCursor.getString(composorColumn);
                int trackNumber = musicCursor.getInt(trackColumn);

                int albumId = musicCursor.getInt(albumIdColumn);
                String album = musicCursor.getString(albumColumn);
                String albumKey = musicCursor.getString(albumKeyColumn);
                String artist = musicCursor.getString(artistColumn);
                long artistId = musicCursor.getLong(artistIdColumn);
                String artistKey = musicCursor.getString(artistKeyColumn);
                long bookmark = musicCursor.getLong(bookmarkColumn);
                String titleKey = musicCursor.getString(titleKeyColumn);
                String dateModified = musicCursor.getString(dateModifiedColumn);
                int year = musicCursor.getInt(yearColumn);
                long size = musicCursor.getLong(sizeColumn);


                if (duration > 60000) {
                    OfflineSong song = new OfflineSong(thisId, thisTitle, thisArtist, fullPath);
                    song.setDuratio(duration);
                    song.setComposer(composor);
                    song.setDate(date);
                    song.setDisplayName(displayName);
                    song.setTrackNumber(trackNumber);

                    song.setAlbumId(albumId);
                    song.setAlbum(album);
                    song.setAlbumKey(albumKey);
                    song.setArtist(artist);
                    song.setArtistId(artistId);
                    song.setArtistKey(artistKey);
                    song.setBookmark(bookmark);
                    song.setTitleKey(titleKey);
                    song.setDateModified(dateModified);
                    song.setYear(year);
                    song.setSize(size);
                    songList.add(song);
                }
            }
            while (musicCursor.moveToNext());
        }
        return songList;
    }
}
