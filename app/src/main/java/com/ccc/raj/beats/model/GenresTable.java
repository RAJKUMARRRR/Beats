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
 * Created by Raj on 2/3/2018.
 */

public class GenresTable {
    public static final Uri TABLE_URI = MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI;
    public static final String NAME = MediaStore.Audio.GenresColumns.NAME;
    public static final String ID = MediaStore.Audio.Genres._ID;

    public static ArrayList<Album> getAllGenresAlbums(Context context){
        ArrayList<Album> genresList = new ArrayList<>();
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = TABLE_URI;
        String[] columns = new String[]{
                ID,NAME
        };
        Cursor cursor = contentResolver.query(uri,columns,null,null,null);
        String TAG = "GenresList";
        Log.i(TAG,"Total Rows"+cursor.getCount()+"");
        if(cursor!=null&&cursor.moveToFirst()){
            int idColumn = cursor.getColumnIndex(ID);
            int nameColumn = cursor.getColumnIndex(NAME);
            do{
                int id = cursor.getInt(idColumn);
                String name = cursor.getString(nameColumn);
                Log.i(TAG,"id:"+id);
                Log.i(TAG,"name:"+name);
                ArrayList<Song> songs = getSongsFromGeneres(context, id);
                if(songs.size()>0) {
                    Bitmap genresAlbumArt = OfflineDataProvider.getBitmapBySongsList(context, songs);
                    GenresAlbum genresAlbum = new GenresAlbum();
                    genresAlbum.setGenresId(id);
                    genresAlbum.setGenresName(name);
                    genresAlbum.setGenresBitmap(genresAlbumArt);
                    genresList.add(genresAlbum);
                }
            }while (cursor.moveToNext());
        }
        return genresList;
    }
    public static ArrayList<Song> getSongsFromGeneres(Context context,int genresId){
        Utitlity.Log("Inside getSongsFromGeneres");
        Utitlity.Log("genresId:"+genresId);
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = MediaStore.Audio.Genres.Members.getContentUri("external",genresId);
        Cursor cursor = contentResolver.query(uri,null,null,null,null);
        ArrayList<Song> songArrayList = SongTable.getSongsFromCursor(cursor);
        return  songArrayList;
    }

    public static ArrayList<Album> getAlbumsByGenresId(Context context,int genresId){
        Utitlity.Log("Inside getAlbumsByGenresId");
        Utitlity.Log("genresId:"+genresId);
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = MediaStore.Audio.Genres.Members.getContentUri("external",genresId);
        String selection = MediaStore.Audio.Media.ALBUM + " IS NOT NULL) GROUP BY (" + MediaStore.Audio.Media.ALBUM;
        Cursor cursor = contentResolver.query(uri,null,selection,null,null);
        ArrayList<Album> albumArrayList = new ArrayList<>();
        if(cursor!=null&&cursor.moveToFirst()){
            int idColumn = cursor.getColumnIndex(MediaStore.Audio.Genres.Members.ALBUM_ID);
            int nameColumn = cursor.getColumnIndex(MediaStore.Audio.Genres.Members.ALBUM);
            int artistColumn = cursor.getColumnIndex(MediaStore.Audio.Genres.Members.ARTIST);
            do{
                int id = cursor.getInt(idColumn);
                String name = cursor.getString(nameColumn);
                String artist = cursor.getString(artistColumn);
                OfflineAlbum offlineAlbum = new OfflineAlbum();
                offlineAlbum.setId(id);
                offlineAlbum.setAlbumId(id);
                offlineAlbum.setAlbumTitle(name);
                String albumArt = OfflineDataProvider.getAlbumArtByAlbumId(context,id);
                offlineAlbum.setAlbumArt(albumArt);
                offlineAlbum.setArtist(artist);
                albumArrayList.add(offlineAlbum);
            }while (cursor.moveToNext());
        }
        return  albumArrayList;
    }
    public static Album getGenresAlbumForGenresId(Context context,int genresId){
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = TABLE_URI;
        String[] columns = new String[]{
                ID,NAME
        };
        String where = ID + "=?";
        String whereVal[] = {
                String.valueOf(genresId)
        };
        GenresAlbum genresAlbum = new GenresAlbum();
        Cursor cursor = contentResolver.query(uri,columns,where,whereVal,null);
        String TAG = "GenresList";
        Log.i(TAG,"Total Rows"+cursor.getCount()+"");
        if(cursor!=null&&cursor.moveToFirst()){
            int idColumn = cursor.getColumnIndex(ID);
            int nameColumn = cursor.getColumnIndex(NAME);
            do{
                int id = cursor.getInt(idColumn);
                String name = cursor.getString(nameColumn);
                Log.i(TAG,"id:"+id);
                Log.i(TAG,"name:"+name);
                ArrayList<Song> songs = getSongsFromGeneres(context, id);
                if(songs.size()>0) {
                    Bitmap genresAlbumArt = OfflineDataProvider.getBitmapBySongsList(context, songs);
                    genresAlbum.setGenresId(id);
                    genresAlbum.setGenresName(name);
                    genresAlbum.setGenresBitmap(genresAlbumArt);
                }
            }while (cursor.moveToNext());
        }
        return genresAlbum;
    }

}
