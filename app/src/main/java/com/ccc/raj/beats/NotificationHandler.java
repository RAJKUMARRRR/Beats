package com.ccc.raj.beats;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.RemoteViews;

import com.ccc.raj.beats.model.OfflineSong;
import com.ccc.raj.beats.model.Song;

/**
 * Created by Raj on 2/24/2018.
 */

public class NotificationHandler {
    public static final String NOTIFICATION_PLAYPAUSE_ACTION = "com.ccc.raj.beats.notifications.action.playpause";
    public static final String NOTIFICATION_NEXT_ACTION = "com.ccc.raj.beats.notifications.action.next";
    public static final String NOTIFICATION_PREVIOUS_ACTION = "com.ccc.raj.beats.notifications.action.previous";
    public static final String NOTIFICATION_THUMBS_UP_ACTION = "com.ccc.raj.beats.notifications.action.thumbsup";
    public static final String NOTIFICATION_THUMBS_DOWN_ACTION = "com.ccc.raj.beats.notifications.action.thumbsdown";

    private static final int NOTIFY_ID = 1;
    private static Notification notification;


    public static void showSongNotification(Context context,Song song){
        Intent notIntent = new Intent(context, MainActivity.class);
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendInt = PendingIntent.getActivity(context, 0,
                notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        OfflineSong offlineSong = (OfflineSong)song;
        RemoteViews contentViewBig = new RemoteViews(context.getPackageName(), R.layout.notification);
        RemoteViews contentViewSmall =  new RemoteViews(context.getPackageName(), R.layout.notification_small);

        contentViewBig.setImageViewResource(R.id.notificationSongAlbumImage, R.drawable.ic_logo);
        contentViewBig.setTextViewText(R.id.notificationSongTitle, offlineSong.getTitle());
        contentViewBig.setTextViewText(R.id.notificationSongArtist, offlineSong.getArtist());
        contentViewBig.setTextViewText(R.id.notificationSongAlbum, offlineSong.getAlbum());
        contentViewBig.setImageViewResource(R.id.notificationUpButton, R.drawable.ic_thumb_up_white);
        contentViewBig.setImageViewResource(R.id.notificationDownButton, R.drawable.ic_thumb_down_white);
        contentViewBig.setImageViewResource(R.id.notificationPrev, R.drawable.ic_previous_white);
        contentViewBig.setImageViewResource(R.id.notificationNext, R.drawable.ic_next_white);
        contentViewBig.setImageViewResource(R.id.notificationPause, R.drawable.ic_pause_arrow_white);

        contentViewSmall.setImageViewResource(R.id.notificationSongAlbumImage, R.drawable.ic_logo);
        contentViewSmall.setTextViewText(R.id.notificationSongTitle, offlineSong.getTitle());
        contentViewSmall.setTextViewText(R.id.notificationSongArtist, offlineSong.getArtist());
        contentViewSmall.setImageViewResource(R.id.notificationPrev, R.drawable.ic_previous_white);
        contentViewSmall.setImageViewResource(R.id.notificationNext, R.drawable.ic_next_white);
        contentViewSmall.setImageViewResource(R.id.notificationPause, R.drawable.ic_pause_arrow_white);

        Intent actionPlayPauseIntent = new Intent(context,MediaControlBaseActivity.NotificationActionReceiver.class);
        actionPlayPauseIntent.setAction(NOTIFICATION_PLAYPAUSE_ACTION);
        PendingIntent pendingPlayPauseIntent = PendingIntent.getBroadcast(context, 0,
                actionPlayPauseIntent, 0);
        contentViewBig.setOnClickPendingIntent(R.id.notificationPause,pendingPlayPauseIntent);
        contentViewSmall.setOnClickPendingIntent(R.id.notificationPause,pendingPlayPauseIntent);


        Intent actionPlayNextIntent = new Intent(context,MediaControlBaseActivity.NotificationActionReceiver.class);
        actionPlayNextIntent.setAction(NOTIFICATION_NEXT_ACTION);
        PendingIntent pendingPlayNextIntent = PendingIntent.getBroadcast(context, 0,
                actionPlayNextIntent, 0);
        contentViewBig.setOnClickPendingIntent(R.id.notificationNext,pendingPlayNextIntent);
        contentViewSmall.setOnClickPendingIntent(R.id.notificationNext,pendingPlayNextIntent);


        Intent actionPlayPrevIntent = new Intent(context,MediaControlBaseActivity.NotificationActionReceiver.class);
        actionPlayPrevIntent.setAction(NOTIFICATION_PREVIOUS_ACTION);
        PendingIntent pendingPlayPrevIntent = PendingIntent.getBroadcast(context, 0,
                actionPlayPrevIntent, 0);
        contentViewBig.setOnClickPendingIntent(R.id.notificationPrev,pendingPlayPrevIntent);
        contentViewSmall.setOnClickPendingIntent(R.id.notificationPrev,pendingPlayPrevIntent);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"default");
/*        builder.setContentIntent(pendInt)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setTicker(offlineSongsList.get(offlineSongPosition).getTitle())
                .setOngoing(true)
                .setContentTitle("Beats Music")
                .setContentText(offlineSongsList.get(offlineSongPosition).getTitle())
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);*/
        builder.setContentIntent(pendInt)
                .setSmallIcon(R.drawable.ic_logo)
                .setCustomBigContentView(contentViewBig)
                .setCustomContentView(contentViewSmall)
                .setCustomHeadsUpContentView(contentViewBig)
                .setAutoCancel(true);
        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);
        notification = builder.build();

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(NOTIFY_ID,notification);
        //startForeground(NOTIFY_ID, notification);
    }

    public static void updateNotificationToPause(Context context){
        if(notification != null) {
            RemoteViews remoteViewsBig = notification.bigContentView;
            RemoteViews remoteViewsSmall = notification.contentView;
            remoteViewsSmall.setImageViewResource(R.id.notificationPause, R.drawable.ic_pause_arrow_white);
            remoteViewsBig.setImageViewResource(R.id.notificationPause, R.drawable.ic_pause_arrow_white);
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            notificationManagerCompat.notify(NOTIFY_ID, notification);
        }
    }
    public static void updateNotificationToPlay(Context context){
        if(notification != null) {
            RemoteViews remoteViewsBig = notification.bigContentView;
            RemoteViews remoteViewsSmall = notification.contentView;
            remoteViewsSmall.setImageViewResource(R.id.notificationPause, R.drawable.ic_play_arrow_white);
            remoteViewsBig.setImageViewResource(R.id.notificationPause, R.drawable.ic_play_arrow_white);
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            notificationManagerCompat.notify(NOTIFY_ID, notification);
        }
    }
}
