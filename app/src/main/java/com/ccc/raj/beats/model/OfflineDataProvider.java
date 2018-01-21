package com.ccc.raj.beats.model;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.ccc.raj.beats.R;

import java.util.ArrayList;
import java.util.Hashtable;

import static com.ccc.raj.beats.Utitlity.formatString;

/**
 * Created by Raj on 1/19/2018.
 */

public class OfflineDataProvider {


    public static ArrayList<Album> getOfflineAlbums(Context context){
        ArrayList<Album> albumArrayList = new ArrayList<>();
        ContentResolver musicResolver = context.getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] columns = {
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.DATA
        };
        String where = "GROUP BY "+MediaStore.Audio.Media.ALBUM;
        Cursor musicCursor = musicResolver.query(musicUri, columns, null, null, null);
        if(musicCursor!=null && musicCursor.moveToFirst()){
            int titleColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.ALBUM);
            int albumId = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.ALBUM_ID);
            int albumPath = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.DATA);
            Hashtable<String,Album> hashtable = new Hashtable<>();
            do {
                String thisTitle = formatString(musicCursor.getString(titleColumn),20);
                String fullPath = musicCursor.getString(albumPath);
                int thisAlbumId = musicCursor.getInt(albumId);
                Log.i("Beats",fullPath);
                Log.i("Beats",thisTitle);
                Log.i("Beats",thisAlbumId+"");
                hashtable.put(thisTitle,new OfflineAlbum(fullPath,thisAlbumId, thisTitle));
            }
            while (musicCursor.moveToNext());
            albumArrayList = new ArrayList<>(hashtable.values());
        }
        return albumArrayList;
    }

    public static ArrayList<Song> getSongsFromAlbum(Context context,String Album,String albumFullPath){
        ArrayList<Song> songList = new ArrayList<>();
        ContentResolver musicResolver = context.getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] columns = {
                android.provider.MediaStore.Audio.Media.TITLE,
                android.provider.MediaStore.Audio.Media._ID,
                android.provider.MediaStore.Audio.Media.ARTIST
        };
        String where = MediaStore.Audio.Media.ALBUM + "=?";
        String whereVal[] = {
          Album
        };
        String orderBy = MediaStore.Audio.Media.TITLE;
        Cursor musicCursor = musicResolver.query(musicUri, columns, where, whereVal, orderBy);
        if(musicCursor!=null && musicCursor.moveToFirst()){
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            //add songs to list
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = formatString(musicCursor.getString(titleColumn),20);
                String thisArtist = formatString(musicCursor.getString(artistColumn),10);
                Log.i("Beats",thisArtist);
                if(!thisArtist.equalsIgnoreCase("<unknown>"))
                    songList.add(new OfflineSong(thisId, thisTitle, thisArtist,albumFullPath));
            }
            while (musicCursor.moveToNext());
        }
        return songList;
    }

    public static ArrayList<Song> getOfflineSongsList(Context context){
        ArrayList<Song> songList = new ArrayList<>();
        ContentResolver musicResolver = context.getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
        if(musicCursor!=null && musicCursor.moveToFirst()){
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            int albumId = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.DATA);
            //add songs to list
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = formatString(musicCursor.getString(titleColumn),20);
                String thisArtist = formatString(musicCursor.getString(artistColumn),10);
                String fullPath = musicCursor.getString(albumId);
                Log.i("Beats",fullPath);
                Log.i("Beats",thisArtist);
                if(!thisArtist.equalsIgnoreCase("<unknown>"))
                songList.add(new OfflineSong(thisId, thisTitle, thisArtist,fullPath));
            }
            while (musicCursor.moveToNext());
        }
        return songList;
    }


    public static Bitmap getBitmapByAlbumPath(Context context,String albumFullPath){
        MediaMetadataRetriever metaRetriver;
        metaRetriver = new MediaMetadataRetriever();
        metaRetriver.setDataSource(albumFullPath);
        byte[] art;
        art = metaRetriver.getEmbeddedPicture();
        Bitmap albumImage;
        if(art != null) {
            albumImage = BitmapFactory.decodeByteArray(art, 0, art.length);
        }else{
            albumImage = BitmapFactory.decodeResource(context.getResources(),R.drawable.music);
        }
        return albumImage;
    }

    public static Bitmap getBitmapByAlbumId(Context context,int albumId){
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                new String[] {MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM_ART},
                MediaStore.Audio.Albums._ID+ "=?",
                new String[] {String.valueOf(albumId)},
                null);

        if (cursor.moveToFirst()) {
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));
            Bitmap bm;
            if(path != null) {
                bm = BitmapFactory.decodeFile(path);
            }else{
                bm = BitmapFactory.decodeResource(context.getResources(),R.drawable.music);
            }
            return bm;
        }else{
            Bitmap bm = BitmapFactory.decodeResource(context.getResources(),R.drawable.music);
            return bm;
        }
    }

}
