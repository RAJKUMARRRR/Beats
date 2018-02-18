package com.ccc.raj.beats.model;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.ccc.raj.beats.Utitlity;

import java.util.ArrayList;

/**
 * Created by Raj on 1/30/2018.
 */

public class PlayListTable {
    public static final Uri TABLE_URI = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
    public static final String DATA = MediaStore.Audio.PlaylistsColumns.DATA;
    public static final String DATE_ADDED = MediaStore.Audio.PlaylistsColumns.DATE_ADDED;
    public static final String DATE_MODIFIED = MediaStore.Audio.PlaylistsColumns.DATE_MODIFIED;
    public static final String NAME = MediaStore.Audio.PlaylistsColumns.NAME;
    public static final String ID = MediaStore.Audio.Playlists._ID;

    public static ArrayList<Album> getAllPlayLists(Context context){
        ArrayList<Album> playList = new ArrayList<>();
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
        String[] columns = new String[]{
                ID,NAME,DATA,DATE_ADDED,DATE_MODIFIED
        };
        Cursor cursor = contentResolver.query(uri,columns,null,null,null);
        String TAG = "PlayLists";
        Log.i(TAG,"Total Rows"+cursor.getCount()+"");
        if(cursor!=null&&cursor.moveToFirst()){
            int idColumn = cursor.getColumnIndex(ID);
            int nameColumn = cursor.getColumnIndex(NAME);
            int dateColumn = cursor.getColumnIndex(DATA);
            int dateAddedColumn = cursor.getColumnIndex(DATE_ADDED);
            int dateModifiedColumn = cursor.getColumnIndex(DATE_MODIFIED);
            do{
                int id = cursor.getInt(idColumn);
                String name = cursor.getString(nameColumn);
                String dateAdded = cursor.getString(dateAddedColumn);
                String date = cursor.getString(dateColumn);
                String dateModified = cursor.getString(dateModifiedColumn);
                Log.i(TAG,"id:"+id);
                Log.i(TAG,"name:"+name);
                Log.i(TAG,"date:"+date);
                Log.i(TAG,"dateAdded:"+dateAdded);
                Log.i(TAG,"dateModified:"+dateModified);
                ArrayList<Song> songs = getSongsFromPlayLists(context,id);
                Bitmap playListAlbumArt = OfflineDataProvider.getBitmapBySongsList(context,songs);
                PlayListAlbum playListAlbum = new PlayListAlbum();
                playListAlbum.setPlayListId(id);
                playListAlbum.setPlayListName(name);
                playListAlbum.setPlayListBitmap(playListAlbumArt);
                playList.add(playListAlbum);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return playList;
    }

    public static int createPlayList(Context context,String name){
        Utitlity.Log("Inside createPlayList");
        Utitlity.Log("Name:"+name);
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = TABLE_URI;
        ContentValues values = new ContentValues();
        values.put(NAME,name);
        Uri rowUri = contentResolver.insert(uri,values);
        int id = -1;
        if(rowUri != null) {
            id = Integer.valueOf(rowUri.getLastPathSegment());
        }
        Log.i("PlayLists", "id:"+id);
        return id;
    }

    public static boolean deletePlayList(Context context,int playListId){
        Utitlity.Log("Insdie deletePlayList");
        Utitlity.Log("playListId:"+playListId);
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = TABLE_URI;
        String where = ID + " = ?";
        String[] whereValues = {
          String.valueOf(playListId)
        };
        int rows = contentResolver.delete(uri,where,whereValues);
        if(rows>0){
            return true;
        }else {
            return false;
        }
    }

    public static boolean updatePlayList(Context context,int playListId,String updatedName){
        Utitlity.Log("Inside updatePlayList");
        Utitlity.Log("updatedName:"+updatedName);
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = TABLE_URI;
        String where = ID + " = ?";
        String[] whereValues = {
                String.valueOf(playListId)
        };
        ContentValues values = new ContentValues();
        values.put(NAME,updatedName);
        int rows = contentResolver.update(uri,values,where,whereValues);
        if(rows != 0) {
            return true;
        }
        return false;
    }

    public static ArrayList<Song> getSongsFromPlayLists(Context context,int playListId){
        Utitlity.Log("Inside getSongsFromPlayLists");
        Utitlity.Log("playListId:"+playListId);
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external",playListId);
        Cursor cursor = contentResolver.query(uri,null,null,null,null);
        ArrayList<Song> songArrayList = SongTable.getSongsFromCursor(cursor);
        cursor.close();
        return  songArrayList;
    }

    public static int addSongToPlayList(Context context,int playListId,Song songParam){
        Utitlity.Log("Inside addSongToPlayList");
        Utitlity.Log("playListId:"+playListId);
        Utitlity.Log("Song Name:"+songParam.getTitle());
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external",playListId);
        OfflineSong song = (OfflineSong) songParam;
        ContentValues contentValues = new ContentValues();
        //contentValues.put(SongTable.ALBUM_KEY,song.getAlbumKey());
        //contentValues.put(SongTable.ALBUM,song.getAlbum());
        //contentValues.put(SongTable.ALBUM_ID,song.getAlbumId());
        //contentValues.put(SongTable.ARTIST_KEY,song.getArtistKey());
        //contentValues.put(SongTable.ARTIST,song.getArtist());
        //contentValues.put(SongTable.TITLE_KEY,song.getTitleKey());
        //contentValues.put(SongTable.TITLE,song.getTitle());
        //contentValues.put(SongTable.COMPOSER,song.getComposer());
        //contentValues.put(SongTable.ARTIST_ID,song.getArtistId());
        //contentValues.put(SongTable.DISPLAY_NAME,song.getDisplayName());
        //contentValues.put(SongTable.DURATION,song.getDuratio());
        //contentValues.put(SongTable.SIZE,song.getSize());
        //contentValues.put(SongTable.TRACK,song.getTrackNumber());
       // contentValues.put(SongTable.YEAR,song.getYear());
        contentValues.put(MediaStore.Audio.Playlists.Members._ID,song.getId());
        contentValues.put(MediaStore.Audio.Playlists.Members.AUDIO_ID,song.getId());
        contentValues.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER,0);
        contentValues.put(MediaStore.Audio.Playlists.Members.PLAYLIST_ID,playListId);
        Uri rowUri = contentResolver.insert(uri,contentValues);
        int id = -1;
        if(rowUri != null){
            id = Integer.valueOf(rowUri.getLastPathSegment());
        }
        return id;
    }

    public static boolean deleteSongFromPlayList(Context context,int playListId,Song songParam){
        Utitlity.Log("Inside deleteSongFromPlayList");
        Utitlity.Log("playListId:"+playListId);
        Utitlity.Log("Song Name:"+songParam.getTitle());
        ContentResolver contentResolver = context.getContentResolver();
        OfflineSong song = (OfflineSong) songParam;
        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external",playListId);
        String where = MediaStore.Audio.Playlists.Members._ID + " = ?";
        String[] whereValues = {
                String.valueOf(song.getId())
        };
        int rows = contentResolver.delete(uri,where,whereValues);
        if(rows>0){
            return true;
        }else {
            return false;
        }
    }

}
