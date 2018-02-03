package com.ccc.raj.beats.model;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.ArrayList;

/**
 * Created by Raj on 1/27/2018.
 */

public class AlbumTable {
    public static final Uri TABLE_URI = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
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

    public static ArrayList<Album> getAllAlbums(Context context) {
        ArrayList<Album> albumArrayList = new ArrayList<>();
        ContentResolver musicResolver = context.getContentResolver();
        Uri musicUri = TABLE_URI;
        String sortOrder = ALBUM + " ASC";
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, sortOrder);
        albumArrayList = getOfflineAlbumsFromCursor(musicCursor);
        return albumArrayList;
    }

    /*public static ArrayList<Album> getAlbumByAlbumId(Context context,int albumId) {
    }*/

    public static ArrayList<Album> getAlbumsGroupByArtist(Context context) {
        ArrayList<Album> albumArrayList = new ArrayList<>();
        ContentResolver musicResolver = context.getContentResolver();
        Uri musicUri = TABLE_URI;
        String where = "0 == 0) GROUP BY (" + ARTIST;
        String sortOrder = ARTIST + " ASC";
        Cursor musicCursor = musicResolver.query(musicUri, null, where, null, sortOrder);
        albumArrayList = getOfflineAlbumsFromCursor(musicCursor);
        return albumArrayList;
    }

    public static ArrayList<Album> getAlbumsByArtistId(Context context,String artist) {
        ArrayList<Album> albumArrayList = new ArrayList<>();
        ContentResolver musicResolver = context.getContentResolver();
        Uri musicUri = TABLE_URI;
        String where = ARTIST + "=?";
        String whereVal[] = {
                artist
        };
        String sortOrder = ALBUM + " ASC";
        Cursor musicCursor = musicResolver.query(musicUri, null, where, whereVal, sortOrder);
        albumArrayList = getOfflineAlbumsFromCursor(musicCursor);
        return albumArrayList;
    }

    public static void addAlbumToResent() {

    }

    public static void getRecentAlbums() {

    }

    public static ArrayList<Album> getOfflineAlbumsFromCursor(Cursor musicCursor) {
        ArrayList<Album> albumArrayList = new ArrayList<>();
        if (musicCursor != null && musicCursor.moveToFirst()) {
            int titleColumn = musicCursor.getColumnIndex(ALBUM);
            int albumIdColumn = musicCursor.getColumnIndex(ALBUM_ID);
            int artistColumn = musicCursor.getColumnIndex(ARTIST);
            int idColumn = musicCursor.getColumnIndex(ID);
            int albumArtColumn = musicCursor.getColumnIndex(ALBUM_ART);
            int albumKeyColumn = musicCursor.getColumnIndex(ALBUM_KEY);
            int firstYearColumn = musicCursor.getColumnIndex(FIRST_YEAR);
            int lastYearColumn = musicCursor.getColumnIndex(LAST_YEAR);
            int noOfSongsForArtistColumn = musicCursor.getColumnIndex(NUMBER_OF_SONGS_FOR_ARTIST);
            int noOfSongsColumn = musicCursor.getColumnIndex(NUMBER_OF_SONGS);
            do {
                String title = musicCursor.getString(titleColumn);
                int albumId = 0;

                if(albumIdColumn>0) {
                   albumId = musicCursor.getInt(albumIdColumn);
                }else{
                    albumId = musicCursor.getInt(idColumn);
                }
                String artist = musicCursor.getString(artistColumn);
                long id = musicCursor.getLong(idColumn);
                String albumArt = musicCursor.getString(albumArtColumn);
                String albumKey = musicCursor.getString(albumKeyColumn);
                int firstYear = musicCursor.getInt(firstYearColumn);
                int lastYear = musicCursor.getInt(lastYearColumn);
                int noOfSongsForArtist = 0;
                if(noOfSongsForArtistColumn>0) {
                    noOfSongsForArtist = musicCursor.getInt(noOfSongsForArtistColumn);
                }
                int noOfSongs = musicCursor.getInt(noOfSongsColumn);
                OfflineAlbum album = new OfflineAlbum();
                album.setArtist(artist);
                album.setAlbumArt(albumArt);
                album.setAlbumId(albumId);
                album.setAlbumKey(albumKey);
                album.setAlbumTitle(title);
                album.setId(id);
                album.setFirstYear(firstYear);
                album.setLastYear(lastYear);
                album.setNumberOfSongs(noOfSongs);
                album.setNumberOfSongsForArtist(noOfSongsForArtist);

                albumArrayList.add(album);
            }
            while (musicCursor.moveToNext());
        }
        return albumArrayList;
    }
}
