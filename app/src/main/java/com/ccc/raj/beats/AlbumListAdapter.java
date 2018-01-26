package com.ccc.raj.beats;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.ccc.raj.beats.model.Album;
import com.ccc.raj.beats.model.OfflineDataProvider;

import java.util.ArrayList;

/**
 * Created by Raj on 1/6/2018.
 */

public class AlbumListAdapter extends RecyclerView.Adapter<AlbumListAdapter.ViewHolder>{
    ArrayList<Album> albumList;
    Context context;
    private OnItemClickListener onItemClickListener;
    public static  class ViewHolder extends RecyclerView.ViewHolder{
        private CardView cardView;
        public ViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
        }
    }

    public interface OnItemClickListener{
        public  void onItemClick(int position);
        public void onPlayButtonClick(int position);
        public void onOptionsButtonClick(View view,int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    public AlbumListAdapter(ArrayList<Album> albumList, Context context){
        this.albumList = albumList;
        this.context = context;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.song_template,parent,false);
        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        CardView cardView = holder.cardView;
        Album album = albumList.get(position);
        TextView textSong = cardView.findViewById(R.id.textSong);
        textSong.setText(Utitlity.formatString(album.getAlbumTitle(),20));
        TextView textArtist = cardView.findViewById(R.id.textArtist);
        textArtist.setText(Utitlity.formatString(album.getComposer()+"",20));
        ImageView imageSong = cardView.findViewById(R.id.imageSong);
        imageSong.setImageBitmap(OfflineDataProvider.getBitmapByAlbumId(context,album.getAlbumId()));
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onItemClick(position);
            }
        });
        ImageButton playButton = cardView.findViewById(R.id.albumPlayButton);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onPlayButtonClick(position);
            }
        });
        ImageButton imageButton = cardView.findViewById(R.id.imageOptions);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onOptionsButtonClick(view,position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }
}
