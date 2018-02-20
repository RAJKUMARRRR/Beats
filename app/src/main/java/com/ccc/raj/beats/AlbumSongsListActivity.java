package com.ccc.raj.beats;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.ccc.raj.beats.model.Album;
import com.ccc.raj.beats.model.AlbumTable;
import com.ccc.raj.beats.model.GenresTable;
import com.ccc.raj.beats.model.OfflineDataProvider;
import com.ccc.raj.beats.model.OfflineSong;
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
    private SongListAdapter songListAdapter;
    public static final String COLUMN = "COLUMN";
    public static final String COLUMN_VALUE = "COLUMN_VALUE";
    public static final String TITLE = "TITLE";
    public static final String ALBUM_ID = "ALBUM_ID";
    public static final String ALBUM_TYPE = "ALBUM_TYPE";
    public static final int OFFLINE_ALBUM = 0;
    public static final int PLAYLIST_ALBUM = 1;
    public static final int ARTIST_ALBUM = 2;
    public static final int GENRES_ALBUM = 3;


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
                albumImage.setImageBitmap(OfflineDataProvider.getBitmapByAlbumId(this,albumId));
                break;
            case PLAYLIST_ALBUM:
                songList = PlayListTable.getSongsFromPlayLists(this,albumId);
                Bitmap playListAlbumArt = OfflineDataProvider.getBitmapBySongsList(this,songList);
                albumImage.setImageBitmap(playListAlbumArt);
                break;
            case ARTIST_ALBUM:
                songList = SongTable.getSongsFromColumn(this, column, columnValue);
                albumImage.setImageBitmap(OfflineDataProvider.getBitmapByAlbumId(this,albumId));
                break;
            case GENRES_ALBUM:
                songList = GenresTable.getSongsFromGeneres(this,albumId);
                Bitmap genresAlbumArt = OfflineDataProvider.getBitmapBySongsList(this,songList);
                albumImage.setImageBitmap(genresAlbumArt);
                break;
            default:
                songList = SongTable.getSongsFromColumn(this, column, columnValue);
                albumImage.setImageBitmap(OfflineDataProvider.getBitmapByAlbumId(this,albumId));
        }


        songListAdapter = new SongListAdapter(this,songList);
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
        setTitle(title);
    }

    public void showSongOptionsMenu(View view,int position){
        PopupMenu popupMenu = new PopupMenu(this,view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.popup_menu,popupMenu.getMenu());
        int album_type = getIntent().getIntExtra(ALBUM_TYPE,0);
        switch (album_type) {
            case OFFLINE_ALBUM:
                popupMenu.getMenu().removeItem(R.id.not_interested);
                popupMenu.getMenu().removeItem(R.id.go_to_album);
                popupMenu.getMenu().removeItem(R.id.remove_from_playlist);
                popupMenu.getMenu().removeItem(R.id.edit_playlist);
                break;
            case PLAYLIST_ALBUM:
                popupMenu.getMenu().removeItem(R.id.not_interested);
                popupMenu.getMenu().removeItem(R.id.delete);
                popupMenu.getMenu().removeItem(R.id.edit_playlist);
                break;
            case ARTIST_ALBUM:
                popupMenu.getMenu().removeItem(R.id.not_interested);
                popupMenu.getMenu().removeItem(R.id.goto_artist);
                popupMenu.getMenu().removeItem(R.id.remove_from_playlist);
                popupMenu.getMenu().removeItem(R.id.edit_playlist);
                break;
            case GENRES_ALBUM:
                popupMenu.getMenu().removeItem(R.id.not_interested);
                popupMenu.getMenu().removeItem(R.id.remove_from_playlist);
                popupMenu.getMenu().removeItem(R.id.edit_playlist);
                break;
            default:
                popupMenu.getMenu().removeItem(R.id.not_interested);
                popupMenu.getMenu().removeItem(R.id.go_to_album);
                popupMenu.getMenu().removeItem(R.id.remove_from_playlist);
                popupMenu.getMenu().removeItem(R.id.edit_playlist);
        }
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
            case R.id.remove_from_playlist:
                onRemoveFromPlaylistClicked();
                break;
            case R.id.goto_artist:
                onGotoArtistClick();
                break;
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

    public void onSongPicked(int position){
        if(songList.size()>0) {
            musicPlayService.setOfflineSongsList(songList);
            musicPlayService.setOfflineSongPosition(position);
            musicPlayService.playOfflineSong();
        }
    }

    public void onAddToPlayListClick(int position){
        ArrayList<Song> songs = new ArrayList<>();
        songs.add(songList.get(position));
        new PlayListSelectionPopup(this,songs).showPopup();
    }
    public void onGotoArtistClick(){
        OfflineSong song = (OfflineSong) songList.get(selectedSongPosition);
        Intent intent = new Intent(this, MoreRecordsActivity.class);
        intent.putExtra(MoreRecordsActivity.VIEW_TYPE, MoreRecordsActivity.ARTIST_ALBUM);
        intent.putExtra(MoreRecordsActivity.SEARCH_QUERY, String.valueOf(song.getArtistId()));
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this);
        startActivity(intent, options.toBundle());
    }
    public void onGotoAlbumClick(){
        OfflineSong song = (OfflineSong) songList.get(selectedSongPosition);
        Intent intent = new Intent(this, AlbumSongsListActivity.class);
        intent.putExtra(AlbumSongsListActivity.COLUMN, AlbumTable.ALBUM);
        intent.putExtra(AlbumSongsListActivity.COLUMN_VALUE, song.getAlbum());
        intent.putExtra(AlbumSongsListActivity.ALBUM_ID, song.getAlbumId());
        intent.putExtra(AlbumSongsListActivity.TITLE, song.getAlbum());
        startActivity(intent);
    }
    public void onPlayNextClick(){
        ArrayList<Song> songs = new ArrayList<>();
        songs.add(songList.get(selectedSongPosition));
        MusicPlayService musicPlayService = MusicPlayServiceHolder.getMusicPlayService();
        if(musicPlayService != null){
            musicPlayService.addToPlayNext(songs);
        }
    }
    public void onAddToQueueClick(){
        ArrayList<Song> songs = new ArrayList<>();
        songs.add(songList.get(selectedSongPosition));
        MusicPlayService musicPlayService = MusicPlayServiceHolder.getMusicPlayService();
        if(musicPlayService != null){
            musicPlayService.addToQueue(songs);
        }
    }

    public void onAllPlayClicked(View view){
        if(songList.size()>0) {
            musicPlayService.setOfflineSongsList(songList);
            musicPlayService.setOfflineSongPosition(0);
            musicPlayService.playOfflineSong();
        }
        ImageButton imageButton = (ImageButton) view;
        imageButton.setImageResource(R.color.transperent);
        imageButton.setBackgroundResource(R.drawable.anim_music);
        AnimationDrawable animationDrawable = (AnimationDrawable) imageButton.getBackground();
        animationDrawable.start();
    }

    public void onRemoveFromPlaylistClicked(){
        boolean status = PlayListTable.deleteSongFromPlayList(this,albumId,songList.get(selectedSongPosition));
        if(status){
            Toast.makeText(this,"'"+songList.get(selectedSongPosition).getTitle()+"' "+getString(R.string.removed_from_playlist),Toast.LENGTH_SHORT).show();
            songListAdapter.songList.remove(selectedSongPosition);
            songListAdapter.notifyItemRemoved(selectedSongPosition);
        }else{
            Toast.makeText(this,"Failed",Toast.LENGTH_SHORT).show();
        }
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
