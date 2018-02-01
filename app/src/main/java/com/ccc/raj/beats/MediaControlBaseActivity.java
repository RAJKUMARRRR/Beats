package com.ccc.raj.beats;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.ccc.raj.beats.model.OfflineDataProvider;
import com.ccc.raj.beats.model.OfflineSong;
import com.ccc.raj.beats.model.Song;

import java.util.ArrayList;

public abstract class MediaControlBaseActivity extends AppCompatActivity implements CustomMediaController.MediaPlayerControl,MusicServiceSubscriber{

    private MusicController controller;
    private static MusicPlayService musicPlayService;
    private boolean musicBound = false;
    private boolean paused = false, playbackPaused = false;
    private Intent playIntent;
    private ImageView imageViewAlbum;
    private FrameLayout mainContainer;

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MusicPlayService.MusicServiceBinder musicServiceBinder = (MusicPlayService.MusicServiceBinder) iBinder;
            musicPlayService = musicServiceBinder.getService();
            MusicPlayServiceHolder.setMusicPlayService(musicPlayService);
            musicBound = true;
            onMusicServiceBind(musicPlayService);
            musicPlayService.subscribeForService(MediaControlBaseActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            musicBound = false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        playIntent = new Intent(this, MusicPlayService.class);
        bindService(playIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        setController();
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
    }

    private void setAlbumArt() {
        if (musicPlayService != null) {
            OfflineSong song = (OfflineSong) musicPlayService.getActiveSong();
            Bitmap bitmap = OfflineDataProvider.getBitmapByAlbumId(this, song.getAlbumId());
            if (imageViewAlbum != null) {
                imageViewAlbum.setImageBitmap(bitmap);
            }
            if (mainContainer != null) {
                Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                mainContainer.setBackground(drawable);
            }
        }
    }

    @Override
    public void updateActivePlayTrack(ArrayList<Song> songsList, int position) {
      Log.i("BeatsSubscriber","Got Update:"+position);
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
    }

    @Override
    protected void onPause() {
        super.onPause();
        paused = true;
    }

    @Override
    protected void onStop() {
        //controller.hide();
        super.onStop();
    }

    @Override
    public void start() {
        musicPlayService.go();
        setAlbumArt();
    }


    @Override
    public void pause() {
        musicPlayService.pausePlayer();
        playbackPaused = true;
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

}
