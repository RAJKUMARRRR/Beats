package com.ccc.raj.beats.searchresult;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.ccc.raj.beats.model.Album;
import com.ccc.raj.beats.model.AlbumTable;
import com.ccc.raj.beats.model.OfflineDataProvider;
import com.ccc.raj.beats.model.Song;
import com.ccc.raj.beats.model.SongTable;

import java.util.ArrayList;


/**
 * Created by Raj on 1/23/2018.
 */

public class SeachDataProvider {
    public ArrayList<Album> searchAlbums(Context context, String query,String column,boolean isCustom,String customWhere){
        ArrayList<Album> albumArrayList = new ArrayList<>();
        ContentResolver musicResolver = context.getContentResolver();
        Uri musicUri = AlbumTable.TABLE_URI;
        String where = column +" Like '%" + query +"%') GROUP BY (" + column;
        String sortOrder = AlbumTable.ALBUM+" DESC";
        if(isCustom){
            where = customWhere;
        }
        Cursor musicCursor = musicResolver.query(musicUri, null, where, null, sortOrder);
        albumArrayList = AlbumTable.getOfflineAlbumsFromCursor(musicCursor);
        return  albumArrayList;
    }
    public ArrayList<Song> searchSongs(Context context, String query, String column,boolean isCustom,String customWhere){
        ContentResolver musicResolver = context.getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String orderBy = MediaStore.Audio.Media.TRACK;
        String where = column +" Like '%" + query +"%') GROUP BY (" + column;
        String sortOrder = MediaStore.Audio.Media.DATE_ADDED+" DESC";
        if(isCustom){
            where = customWhere;
        }
        Cursor musicCursor = musicResolver.query(musicUri, null, where, null, sortOrder);
        ArrayList<Song> songList = SongTable.getSongsFromCursor(musicCursor);
        return songList;
    }
}
