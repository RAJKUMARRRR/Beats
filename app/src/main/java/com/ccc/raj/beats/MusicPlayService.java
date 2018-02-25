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
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.ccc.raj.beats.model.OfflineSong;
import com.ccc.raj.beats.model.Song;

import java.io.IOException;
import java.util.ArrayList;

public class MusicPlayService extends Service implements MediaPlayer.OnPreparedListener,MediaPlayer.OnCompletionListener,MediaPlayer.OnErrorListener,MusicServicePublisher{

    private MusicServiceBinder musicServiceBinder = new MusicServiceBinder();
    private MediaPlayer mediaPlayer;
    private ArrayList<Song> offlineSongsList;
    private int offlineSongPosition;
    private ArrayList<MusicServiceSubscriber> subsciberList = new ArrayList<>();


    @Override
    public void subscribeForService(MusicServiceSubscriber obj) {
       if(obj != null){
           subsciberList.add(obj);
       }
    }

    @Override
    public void unsubscribeForService(MusicServiceSubscriber obj) {
        if(obj != null){
            int index = subsciberList.indexOf(obj);
            subsciberList.remove(index);
        }
    }

    @Override
    public void notifyAllSubscribers() {
        for(int i=0;i<subsciberList.size();i++){
            subsciberList.get(i).updateActivePlayTrack(offlineSongsList,offlineSongPosition);
        }
    }


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

    public ArrayList<Song> getActivePlayList(){
        return offlineSongsList;
    }

    public Song getActiveSong(){
        return offlineSongsList.get(offlineSongPosition);
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
        notifyAllSubscribers();
    }

    public void addToQueue(ArrayList<Song> songs){
        offlineSongsList.addAll(songs);
    }

    public void addToPlayNext(ArrayList<Song> songs){
        offlineSongsList.addAll(offlineSongPosition+1,songs);
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
        NotificationHandler.showSongNotification(this,offlineSongsList.get(offlineSongPosition));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
    }

  /*  public void showNotification(){
        Intent notIntent = new Intent(this, MainActivity.class);
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendInt = PendingIntent.getActivity(this, 0,
                notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        OfflineSong offlineSong = (OfflineSong) offlineSongsList.get(offlineSongPosition);
        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notification);
        contentView.setImageViewResource(R.id.notificationSongAlbumImage, R.drawable.ic_logo);
        contentView.setTextViewText(R.id.notificationSongTitle, offlineSong.getTitle());
        contentView.setTextViewText(R.id.notificationSongArtist, offlineSong.getArtist());
        contentView.setTextViewText(R.id.notificationSongAlbum, offlineSong.getAlbum());
        contentView.setImageViewResource(R.id.notificationUpButton, R.drawable.ic_thumb_up_white);
        contentView.setImageViewResource(R.id.notificationDownButton, R.drawable.ic_thumb_down_white);
        contentView.setImageViewResource(R.id.notificationPrev, R.drawable.ic_previous_white);
        contentView.setImageViewResource(R.id.notificationNext, R.drawable.ic_next_white);
        contentView.setImageViewResource(R.id.notificationPause, R.drawable.ic_play_arrow_white);

        Intent actionPlayPauseIntent = new Intent(this,MediaControlBaseActivity.NotificationActionReceiver.class);
        actionPlayPauseIntent.setAction(NotificationHandler.NOTIFICATION_PLAYPAUSE_ACTION);
        PendingIntent pendingPlayPauseIntent = PendingIntent.getBroadcast(this, 0,
                actionPlayPauseIntent, 0);
        contentView.setOnClickPendingIntent(R.id.notificationPause,pendingPlayPauseIntent);


        Intent actionPlayNextIntent = new Intent(this,MediaControlBaseActivity.NotificationActionReceiver.class);
        actionPlayPauseIntent.setAction(NotificationHandler.NOTIFICATION_NEXT_ACTION);
        PendingIntent pendingPlayNextIntent = PendingIntent.getBroadcast(this, 0,
                actionPlayNextIntent, 0);
        contentView.setOnClickPendingIntent(R.id.notificationNext,pendingPlayNextIntent);


        Intent actionPlayPrevIntent = new Intent(this,MediaControlBaseActivity.NotificationActionReceiver.class);
        actionPlayPauseIntent.setAction(NotificationHandler.NOTIFICATION_PREVIOUS_ACTION);
        PendingIntent pendingPlayPrevIntent = PendingIntent.getBroadcast(this, 0,
                actionPlayPrevIntent, 0);
        contentView.setOnClickPendingIntent(R.id.notificationPrev,pendingPlayPrevIntent);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,"default");
        builder.setContentIntent(pendInt)
                .setSmallIcon(R.drawable.ic_logo)
                .setCustomBigContentView(contentView)
                .setCustomContentView(contentView)
                .setCustomHeadsUpContentView(contentView)
                .setAutoCancel(true);
        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);
        Notification not = builder.build();

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(NOTIFY_ID,not);
    }
    */
}
