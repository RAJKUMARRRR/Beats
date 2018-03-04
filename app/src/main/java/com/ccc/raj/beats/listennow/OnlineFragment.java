package com.ccc.raj.beats.listennow;


import android.Manifest;
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

import com.ccc.raj.beats.R;
import com.ccc.raj.beats.SongListAdapter;
import com.ccc.raj.beats.model.Song;
import com.ccc.raj.beats.model.sqlite.DatabaseHelper;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class OnlineFragment extends Fragment {
    private RecyclerView frequentSongList;
    private ArrayList<Song> frequentSongs;
    private SongListAdapter songListAdapter;
    private static String writePermissionString = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private static final int REQUEST_CODE = 123;
    public OnlineFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_online, container, false);
        frequentSongList = view.findViewById(R.id.frequentSongList);
        if (checkPermission()) {
            setFrequentSongList();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    writePermissionString)) {
                Toast.makeText(getContext(), "Permissin needed to store data", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{writePermissionString},
                        REQUEST_CODE);
            }
        }
        return view;
    }

    private void setFrequentSongList(){
        frequentSongs = DatabaseHelper.getDatabaseHelper(getContext()).getFrequentlyPlayedSongs(-1);
        songListAdapter = new SongListAdapter(getContext(), frequentSongs, false);
        songListAdapter.setOnItemClickListener(new SongListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                //onSongPicked(position);
            }

            @Override
            public void onMoreButtonClick(View view, int position) {
                //showSongOptionsMenu(view, position);
            }
        });
        frequentSongList.setAdapter(songListAdapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1);
        frequentSongList.setLayoutManager(gridLayoutManager);
    }

    public boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(getContext(), writePermissionString) != PackageManager.PERMISSION_GRANTED) {
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
                    setFrequentSongList();
                    Toast.makeText(getContext(), "Permissin granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Permissin denied,can't download image", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }
}
