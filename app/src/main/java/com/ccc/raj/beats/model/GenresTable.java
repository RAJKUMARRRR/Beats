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

    /*public static ArrayList<Album> getAlbumsByGenresId(Context context,int genresId){
        Utitlity.Log("Inside getAlbumsByGenresId");
        Utitlity.Log("genresId:"+genresId);
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = MediaStore.Audio.Genres.Members.getContentUri("external",genresId);
        Cursor cursor = contentResolver.query(uri,null,null,null,null);
        ArrayList<Album> albumArrayList = AlbumTable.getOfflineAlbumsFromCursor(cursor);
        return  albumArrayList;
    }*/

}
