package com.ccc.raj.beats;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.os.IBinder;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.ccc.raj.beats.model.MediaTables;
import com.ccc.raj.beats.model.OfflineDataProvider;
import com.ccc.raj.beats.model.PlayListTable;
import com.ccc.raj.beats.model.Song;
import com.ccc.raj.beats.model.SongTable;

import java.util.ArrayList;

public class AlbumSongsListActivity extends MediaControlBaseActivity implements PopupMenu.OnMenuItemClickListener{
    private RecyclerView albumSongListView;
    private ArrayList<Song> songList;
    private ImageView albumImage;
    private Toolbar toolbar;
    private int selectedSongPosition = -1;

    public static final String COLUMN = "COLUMN";
    public static final String COLUMN_VALUE = "COLUMN_VALUE";
    public static final String TITLE = "TITLE";
    public static final String ALBUM_ID = "ALBUM_ID";
    public static final String ALBUM_TYPE = "ALBUM_TYPE";
    public static final int OFFLINE_ALBUM = 0;
    public static final int PLAYLIST_ALBUM = 1;
    public static final int ARTIST_ALBUM = 2;


    private  MusicPlayService musicPlayService;

    private int albumId;
    private String column;
    private String columnValue;
    private String title;
    FrameLayout mediaViewContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onMusicServiceBind(MusicPlayService musicPlayService) {
        this.musicPlayService = musicPlayService;
    }

    @Override
    protected void setControllerAnchorView(MusicController musicController) {
        setContentView(R.layout.activity_album_songs_list);
        albumImage = findViewById(R.id.album_image);
        setActionBar();

        setSongsListData();

        mediaViewContainer = findViewById(R.id.media_container);
        musicController.setAnchorView(mediaViewContainer);
    }

    public void setSongsListData(){
        albumSongListView = findViewById(R.id.albumSongListView);
        Intent intent = getIntent();
        column = intent.getStringExtra(COLUMN);
        columnValue = intent.getStringExtra(COLUMN_VALUE);
        title = intent.getStringExtra(TITLE);
        albumId = intent.getIntExtra(ALBUM_ID,0);

        int album_type = intent.getIntExtra(ALBUM_TYPE,0);
        switch (album_type) {
            case OFFLINE_ALBUM:
                songList = SongTable.getSongsFromColumn(this, column, columnValue);
                break;
            case PLAYLIST_ALBUM:
                songList = PlayListTable.getSongsFromPlayLists(this,albumId);
                break;
            case ARTIST_ALBUM:
                songList = SongTable.getSongsFromColumn(this, column, columnValue);
                break;
            default:
                songList = SongTable.getSongsFromColumn(this, column, columnValue);
        }


        SongListAdapter songListAdapter = new SongListAdapter(this,songList);
        songListAdapter.setOnItemClickListener(new SongListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
              onSongPicked(position);
            }

            @Override
            public void onMoreButtonClick(View view, int position) {
                selectedSongPosition = position;
                showSongOptionsMenu(view,position);
            }
        });
        albumSongListView.setAdapter(songListAdapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,1);
        albumSongListView.setLayoutManager(gridLayoutManager);
        albumImage.setImageBitmap(OfflineDataProvider.getBitmapByAlbumId(this,albumId));
        setTitle(title);
    }

    public void showSongOptionsMenu(View view,int position){
        PopupMenu popupMenu = new PopupMenu(this,view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.song_menu,popupMenu.getMenu());
        popupMenu.getMenu().add(Menu.NONE,R.id.delete,5,R.string.delete);
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(this);
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.shuffle:
                Toast.makeText(this,"shuffle",Toast.LENGTH_SHORT).show();
                break;
            case R.id.add_to_playlist:
                onAddToPlayListClick(selectedSongPosition);
                break;
        }
        return false;
    }

    public void onSongPicked(int position){
        ArrayList<Song> songsAlbum = SongTable.getSongsFromColumn(this,column,columnValue);
        if(songsAlbum.size()>0) {
            musicPlayService.setOfflineSongsList(songsAlbum);
            musicPlayService.setOfflineSongPosition(position);
            musicPlayService.playOfflineSong();
        }
    }

    public void onAddToPlayListClick(int position){
        ArrayList<Song> songs = new ArrayList<>();
        songs.add(songList.get(position));
        new PlayListSelectionPopup(this,songs).showPopup();
    }

    public void onAllPlayClicked(View view){
        ArrayList<Song> songsAlbum = SongTable.getSongsFromColumn(this,column,columnValue);
        if(songsAlbum.size()>0) {
            musicPlayService.setOfflineSongsList(songsAlbum);
            musicPlayService.setOfflineSongPosition(0);
            musicPlayService.playOfflineSong();
        }
        ImageButton imageButton = (ImageButton) view;
        imageButton.setImageResource(R.color.transperent);
        imageButton.setBackgroundResource(R.drawable.anim_music);
        AnimationDrawable animationDrawable = (AnimationDrawable) imageButton.getBackground();
        animationDrawable.start();
    }

    public void setActionBar(){
        toolbar = findViewById(R.id.toolbar_album_songs);
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
