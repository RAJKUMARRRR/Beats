package com.ccc.raj.beats;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

public class MoreRecordsActivity extends MediaControlBaseActivity {
    FrameLayout mediaContainer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onMusicServiceBind(MusicPlayService musicPlayService) {

    }

    @Override
    protected void setControllerAnchorView(MusicController musicController) {
        setContentView(R.layout.activity_more_records);
        mediaContainer = findViewById(R.id.media_container);
        musicController.setAnchorView(mediaContainer);
    }
}
