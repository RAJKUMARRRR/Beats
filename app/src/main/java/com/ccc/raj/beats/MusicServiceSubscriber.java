package com.ccc.raj.beats;

import com.ccc.raj.beats.model.Song;

import java.util.ArrayList;

/**
 * Created by Raj on 1/29/2018.
 */

public interface MusicServiceSubscriber {
    public void updateActivePlayTrack(ArrayList<Song> songsList, int position);
}
