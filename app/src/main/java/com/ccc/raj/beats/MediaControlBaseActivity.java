package com.ccc.raj.beats;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Build;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ccc.raj.beats.model.OfflineDataProvider;
import com.ccc.raj.beats.model.OfflineSong;
import com.ccc.raj.beats.model.Song;
import com.ccc.raj.beats.model.localstorage.SessionStorageManager;
import com.ccc.raj.beats.settings.ThemeData;

import java.util.ArrayList;

public abstract class MediaControlBaseActivity extends AppCompatActivity implements CustomMediaController.MediaPlayerControl, MusicServiceSubscriber {

    private MusicController controller;
    private static MusicPlayService musicPlayService;
    private boolean musicBound = false;
    private boolean paused = false, playbackPaused = false;
    private Intent playIntent;
    private ImageView imageViewAlbum;
    private FrameLayout mainContainer;

    private LinearLayout activePlayListContainer;
    private RecyclerView activePlayListView;
    private TextView activePlayAlbumTitle;
    private ImageButton activePlayListButton;
    public static ImageButton playPauseButtonTwo;
    public static ImageButton playPauseButton;
    private ImageView activeAlbumImage;
    private ImageButton slidingWindowOptionsButton;
    private TextView activeSongTitle;
    private TextView activeSongArtistTitle;
    private ImageView imageAlbumSlidingBackground;

    private IntentFilter intentFilter;
    private AudioOutputChangeReceiver audioOutputChangeReceiver;

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MusicPlayService.MusicServiceBinder musicServiceBinder = (MusicPlayService.MusicServiceBinder) iBinder;
            musicPlayService = musicServiceBinder.getService();
            MusicPlayServiceHolder.setMusicPlayService(musicPlayService);
            musicBound = true;
            onMusicServiceBind(musicPlayService);
            musicPlayService.subscribeForService(MediaControlBaseActivity.this);
            setAlbumArt();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            musicBound = false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(ThemeData.theme);
        super.onCreate(savedInstanceState);
        playIntent = new Intent(this, MusicPlayService.class);
        bindService(playIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        setController();
        //if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //}
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
    }



    protected abstract void onMusicServiceBind(MusicPlayService musicPlayService);

    private void setController() {
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
        setControllerAnchorView(controller);
        controller.setEnabled(true);
        //controller.show();
        imageViewAlbum = findViewById(R.id.imageViewAlbum);
        mainContainer = findViewById(R.id.mainMediaContainer);
        imageAlbumSlidingBackground = findViewById(R.id.imageAlbumSlidingBackground);

        activePlayListView = findViewById(R.id.activePlayList);
        activePlayAlbumTitle = findViewById(R.id.activePlayAlbum);
        activePlayListButton = findViewById(R.id.activePlayListButton);
        activePlayListContainer = findViewById(R.id.activePlayListContainer);
        playPauseButtonTwo = findViewById(R.id.playpauseButtonTwo);
        playPauseButton = findViewById(R.id.pause);
        activeAlbumImage = findViewById(R.id.activeAlbumImage);
        slidingWindowOptionsButton = findViewById(R.id.slidingWindowOptionsButton);
        activeSongTitle = findViewById(R.id.activeSongTitle);
        activeSongArtistTitle = findViewById(R.id.activeSongArtistTitle);
        activePlayListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onActivePlayListClick();
            }
        });
    }

    private void onActivePlayListClick() {
        try {
            if (activePlayListContainer.getVisibility() == View.INVISIBLE) {
                final MusicPlayService musicPlayService = MusicPlayServiceHolder.getMusicPlayService();
                ArrayList<Song> songs = musicPlayService.getActivePlayList();
                if (musicPlayService != null && songs != null) {
                    final SongListAdapter songListAdapter = new SongListAdapter(this, songs, true);
                    songListAdapter.setOnItemClickListener(new SongListAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            musicPlayService.setOfflineSongPosition(position);
                            musicPlayService.playOfflineSong();
                        }

                        @Override
                        public void onMoreButtonClick(View view, int position) {

                        }
                    });
                    activePlayListView.setAdapter(songListAdapter);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                    activePlayListView.setLayoutManager(linearLayoutManager);
                    activePlayListButton.setImageResource(R.drawable.ic_music_library_primary);
                    activePlayListContainer.setVisibility(View.VISIBLE);
                    OfflineSong offlineSong = (OfflineSong) musicPlayService.getActiveSong();
                    activePlayAlbumTitle.setText(musicPlayService.getActiveAlbumTitle());
                    Bitmap bitmap = OfflineDataProvider.getBitmapByAlbumId(this, offlineSong.getAlbumId());
                    activeAlbumImage.setImageBitmap(bitmap);
                    activeAlbumImage.getLayoutParams().height = findViewById(R.id.frameActiveListHolder).getHeight();
                }
            } else {
                activePlayListContainer.setVisibility(View.INVISIBLE);
                activePlayListButton.setImageResource(R.drawable.ic_music_library);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void onSlideUp() {
        playPauseButtonTwo.setVisibility(View.GONE);
        activePlayListButton.setVisibility(View.VISIBLE);
        slidingWindowOptionsButton.setVisibility(View.VISIBLE);
    }

    public void onSlideDown() {
        playPauseButtonTwo.setVisibility(View.VISIBLE);
        activePlayListButton.setVisibility(View.GONE);
        slidingWindowOptionsButton.setVisibility(View.GONE);
        activePlayListContainer.setVisibility(View.INVISIBLE);
        activePlayListButton.setImageResource(R.drawable.ic_music_library);
    }

    private void setAlbumArt() {
        if (musicPlayService != null) {
            if( musicPlayService.getActiveSong() != null) {
                OfflineSong song = (OfflineSong) musicPlayService.getActiveSong();
                Bitmap bitmap = OfflineDataProvider.getBitmapByAlbumId(this, song.getAlbumId());
                if (imageViewAlbum != null) {
                    imageViewAlbum.setImageBitmap(bitmap);
                }
                if (imageAlbumSlidingBackground != null) {
                    imageAlbumSlidingBackground.setImageBitmap(bitmap);
                }
                if (activeSongTitle != null) {
                    activeSongTitle.setText(song.getTitle() + "");
                }
                if (activeSongArtistTitle != null) {
                    activeSongArtistTitle.setText(song.getArtist() + "");
                }
            }else{

            }
        }
    }

    @Override
    public void updateActivePlayTrack(ArrayList<Song> songsList, int position) {
        Log.i("BeatsSubscriber", "Got Update:" + position);
        setAlbumArt();
    }


    protected abstract void setControllerAnchorView(MusicController musicController);

    private void playNext() {
        musicPlayService.playNext();
        controller.updatePausePlay();
        if (playbackPaused) {
            //setController();
            playbackPaused = false;
        }
        //controller.show(0);
    }

    private void playPrev() {
        musicPlayService.playPrev();
        controller.updatePausePlay();
        if (playbackPaused) {
            //setController();
            playbackPaused = false;
        }
        //controller.show(0);
    }

    @Override
    protected void onStart() {
        super.onStart();
        setAlbumArt();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (paused) {
            //setController();
            paused = false;
        }
        /*Registering receiver for change in audio output like when removing headset*/
        intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        audioOutputChangeReceiver = new AudioOutputChangeReceiver((ImageButton) findViewById(R.id.pause),(ImageButton) findViewById(R.id.playpauseButtonTwo));
        registerReceiver(audioOutputChangeReceiver,intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        paused = true;
        unregisterReceiver(audioOutputChangeReceiver);
    }

    @Override
    protected void onStop() {
        //controller.hide();
        super.onStop();
        /*******Storing session(current play album in local storage to retrieve state after next launch)******/
        MusicPlayService musicPlayService = MusicPlayServiceHolder.getMusicPlayService();
        if(musicPlayService != null) {
            Utitlity.Log("onTaskRemoved");
            OfflineSong offlineSong = (OfflineSong) musicPlayService.getActiveSong();
            Utitlity.Log("Position" + musicPlayService.getPosn());
            SessionStorageManager.storeSessionData(this, musicPlayService.getActiveAlbumId(),musicPlayService.getActiveAlbumType(), musicPlayService.getOfflineSongPosition(), musicPlayService.getPosn(),musicPlayService.getActiveAlbumTitle());
        }
    }

    @Override
    public void start() {
        musicPlayService.go();
        setAlbumArt();
        NotificationHandler.updateNotificationToPause(this);
    }


    @Override
    public void pause() {
        musicPlayService.pausePlayer();
        playbackPaused = true;
        NotificationHandler.updateNotificationToPlay(this);
    }

    @Override
    public int getDuration() {
        if (musicPlayService != null && musicBound/*&&musicPlayService.isPng()*/) {
            return musicPlayService.getDur();
        }
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        if (musicPlayService != null && musicBound/*&&musicPlayService.isPng()*/) {
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
        if (musicPlayService != null && musicBound) {
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


    public static class NotificationActionReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()){
                case NotificationHandler.NOTIFICATION_PLAYPAUSE_ACTION:
                    if(musicPlayService != null) {
                        if (musicPlayService.isPng()) {
                            musicPlayService.pausePlayer();
                            NotificationHandler.updateNotificationToPlay(context);
                            if(playPauseButton != null) {
                                playPauseButton.setImageResource(R.drawable.ic_play);
                            }
                            if(playPauseButtonTwo != null) {
                                playPauseButtonTwo.setImageResource(R.drawable.ic_play_arrow);
                            }
                        } else {
                            musicPlayService.go();
                            NotificationHandler.updateNotificationToPause(context);
                            if(playPauseButton != null) {
                                playPauseButton.setImageResource(R.drawable.ic_pause);
                            }
                            if(playPauseButtonTwo != null) {
                                playPauseButtonTwo.setImageResource(R.drawable.ic_pause_arrow);
                            }
                        }
                    }
                    break;
                case NotificationHandler.NOTIFICATION_NEXT_ACTION:
                    if(musicPlayService != null){
                        musicPlayService.playNext();
                    }
                    break;
                case NotificationHandler.NOTIFICATION_PREVIOUS_ACTION:
                    if(musicPlayService != null){
                        musicPlayService.playPrev();
                    }
                    break;
                case NotificationHandler.NOTIFICATION_THUMBS_UP_ACTION:
                    Toast.makeText(context,"Thumbs Up",Toast.LENGTH_SHORT).show();
                    break;
                case NotificationHandler.NOTIFICATION_THUMBS_DOWN_ACTION:
                    Toast.makeText(context,"Thumbs Down",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

}
