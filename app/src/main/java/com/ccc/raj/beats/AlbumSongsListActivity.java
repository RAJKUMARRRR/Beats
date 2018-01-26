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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.ccc.raj.beats.model.OfflineDataProvider;
import com.ccc.raj.beats.model.Song;

import java.util.ArrayList;

public class AlbumSongsListActivity extends AppCompatActivity implements MediaController.MediaPlayerControl,PopupMenu.OnMenuItemClickListener{
    private CoordinatorLayout mainContainer;
    private RecyclerView albumSongListView;
    private ArrayList<Song> songList;
    private ImageView albumImage;
    private Toolbar toolbar;

    public static final String COLUMN = "COLUMN";
    public static final String COLUMN_VALUE = "COLUMN_VALUE";
    public static final String TITLE = "TITLE";
    public static final String ALBUM_PATH = "ALBUM_PATH";


    MusicController controller;
    public static MusicPlayService musicPlayService;
    private boolean musicBound = false;
    private boolean paused=false, playbackPaused=false;
    private Intent playIntent;
    //private String album;
    private String albumPath;
    private String column;
    private String columnValue;
    private String title;

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MusicPlayService.MusicServiceBinder musicServiceBinder = (MusicPlayService.MusicServiceBinder) iBinder;
            musicPlayService = musicServiceBinder.getService();
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            musicBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_songs_list);
        albumImage = findViewById(R.id.album_image);
        setActionBar();

        setSongsListData();

        playIntent = new Intent(this,MusicPlayService.class);
        bindService(playIntent,serviceConnection, Context.BIND_AUTO_CREATE);

        mainContainer = findViewById(R.id.coordinator_album_songs);
        ViewTreeObserver vto = mainContainer.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new  ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                setController();
            }
        });
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

        //registerForContextMenu(albumSongListView);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
    }

    public void setController(){
        controller = new MusicController(this);
        controller.setPrevNextListeners(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNext();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPrev();
            }
        });
        controller.setMediaPlayer(this);
        controller.setAnchorView(mainContainer);
        controller.setEnabled(true);
        controller.show();
    }
    public void playNext(){
        musicPlayService.playNext();
        if(playbackPaused){
            setController();
            playbackPaused=false;
        }
        controller.show(0);
    }

    public void playPrev(){
        musicPlayService.playPrev();
        if(playbackPaused){
            setController();
            playbackPaused=false;
        }
        controller.show(0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(paused){
            setController();
            paused = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        paused = true;
    }
    @Override
    protected void onStop() {
        controller.hide();
        super.onStop();
    }

    @Override
    public void start() {
        musicPlayService.go();
    }


    @Override
    public void pause() {
        musicPlayService.pausePlayer();
        playbackPaused = true;
    }

    @Override
    public int getDuration() {
        if(musicPlayService!=null&&musicBound&&musicPlayService.isPng()){
            return musicPlayService.getDur();
        }
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        if(musicPlayService!=null&&musicBound&&musicPlayService.isPng()){
            return musicPlayService.getPosn();
        }
        return 0;
    }

    @Override
    public void seekTo(int i) {
        musicPlayService.seek(i);
    }

    @Override
    public boolean isPlaying() {
        if(musicPlayService!=null&&musicBound){
            return musicPlayService.isPng();
        }
        return false;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

}
