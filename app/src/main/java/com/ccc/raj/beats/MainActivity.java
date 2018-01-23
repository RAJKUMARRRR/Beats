package com.ccc.raj.beats;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.MediaController;
import android.widget.Toast;

import com.ccc.raj.beats.listennow.ListenNowFragment;
import com.ccc.raj.beats.listennow.OfflineFragment;
import com.ccc.raj.beats.listennow.OnlineFragment;
import com.ccc.raj.beats.model.Song;
import com.ccc.raj.beats.musiclibrary.MusicLibraryFragment;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MediaController.MediaPlayerControl,NavigationView.OnNavigationItemSelectedListener{
    Toolbar toolbar;
    public static MusicPlayService musicPlayService;
    private ArrayList<Song> songsList;
    private Intent playIntent;
    private boolean musicBound = false;
    private MusicController controller;
    private boolean paused=false, playbackPaused=false;
    DrawerLayout drawerLayout;
    ListenNowFragment listenNowFragment;
    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MusicPlayService.MusicServiceBinder musicServiceBinder = (MusicPlayService.MusicServiceBinder) iBinder;
            musicPlayService = musicServiceBinder.getService();
            musicBound = true;
            listenNowFragment.setMusicPlayService(musicPlayService);
            MusicPlayServiceHolder.setMusicPlayService(musicPlayService);
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

        FragmentManager fragmentManager = getSupportFragmentManager();
        listenNowFragment = new ListenNowFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.main_container,listenNowFragment);
        fragmentTransaction.commit();


        playIntent = new Intent(this,MusicPlayService.class);
        bindService(playIntent,serviceConnection, Context.BIND_AUTO_CREATE);

        setNavigationDrawer();

        ViewTreeObserver vto = drawerLayout.getViewTreeObserver();
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

    public void setNavigationDrawer(){
        drawerLayout = findViewById(R.id.drawer_view);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,drawerLayout,toolbar,R.string.nav_open_drawer,R.string.nav_close_drawer
        );
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Fragment fragment = null;
        Intent intent = null;
        switch (id){
            case R.id.nav_listen_now:
                fragment = new ListenNowFragment();
                //((ListenNowFragment)fragment).setMusicPlayService(musicPlayService);
                break;
            case R.id.nav_music_library:
                fragment = new MusicLibraryFragment();
                //((MusicLibraryFragment)fragment).setMusicPlayService(musicPlayService);
                break;
            /*case R.id.nav_sent:
                fragment = new InboxFragment();
                break;
            case R.id.nav_trash:
                fragment = new InboxFragment();
                break;
            case R.id.help:
                intent = new Intent(this,HelpActivity.class);
                break;
            case R.id.feedback:
                intent = new Intent(this,FeedbackActivity.class);
                break;*/
        }
        if(fragment!=null){
            FragmentTransaction fragmentTransaction= getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.main_container,fragment);
            fragmentTransaction.commit();
        }else{
            //startActivity(intent);
        }
        DrawerLayout drawerLayout = findViewById(R.id.drawer_view);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
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
        //controller.setAnchorView(drawerLayout);
        controller.setEnabled(true);
        controller.show();
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


}

