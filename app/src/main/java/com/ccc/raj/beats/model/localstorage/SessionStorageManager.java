package com.ccc.raj.beats.model.localstorage;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.ccc.raj.beats.AlbumSongsListActivity;
import com.ccc.raj.beats.MoreRecordsActivity;
import com.ccc.raj.beats.model.AlbumTable;
import com.ccc.raj.beats.model.SongTable;

/**
 * Created by Raj on 3/10/2018.
 */

public class SessionStorageManager {
    public static final String SHARED_PREFERENCE_FILE_NAME = "com.ccc.raj.beats.model.localstorage.localstoragemedia";
    public static final String ALBUM_TYPE = "com.ccc.raj.beats.model.localstorage.localstoragemedia.albumtype";
    public static final String ALBUM_TITLE = "com.ccc.raj.beats.model.localstorage.localstoragemedia.albumtitle";

    public static void storeSessionData(Context context, int albumId,int albumType,int songPosition,int playedDuration,String albumTitle){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCE_FILE_NAME,context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(AlbumTable.ALBUM_ID,albumId);
        editor.putInt(ALBUM_TYPE,albumType);
        editor.putString(ALBUM_TITLE,albumTitle);
        editor.putInt(SongTable.TRACK,songPosition);
        editor.putInt(SongTable.BOOKMARK,playedDuration);
        editor.commit();
        Toast.makeText(context,"Bookmark:"+playedDuration+",AlbumId:"+albumId+",songPos:"+songPosition,Toast.LENGTH_SHORT).show();
    }

    public static int getStoredAlbumId(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCE_FILE_NAME,context.MODE_PRIVATE);
        return sharedPreferences.getInt(AlbumTable.ALBUM_ID,-1);
    }
    public static int getStoredAlbumType(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCE_FILE_NAME,context.MODE_PRIVATE);
        return sharedPreferences.getInt(ALBUM_TYPE,-1);
    }
    public static String getStoredAlbumTitle(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCE_FILE_NAME,context.MODE_PRIVATE);
        return sharedPreferences.getString(ALBUM_TITLE,"");
    }
    public static int getStoredSongPosition(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCE_FILE_NAME,context.MODE_PRIVATE);
        return sharedPreferences.getInt(SongTable.TRACK,-1);
    }
    public static int getStoredBookmark(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCE_FILE_NAME,context.MODE_PRIVATE);
        return sharedPreferences.getInt(SongTable.BOOKMARK,0);
    }
}
