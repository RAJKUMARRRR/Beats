package com.ccc.raj.beats.musiclibrary;


import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.ccc.raj.beats.MoreRecordsActivity;
import com.ccc.raj.beats.R;
import com.ccc.raj.beats.model.Album;
import com.ccc.raj.beats.model.AlbumTable;
import com.ccc.raj.beats.model.GenresTable;
import com.ccc.raj.beats.model.OfflineAlbum;
import com.ccc.raj.beats.model.OfflineDataProvider;
import com.ccc.raj.beats.model.PlayListTable;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class GenresFragment extends Fragment implements PopupMenu.OnMenuItemClickListener{

    RecyclerView genresListView;
    ArrayList<Album> genresAlbumList;
    private int selectedAlbumPosition = -1;
    public GenresFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_genres, container, false);
        genresListView = view.findViewById(R.id.genresListView);
        genresAlbumList = GenresTable.getAllGenresAlbums(getContext());
        AlbumListAdapter albumListAdapter = new AlbumListAdapter(genresAlbumList,getContext());
        albumListAdapter.setOnItemClickListener(new AlbumListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Album album = genresAlbumList.get(position);
                Intent intent = new Intent(getContext(), MoreRecordsActivity.class);
                intent.putExtra(MoreRecordsActivity.VIEW_TYPE,MoreRecordsActivity.GENRES_ALBUM);
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
                showAlbumOptionsMenu(view,position);
            }
        });
        genresListView.setAdapter(albumListAdapter);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(),2);
        genresListView.setLayoutManager(layoutManager);
        return view;
    }
    public void showAlbumOptionsMenu(View view, int position) {
        selectedAlbumPosition = position;
        PopupMenu popupMenu = new PopupMenu(getContext(), view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.popup_menu_empty, popupMenu.getMenu());
        popupMenu.getMenu().add(Menu.NONE,R.id.play_next,0,R.string.play_next);
        popupMenu.getMenu().add(Menu.NONE,R.id.add_to_queue,1,R.string.add_to_queue);
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(this);
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.play_next:
                Toast.makeText(getContext(), "play next", Toast.LENGTH_SHORT).show();
                break;
            case R.id.add_to_queue:
                break;
        }
        return false;
    }
}
