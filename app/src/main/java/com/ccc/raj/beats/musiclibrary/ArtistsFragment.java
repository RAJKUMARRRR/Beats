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
import com.ccc.raj.beats.MoreRecordsActivity;
import com.ccc.raj.beats.R;
import com.ccc.raj.beats.model.Album;
import com.ccc.raj.beats.model.AlbumTable;
import com.ccc.raj.beats.model.ArtistTable;
import com.ccc.raj.beats.model.OfflineAlbum;
import com.ccc.raj.beats.model.OfflineDataProvider;
import com.ccc.raj.beats.model.Song;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ArtistsFragment extends Fragment {
    ArrayList<Album> mAlbumArrayList;
    RecyclerView artistsListView;
    public ArtistsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_artists, container, false);
        artistsListView = view.findViewById(R.id.artistsListView);
        mAlbumArrayList = ArtistTable.getAllArtistAlbums(getContext());//AlbumTable.getAlbumsGroupByArtist(getContext());
        AlbumListAdapter albumListAdapter = new AlbumListAdapter(mAlbumArrayList,getContext());
        albumListAdapter.setOnItemClickListener(new AlbumListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Album album = mAlbumArrayList.get(position);
                /*Intent intent = new Intent(getContext(),AlbumSongsListActivity.class);
                intent.putExtra(AlbumSongsListActivity.COLUMN, AlbumTable.ARTIST);
                intent.putExtra(AlbumSongsListActivity.COLUMN_VALUE,album.getAlbumTitle());
                intent.putExtra(AlbumSongsListActivity.ALBUM_ID,album.getAlbumId());
                intent.putExtra(AlbumSongsListActivity.TITLE,album.getAlbumTitle());*/
                Intent intent = new Intent(getContext(), MoreRecordsActivity.class);
                intent.putExtra(MoreRecordsActivity.VIEW_TYPE,MoreRecordsActivity.ARTIST_ALBUM);
                intent.putExtra(MoreRecordsActivity.SEARCH_QUERY,String.valueOf(album.getAlbumId()));
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
        artistsListView.setAdapter(albumListAdapter);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(),2);
        artistsListView.setLayoutManager(layoutManager);
        return view;
    }

}
