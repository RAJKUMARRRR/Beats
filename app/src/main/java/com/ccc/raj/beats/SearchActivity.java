package com.ccc.raj.beats;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Toast;

import com.ccc.raj.beats.R;
import com.ccc.raj.beats.model.Album;
import com.ccc.raj.beats.model.Song;
import com.ccc.raj.beats.searchresult.SeachDataProvider;
import com.ccc.raj.beats.searchresult.SearchListAdapter;
import com.ccc.raj.beats.searchresult.SearchRecord;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {
    Toolbar mToolbar;
    RecyclerView searchListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mToolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(mToolbar);
        searchListView = findViewById(R.id.searchListView);
        handleIntent(getIntent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.options_menu,menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    public void handleIntent(Intent intent){
        SeachDataProvider seachDataProvider = new SeachDataProvider();
        ArrayList<SearchRecord> searchRecords = new ArrayList<>();
        if(Intent.ACTION_SEARCH.equalsIgnoreCase(intent.getAction())){
            String query = intent.getStringExtra(SearchManager.QUERY);
            Toast.makeText(this,query,Toast.LENGTH_SHORT).show();
            searchRecords.addAll(getSongsSearchData(seachDataProvider,query));
            searchRecords.addAll(getAlbumsSearchData(seachDataProvider,query));
            searchRecords.addAll(getArtistsSearchData(seachDataProvider,query));
        }else if("SONG".equalsIgnoreCase(intent.getAction())){
            String query = intent.getDataString();
            searchRecords.addAll(getSongsSearchData(seachDataProvider,query));
            Toast.makeText(this,query,Toast.LENGTH_SHORT).show();
        }else if("ALBUM".equalsIgnoreCase(intent.getAction())){
            String query = intent.getDataString();
            searchRecords.addAll(getAlbumsSearchData(seachDataProvider,query));
            Toast.makeText(this,query,Toast.LENGTH_SHORT).show();
        }else if("ARTIST".equalsIgnoreCase(intent.getAction())){
            String query = intent.getDataString();
            searchRecords.addAll(getArtistsSearchData(seachDataProvider,query));
            Toast.makeText(this,query,Toast.LENGTH_SHORT).show();
        }
        populateSearchData(searchRecords);
    }

    private ArrayList<SearchRecord> getSongsSearchData(SeachDataProvider seachDataProvider,String query){
        ArrayList<Song> songsSearchData = seachDataProvider.searchSongs(this,query,MediaStore.Audio.Media.TITLE,false,null);
        ArrayList<SearchRecord> searchRecords = new ArrayList<>();
        SearchRecord record;
        if(songsSearchData.size()>0) {
            record = new SearchRecord(SearchRecord.SECTION_VIEW);
            record.setSectionData(new SearchRecord.Section("Songs",songsSearchData.size()+" more"));
            searchRecords.add(record);
            for (Song song : songsSearchData) {
                record = new SearchRecord(SearchRecord.SONG_VIEW);
                record.setSong(song);
                searchRecords.add(record);
            }
        }
        return searchRecords;
    }
    private ArrayList<SearchRecord> getAlbumsSearchData(SeachDataProvider seachDataProvider,String query){
        ArrayList<Album> albumSearchData = seachDataProvider.searchAlbums(this,query, MediaStore.Audio.Media.ALBUM,false,null);
        ArrayList<SearchRecord> searchRecords = new ArrayList<>();
        SearchRecord record;
        if(albumSearchData.size()>0) {
            record = new SearchRecord(SearchRecord.SECTION_VIEW);
            record.setSectionData(new SearchRecord.Section("Albums",albumSearchData.size()+" more"));
            searchRecords.add(record);
            for (Album album : albumSearchData) {
                record = new SearchRecord(SearchRecord.ALBUM_VIEW);
                record.setOfflineAlbum(album);
                searchRecords.add(record);
            }
        }
        return searchRecords;
    }
    private ArrayList<SearchRecord> getArtistsSearchData(SeachDataProvider seachDataProvider,String query){
        ArrayList<Album> artistSearchData = seachDataProvider.searchAlbums(this,query, MediaStore.Audio.Media.COMPOSER,false,null);
        ArrayList<SearchRecord> searchRecords = new ArrayList<>();
        SearchRecord record;
        if(artistSearchData.size()>0) {
            record = new SearchRecord(SearchRecord.SECTION_VIEW);
            record.setSectionData(new SearchRecord.Section("Artists",artistSearchData.size()+" more"));
            searchRecords.add(record);
            for (Album album : artistSearchData) {
                record = new SearchRecord(SearchRecord.ARTIST_VIEW);
                record.setOfflineAlbum(album);
                searchRecords.add(record);
            }
        }
        return  searchRecords;
    }

    public  void populateSearchData(final ArrayList<SearchRecord> searchRecords){
        SearchListAdapter songListAdapter = new SearchListAdapter(this,searchRecords);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int type = searchRecords.get(position).getViewType();
                switch (type){
                    case SearchRecord.SECTION_VIEW:
                        return 2;
                    case SearchRecord.SONG_VIEW:
                        return 2;
                    case SearchRecord.ALBUM_VIEW:
                        return 1;
                    case SearchRecord.ARTIST_VIEW:
                        return 1;
                }
                return 1;
            }
        });
        searchListView.setAdapter(songListAdapter);
        searchListView.setLayoutManager(gridLayoutManager);
    }
}
