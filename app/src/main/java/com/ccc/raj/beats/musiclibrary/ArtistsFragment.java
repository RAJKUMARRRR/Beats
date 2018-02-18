package com.ccc.raj.beats.musiclibrary;


import android.app.ActivityOptions;
import android.content.Intent;
import android.os.AsyncTask;
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
        new AsyncArtistsFetch().execute();
        /*mAlbumArrayList = ArtistTable.getAllArtistAlbums(getContext());
        AlbumListAdapter albumListAdapter = new AlbumListAdapter(mAlbumArrayList,getContext());
        albumListAdapter.setOnItemClickListener(new AlbumListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Album album = mAlbumArrayList.get(position);
                Intent intent = new Intent(getContext(), MoreRecordsActivity.class);
                intent.putExtra(MoreRecordsActivity.VIEW_TYPE,MoreRecordsActivity.ARTIST_ALBUM);
                intent.putExtra(MoreRecordsActivity.SEARCH_QUERY,String.valueOf(album.getAlbumId()));
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity());
                startActivity(intent,options.toBundle());
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
        */
        return view;
    }

    class AsyncArtistsFetch extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            mAlbumArrayList = ArtistTable.getAllArtistAlbums(getContext());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(mAlbumArrayList != null) {
                AlbumListAdapter albumListAdapter = new AlbumListAdapter(mAlbumArrayList, getContext());
                albumListAdapter.setOnItemClickListener(new AlbumListAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position,View view) {
                        Album album = mAlbumArrayList.get(position);
                        Intent intent = new Intent(getContext(), MoreRecordsActivity.class);
                        intent.putExtra(MoreRecordsActivity.VIEW_TYPE, MoreRecordsActivity.ARTIST_ALBUM);
                        intent.putExtra(MoreRecordsActivity.SEARCH_QUERY, String.valueOf(album.getAlbumId()));
                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity());
                        startActivity(intent, options.toBundle());
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
                GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
                artistsListView.setLayoutManager(layoutManager);
            }
        }
    }
}
