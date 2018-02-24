package com.ccc.raj.beats;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.widget.ImageButton;
import android.widget.Toast;

public class AudioOutputChangeReceiver extends BroadcastReceiver {
    ImageButton playpauseButton;
    ImageButton playpauseButtonTwo;
    public AudioOutputChangeReceiver(ImageButton playpauseButton,ImageButton playpauseButtonTwo){
        this.playpauseButton = playpauseButton;
        this.playpauseButtonTwo = playpauseButtonTwo;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        if(AudioManager.ACTION_AUDIO_BECOMING_NOISY.equalsIgnoreCase(intent.getAction())){
            MusicPlayService musicPlayService = MusicPlayServiceHolder.getMusicPlayService();
            if(musicPlayService != null){
                musicPlayService.pausePlayer();
            }
            if(playpauseButton != null){
                playpauseButton.setImageResource(R.drawable.ic_play);
            }
            if(playpauseButtonTwo != null){
                playpauseButtonTwo.setImageResource(R.drawable.ic_play_arrow);
            }
        }
    }
}
