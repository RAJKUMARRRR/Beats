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

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Raj on 3/1/2018.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    private static String DATABASE_NAME = "BeatsDB";
    private static int DATABASE_VERSION = 4;
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
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_RECENT_ALBUM);
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
}
