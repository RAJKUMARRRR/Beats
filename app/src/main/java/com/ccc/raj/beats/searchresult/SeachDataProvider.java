package com.ccc.raj.beats.searchresult;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.ccc.raj.beats.model.Album;
import com.ccc.raj.beats.model.OfflineDataProvider;
import com.ccc.raj.beats.model.Song;

import java.util.ArrayList;


/**
 * Created by Raj on 1/23/2018.
 */

public class SeachDataProvider {
    public ArrayList<Album> searchAlbums(Context context, String query, String column){
        ArrayList<Album> albumArrayList = new ArrayList<>();
        ContentResolver musicResolver = context.getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] columns = {
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.COMPOSER,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ARTIST_ID
        };
        String where = column +" Like '%" + query +"%') GROUP BY (" + column;
        String sortOrder = MediaStore.Audio.Media.DATE_ADDED+" DESC";
        Cursor musicCursor = musicResolver.query(musicUri, columns, where, null, sortOrder);
        albumArrayList = OfflineDataProvider.getOfflineAlbumsFromCursor(musicCursor);
        return  albumArrayList;
    }
    public ArrayList<Song> searchSongs(Context context, String query, String column){
        ContentResolver musicResolver = context.getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String orderBy = MediaStore.Audio.Media.TRACK;
        String where = column +" Like '%" + query +"%') GROUP BY (" + column;
        String sortOrder = MediaStore.Audio.Media.DATE_ADDED+" DESC";
        Cursor musicCursor = musicResolver.query(musicUri, null, where, null, sortOrder);
        ArrayList<Song> songList = OfflineDataProvider.getSongsFromCursor(musicCursor);
        return songList;
    }
}
