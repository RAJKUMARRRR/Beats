package com.ccc.raj.beats.musiclibrary;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.ccc.raj.beats.AlbumListAdapter;
import com.ccc.raj.beats.AlbumSongsListActivity;
import com.ccc.raj.beats.PlayListSelectionPopup;
import com.ccc.raj.beats.R;
import com.ccc.raj.beats.Utitlity;
import com.ccc.raj.beats.model.Album;
import com.ccc.raj.beats.model.AlbumTable;
import com.ccc.raj.beats.model.MediaTables;
import com.ccc.raj.beats.model.OfflineAlbum;
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
        playList = PlayListTable.getAllPlayLists(getContext());
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
                startActivity(intent);
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
        playlistView.setLayoutManager(gridLayoutManager);
        return view;
    }

    public void showPlaylistOptionsMenu(View view,int position){
        selectedAlbumPosition = position;
        PopupMenu popupMenu = new PopupMenu(getContext(),view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.popup_menu,popupMenu.getMenu());
        popupMenu.getMenu().removeItem(R.id.goto_artist);
        popupMenu.getMenu().removeItem(R.id.not_interested);
        //popupMenu.getMenu().add(Menu.NONE,R.id.edit_playlist,5,R.string.edit_playlist);
        //popupMenu.getMenu().add(Menu.NONE,R.id.delete,6,R.string.delete);
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
        }
        return false;
    }
    public void onAddToPlayListClick(){
        Album album =  playList.get(selectedAlbumPosition);
        ArrayList<Song> songs = SongTable.getSongsFromAlbum(getContext(),album.getAlbumTitle());
        new PlayListSelectionPopup(getContext(),songs).showPopup();
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
}
