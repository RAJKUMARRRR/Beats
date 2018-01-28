package com.ccc.raj.beats.musiclibrary;


import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ccc.raj.beats.AlbumListAdapter;
import com.ccc.raj.beats.AlbumSongsListActivity;
import com.ccc.raj.beats.R;
import com.ccc.raj.beats.model.Album;
import com.ccc.raj.beats.model.AlbumTable;
import com.ccc.raj.beats.model.OfflineAlbum;
import com.ccc.raj.beats.model.OfflineDataProvider;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class GenresFragment extends Fragment {

    RecyclerView genresListView;
    ArrayList<Album> mAlbumArrayList;
    public GenresFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_genres, container, false);
        genresListView = view.findViewById(R.id.genresListView);
        mAlbumArrayList = AlbumTable.getAlbumsGroupByArtist(getContext());
        AlbumListAdapter albumListAdapter = new AlbumListAdapter(mAlbumArrayList,getContext());
        albumListAdapter.setOnItemClickListener(new AlbumListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(getContext(),AlbumSongsListActivity.class);
                OfflineAlbum album = (OfflineAlbum) mAlbumArrayList.get(position);
                intent.putExtra(AlbumSongsListActivity.COLUMN, AlbumTable.ARTIST);
                intent.putExtra(AlbumSongsListActivity.COLUMN_VALUE,album.getArtist());
                intent.putExtra(AlbumSongsListActivity.ALBUM_ID,album.getAlbumId());
                intent.putExtra(AlbumSongsListActivity.TITLE,album.getArtist());
                startActivity(intent);
            }

            @Override
            public void onPlayButtonClick(int position) {
                //ArrayList<Song> songsAlbum = OfflineDataProvider.getSongsFromAlbum(context,mAlbumArrayList.get(position).getAlbumTitle(),mAlbumArrayList.get(0).getAlbumPath());
                //if(songsAlbum.size()>0) {
                //musicPlayService.setOfflineSongsList(songsAlbum);
                //musicPlayService.setOfflineSongPosition(0);
                //musicPlayService.playOfflineSong();
                //}
            }

            @Override
            public void onOptionsButtonClick(View view, int position) {

            }
        });
        genresListView.setAdapter(albumListAdapter);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(),2);
        genresListView.setLayoutManager(layoutManager);
        return view;
    }
}
