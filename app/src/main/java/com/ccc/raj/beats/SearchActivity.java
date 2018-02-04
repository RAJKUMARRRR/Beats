package com.ccc.raj.beats;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.ccc.raj.beats.model.Album;
import com.ccc.raj.beats.model.AlbumTable;
import com.ccc.raj.beats.model.Song;
import com.ccc.raj.beats.searchresult.SearchDataProvider;
import com.ccc.raj.beats.searchresult.SearchListAdapter;
import com.ccc.raj.beats.searchresult.SearchRecord;

import java.util.ArrayList;

public class SearchActivity extends MediaControlBaseActivity {
    Toolbar mToolbar;
    RecyclerView searchListView;
    private String searchQuery;
    private static final int SECTION_LIMIT = 4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onMusicServiceBind(MusicPlayService musicPlayService) {

    }

    @Override
    protected void setControllerAnchorView(MusicController musicController) {
        setContentView(R.layout.activity_search);
        mToolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(mToolbar);
        searchListView = findViewById(R.id.searchListView);
        musicController.setAnchorView((FrameLayout) findViewById(R.id.media_container));
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
        SearchDataProvider searchDataProvider = new SearchDataProvider();
        ArrayList<SearchRecord> searchRecords = new ArrayList<>();
        String query = "";
        if(Intent.ACTION_SEARCH.equalsIgnoreCase(intent.getAction())){
            query = intent.getStringExtra(SearchManager.QUERY);
            Toast.makeText(this,query,Toast.LENGTH_SHORT).show();
            searchRecords.addAll(getAlbumsSearchData(searchDataProvider,query));
            searchRecords.addAll(getArtistsSearchData(searchDataProvider,query));
            searchRecords.addAll(getSongsSearchData(searchDataProvider,query));
        }else if("SONG".equalsIgnoreCase(intent.getAction())){
            query = intent.getDataString();
            searchRecords.addAll(getSongsSearchData(searchDataProvider,query));
            Toast.makeText(this,query,Toast.LENGTH_SHORT).show();
        }else if("ALBUM".equalsIgnoreCase(intent.getAction())){
            query = intent.getDataString();
            searchRecords.addAll(getAlbumsSearchData(searchDataProvider,query));
            Toast.makeText(this,query,Toast.LENGTH_SHORT).show();
        }else if("ARTIST".equalsIgnoreCase(intent.getAction())){
            query = intent.getDataString();
            searchRecords.addAll(getArtistsSearchData(searchDataProvider,query));
            Toast.makeText(this,query,Toast.LENGTH_SHORT).show();
        }
        populateSearchData(searchRecords);
        searchQuery = query;
    }

    private ArrayList<SearchRecord> getSongsSearchData(SearchDataProvider searchDataProvider, String query){
        ArrayList<Song> songsSearchData = searchDataProvider.searchSongs(this,query,MediaStore.Audio.Media.TITLE,false,null);
        ArrayList<SearchRecord> searchRecords = new ArrayList<>();
        SearchRecord record;
        if(songsSearchData.size()>0) {
            record = new SearchRecord(SearchRecord.SECTION_VIEW);
            if(songsSearchData.size()>SECTION_LIMIT) {
                record.setSectionData(new SearchRecord.Section("Songs", (songsSearchData.size()-SECTION_LIMIT) + " more", SearchRecord.SONG_VIEW));
            }else{
                record.setSectionData(new SearchRecord.Section("Songs", "", SearchRecord.SONG_VIEW));
            }
            searchRecords.add(record);
            int count = 0;
            for (Song song : songsSearchData) {
                record = new SearchRecord(SearchRecord.SONG_VIEW);
                record.setSong(song);
                searchRecords.add(record);
                count++;
                if(count>=SECTION_LIMIT){
                    break;
                }
            }
        }
        return searchRecords;
    }
    private ArrayList<SearchRecord> getAlbumsSearchData(SearchDataProvider searchDataProvider, String query){
        ArrayList<Album> albumSearchData = searchDataProvider.searchAlbums(this,query, AlbumTable.ALBUM,false,null);
        ArrayList<SearchRecord> searchRecords = new ArrayList<>();
        SearchRecord record;
        if(albumSearchData.size()>0) {
            record = new SearchRecord(SearchRecord.SECTION_VIEW);
            if(albumSearchData.size()>SECTION_LIMIT) {
                record.setSectionData(new SearchRecord.Section("Albums", (albumSearchData.size()-SECTION_LIMIT) + " more", SearchRecord.ALBUM_VIEW));
            }else{
                record.setSectionData(new SearchRecord.Section("Albums", "", SearchRecord.ALBUM_VIEW));
            }
            searchRecords.add(record);
            int count = 0;
            for (Album album : albumSearchData) {
                record = new SearchRecord(SearchRecord.ALBUM_VIEW);
                record.setOfflineAlbum(album);
                searchRecords.add(record);
                count++;
                if(count>=SECTION_LIMIT){
                    break;
                }
            }
        }
        return searchRecords;
    }
    private ArrayList<SearchRecord> getArtistsSearchData(SearchDataProvider searchDataProvider, String query){
        ArrayList<Album> artistSearchData = searchDataProvider.searchAlbums(this,query, AlbumTable.ARTIST,false,null);
        ArrayList<SearchRecord> searchRecords = new ArrayList<>();
        SearchRecord record;
        if(artistSearchData.size()>0) {
            record = new SearchRecord(SearchRecord.SECTION_VIEW);
            if(artistSearchData.size()>SECTION_LIMIT) {
                record.setSectionData(new SearchRecord.Section("Artists", (artistSearchData.size()-SECTION_LIMIT) + " more", SearchRecord.ARTIST_VIEW));
            }else{
                record.setSectionData(new SearchRecord.Section("Artists", "", SearchRecord.ARTIST_VIEW));
            }
            searchRecords.add(record);
            int count=0;
            for (Album album : artistSearchData) {
                record = new SearchRecord(SearchRecord.ARTIST_VIEW);
                record.setOfflineAlbum(album);
                searchRecords.add(record);
                count++;
                if(count>=SECTION_LIMIT){
                    break;
                }
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
        songListAdapter.setOnItemClickListener(new SearchListAdapter.OnItemClickListener() {
            @Override
            public void onListItemClick(View view, int position) {
            }

            @Override
            public void onMoreButtonClick(View view, int position) {
                SearchRecord searchRecord = searchRecords.get(position);
                Intent intent = new Intent(getApplicationContext(),MoreRecordsActivity.class);
                switch (searchRecord.getSectionData().getSectionType()){
                    case SearchRecord.SONG_VIEW:
                        intent.putExtra(MoreRecordsActivity.VIEW_TYPE,MoreRecordsActivity.SONG);
                        break;
                    case SearchRecord.ALBUM_VIEW:
                        intent.putExtra(MoreRecordsActivity.VIEW_TYPE,MoreRecordsActivity.ALBUM);
                        break;
                    case SearchRecord.ARTIST_VIEW:
                        intent.putExtra(MoreRecordsActivity.VIEW_TYPE,MoreRecordsActivity.ARTIST);
                        break;
                }
                intent.putExtra(MoreRecordsActivity.SEARCH_QUERY,searchQuery);
                startActivity(intent);
            }
        });
        searchListView.setAdapter(songListAdapter);
        searchListView.setLayoutManager(gridLayoutManager);
    }

    public void testOnClick(View view){
        Intent intent = new Intent(getApplicationContext(),MoreRecordsActivity.class);
        startActivity(intent);
    }

}
