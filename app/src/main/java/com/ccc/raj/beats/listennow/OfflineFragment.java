package com.ccc.raj.beats.listennow;


import android.Manifest;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.ccc.raj.beats.AlbumListAdapter;
import com.ccc.raj.beats.AlbumSongsListActivity;
import com.ccc.raj.beats.MainActivity;
import com.ccc.raj.beats.MoreRecordsActivity;
import com.ccc.raj.beats.MusicPlayService;
import com.ccc.raj.beats.MusicPlayServiceHolder;
import com.ccc.raj.beats.PlayListSelectionPopup;
import com.ccc.raj.beats.R;
import com.ccc.raj.beats.model.Album;
import com.ccc.raj.beats.model.AlbumTable;
import com.ccc.raj.beats.model.ArtistTable;
import com.ccc.raj.beats.model.OfflineAlbum;
import com.ccc.raj.beats.model.OfflineDataProvider;
import com.ccc.raj.beats.model.Song;
import com.ccc.raj.beats.model.SongTable;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class OfflineFragment extends Fragment implements PopupMenu.OnMenuItemClickListener {
    ArrayList<Album> mAlbumArrayList = null;
    RecyclerView songsListView;
    Context context;
    MusicPlayService musicPlayService;
    private static String permissionString = Manifest.permission.READ_EXTERNAL_STORAGE;
    private static final int REQUEST_CODE = 9867;
    private int selectedAlbumPosition = -1;

    public OfflineFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onStart() {
        super.onStart();
        musicPlayService = MusicPlayServiceHolder.getMusicPlayService();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_offline, container, false);
        songsListView = view.findViewById(R.id.offlineTrackListView);
        if (checkPermission()) {
            setMusicAlbumData();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    permissionString)) {
                Toast.makeText(context, "Permissin needed to store image", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{permissionString},
                        REQUEST_CODE);
            }
        }
        return view;
    }

    private void setMusicAlbumData() {
        mAlbumArrayList = AlbumTable.getAllAlbums(context);
        final AlbumListAdapter albumListAdapter = new AlbumListAdapter(mAlbumArrayList, context);
        albumListAdapter.setOnItemClickListener(new AlbumListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position,View view) {
                Intent intent = new Intent(getContext(), AlbumSongsListActivity.class);
                OfflineAlbum album = (OfflineAlbum) mAlbumArrayList.get(position);
                intent.putExtra(AlbumSongsListActivity.COLUMN, AlbumTable.ALBUM);
                intent.putExtra(AlbumSongsListActivity.COLUMN_VALUE, album.getAlbumTitle());
                intent.putExtra(AlbumSongsListActivity.ALBUM_ID, album.getAlbumId());
                intent.putExtra(AlbumSongsListActivity.TITLE, album.getAlbumTitle());
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity(),view.findViewById(R.id.imageSong),getString(R.string.image_transition));
                startActivity(intent,options.toBundle());
            }

            @Override
            public void onPlayButtonClick(int position) {
                OfflineAlbum album = (OfflineAlbum) mAlbumArrayList.get(position);
                ArrayList<Song> songsAlbum = SongTable.getSongsFromAlbum(context,album.getAlbumTitle());
                if (songsAlbum.size() > 0) {
                    musicPlayService.setOfflineSongsList(songsAlbum);
                    musicPlayService.setOfflineSongPosition(0);
                    musicPlayService.playOfflineSong();
                }
            }

            @Override
            public void onOptionsButtonClick(View view, int position) {
                showAlbumOptionsMenu(view, position);
            }
        });
        songsListView.setAdapter(albumListAdapter);
            /*songsListView.setItemViewCacheSize(1000);
            songsListView.setDrawingCacheEnabled(true);
            songsListView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
            songsListView.setHasFixedSize(true);*/
        GridLayoutManager layoutManager;
        if(getParentFragment() instanceof ListenNowFragment){
            layoutManager = new GridLayoutManager(getActivity(), 3);
        }else{
            layoutManager = new GridLayoutManager(getActivity(), 2);
        }
        songsListView.setLayoutManager(layoutManager);
    }

    public void showAlbumOptionsMenu(View view, int position) {
        selectedAlbumPosition = position;
        PopupMenu popupMenu = new PopupMenu(getContext(), view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.popup_menu, popupMenu.getMenu());
        popupMenu.getMenu().removeItem(R.id.not_interested);
        popupMenu.getMenu().removeItem(R.id.edit_playlist);
        popupMenu.getMenu().removeItem(R.id.delete);
        popupMenu.getMenu().removeItem(R.id.go_to_album);
        popupMenu.getMenu().removeItem(R.id.remove_from_playlist);
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(this);
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.shuffle:
                Toast.makeText(getContext(), "shuffle", Toast.LENGTH_SHORT).show();
                break;
            case R.id.add_to_playlist:
                onAddToPlayListClick(selectedAlbumPosition);
                break;
            case R.id.goto_artist:
                onGotoArtistClick();
                break;
            case R.id.play_next:
                onPlayNextClick();
                break;
            case R.id.add_to_queue:
                onAddToQueueClick();
                break;
        }
        return false;
    }
    public void onAddToPlayListClick(int position){
        Album album =  mAlbumArrayList.get(position);
        ArrayList<Song> songs = SongTable.getSongsFromAlbum(getContext(),album.getAlbumTitle());
        new PlayListSelectionPopup(getContext(),songs).showPopup();
    }

    public void onGotoArtistClick(){
        OfflineAlbum album = (OfflineAlbum) mAlbumArrayList.get(selectedAlbumPosition);
        Intent intent = new Intent(getContext(), MoreRecordsActivity.class);
        intent.putExtra(MoreRecordsActivity.VIEW_TYPE, MoreRecordsActivity.ARTIST_ALBUM);
        intent.putExtra(MoreRecordsActivity.SEARCH_QUERY, String.valueOf(ArtistTable.getArtistIdByArtist(getContext(),album.getArtist())));
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity());
        startActivity(intent, options.toBundle());
    }

    public void onPlayNextClick(){
        OfflineAlbum album = (OfflineAlbum) mAlbumArrayList.get(selectedAlbumPosition);
        ArrayList<Song> songs = SongTable.getSongsFromAlbum(getContext(),album.getAlbumTitle());
        MusicPlayService musicPlayService = MusicPlayServiceHolder.getMusicPlayService();
        if(musicPlayService != null){
            musicPlayService.addToPlayNext(songs);
        }
    }
    public void onAddToQueueClick(){
        OfflineAlbum album = (OfflineAlbum) mAlbumArrayList.get(selectedAlbumPosition);
        ArrayList<Song> songs = SongTable.getSongsFromAlbum(getContext(),album.getAlbumTitle());
        MusicPlayService musicPlayService = MusicPlayServiceHolder.getMusicPlayService();
        if(musicPlayService != null){
            musicPlayService.addToQueue(songs);
        }
    }

    public void setMusicPlayService(MusicPlayService musicPlayService) {
        this.musicPlayService = musicPlayService;
        if (mAlbumArrayList != null) {
            OfflineAlbum album = (OfflineAlbum) mAlbumArrayList.get(0);
            this.musicPlayService.setOfflineSongsList(SongTable.getSongsFromAlbum(context, album.getAlbumTitle()));
            //this.musicPlayService.playOfflineSong();
        }
    }


    public boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(context, permissionString) != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else {
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
                    setMusicAlbumData();
                } else {
                    Toast.makeText(context, "Permissin denied,can't download image", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }
}
