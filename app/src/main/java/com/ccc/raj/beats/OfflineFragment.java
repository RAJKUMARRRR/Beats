package com.ccc.raj.beats;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ccc.raj.beats.model.Album;
import com.ccc.raj.beats.model.OfflineDataProvider;
import com.ccc.raj.beats.model.Song;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class OfflineFragment extends Fragment {
    ArrayList<Album> mAlbumArrayList;
    RecyclerView songsListView;
    Context context;
    MusicPlayService musicPlayService;
    private static String permissionString = Manifest.permission.READ_EXTERNAL_STORAGE;
    private static final int REQUEST_CODE = 9867;
    public OfflineFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_offline, container, false);
        songsListView = view.findViewById(R.id.offlineTrackListView);
        if(checkPermission()){
            mAlbumArrayList = OfflineDataProvider.getOfflineAlbums(context);
            AlbumListAdapter albumListAdapter = new AlbumListAdapter(mAlbumArrayList,context);
            albumListAdapter.setOnItemClickListener(new AlbumListAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    Intent intent = new Intent(getContext(),AlbumSongsListActivity.class);
                    Album album = mAlbumArrayList.get(position);
                    intent.putExtra(AlbumSongsListActivity.ALBUM,album.getAlbumTitle());
                    intent.putExtra(AlbumSongsListActivity.ALBUM_PATH,album.getAlbumPath());
                    startActivity(intent);
                }

                @Override
                public void onPlayButtonClick(int position) {
                    ArrayList<Song> songsAlbum = OfflineDataProvider.getSongsFromAlbum(context,mAlbumArrayList.get(position).getAlbumTitle(),mAlbumArrayList.get(0).getAlbumPath());
                    if(songsAlbum.size()>0) {
                        musicPlayService.setOfflineSongsList(songsAlbum);
                        musicPlayService.setOfflineSongPosition(0);
                        musicPlayService.playOfflineSong();
                    }
                }
            });
            songsListView.setAdapter(albumListAdapter);
            /*songsListView.setItemViewCacheSize(1000);
            songsListView.setDrawingCacheEnabled(true);
            songsListView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
            songsListView.setHasFixedSize(true);*/
            GridLayoutManager layoutManager = new GridLayoutManager(getActivity(),2);
            songsListView.setLayoutManager(layoutManager);
        }else{
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    permissionString)) {
                Toast.makeText(context,"Permissin needed to store image",Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{permissionString},
                        REQUEST_CODE);
            }
        }
        return view;
    }


    public void setMusicPlayService(MusicPlayService musicPlayService){
        this.musicPlayService = musicPlayService;
        this.musicPlayService.setOfflineSongsList(OfflineDataProvider.getSongsFromAlbum(context,mAlbumArrayList.get(0).getAlbumTitle(),mAlbumArrayList.get(0).getAlbumPath()));
        //this.musicPlayService.playOfflineSong();
    }



    public boolean checkPermission(){
        if(ContextCompat.checkSelfPermission(context,permissionString) != PackageManager.PERMISSION_GRANTED){
           return false;
        }else{
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mAlbumArrayList = OfflineDataProvider.getOfflineAlbums(context);
                    AlbumListAdapter albumListAdapter = new AlbumListAdapter(mAlbumArrayList,context);
                    albumListAdapter.setOnItemClickListener(new AlbumListAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            Intent intent = new Intent(getContext(),AlbumSongsListActivity.class);
                            Album album = mAlbumArrayList.get(position);
                            intent.putExtra(AlbumSongsListActivity.ALBUM,album.getAlbumTitle());
                            intent.putExtra(AlbumSongsListActivity.ALBUM_PATH,album.getAlbumPath());
                            startActivity(intent);
                        }

                        @Override
                        public void onPlayButtonClick(int position) {
                            ArrayList<Song> songsAlbum = OfflineDataProvider.getSongsFromAlbum(context,mAlbumArrayList.get(position).getAlbumTitle(),mAlbumArrayList.get(0).getAlbumPath());
                            if(songsAlbum.size()>0) {
                                musicPlayService.setOfflineSongsList(songsAlbum);
                                musicPlayService.setOfflineSongPosition(0);
                                musicPlayService.playOfflineSong();
                            }
                        }
                    });
                    songsListView.setAdapter(albumListAdapter);
                    GridLayoutManager layoutManager = new GridLayoutManager(getActivity(),2);
                    songsListView.setLayoutManager(layoutManager);
                } else {
                    Toast.makeText(context,"Permissin denied,can't download image",Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }
}
