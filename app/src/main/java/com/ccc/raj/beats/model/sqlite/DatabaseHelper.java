package com.ccc.raj.beats.model.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ccc.raj.beats.model.Album;
import com.ccc.raj.beats.model.AlbumTable;
import com.ccc.raj.beats.model.OfflineAlbum;
import com.ccc.raj.beats.model.OfflineSong;
import com.ccc.raj.beats.model.Song;
import com.ccc.raj.beats.model.SongTable;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Raj on 3/1/2018.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    private static String DATABASE_NAME = "BeatsDB";
    private static int DATABASE_VERSION = 5;
    private static DatabaseHelper sDatabaseHelper;

    //Table for RecentAlbums
    private static String TABLE_RECENT_ALBUM = "RecentAlbums";
    private static String DATE_ADDED = "date_added";
    private static final String CREATE_TABLE_RECENT_ALBUMS = "CREATE TABLE "
            + TABLE_RECENT_ALBUM + "(" + AlbumTable.ID + " INTEGER PRIMARY KEY," + AlbumTable.ARTIST
            + " TEXT," + AlbumTable.ALBUM + " TEXT," + AlbumTable.ALBUM_ART + " TEXT," + AlbumTable.ALBUM_ID +" INTEGER,"
            + AlbumTable.ALBUM_KEY +" TEXT," + AlbumTable.FIRST_YEAR +" INTEGER,"+ AlbumTable.LAST_YEAR +" INTEGER," + AlbumTable.NUMBER_OF_SONGS+" INTEGER,"
            + AlbumTable.NUMBER_OF_SONGS_FOR_ARTIST +" INTEGER,"
            + DATE_ADDED + " INTEGER" + ")";


    //Table for Songs
    private static String TABLE_SONGS = "Songs";
    public static String SONG_PLAY_FREQUENCY = "SongPlayedCount";
    private static final String CREATE_TABLE_SONGS = "CREATE TABLE "
            + TABLE_SONGS + "("
            + SongTable.ID + " INTEGER PRIMARY KEY,"
            + SongTable.ARTIST + " TEXT,"
            + SongTable.ARTIST_ID + " INTEGER,"
            + SongTable.ARTIST_KEY + " TEXT,"
            + SongTable.ALBUM + " TEXT,"
            + SongTable.ALBUM_ID +" INTEGER,"
            + SongTable.ALBUM_KEY +" TEXT,"
            + SongTable.DATE_MODIFIED +" INTEGER,"
            + SongTable.BOOKMARK +" INTEGER,"
            + SongTable.DURATION+" INTEGER,"
            + SongTable.DATA +" TEXT,"
            + SongTable.SIZE +" INTEGER,"
            + SongTable.TRACK +" INTEGER,"
            + SongTable.YEAR +" INTEGER,"
            + SongTable.TITLE +" TEXT,"
            + SongTable.TITLE_KEY+" TEXT,"
            + SongTable.COMPOSER +" TEXT,"
            + SongTable.DISPLAY_NAME +" TEXT,"
            + SONG_PLAY_FREQUENCY +" INTEGER,"
            + SongTable.DATE_ADDED + " INTEGER" + ")";

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static DatabaseHelper getDatabaseHelper(Context context){
        if(sDatabaseHelper == null){
            sDatabaseHelper = new DatabaseHelper(context);
        }
        return sDatabaseHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_RECENT_ALBUMS);
        sqLiteDatabase.execSQL(CREATE_TABLE_SONGS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_RECENT_ALBUM);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_SONGS);
        onCreate(sqLiteDatabase);
    }

    public ArrayList<Album> getRecentAlbums(int limit){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_RECENT_ALBUM,null,null,null,null,null,DATE_ADDED+" DESC LIMIT "+limit);
        ArrayList<Album> recentAlbumsList = AlbumTable.getOfflineAlbumsFromCursor(cursor);
        //cursor.close();
        db.close();
        return recentAlbumsList;
    }

    public void addRecentAlbum(Context context,int albumId,boolean isRecent){
        OfflineAlbum offlineAlbum = (OfflineAlbum) AlbumTable.getAlbumsByAlbumId(context,albumId);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(AlbumTable.ALBUM,offlineAlbum.getAlbumTitle());
        contentValues.put(AlbumTable.ALBUM_KEY,offlineAlbum.getAlbumKey());
        contentValues.put(AlbumTable.ALBUM_ART,offlineAlbum.getAlbumArt());
        contentValues.put(AlbumTable.ALBUM_ID,offlineAlbum.getAlbumId());
        contentValues.put(AlbumTable.ARTIST,offlineAlbum.getArtist());
        contentValues.put(AlbumTable.FIRST_YEAR,offlineAlbum.getFirstYear());
        contentValues.put(AlbumTable.ID,offlineAlbum.getId());
        contentValues.put(AlbumTable.LAST_YEAR,offlineAlbum.getLastYear());
        contentValues.put(AlbumTable.NUMBER_OF_SONGS,offlineAlbum.getNumberOfSongs());
        contentValues.put(AlbumTable.NUMBER_OF_SONGS_FOR_ARTIST,offlineAlbum.getNumberOfSongsForArtist());
        long timemillsecs = new Date().getTime();
        contentValues.put(DATE_ADDED,timemillsecs);
        long id = db.insert(TABLE_RECENT_ALBUM,null,contentValues);
        if(id == -1 && isRecent){
            String where = AlbumTable.ID +"=?";
            String[] values = {
                    String.valueOf(offlineAlbum.getId())
            };
            contentValues.clear();
            contentValues.put(DATE_ADDED,timemillsecs);
            db.update(TABLE_RECENT_ALBUM,contentValues,where,values);
        }
        db.close();
    }


    public ArrayList<Song> getFrequentlyPlayedSongs(int limit){
        SQLiteDatabase db = this.getReadableDatabase();
        String orderBy = SONG_PLAY_FREQUENCY+" DESC LIMIT "+limit;
        Cursor cursor = db.query(TABLE_SONGS,null,null,null,null,null,orderBy);
        ArrayList<Song> frequentlyPlayedSongsList = SongTable.getSongsFromCursor(cursor);
        db.close();
        return frequentlyPlayedSongsList;
    }

    public void addToFrequentList(Context context,Song song){
        OfflineSong offlineSong = (OfflineSong) song;
        SQLiteDatabase db = this.getWritableDatabase();
        String[] values = {String.valueOf(offlineSong.getId())};
        Cursor cursor = db.query(TABLE_SONGS,null,SongTable.ID +"=?",values,null,null,null);
        ContentValues contentValues = new ContentValues();
        if(cursor.moveToFirst()){
             int countColumnInd = cursor.getColumnIndex(SONG_PLAY_FREQUENCY);
             int count = cursor.getInt(countColumnInd);
             contentValues.put(SONG_PLAY_FREQUENCY,++count);
             db.update(TABLE_SONGS,contentValues,SongTable.ID+"=?",values);
        }else{
            contentValues.put(SongTable.ALBUM_KEY,offlineSong.getAlbumKey());
            contentValues.put(SongTable.ALBUM,offlineSong.getAlbum());
            contentValues.put(SongTable.ALBUM_ID,offlineSong.getAlbumId());
            contentValues.put(SongTable.ARTIST_KEY,offlineSong.getArtistKey());
            contentValues.put(SongTable.ARTIST,offlineSong.getArtist());
            contentValues.put(SongTable.TITLE_KEY,offlineSong.getTitleKey());
            contentValues.put(SongTable.TITLE,offlineSong.getTitle());
            contentValues.put(SongTable.COMPOSER,offlineSong.getComposer());
            contentValues.put(SongTable.ARTIST_ID,offlineSong.getArtistId());
            contentValues.put(SongTable.DISPLAY_NAME,offlineSong.getDisplayName());
            contentValues.put(SongTable.DURATION,offlineSong.getDuratio());
            contentValues.put(SongTable.SIZE,offlineSong.getSize());
            contentValues.put(SongTable.TRACK,offlineSong.getTrackNumber());
            contentValues.put(SongTable.YEAR,offlineSong.getYear());
            contentValues.put(SongTable.DATE_ADDED,offlineSong.getDate());
            contentValues.put(SongTable.DATE_MODIFIED,offlineSong.getDateModified());
            contentValues.put(SongTable.BOOKMARK,offlineSong.getBookmark());
            contentValues.put(SongTable.DATA,offlineSong.getAlbumFullPath());
            contentValues.put(SONG_PLAY_FREQUENCY,1);
            contentValues.put(SongTable.ID,offlineSong.getId());
            long id = db.insert(TABLE_SONGS,null,contentValues);
        }
        db.close();
    }

}
