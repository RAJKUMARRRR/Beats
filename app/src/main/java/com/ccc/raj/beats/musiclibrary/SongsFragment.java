package com.ccc.raj.beats.musiclibrary;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ccc.raj.beats.MusicPlayService;
import com.ccc.raj.beats.MusicPlayServiceHolder;
import com.ccc.raj.beats.R;
import com.ccc.raj.beats.SongListAdapter;
import com.ccc.raj.beats.model.OfflineDataProvider;
import com.ccc.raj.beats.model.OfflineSong;
import com.ccc.raj.beats.model.Song;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class SongsFragment extends Fragment {

    RecyclerView songsListView;
    SongListAdapter songListAdapter;
    private MusicPlayService musicPlayService;
    public SongsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        musicPlayService = MusicPlayServiceHolder.getMusicPlayService();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_songs, container, false);
        songsListView = view.findViewById(R.id.songsListView);
        songListAdapter = new SongListAdapter(getContext(),OfflineDataProvider.getOfflineSongsList(getContext()),false);
        songListAdapter.setOnItemClickListener(new SongListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                //Toast.makeText(getContext(),"Implement this",Toast.LENGTH_SHORT).show();
                onSongPicked(position);
            }

            @Override
            public void onMoreButtonClick(View view, int position) {

            }
        });
        songsListView.setAdapter(songListAdapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),1);
        songsListView.setLayoutManager(gridLayoutManager);
        return view;
    }
    public void onSongPicked(int position){
        ArrayList<Song> songsAlbum = songListAdapter.songList;
        if(songsAlbum.size()>0) {
            musicPlayService.setOfflineSongsList(songsAlbum);
            musicPlayService.setOfflineSongPosition(position);
            musicPlayService.playOfflineSong();
        }
    }

}
