package com.ccc.raj.beats.model;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.ccc.raj.beats.Utitlity;

import java.util.ArrayList;

/**
 * Created by Raj on 2/4/2018.
 */

public class ArtistTable {
    public static final Uri TABLE_URI = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;
    public static final String NAME = MediaStore.Audio.ArtistColumns.ARTIST;
    public static final String ARTIST_KEY = MediaStore.Audio.ArtistColumns.ARTIST_KEY;
    public static final String NUMBER_OF_ALBUMS = MediaStore.Audio.ArtistColumns.NUMBER_OF_ALBUMS;
    public static final String NUMBER_OF_TRACKS = MediaStore.Audio.ArtistColumns.NUMBER_OF_TRACKS;
    public static final String ID = MediaStore.Audio.Artists._ID;

    public static ArrayList<Album> getAllArtistAlbums(Context context){
        ArrayList<Album> artistAlbumList = new ArrayList<>();
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = TABLE_URI;
        Cursor cursor = contentResolver.query(uri,null,null,null,null);
        String TAG = "ArtistAlbums";
        Log.i(TAG,"Total Rows"+cursor.getCount()+"");
        if(cursor!=null&&cursor.moveToFirst()){
            int idColumn = cursor.getColumnIndex(ID);
            int nameColumn = cursor.getColumnIndex(NAME);
            do{
                int id = cursor.getInt(idColumn);
                String name = cursor.getString(nameColumn);
                Log.i(TAG,"id:"+id);
                Log.i(TAG,"name:"+name);
                ArtistAlbum artistAlbum = new ArtistAlbum();
                artistAlbum.setArtistId(id);
                artistAlbum.setArtistName(name);
                artistAlbum.setAlbumArt(getAlbumsForArtistId(context,id).get(0).getAlbumArt());

                artistAlbumList.add(artistAlbum);
            }while (cursor.moveToNext());
        }
        return artistAlbumList;
    }
    public static ArrayList<Album> getAlbumsForArtistId(Context context,int artistId){
        Utitlity.Log("Inside getSongsFromArtist");
        Utitlity.Log("artistId:"+artistId);
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = MediaStore.Audio.Artists.Albums.getContentUri("external",artistId);
        Cursor cursor = contentResolver.query(uri,null,null,null,null);
        ArrayList<Album> albumArrayList = AlbumTable.getOfflineAlbumsFromCursor(cursor);
        return  albumArrayList;
    }
    public static Album getArtistAlbumForArtistId(Context context,int artistId){
        Utitlity.Log("Inside getSongsFromArtist");
        Utitlity.Log("artistId:"+artistId);
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = TABLE_URI;
        String where = ID + "=?";
        String whereVal[] = {
                String.valueOf(artistId)
        };
        ArtistAlbum artistAlbum = new ArtistAlbum();
        Cursor cursor = contentResolver.query(uri,null,where,whereVal,null);
        if(cursor!=null&&cursor.moveToFirst()){
            int idColumn = cursor.getColumnIndex(ID);
            int nameColumn = cursor.getColumnIndex(NAME);
            do{
                int id = cursor.getInt(idColumn);
                String name = cursor.getString(nameColumn);
                artistAlbum.setArtistId(id);
                artistAlbum.setArtistName(name);
                artistAlbum.setAlbumArt(getAlbumsForArtistId(context,id).get(0).getAlbumArt());
            }while (cursor.moveToNext());
        }
        return artistAlbum;
    }
}
