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


  /*  public static ArrayList<Album> getOfflineAlbums(Context context, String column) {
        ArrayList<Album> albumArrayList = new ArrayList<>();
        ContentResolver musicResolver = context.getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] columns = {
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.COMPOSER,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ARTIST_ID,
        };
        String where = "0 == 0) GROUP BY (" + column;
        String sortOrder = MediaStore.Audio.Media.DATE_ADDED + " DESC";
        Cursor musicCursor = musicResolver.query(musicUri, columns, where, null, sortOrder);
        Log.i("BeatsAlbumCount", musicCursor.getCount() + "");
        albumArrayList = getOfflineAlbumsFromCursor(musicCursor);
        return albumArrayList;
    }

    public static ArrayList<Song> getSongsFromAlbum(Context context, String Album) {
        ContentResolver musicResolver = context.getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String where = MediaStore.Audio.Media.ALBUM + "=?";
        String whereVal[] = {
                Album
        };
        String orderBy = MediaStore.Audio.Media.TRACK;
        Cursor musicCursor = musicResolver.query(musicUri, null, where, whereVal, orderBy);
        ArrayList<Song> songList = getSongsFromCursor(musicCursor);
        return songList;
    }

    public static ArrayList<Album> getOfflineAlbumsFromCursor(Cursor musicCursor) {
        ArrayList<Album> albumArrayList = new ArrayList<>();
        if (musicCursor != null && musicCursor.moveToFirst()) {
            int titleColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.ALBUM);
            int albumId = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.ALBUM_ID);
            int albumPath = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.DATA);
            int composerColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.COMPOSER);
            int artistColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int artistIdColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID);
            do {
                String thisTitle = musicCursor.getString(titleColumn);
                String fullPath = musicCursor.getString(albumPath);
                int thisAlbumId = musicCursor.getInt(albumId);
                String composer = musicCursor.getString(composerColumn);
                String artist = musicCursor.getString(artistColumn);
                String artistId = musicCursor.getString(artistIdColumn);
                Log.i("Beats", fullPath);
                Log.i("Beats", thisTitle);
                Log.i("Beats", thisAlbumId + "");
                Log.i("BeatsArtist", artist + "");
                Log.i("BeatsArtistID", "ID" + artistId);
                OfflineAlbum album = new OfflineAlbum();
                album.setArtist(artist);
                albumArrayList.add(album);
            }
            while (musicCursor.moveToNext());
        }
        return albumArrayList;
    }

    public static ArrayList<Song> getSongsFromColumn(Context context, String column, String columnValue) {
        ContentResolver musicResolver = context.getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String where = column + "=?";//MediaStore.Audio.Media.ALBUM
        String whereVal[] = {
                columnValue
        };
        String orderBy = MediaStore.Audio.Media.TRACK;
        Cursor musicCursor = musicResolver.query(musicUri, null, where, whereVal, orderBy);
        ArrayList<Song> songList = getSongsFromCursor(musicCursor);
        return songList;
    }

    public static ArrayList<Song> getOfflineSongsList(Context context) {
        ContentResolver musicResolver = context.getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
        ArrayList<Song> songList = getSongsFromCursor(musicCursor);
        return songList;
    }

    public static ArrayList<Song> getSongsFromCursor(Cursor musicCursor) {
        ArrayList<Song> songList = new ArrayList<>();
        if (musicCursor != null && musicCursor.moveToFirst()) {
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            int fullpathColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.DATA);
            int durationColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.DURATION);
            int displayNameColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.DISPLAY_NAME);
            int dateAddedColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.DATE_ADDED);
            int composorColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.COMPOSER);
            int trackColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.TRACK);


            int albumIdColumn;
            int albumColumn;
            int albumKeyColumn;
            int artistIdColumn;
            int artistKeyColumn;
            int bookmarkColumn;
            int titleKeyColumn;
            int dateModifiedColumn;
            int yearColumn;
            int sizeColumn;


            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                String fullPath = musicCursor.getString(fullpathColumn);

                long duration = musicCursor.getLong(durationColumn);
                String displayName = musicCursor.getString(displayNameColumn);
                String date = musicCursor.getString(dateAddedColumn);
                String composor = musicCursor.getString(composorColumn);
                int trackNumber = musicCursor.getInt(trackColumn);
                if (duration > 60000) {
                    OfflineSong song = new OfflineSong(thisId, thisTitle, thisArtist, fullPath);
                    song.setDuratio(duration);
                    Log.i("Beats", "duration:" + duration);
                    song.setComposer(composor);
                    song.setDate(date);
                    song.setDisplayName(displayName);
                    song.setTrackNumber(trackNumber);
                    songList.add(song);
                }
            }
            while (musicCursor.moveToNext());
        }
        return songList;
    }

*/
    public static Bitmap getBitmapByAlbumPath(Context context, String albumFullPath) {
        MediaMetadataRetriever metaRetriver;
        metaRetriver = new MediaMetadataRetriever();
        metaRetriver.setDataSource(albumFullPath);
        byte[] art;
        art = metaRetriver.getEmbeddedPicture();
        Bitmap albumImage;
        if (art != null) {
            albumImage = BitmapFactory.decodeByteArray(art, 0, art.length);
        } else {
            albumImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.music);
        }
        return albumImage;
    }

    public static Bitmap getBitmapByAlbumId(Context context, int albumId) {
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM_ART},
                MediaStore.Audio.Albums._ID + "=?",
                new String[]{String.valueOf(albumId)},
                null);

        if (cursor.moveToFirst()) {
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));
            Bitmap bm;
            if (path != null) {
                bm = BitmapFactory.decodeFile(path);
            } else {
                bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.music);
            }
            return bm;
        } else {
            Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.music);
            return bm;
        }
    }

    public static Bitmap getBitmapByAlbumArt(String albumArt) {
        Bitmap bm = BitmapFactory.decodeFile(albumArt);
        return bm;
    }

    public static Bitmap getBitmapBySongId(Context context, long songId) {
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Media._ID, MediaStore.Audio.Media.ALBUM_ID},
                MediaStore.Audio.Media._ID + "=?",
                new String[]{String.valueOf(songId)},
                null);

        if (cursor.moveToFirst()) {
            int albumId = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
            Bitmap bm = getBitmapByAlbumId(context, albumId);
            return bm;
        } else {
            Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.music);
            return bm;
        }
    }
}
