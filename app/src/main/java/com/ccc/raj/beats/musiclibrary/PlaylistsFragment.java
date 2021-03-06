package com.ccc.raj.beats.musiclibrary;


import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.ccc.raj.beats.AlbumListAdapter;
import com.ccc.raj.beats.AlbumSongsListActivity;
import com.ccc.raj.beats.MusicPlayService;
import com.ccc.raj.beats.MusicPlayServiceHolder;
import com.ccc.raj.beats.PlayListSelectionPopup;
import com.ccc.raj.beats.R;
import com.ccc.raj.beats.Utitlity;
import com.ccc.raj.beats.model.Album;
import com.ccc.raj.beats.model.OfflineAlbum;
import com.ccc.raj.beats.model.PlayListAlbum;
import com.ccc.raj.beats.model.PlayListTable;
import com.ccc.raj.beats.model.Song;
import com.ccc.raj.beats.model.SongTable;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlaylistsFragment extends Fragment implements PopupMenu.OnMenuItemClickListener {
    private ArrayList<Album> playList;
    RecyclerView playlistView;
    private int selectedAlbumPosition = -1;
    private AlbumListAdapter albumListAdapter;
    public PlaylistsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlists, container, false);
        playlistView = view.findViewById(R.id.playlistView);
        new AsyncPlaylistFetch().execute();
        /*playList = PlayListTable.getAllPlayLists(getContext());
        albumListAdapter = new AlbumListAdapter(playList,getContext());
        albumListAdapter.setOnItemClickListener(new AlbumListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                ArrayList<Song> songArrayList = PlayListTable.getSongsFromPlayLists(getContext(),playList.get(position).getAlbumId());
                Utitlity.Log(songArrayList.size()+"");
                Intent intent = new Intent(getContext(), AlbumSongsListActivity.class);
                Album album =  playList.get(position);
                intent.putExtra(AlbumSongsListActivity.COLUMN, PlayListTable.NAME);
                intent.putExtra(AlbumSongsListActivity.COLUMN_VALUE, album.getAlbumTitle());
                intent.putExtra(AlbumSongsListActivity.ALBUM_ID, album.getAlbumId());
                intent.putExtra(AlbumSongsListActivity.TITLE, album.getAlbumTitle());
                intent.putExtra(AlbumSongsListActivity.ALBUM_TYPE,AlbumSongsListActivity.PLAYLIST_ALBUM);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity(),playlistView.getChildAt(position).findViewById(R.id.imageSong),getString(R.string.image_transition));
                startActivity(intent,options.toBundle());
            }

            @Override
            public void onPlayButtonClick(int position) {

            }

            @Override
            public void onOptionsButtonClick(View view, int position) {
               showPlaylistOptionsMenu(view,position);
            }
        });
        playlistView.setAdapter(albumListAdapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),2);
        playlistView.setLayoutManager(gridLayoutManager);*/
        return view;
    }

    public void showPlaylistOptionsMenu(View view,int position){
        selectedAlbumPosition = position;
        PopupMenu popupMenu = new PopupMenu(getContext(),view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.popup_menu,popupMenu.getMenu());
        popupMenu.getMenu().removeItem(R.id.goto_artist);
        popupMenu.getMenu().removeItem(R.id.not_interested);
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
                onAddToPlayListClick();
                break;
            case R.id.delete:
                onDeleteOptionClick();
                break;
            case R.id.edit_playlist:
                onEditPlaylistClick();
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
    public void onAddToPlayListClick(){
        Album album =  playList.get(selectedAlbumPosition);
        ArrayList<Song> songs = SongTable.getSongsFromAlbum(getContext(),album.getAlbumTitle());
        new PlayListSelectionPopup(getContext(),songs).showPopup();
    }

    public void onPlayNextClick(){
        Album album =  playList.get(selectedAlbumPosition);
        ArrayList<Song> songs = PlayListTable.getSongsFromPlayLists(getContext(),album.getAlbumId());
        MusicPlayService musicPlayService = MusicPlayServiceHolder.getMusicPlayService();
        if(musicPlayService != null){
            musicPlayService.addToPlayNext(songs);
        }
    }
    public void onAddToQueueClick(){
        Album album =  playList.get(selectedAlbumPosition);
        ArrayList<Song> songs = PlayListTable.getSongsFromPlayLists(getContext(),album.getAlbumId());
        MusicPlayService musicPlayService = MusicPlayServiceHolder.getMusicPlayService();
        if(musicPlayService != null){
            musicPlayService.addToQueue(songs);
        }
    }

    public void onDeleteOptionClick(){
        final Album album =  playList.get(selectedAlbumPosition);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(getContext().getString(R.string.delete_message)+" "+album.getAlbumTitle()+"?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                boolean status = PlayListTable.deletePlayList(getContext(),album.getAlbumId());
                if(status){
                    Toast.makeText(getContext(),"Successfully Deleted",Toast.LENGTH_SHORT).show();
                    playList = PlayListTable.getAllPlayLists(getContext());
                    albumListAdapter.albumList = playList;
                    albumListAdapter.notifyDataSetChanged();
                }else{
                    Toast.makeText(getContext(),"Delete failed",Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton(getContext().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void onEditPlaylistClick(){
        final Album album =  playList.get(selectedAlbumPosition);
        View view = getLayoutInflater().inflate(R.layout.create_playlist_popup,null);
        final EditText editText = view.findViewById(R.id.textPlayList);
        editText.setText(album.getAlbumTitle());
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(view);
        builder.setTitle(getContext().getString(R.string.edit_playlist));
        builder.setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int j) {
                String name = editText.getText().toString();
                if(name!=null&&(!name.isEmpty())){
                    boolean status = PlayListTable.updatePlayList(getContext(),album.getAlbumId(),name);
                    if(status){
                        PlayListAlbum playListAlbum = (PlayListAlbum) album;
                        playListAlbum.setPlayListName(name);
                        albumListAdapter.albumList.set(selectedAlbumPosition,playListAlbum);
                        albumListAdapter.notifyItemChanged(selectedAlbumPosition);
                        Toast.makeText(getContext(),getString(R.string.playlist_updated),Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getContext(),getString(R.string.update_failed),Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        final AlertDialog dialog = builder.create();
        dialog.show();
    }

    class AsyncPlaylistFetch extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            playList = PlayListTable.getAllPlayLists(getContext());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (playList != null) {
                albumListAdapter = new AlbumListAdapter(playList, getContext());
                albumListAdapter.setOnItemClickListener(new AlbumListAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position,View view) {
                        ArrayList<Song> songArrayList = PlayListTable.getSongsFromPlayLists(getContext(), playList.get(position).getAlbumId());
                        Utitlity.Log(songArrayList.size() + "");
                        Intent intent = new Intent(getContext(), AlbumSongsListActivity.class);
                        Album album = playList.get(position);
                        intent.putExtra(AlbumSongsListActivity.COLUMN, PlayListTable.NAME);
                        intent.putExtra(AlbumSongsListActivity.COLUMN_VALUE, album.getAlbumTitle());
                        intent.putExtra(AlbumSongsListActivity.ALBUM_ID, album.getAlbumId());
                        intent.putExtra(AlbumSongsListActivity.TITLE, album.getAlbumTitle());
                        intent.putExtra(AlbumSongsListActivity.ALBUM_TYPE, AlbumSongsListActivity.PLAYLIST_ALBUM);
                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity(), view.findViewById(R.id.imageSong), getString(R.string.image_transition));
                        startActivity(intent, options.toBundle());
                    }

                    @Override
                    public void onPlayButtonClick(int position) {

                    }

                    @Override
                    public void onOptionsButtonClick(View view, int position) {
                        showPlaylistOptionsMenu(view, position);
                    }
                });
                playlistView.setAdapter(albumListAdapter);
                GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
                playlistView.setLayoutManager(gridLayoutManager);
            }
        }
    }
}
