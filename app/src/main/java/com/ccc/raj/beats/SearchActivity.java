package com.ccc.raj.beats;

import android.app.ActivityOptions;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.ccc.raj.beats.model.Album;
import com.ccc.raj.beats.model.AlbumTable;
import com.ccc.raj.beats.model.ArtistAlbum;
import com.ccc.raj.beats.model.ArtistTable;
import com.ccc.raj.beats.model.GenresAlbum;
import com.ccc.raj.beats.model.GenresTable;
import com.ccc.raj.beats.model.OfflineAlbum;
import com.ccc.raj.beats.model.OfflineSong;
import com.ccc.raj.beats.model.Song;
import com.ccc.raj.beats.model.SongTable;
import com.ccc.raj.beats.searchresult.CursorSuggestionAdapter;
import com.ccc.raj.beats.searchresult.SearchDataProvider;
import com.ccc.raj.beats.searchresult.SearchListAdapter;
import com.ccc.raj.beats.searchresult.SearchRecord;
import com.ccc.raj.beats.searchresult.SearchSuggestionProvider;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;

public class SearchActivity extends MediaControlBaseActivity implements PopupMenu.OnMenuItemClickListener,SearchView.OnSuggestionListener,SearchView.OnQueryTextListener {
    Toolbar mToolbar;
    RecyclerView searchListView;
    private String searchQuery;
    private static final int SECTION_LIMIT = 4;
    private int selectedAlbumPosition = -1;
    private ArrayList<SearchRecord> searchRecords = new ArrayList<>();


    SearchView searchView;
    CursorSuggestionAdapter mCursorSuggestionAdapter;
    SearchSuggestionProvider mSearchSuggestionProvider;
    ImageButton searchBack;

    SlidingUpPanelLayout mSlidingUpPanelLayout;

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
        //mToolbar = findViewById(R.id.toolbar_main);
        //setSupportActionBar(mToolbar);
        searchListView = findViewById(R.id.searchListView);
        musicController.setAnchorView((FrameLayout) findViewById(R.id.media_container));
        LinearLayout searchBar = findViewById(R.id.search_container_main);
        searchBar.setVisibility(View.VISIBLE);
        setSearchBar();
        handleIntent(getIntent());
        setSlidingLayout();
    }
    public void setSlidingLayout() {
        mSlidingUpPanelLayout = findViewById(R.id.sliding_layout);
        mSlidingUpPanelLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                if (SlidingUpPanelLayout.PanelState.COLLAPSED.name().equalsIgnoreCase(newState.name())) {
                    Log.i("SlideUp", newState.name());
                    SearchActivity.super.onSlideDown();
                } else if (SlidingUpPanelLayout.PanelState.EXPANDED.name().equalsIgnoreCase(newState.name())) {
                    Log.i("SlideUp", newState.name());
                    SearchActivity.super.onSlideUp();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.options_menu, menu);
        //SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        //SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        //searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    public void handleIntent(Intent intent) {
        SearchDataProvider searchDataProvider = new SearchDataProvider();
        String query = "";
        searchRecords.removeAll(searchRecords);
        if (Intent.ACTION_SEARCH.equalsIgnoreCase(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
            Toast.makeText(this, query, Toast.LENGTH_SHORT).show();
            searchRecords.addAll(getAlbumsSearchData(searchDataProvider, query));
            searchRecords.addAll(getArtistsSearchData(searchDataProvider, query));
            searchRecords.addAll(getSongsSearchData(searchDataProvider, query));
        } else if ("SONG".equalsIgnoreCase(intent.getAction())) {
            query = intent.getDataString();
            searchRecords.addAll(getSongsSearchData(searchDataProvider, query));
            Toast.makeText(this, query, Toast.LENGTH_SHORT).show();
        } else if ("ALBUM".equalsIgnoreCase(intent.getAction())) {
            query = intent.getDataString();
            searchRecords.addAll(getAlbumsSearchData(searchDataProvider, query));
            Toast.makeText(this, query, Toast.LENGTH_SHORT).show();
        } else if ("ARTIST".equalsIgnoreCase(intent.getAction())) {
            query = intent.getDataString();
            searchRecords.addAll(getArtistsSearchData(searchDataProvider, query));
            Toast.makeText(this, query, Toast.LENGTH_SHORT).show();
        }
        searchView.setQuery(query,false);
        populateSearchData(searchRecords);
        searchQuery = query;
    }

    private ArrayList<SearchRecord> getSongsSearchData(SearchDataProvider searchDataProvider, String query) {
        ArrayList<Song> songsSearchData = searchDataProvider.searchSongs(this, query, MediaStore.Audio.Media.TITLE, false, null);
        ArrayList<SearchRecord> searchRecords = new ArrayList<>();
        SearchRecord record;
        if (songsSearchData.size() > 0) {
            record = new SearchRecord(SearchRecord.SECTION_VIEW);
            if (songsSearchData.size() > SECTION_LIMIT) {
                record.setSectionData(new SearchRecord.Section("Songs", (songsSearchData.size() - SECTION_LIMIT) + " more", SearchRecord.SONG_VIEW));
            } else {
                record.setSectionData(new SearchRecord.Section("Songs", "", SearchRecord.SONG_VIEW));
            }
            searchRecords.add(record);
            int count = 0;
            for (Song song : songsSearchData) {
                record = new SearchRecord(SearchRecord.SONG_VIEW);
                record.setSong(song);
                searchRecords.add(record);
                count++;
                if (count >= SECTION_LIMIT) {
                    break;
                }
            }
        }
        return searchRecords;
    }

    private ArrayList<SearchRecord> getAlbumsSearchData(SearchDataProvider searchDataProvider, String query) {
        ArrayList<Album> albumSearchData = searchDataProvider.searchAlbums(this, query, AlbumTable.ALBUM, false, null);
        ArrayList<SearchRecord> searchRecords = new ArrayList<>();
        SearchRecord record;
        if (albumSearchData.size() > 0) {
            record = new SearchRecord(SearchRecord.SECTION_VIEW);
            if (albumSearchData.size() > SECTION_LIMIT) {
                record.setSectionData(new SearchRecord.Section("Albums", (albumSearchData.size() - SECTION_LIMIT) + " more", SearchRecord.ALBUM_VIEW));
            } else {
                record.setSectionData(new SearchRecord.Section("Albums", "", SearchRecord.ALBUM_VIEW));
            }
            searchRecords.add(record);
            int count = 0;
            for (Album album : albumSearchData) {
                record = new SearchRecord(SearchRecord.ALBUM_VIEW);
                record.setOfflineAlbum(album);
                searchRecords.add(record);
                count++;
                if (count >= SECTION_LIMIT) {
                    break;
                }
            }
        }
        return searchRecords;
    }

    private ArrayList<SearchRecord> getArtistsSearchData(SearchDataProvider searchDataProvider, String query) {
        ArrayList<Album> artistSearchData = searchDataProvider.searchAlbums(this, query, AlbumTable.ARTIST, false, null);
        ArrayList<SearchRecord> searchRecords = new ArrayList<>();
        SearchRecord record;
        if (artistSearchData.size() > 0) {
            record = new SearchRecord(SearchRecord.SECTION_VIEW);
            if (artistSearchData.size() > SECTION_LIMIT) {
                record.setSectionData(new SearchRecord.Section("Artists", (artistSearchData.size() - SECTION_LIMIT) + " more", SearchRecord.ARTIST_VIEW));
            } else {
                record.setSectionData(new SearchRecord.Section("Artists", "", SearchRecord.ARTIST_VIEW));
            }
            searchRecords.add(record);
            int count = 0;
            for (Album album : artistSearchData) {
                record = new SearchRecord(SearchRecord.ARTIST_VIEW);
                record.setOfflineAlbum(album);
                searchRecords.add(record);
                count++;
                if (count >= SECTION_LIMIT) {
                    break;
                }
            }
        }
        return searchRecords;
    }

    public void populateSearchData(final ArrayList<SearchRecord> searchRecords) {
        SearchListAdapter songListAdapter = new SearchListAdapter(this, searchRecords);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int type = searchRecords.get(position).getViewType();
                switch (type) {
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
                Intent intent = new Intent(getApplicationContext(), AlbumSongsListActivity.class);
                SearchRecord searchRecord = searchRecords.get(position);
                Album album;
                if (searchRecord.getViewType() == SearchRecord.SONG_VIEW) {
                    ArrayList<Song> songs = new ArrayList<>();
                    songs.add(searchRecord.getSong());
                    MusicPlayService musicPlayService = MusicPlayServiceHolder.getMusicPlayService();
                    if(musicPlayService != null) {
                        musicPlayService.setOfflineSongsList(songs);
                        musicPlayService.setOfflineSongPosition(0);
                        musicPlayService.playOfflineSong();
                    }
                } else if (searchRecord.getViewType() == SearchRecord.ALBUM_VIEW) {
                    album = searchRecord.getOfflineAlbum();
                    intent.putExtra(AlbumSongsListActivity.COLUMN, AlbumTable.ALBUM);
                    intent.putExtra(AlbumSongsListActivity.COLUMN_VALUE, album.getAlbumTitle());
                    intent.putExtra(AlbumSongsListActivity.ALBUM_ID, album.getAlbumId());
                    intent.putExtra(AlbumSongsListActivity.TITLE, album.getAlbumTitle());
                    startActivity(intent);
                } else {
                    album = searchRecord.getOfflineAlbum();
                    intent.putExtra(AlbumSongsListActivity.COLUMN, AlbumTable.ARTIST);
                    intent.putExtra(AlbumSongsListActivity.COLUMN_VALUE, album.getAlbumTitle());
                    intent.putExtra(AlbumSongsListActivity.ALBUM_ID, album.getAlbumId());
                    intent.putExtra(AlbumSongsListActivity.TITLE, album.getAlbumTitle());
                    startActivity(intent);
                }
            }

            @Override
            public void onMoreButtonClick(View view, int position) {
                SearchRecord searchRecord = searchRecords.get(position);
                Intent intent = new Intent(getApplicationContext(), MoreRecordsActivity.class);
                switch (searchRecord.getSectionData().getSectionType()) {
                    case SearchRecord.SONG_VIEW:
                        intent.putExtra(MoreRecordsActivity.VIEW_TYPE, MoreRecordsActivity.SONG);
                        break;
                    case SearchRecord.ALBUM_VIEW:
                        intent.putExtra(MoreRecordsActivity.VIEW_TYPE, MoreRecordsActivity.ALBUM);
                        break;
                    case SearchRecord.ARTIST_VIEW:
                        intent.putExtra(MoreRecordsActivity.VIEW_TYPE, MoreRecordsActivity.ARTIST);
                        break;
                }
                intent.putExtra(MoreRecordsActivity.SEARCH_QUERY, searchQuery);
                startActivity(intent);
            }

            @Override
            public void onMenuOptionsClick(View view, int position) {
                SearchRecord searchRecord = searchRecords.get(position);
                if (searchRecord.getViewType() == SearchRecord.SONG_VIEW) {
                    showSongOptionsMenu(view, position);
                } else {
                    showAlbumOptionsMenu(view, position);
                }
            }
        });
        searchListView.setAdapter(songListAdapter);
        searchListView.setLayoutManager(gridLayoutManager);
    }

    public void showSongOptionsMenu(View view, int position) {
        selectedAlbumPosition = position;
        PopupMenu popupMenu = new PopupMenu(this, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.popup_menu, popupMenu.getMenu());
        popupMenu.getMenu().removeItem(R.id.not_interested);
        popupMenu.getMenu().removeItem(R.id.remove_from_playlist);
        popupMenu.getMenu().removeItem(R.id.edit_playlist);
        popupMenu.getMenu().removeItem(R.id.shuffle);
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(this);
    }

    public void showAlbumOptionsMenu(View view, int position) {
        selectedAlbumPosition = position;
        PopupMenu popupMenu = new PopupMenu(this, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.popup_menu, popupMenu.getMenu());
        popupMenu.getMenu().removeItem(R.id.not_interested);
        popupMenu.getMenu().removeItem(R.id.edit_playlist);
        popupMenu.getMenu().removeItem(R.id.delete);
        popupMenu.getMenu().removeItem(R.id.go_to_album);
        popupMenu.getMenu().removeItem(R.id.remove_from_playlist);
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(this);
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.shuffle:
                Toast.makeText(this, "shuffle", Toast.LENGTH_SHORT).show();
                break;
            case R.id.add_to_playlist:
                onAddToPlayListClick(selectedAlbumPosition);
                break;
            case R.id.goto_artist:
                onGotoArtistClick();
            case R.id.play_next:
                onPlayNextClick();
                break;
            case R.id.add_to_queue:
                onAddToQueueClick();
                break;
            case R.id.go_to_album:
                onGotoAlbumClick();
                break;
        }
        return false;
    }

    public void onAddToPlayListClick(int position) {
        SearchRecord searchRecord = searchRecords.get(position);
        ArrayList<Song> songs = new ArrayList<>();
        switch (searchRecord.getViewType()) {
            case SearchRecord.SONG_VIEW:
                songs.add(searchRecord.getSong());
                new PlayListSelectionPopup(this, songs).showPopup();
                break;
            case SearchRecord.ALBUM_VIEW:
                Album album = searchRecord.getOfflineAlbum();
                songs = SongTable.getSongsFromAlbum(this, album.getAlbumTitle());
                new PlayListSelectionPopup(this, songs).showPopup();
                break;
        }
    }
    public void onGotoArtistClick(){
        SearchRecord searchRecord = searchRecords.get(selectedAlbumPosition);
        Intent intent = new Intent(this, MoreRecordsActivity.class);
        intent.putExtra(MoreRecordsActivity.VIEW_TYPE, MoreRecordsActivity.ARTIST_ALBUM);
        switch (searchRecord.getViewType()) {
            case SearchRecord.SONG_VIEW:
                OfflineSong song = (OfflineSong) searchRecord.getSong();
                intent.putExtra(MoreRecordsActivity.SEARCH_QUERY, String.valueOf(song.getArtistId()));
                break;
            case SearchRecord.ALBUM_VIEW:
                Album album = searchRecord.getOfflineAlbum();
                intent.putExtra(MoreRecordsActivity.SEARCH_QUERY, String.valueOf(ArtistTable.getArtistIdByArtist(this,album.getArtist())));
                break;
            case SearchRecord.ARTIST_VIEW:
                Album albumArtist = searchRecord.getOfflineAlbum();
                intent.putExtra(MoreRecordsActivity.SEARCH_QUERY, String.valueOf(ArtistTable.getArtistIdByArtist(this,albumArtist.getArtist())));
                break;
        }
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this);
        startActivity(intent, options.toBundle());
    }
    public void onPlayNextClick(){
        SearchRecord searchRecord = searchRecords.get(selectedAlbumPosition);
        ArrayList<Song> songs = new ArrayList<>();
        switch (searchRecord.getViewType()) {
            case SearchRecord.SONG_VIEW:
                OfflineSong song = (OfflineSong) searchRecord.getSong();
                songs.add(song);
                break;
            case SearchRecord.ALBUM_VIEW:
                Album album = searchRecord.getOfflineAlbum();
                songs = SongTable.getSongsFromAlbum(this,album.getAlbumTitle());
                break;
            case SearchRecord.ARTIST_VIEW:
                Album albumArtist = searchRecord.getOfflineAlbum();
                songs = SongTable.getSongsFromColumn(this,AlbumTable.ARTIST,albumArtist.getAlbumTitle()) ;
                break;
        }
        MusicPlayService musicPlayService = MusicPlayServiceHolder.getMusicPlayService();
        if(musicPlayService != null){
            musicPlayService.addToPlayNext(songs);
        }
    }
    public void onAddToQueueClick(){
        SearchRecord searchRecord = searchRecords.get(selectedAlbumPosition);
        ArrayList<Song> songs = new ArrayList<>();
        switch (searchRecord.getViewType()) {
            case SearchRecord.SONG_VIEW:
                OfflineSong song = (OfflineSong) searchRecord.getSong();
                songs.add(song);
                break;
            case SearchRecord.ALBUM_VIEW:
                Album album = searchRecord.getOfflineAlbum();
                songs = SongTable.getSongsFromAlbum(this,album.getAlbumTitle());
                break;
            case SearchRecord.ARTIST_VIEW:
                Album albumArtist = searchRecord.getOfflineAlbum();
                songs = SongTable.getSongsFromColumn(this,AlbumTable.ARTIST,albumArtist.getAlbumTitle()) ;
                break;
        }
        MusicPlayService musicPlayService = MusicPlayServiceHolder.getMusicPlayService();
        if(musicPlayService != null){
            musicPlayService.addToQueue(songs);
        }
    }
    public void onGotoAlbumClick(){
        SearchRecord searchRecord = searchRecords.get(selectedAlbumPosition);
        switch (searchRecord.getViewType()) {
            case SearchRecord.SONG_VIEW:
                OfflineSong song = (OfflineSong) searchRecord.getSong();
                Intent intent = new Intent(this, AlbumSongsListActivity.class);
                intent.putExtra(AlbumSongsListActivity.COLUMN, AlbumTable.ALBUM);
                intent.putExtra(AlbumSongsListActivity.COLUMN_VALUE, song.getAlbum());
                intent.putExtra(AlbumSongsListActivity.ALBUM_ID, song.getAlbumId());
                intent.putExtra(AlbumSongsListActivity.TITLE, song.getAlbum());
                startActivity(intent);
                break;
        }
    }

    public void setSearchBar(){
        searchView = findViewById(R.id.search_bar_edit_text);
        searchView.setOnQueryTextListener(this);
        searchView.setOnSuggestionListener(this);
        ArrayList<String> list = new ArrayList<>();
        mCursorSuggestionAdapter = new CursorSuggestionAdapter(this,null,true);
        mCursorSuggestionAdapter.setOnSuggessionClickListener(new CursorSuggestionAdapter.OnSuggessionClickListener() {
            @Override
            public void onSuggessionSelect(String text,String action) {
                Intent intent = getIntent();//new Intent(SearchActivity.this, SearchActivity.class);
                intent.setAction(action);
                intent.setData(Uri.parse(text));
                //startActivity(intent);
                handleIntent(intent);
            }
        });
        searchView.setSuggestionsAdapter(mCursorSuggestionAdapter);
        mSearchSuggestionProvider = new SearchSuggestionProvider(this);
        formattSuggestionView();
        searchBack = findViewById(R.id.search_bar_back);
        searchBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
    @Override
    public boolean onQueryTextSubmit(String query) {
        Intent intent = getIntent();//new Intent(this,SearchActivity.class);
        intent.setAction(Intent.ACTION_SEARCH);
        intent.putExtra(SearchManager.QUERY,query);
        //startActivity(intent);
        handleIntent(intent);
        searchView.clearFocus();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Cursor cursor = mSearchSuggestionProvider.getSearchSuggestionCursor(newText); //getCursor(list);
        mCursorSuggestionAdapter.changeCursor(cursor);
        return true;
    }

    @Override
    public boolean onSuggestionSelect(int position) {
        Toast.makeText(this,"select",Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public boolean onSuggestionClick(int position) {
        Toast.makeText(this,"click",Toast.LENGTH_SHORT).show();
        return false;
    }

    private void formattSuggestionView(){
        int searchEditTextId = R.id.search_src_text;
        final AutoCompleteTextView searchEditText = (AutoCompleteTextView) searchView.findViewById(searchEditTextId);
        final View dropDownAnchor = searchView.findViewById(searchEditText.getDropDownAnchor());

        if (dropDownAnchor != null) {
            dropDownAnchor.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                           int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    int screenWidthPixel = SearchActivity.this.getResources().getDisplayMetrics().widthPixels;
                    final LinearLayout searchBar = findViewById(R.id.search_container_main);
                    searchEditText.setDropDownWidth(screenWidthPixel);
                }
            });
        }
    }
}
