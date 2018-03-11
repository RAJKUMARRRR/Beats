package com.ccc.raj.beats.listennow;


import android.Manifest;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
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
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
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
import com.ccc.raj.beats.model.ArtistAlbum;
import com.ccc.raj.beats.model.ArtistTable;
import com.ccc.raj.beats.model.GenresTable;
import com.ccc.raj.beats.model.OfflineAlbum;
import com.ccc.raj.beats.model.OfflineDataProvider;
import com.ccc.raj.beats.model.PlayListTable;
import com.ccc.raj.beats.model.Song;
import com.ccc.raj.beats.model.SongTable;
import com.ccc.raj.beats.model.localstorage.SessionStorageManager;
import com.ccc.raj.beats.model.sqlite.DatabaseHelper;
import com.ccc.raj.beats.searchresult.SearchRecord;

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
                requestPermissions(
                        new String[]{permissionString},
                        REQUEST_CODE);

            }
        }
        if(getParentFragment() instanceof ListenNowFragment){
            TextView buttonMore = (TextView) view.findViewById(R.id.buttomMore);
            buttonMore.setVisibility(View.VISIBLE);
            buttonMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), MoreRecordsActivity.class);
                    intent.putExtra(MoreRecordsActivity.VIEW_TYPE, MoreRecordsActivity.ALBUM);
                    intent.putExtra(MoreRecordsActivity.SEARCH_QUERY, "*");
                    startActivity(intent);
                }
            });
        }else{
            view.findViewById(R.id.buttomMore).setVisibility(View.GONE);
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(checkPermission()) {
            DatabaseHelper databaseHelper = DatabaseHelper.getDatabaseHelper(getContext());
            if (getParentFragment() instanceof ListenNowFragment) {
                mAlbumArrayList = databaseHelper.getRecentAlbums(6);
            }
            AlbumListAdapter albumListAdapter = (AlbumListAdapter) songsListView.getAdapter();
            albumListAdapter.albumList = mAlbumArrayList;
            albumListAdapter.notifyDataSetChanged();
        }
    }

    private void setMusicAlbumData() {
        new AlbumAsync().execute();
        DatabaseHelper databaseHelper = DatabaseHelper.getDatabaseHelper(getContext());
        if(getParentFragment() instanceof ListenNowFragment){
            ArrayList<Album> list = databaseHelper.getRecentAlbums(6);//AlbumTable.getAllAlbums(context);
            mAlbumArrayList = list; /*new ArrayList<>();*/
            /*if(list.size()>5) {
                for (int i = 0; i < 6; i++) {
                    mAlbumArrayList.add(list.get(i));
                }
            }else{
                mAlbumArrayList.addAll(list);
            }*/
        }else{
            mAlbumArrayList = AlbumTable.getAllAlbums(context);
        }
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
                    musicPlayService.setActiveAlbumDetails(album.getAlbumId(),AlbumSongsListActivity.OFFLINE_ALBUM,album.getAlbumTitle());
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
        int albumId = SessionStorageManager.getStoredAlbumId(getContext());
        if(albumId >0){
            ArrayList<Song> songs = new ArrayList<>();
            int albumType = SessionStorageManager.getStoredAlbumType(getContext());
            String albumTitle = SessionStorageManager.getStoredAlbumTitle(getContext());
            switch (albumType){
                case AlbumSongsListActivity.OFFLINE_ALBUM:
                    OfflineAlbum album = (OfflineAlbum) AlbumTable.getAlbumsByAlbumId(getContext(),albumId);
                    songs = SongTable.getSongsFromAlbum(context, album.getAlbumTitle());
                    break;
                case AlbumSongsListActivity.PLAYLIST_ALBUM:
                    songs = PlayListTable.getSongsFromPlayLists(getContext(),albumId);
                    break;
                case AlbumSongsListActivity.ARTIST_ALBUM:
                    ArtistAlbum artistAlbum = (ArtistAlbum) ArtistTable.getArtistAlbumForArtistId(getContext(),albumId);
                    songs = SongTable.getSongsFromColumn(getContext(), SongTable.ARTIST_ID, artistAlbum.getAlbumId()+"");
                    break;
                case AlbumSongsListActivity.GENRES_ALBUM:
                    songs = GenresTable.getSongsFromGeneres(getContext(),albumId);
                    break;
                default:
                    songs = SongTable.getAllSongs(getContext());
            }
            this.musicPlayService.setOfflineSongsList(songs);
            int songPos = SessionStorageManager.getStoredSongPosition(getContext());
            int bookmark = SessionStorageManager.getStoredBookmark(getContext());
            Toast.makeText(getContext(),"Bookmark:"+bookmark+",AlbumId:"+albumId+",songPos:"+songPos,Toast.LENGTH_SHORT).show();
            if(songPos >=0){
                this.musicPlayService.setOfflineSongPosition(songPos);
                this.musicPlayService.setActiveAlbumDetails(albumId,albumType,albumTitle);
                this.musicPlayService.restoreOldPlaySession(bookmark);
            }
        }else{
            if (mAlbumArrayList != null && mAlbumArrayList.size()>0) {
                OfflineAlbum album = (OfflineAlbum) mAlbumArrayList.get(0);
                this.musicPlayService.setOfflineSongsList(SongTable.getSongsFromAlbum(context, album.getAlbumTitle()));
                this.musicPlayService.setActiveAlbumDetails(album.getAlbumId(),AlbumSongsListActivity.OFFLINE_ALBUM, album.getAlbumTitle());
            }
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

    public class AlbumAsync extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            ArrayList<Album> albums = AlbumTable.getAllAlbums(getContext());
            DatabaseHelper databaseHelper = DatabaseHelper.getDatabaseHelper(getContext());
            for(Album album : albums){
                databaseHelper.addRecentAlbum(getContext(),album.getAlbumId(),false);
            }
            return null;
        }
    }
}
