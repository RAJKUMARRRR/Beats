package com.ccc.raj.beats;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Formatter;
import java.util.Locale;

/**
 * Created by Raj on 1/26/2018.
 */

public class CustomMediaController {
    private MediaPlayerControl mControl;
    private ImageButton playpauseButton;
    private ImageButton playpauseButtonTwo;
    private ImageButton next;
    private ImageButton prev;
    private ImageButton thumbsUp;
    private ImageButton thumbsDown;
    private ImageView imageViewAlbum;
    private FrameLayout mainContainer;
    private AppCompatSeekBar progressBar;
    private TextView time;
    private TextView currentTime;


    private View.OnClickListener prevListenr;
    private View.OnClickListener nextListenr;
    private View mRoot;
    StringBuilder mFormatBuilder;
    Formatter mFormatter;
    private boolean mDragging;

    private Context context;


    public CustomMediaController(Context context) {
        this.context = context;
    }

    public void setPrevNextListeners(View.OnClickListener prevListener, View.OnClickListener nextListener) {
        this.prevListenr = prevListener;
        this.nextListenr = nextListener;
    }

    public void setMediaPlayer(MediaPlayerControl mediaPlayer) {
        this.mControl = mediaPlayer;
    }

    public void setEnabled(boolean enabled) {

    }

    public void show() {

    }

    public void hide(){

    }

    public void runTimer() {
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (mControl != null) {
                    if(mControl.isPlaying()) {
                        setProgress();
                        updatePausePlay();
                    }
                }
                handler.postDelayed(this, 1000);
            }
        });
    }

    public  void setAlbumArt(Bitmap bitmap){
        if(imageViewAlbum!=null){
           imageViewAlbum.setImageBitmap(bitmap);
        }
        if(mainContainer!=null){
            Drawable drawable = new BitmapDrawable(context.getResources(),bitmap);
            mainContainer.setBackground(drawable);
        }
    }

    public void setAnchorView(ViewGroup viewGroup) {
        LayoutInflater inflate = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRoot = inflate.inflate(R.layout.media_sliding_windows, null);
        viewGroup.addView(mRoot);
        initControllerView(mRoot);
        runTimer();
    }

    private void initControllerView(View v) {
        playpauseButton = (ImageButton) v.findViewById(R.id.pause);
        playpauseButtonTwo = v.findViewById(R.id.playpauseButtonTwo);
        if (playpauseButton != null) {
            playpauseButton.requestFocus();
            playpauseButton.setOnClickListener(mPauseListener);
        }
        if (playpauseButtonTwo != null) {
            playpauseButtonTwo.requestFocus();
            playpauseButtonTwo.setOnClickListener(mPauseListener);
        }


        next = (ImageButton) v.findViewById(R.id.next);
        prev = (ImageButton) v.findViewById(R.id.prev);

        progressBar = (AppCompatSeekBar) v.findViewById(R.id.mediacontroller_progress);
        if (progressBar != null) {
            if (progressBar instanceof AppCompatSeekBar) {
                SeekBar seeker = (SeekBar) progressBar;
                seeker.setOnSeekBarChangeListener(mSeekListener);
            }
            progressBar.setMax(1000);
        }

        time = (TextView) v.findViewById(R.id.time);
        currentTime = (TextView) v.findViewById(R.id.time_current);
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());

        imageViewAlbum = v.findViewById(R.id.imageViewAlbum);
        mainContainer = v.findViewById(R.id.mainMediaContainer);
        installPrevNextListeners();

    }


    private void installPrevNextListeners() {
        if (next != null) {
            next.setOnClickListener(nextListenr);
            next.setEnabled(nextListenr != null);
        }

        if (prev != null) {
            prev.setOnClickListener(prevListenr);
            prev.setEnabled(prevListenr != null);
        }
    }

    private SeekBar.OnSeekBarChangeListener mSeekListener = new SeekBar.OnSeekBarChangeListener() {
        public void onStartTrackingTouch(SeekBar bar) {
            mDragging = true;
            //mHandler.removeMessages(SHOW_PROGRESS);
        }

        public void onProgressChanged(SeekBar bar, int progress, boolean fromuser) {
            if (!fromuser) {
                return;
            }

            long duration = mControl.getDuration();
            long newposition = (duration * progress) / 1000L;
            mControl.seekTo((int) newposition);
            if (currentTime != null)
                currentTime.setText(stringForTime((int) newposition));
        }

        public void onStopTrackingTouch(SeekBar bar) {
            mDragging = false;
            setProgress();
            //updatePausePlay();
            //mHandler.sendEmptyMessage(SHOW_PROGRESS);
        }
    };
    private View.OnClickListener mPauseListener = new View.OnClickListener() {
        public void onClick(View v) {
            doPauseResume();
        }
    };

    private void doPauseResume() {
        if (mControl.isPlaying()) {
            mControl.pause();
        } else {
            mControl.start();
        }
        updatePausePlay();
    }

    public void updatePausePlay() {
        if (mRoot == null || playpauseButton == null) {
            return;
        }
        if (mControl.isPlaying()) {
            playpauseButton.setImageResource(R.drawable.ic_pause);
            playpauseButtonTwo.setImageResource(R.drawable.ic_pause_arrow);
        } else {
            playpauseButton.setImageResource(R.drawable.ic_play);
            playpauseButtonTwo.setImageResource(R.drawable.ic_play_arrow);
        }
    }

    private int setProgress() {
        if (mDragging) {
            return 0;
        }

        int position = mControl.getCurrentPosition();
        int duration = mControl.getDuration();
        if (progressBar != null) {
            if (duration > 0) {
                // use long to avoid overflow
                long pos = 1000L * position / duration;
                progressBar.setProgress((int) pos);
            }
            int percent = mControl.getBufferPercentage();
            progressBar.setSecondaryProgress(percent * 10);
        }

        if (time != null)
            time.setText(stringForTime(duration));
        if (currentTime != null)
            currentTime.setText(stringForTime(position));

        return position;
    }

    private String stringForTime(int timeMs) {
        int totalSeconds = timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }


    public static interface MediaPlayerControl {
        public void start();

        public void pause();

        public int getDuration();

        public int getCurrentPosition();

        public void seekTo(int i);

        public boolean isPlaying();

        public int getBufferPercentage();

        public boolean canPause();

        public boolean canSeekBackward();

        public boolean canSeekForward();

        public int getAudioSessionId();
    }
}
