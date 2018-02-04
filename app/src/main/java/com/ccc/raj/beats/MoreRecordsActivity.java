package com.ccc.raj.beats;

import android.content.Intent;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.ccc.raj.beats.model.Album;
import com.ccc.raj.beats.model.AlbumTable;
import com.ccc.raj.beats.model.ArtistAlbum;
import com.ccc.raj.beats.model.ArtistTable;
import com.ccc.raj.beats.model.GenresAlbum;
import com.ccc.raj.beats.model.GenresTable;
import com.ccc.raj.beats.model.OfflineAlbum;
import com.ccc.raj.beats.model.Song;
import com.ccc.raj.beats.searchresult.SearchDataProvider;

import java.util.ArrayList;

public class MoreRecordsActivity extends MediaControlBaseActivity {
    public static final String VIEW_TYPE = "view type";
    public static final String SEARCH_QUERY = "search query";
    public static final int ALBUM = 0;
    public static final int ARTIST = 1;
    public static final int SONG = 2;
    public static final int ARTIST_ALBUM = 3;
    public static final int GENRES_ALBUM = 4;

    FrameLayout mediaContainer;
    RecyclerView listView;
    AlbumListAdapter albumListAdapter;
    SongListAdapter songListAdapter;
    Toolbar toolbar;
    private ArrayList<Album> albumList;
    private ArrayList<Song> songList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onMusicServiceBind(MusicPlayService musicPlayService) {

    }

    @Override
    protected void setControllerAnchorView(MusicController musicController) {
        setContentView(R.layout.activity_more_records);
        mediaContainer = findViewById(R.id.media_container);
        musicController.setAnchorView(mediaContainer);
        listView = findViewById(R.id.albumSongListView);
        setActionBar();
        setListData();
    }

    private void setListData(){
       String searchQuery = getIntent().getStringExtra(SEARCH_QUERY);
       switch (getViewType()){
           case ALBUM:
               if(searchQuery.isEmpty()) {
                   albumList = AlbumTable.getAllAlbums(this);
               }else{
                   albumList =  new SearchDataProvider().searchAlbums(this,searchQuery, AlbumTable.ALBUM,false,null);
               }
               handleAlbumData();
               setTitle("Albums");
               break;
           case ARTIST:
               albumList =  new SearchDataProvider().searchAlbums(this,searchQuery, AlbumTable.ARTIST,false,null);
               handleAlbumData();
               setTitle("Artists");
               break;
           case SONG:
               songList = new SearchDataProvider().searchSongs(this,searchQuery, MediaStore.Audio.Media.TITLE,false,null);
               handleSongData();
               setTitle("Songs");
               break;

           case ARTIST_ALBUM:
               int artistId = Integer.valueOf(searchQuery);
               ArtistAlbum artistAlbum = (ArtistAlbum) ArtistTable.getArtistAlbumForArtistId(this,artistId);
               albumList = ArtistTable.getAlbumsForArtistId(this,artistId);
               albumList.add(0,artistAlbum);
               handleAlbumData();
               setTitle(artistAlbum.getAlbumTitle());
               break;
           case GENRES_ALBUM:
               int genresId = Integer.valueOf(searchQuery);
               GenresAlbum genresAlbum = (GenresAlbum) GenresTable.getGenresAlbumForGenresId(this,genresId);
               albumList = GenresTable.getAlbumsByGenresId(this,genresId);
               albumList.add(0,genresAlbum);
               handleAlbumData();
               setTitle(genresAlbum.getAlbumTitle());
               break;
       }
    }

    private void handleAlbumData(){
         albumListAdapter = new AlbumListAdapter(albumList,this);
         albumListAdapter.setOnItemClickListener(new AlbumListAdapter.OnItemClickListener() {
             @Override
             public void onItemClick(int position) {
                 Intent intent = new Intent(getApplicationContext(),AlbumSongsListActivity.class);
                 Album album = albumList.get(position);
                 if(album instanceof OfflineAlbum){
                     intent.putExtra(AlbumSongsListActivity.COLUMN, AlbumTable.ALBUM);
                     intent.putExtra(AlbumSongsListActivity.COLUMN_VALUE, album.getAlbumTitle());
                     intent.putExtra(AlbumSongsListActivity.ALBUM_ID, album.getAlbumId());
                     intent.putExtra(AlbumSongsListActivity.TITLE, album.getAlbumTitle());
                     startActivity(intent);
                 }else if(album instanceof ArtistAlbum){
                     intent.putExtra(AlbumSongsListActivity.COLUMN, AlbumTable.ARTIST);
                     intent.putExtra(AlbumSongsListActivity.COLUMN_VALUE,album.getAlbumTitle());
                     intent.putExtra(AlbumSongsListActivity.ALBUM_ID,album.getAlbumId());
                     intent.putExtra(AlbumSongsListActivity.TITLE,album.getAlbumTitle());
                     startActivity(intent);
                 }else if(album instanceof GenresAlbum){
                     intent.putExtra(AlbumSongsListActivity.COLUMN, GenresTable.NAME);
                     intent.putExtra(AlbumSongsListActivity.COLUMN_VALUE, album.getAlbumTitle());
                     intent.putExtra(AlbumSongsListActivity.ALBUM_ID, album.getAlbumId());
                     intent.putExtra(AlbumSongsListActivity.TITLE, album.getAlbumTitle());
                     intent.putExtra(AlbumSongsListActivity.ALBUM_TYPE,AlbumSongsListActivity.GENRES_ALBUM);
                     startActivity(intent);
                 }
             }

             @Override
             public void onPlayButtonClick(int position) {

             }

             @Override
             public void onOptionsButtonClick(View view, int position) {

             }
         });
         listView.setAdapter(albumListAdapter);
         GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2);
         listView.setLayoutManager(gridLayoutManager);
    }
    private void handleSongData(){
        songListAdapter = new SongListAdapter(this,songList);
        listView.setAdapter(songListAdapter);
        songListAdapter.setOnItemClickListener(new SongListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

            }

            @Override
            public void onMoreButtonClick(View view, int position) {

            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayout.VERTICAL,false);
        listView.setLayoutManager(linearLayoutManager);
    }

    private int getViewType(){
       return getIntent().getIntExtra(VIEW_TYPE,0);
    }
    public void setActionBar(){
        toolbar = findViewById(R.id.toolbar_more_records);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                break;
        }
        return true;
    }
}
