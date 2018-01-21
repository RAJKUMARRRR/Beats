package com.ccc.raj.beats;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.MediaController;

import com.ccc.raj.beats.model.Song;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MediaController.MediaPlayerControl{
    Toolbar toolbar;
    ViewPager viewPager;
    TabLayout tabLayout;
    public static MusicPlayService musicPlayService;
    private ArrayList<Song> songsList;
    private Intent playIntent;
    private boolean musicBound = false;
    OfflineFragment offlineFragment = null;
    OnlineFragment onlineFragment = null;
    private MusicController controller;
    private boolean paused=false, playbackPaused=false;

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MusicPlayService.MusicServiceBinder musicServiceBinder = (MusicPlayService.MusicServiceBinder) iBinder;
            musicPlayService = musicServiceBinder.getService();
            musicBound = true;
            offlineFragment.setMusicPlayService(musicPlayService);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            musicBound = false;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        viewPager = findViewById(R.id.pager_view);
        PagerViewAdapter pagerViewAdapter = new PagerViewAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerViewAdapter);
        tabLayout = findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);
        playIntent = new Intent(this,MusicPlayService.class);
        bindService(playIntent,serviceConnection, Context.BIND_AUTO_CREATE);
        ViewTreeObserver vto = viewPager.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new  ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                setController();
            }
        });
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
        controller.setAnchorView(viewPager);
        controller.setEnabled(true);
        controller.show();
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

    private class PagerViewAdapter extends FragmentPagerAdapter{
        public PagerViewAdapter(FragmentManager fm) {
            super(fm);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 1:
                    return "Online";
                case 0:
                    return "Offline";
            }
            return null;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    if(offlineFragment == null) {
                        offlineFragment = new OfflineFragment();
                    }
                    return offlineFragment;
                case 1:
                    if(onlineFragment == null) {
                        onlineFragment = new OnlineFragment();
                    }
                    return onlineFragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}

