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
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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

import com.ccc.raj.beats.model.OfflineDataProvider;
import com.ccc.raj.beats.model.Song;

import java.util.ArrayList;

public class AlbumSongsListActivity extends MediaControlBaseActivity implements PopupMenu.OnMenuItemClickListener{
    private RecyclerView albumSongListView;
    private ArrayList<Song> songList;
    private ImageView albumImage;
    private Toolbar toolbar;

    public static final String COLUMN = "COLUMN";
    public static final String COLUMN_VALUE = "COLUMN_VALUE";
    public static final String TITLE = "TITLE";
    public static final String ALBUM_PATH = "ALBUM_PATH";


    private  MusicPlayService musicPlayService;

    private String albumPath;
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
        albumPath = intent.getStringExtra(ALBUM_PATH);
        //songList = OfflineDataProvider.getSongsFromAlbum(this,album,albumPath);
        songList = OfflineDataProvider.getSongsFromColumn(this,column,columnValue);
        SongListAdapter songListAdapter = new SongListAdapter(this,songList);
        songListAdapter.setOnItemClickListener(new SongListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
              onSongPicked(position);
            }

            @Override
            public void onMoreButtonClick(View view, int position) {
                showSongOptionsMenu(view,position);
            }
        });
        albumSongListView.setAdapter(songListAdapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,1);
        albumSongListView.setLayoutManager(gridLayoutManager);
        albumImage.setImageBitmap(OfflineDataProvider.getBitmapByAlbumPath(this,albumPath));
        setTitle(title);
    }

    public void showSongOptionsMenu(View view,int position){
        PopupMenu popupMenu = new PopupMenu(this,view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.song_menu,popupMenu.getMenu());
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(this);
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.shuffle:
                Toast.makeText(this,"shuffle",Toast.LENGTH_SHORT).show();
                break;
        }
        return false;
    }

    public void onSongPicked(int position){
        ArrayList<Song> songsAlbum = OfflineDataProvider.getSongsFromColumn(this,column,columnValue);
        if(songsAlbum.size()>0) {
            musicPlayService.setOfflineSongsList(songsAlbum);
            musicPlayService.setOfflineSongPosition(position);
            musicPlayService.playOfflineSong();
        }
    }

    public void onAllPlayClicked(View view){
        ArrayList<Song> songsAlbum = OfflineDataProvider.getSongsFromColumn(this,column,columnValue);
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
