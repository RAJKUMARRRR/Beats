package com.ccc.raj.beats.searchresult;

import android.app.SearchManager;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.provider.MediaStore;
import android.util.Log;

import com.ccc.raj.beats.R;
import com.ccc.raj.beats.model.Album;
import com.ccc.raj.beats.model.AlbumTable;
import com.ccc.raj.beats.model.OfflineAlbum;
import com.ccc.raj.beats.model.Song;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Raj on 2/18/2018.
 */

public class SearchSuggestionProvider {
    public  static final String TAG = CustomSuggestionsProvider.class.getSimpleName();
    public static  final String COLUMNS[] = new String[]{
            "_id",
            SearchManager.SUGGEST_COLUMN_TEXT_1,
            SearchManager.SUGGEST_COLUMN_INTENT_ACTION,
            SearchManager.SUGGEST_COLUMN_INTENT_DATA,
            SearchManager.SUGGEST_COLUMN_ICON_1
    };
    public Context context;
    SearchDataProvider searchDataProvider = new SearchDataProvider();

    public SearchSuggestionProvider(Context context){
        this.context = context;
    }

    public Cursor getSearchSuggestionCursor(String query) {
        MatrixCursor cursor = new MatrixCursor(COLUMNS);
        if (query == null || query.length() == 0) {
            return cursor;
        }

        try {
            List<Song> list = getSongsData(query);
            int n = 0;
            for (Song obj : list) {
                String title =  obj.getTitle();
                cursor.addRow(new Object[] { n,
                        obj.getTitle(),
                        "SONG",
                        obj.getTitle(),
                        "drawable://" + R.drawable.ic_search//getContext().getResources().getDrawable(R.drawable.ic_search)
                });
                n++;
            }
            List<Album> albums = getAlbumData(query);
            for (Album obj : albums) {
                OfflineAlbum album = (OfflineAlbum) obj;
                cursor.addRow(new Object[] { n,
                        album.getAlbumTitle(),
                        "ALBUM",
                        album.getAlbumTitle(),
                        "drawable://" + R.drawable.ic_search//getContext().getResources().getDrawable(R.drawable.ic_search)
                });
                n++;
            }
            List<Album> artists = getArtistData(query);
            for (Album obj : artists) {
                OfflineAlbum album = (OfflineAlbum) obj;
                cursor.addRow(new Object[] { n,
                        album.getArtist(),
                        "ARTIST",
                        album.getArtist(),
                        "drawable://" + R.drawable.ic_search//getContext().getResources().getDrawable(R.drawable.ic_search)
                });
                n++;
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to lookup " + query, e);
        }
        return cursor;
    }
    public ArrayList<Song> getSongsData(String query){
        String where = MediaStore.Audio.Media.ALBUM +" Like '%" + query +"%' OR "+MediaStore.Audio.Media.TITLE +" Like '%" + query +"%' OR "+MediaStore.Audio.Media.COMPOSER +" Like '%" + query +"%') GROUP BY (" +
                MediaStore.Audio.Media.TITLE;/*+","+MediaStore.Audio.Media.TITLE+","+MediaStore.Audio.Media.COMPOSER*/;
        where = MediaStore.Audio.Media.TITLE +" Like '%" + query+"%') GROUP BY (" + MediaStore.Audio.Media.TITLE;
        ArrayList<Song> songsSearchData = searchDataProvider.searchSongs(context,query, MediaStore.Audio.Media.TITLE,true,where);
        return  songsSearchData;
    }

    private ArrayList<Album> getAlbumData(String query){
        String where = AlbumTable.ALBUM +" Like '%" + query+"%') GROUP BY (" + AlbumTable.ALBUM;
        ArrayList<Album> albumsSearchData = searchDataProvider.searchAlbums(context,query, AlbumTable.ALBUM,true,where);
        return albumsSearchData;
    }

    private ArrayList<Album> getArtistData(String query){
        String where = AlbumTable.ARTIST +" Like '%" + query+"%') GROUP BY (" + AlbumTable.ARTIST;
        ArrayList<Album> artistSearchData = searchDataProvider.searchAlbums(context,query, AlbumTable.ALBUM,true,where);
        return artistSearchData;
    }
}
