package com.ccc.raj.beats;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.ListPopupWindow;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ccc.raj.beats.model.Album;
import com.ccc.raj.beats.model.PlayListTable;
import com.ccc.raj.beats.model.Song;

import java.util.ArrayList;

/**
 * Created by Raj on 1/30/2018.
 */

public class PlayListSelectionPopup {
    private Context context;
    private ArrayList<Album> listAlbum;
    private LayoutInflater mLayoutInflater;
    private  ArrayList<Song> songs;
    public PlayListSelectionPopup(Context context,ArrayList<Song> songs){
        this.context = context;
        this.listAlbum = PlayListTable.getAllPlayLists(context);
        this.songs = songs;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    private void showCreatePlayListPopup(){
        View view = mLayoutInflater.inflate(R.layout.create_playlist_popup,null);
        final EditText editText = view.findViewById(R.id.textPlayList);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        builder.setMessage(context.getString(R.string.new_playlist));
        builder.setPositiveButton(R.string.create_playlist, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int j) {
                Toast.makeText(context,editText.getText().toString(),Toast.LENGTH_SHORT).show();
                String name = editText.getText().toString();
                if(name!=null&&(!name.isEmpty())){
                    int playListId = PlayListTable.createPlayList(context,name);
                    addSongsToPlaylist(playListId);
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


    public void showPopup(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = mLayoutInflater.inflate(R.layout.custom_dialogue,null);
        builder.setView(view);
        builder.setMessage(context.getString(R.string.select_playlist));
        builder.setPositiveButton(R.string.new_playlist, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
              showCreatePlayListPopup();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        final AlertDialog dialog = builder.create();
        ListView listView = view.findViewById(R.id.listItems);
        listView.setAdapter(new PopupListAdapter());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                  Album album = listAlbum.get(i);
                addSongsToPlaylist(album.getAlbumId());
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void addSongsToPlaylist(int playListId){
        int count = 0;
        for(int i=0;i<songs.size();i++) {
            int id = PlayListTable.addSongToPlayList(context, playListId, songs.get(i));
            if (id > 0) {
                count++;
            }
        }
        Toast.makeText(context, count+" Songs added to playlist", Toast.LENGTH_SHORT).show();
    }

    class PopupListAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return listAlbum.size();
        }

        @Override
        public Object getItem(int i) {
            return listAlbum.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if(view == null){
                view = mLayoutInflater.inflate(R.layout.popup_list,null);
            }
            TextView textView = view.findViewById(R.id.listItem);
            textView.setText(listAlbum.get(i).getAlbumTitle());
            return view;
        }
    }

}
