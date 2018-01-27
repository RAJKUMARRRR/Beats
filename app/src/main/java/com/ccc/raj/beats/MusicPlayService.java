package com.ccc.raj.beats;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.util.Log;

import com.ccc.raj.beats.model.Song;

import java.io.IOException;
import java.util.ArrayList;

public class MusicPlayService extends Service implements MediaPlayer.OnPreparedListener,MediaPlayer.OnCompletionListener,MediaPlayer.OnErrorListener{

    private MusicServiceBinder musicServiceBinder = new MusicServiceBinder();
    private MediaPlayer mediaPlayer;
    private ArrayList<Song> offlineSongsList;
    private int offlineSongPosition;
    private static final int NOTIFY_ID = 1;


    class MusicServiceBinder extends Binder {
        public MusicPlayService getService(){
            return MusicPlayService.this;
        }
    }

    public MusicPlayService() {
        Log.i("MusicPlayerService","OnCreateService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        offlineSongPosition = 0;
        mediaPlayer = new MediaPlayer();
        initMusicPlayer();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return  musicServiceBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mediaPlayer.stop();
        mediaPlayer.release();
        return false;
    }

    private void initMusicPlayer(){
        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        AudioAttributes.Builder builder = new AudioAttributes.Builder();
        builder.setUsage(AudioAttributes.USAGE_MEDIA);
        builder.setContentType(AudioAttributes.CONTENT_TYPE_MUSIC);
        mediaPlayer.setAudioAttributes(builder.build());
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
    }

    public void setOfflineSongsList(ArrayList<Song> list){
        this.offlineSongsList = list;
    }

    public void playOfflineSong(){
        mediaPlayer.reset();
        Song song = offlineSongsList.get(offlineSongPosition);
        long songId = song.getId();
        Uri trackUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,songId);
        try {
            mediaPlayer.setDataSource(getApplicationContext(),trackUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.prepareAsync();
    }

    public void setOfflineSongPosition(int position){
        this.offlineSongPosition = position;
    }

    public int getPosn(){
        return mediaPlayer.getCurrentPosition();
    }

    public int getDur(){
        return mediaPlayer.getDuration();
    }

    public boolean isPng(){
        return mediaPlayer.isPlaying();
    }

    public void pausePlayer(){
        mediaPlayer.pause();
    }

    public void seek(int posn){
        mediaPlayer.seekTo(posn);
    }

    public void go(){
        mediaPlayer.start();
    }

    public void playNext(){
       offlineSongPosition++;
       if(offlineSongPosition>=offlineSongsList.size()){
           offlineSongPosition = 0;
       }
       playOfflineSong();
    }

    public void playPrev(){
        offlineSongPosition--;
        if(offlineSongPosition<0){
            offlineSongPosition = offlineSongsList.size()-1;
        }
        playOfflineSong();
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if(offlineSongsList.size()>1) {
            mediaPlayer.reset();
            playNext();
        }
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        mediaPlayer.reset();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
       mediaPlayer.start();
        Intent notIntent = new Intent(this, MainActivity.class);
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendInt = PendingIntent.getActivity(this, 0,
                notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);

        builder.setContentIntent(pendInt)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setTicker(offlineSongsList.get(offlineSongPosition).getTitle())
                .setOngoing(true)
                .setContentTitle("Beats Music")
                .setContentText(offlineSongsList.get(offlineSongPosition).getTitle());
        Notification not = builder.build();

        startForeground(NOTIFY_ID, not);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
    }
}
