package com.ccc.raj.beats.musiclibrary;


import android.app.ActivityOptions;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.ccc.raj.beats.MoreRecordsActivity;
import com.ccc.raj.beats.MusicPlayService;
import com.ccc.raj.beats.MusicPlayServiceHolder;
import com.ccc.raj.beats.PlayListSelectionPopup;
import com.ccc.raj.beats.R;
import com.ccc.raj.beats.SongListAdapter;
import com.ccc.raj.beats.model.OfflineDataProvider;
import com.ccc.raj.beats.model.OfflineSong;
import com.ccc.raj.beats.model.Song;
import com.ccc.raj.beats.model.SongTable;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class SongsFragment extends Fragment implements PopupMenu.OnMenuItemClickListener{

    RecyclerView songsListView;
    SongListAdapter songListAdapter;
    private MusicPlayService musicPlayService;
    private int selectedSongPosition=-1;
    private ArrayList<Song> songsList;
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
        new AsyncSongsFetch().execute();
        /*songsList = SongTable.getAllSongs(getContext());
        songListAdapter = new SongListAdapter(getContext(),songsList ,false);
        songListAdapter.setOnItemClickListener(new SongListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                onSongPicked(position);
            }

            @Override
            public void onMoreButtonClick(View view, int position) {
                showSongOptionsMenu(view, position);
            }
        });
        songsListView.setAdapter(songListAdapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),1);
        songsListView.setLayoutManager(gridLayoutManager);
        */
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
    public void showSongOptionsMenu(View view,int position){
        selectedSongPosition = position;
        PopupMenu popupMenu = new PopupMenu(getContext(),view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.popup_menu,popupMenu.getMenu());
        popupMenu.getMenu().removeItem(R.id.not_interested);
        popupMenu.getMenu().removeItem(R.id.remove_from_playlist);
        popupMenu.getMenu().removeItem(R.id.edit_playlist);
        popupMenu.getMenu().removeItem(R.id.shuffle);
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(this);
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.play_next:
                Toast.makeText(getContext(), "play next", Toast.LENGTH_SHORT).show();
                break;
            case R.id.add_to_playlist:
                onAddToPlayListClick(selectedSongPosition);
                break;
            case R.id.goto_artist:
                onGotoArtistClick();
                break;
        }
        return false;
    }

    public void onAddToPlayListClick(int position){
        ArrayList<Song> songs = new ArrayList<>();
        songs.add(songsList.get(position));
        new PlayListSelectionPopup(getContext(),songs).showPopup();
    }

    public void onGotoArtistClick(){
        OfflineSong song = (OfflineSong) songsList.get(selectedSongPosition);
        Intent intent = new Intent(getContext(), MoreRecordsActivity.class);
        intent.putExtra(MoreRecordsActivity.VIEW_TYPE, MoreRecordsActivity.ARTIST_ALBUM);
        intent.putExtra(MoreRecordsActivity.SEARCH_QUERY, String.valueOf(song.getArtistId()));
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity());
        startActivity(intent, options.toBundle());
    }

    class AsyncSongsFetch extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            songsList = SongTable.getAllSongs(getContext());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(songsList != null) {
                songListAdapter = new SongListAdapter(getContext(), songsList, false);
                songListAdapter.setOnItemClickListener(new SongListAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        onSongPicked(position);
                    }

                    @Override
                    public void onMoreButtonClick(View view, int position) {
                        showSongOptionsMenu(view, position);
                    }
                });
                songsListView.setAdapter(songListAdapter);
                GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1);
                songsListView.setLayoutManager(gridLayoutManager);
            }
        }
    }

}
