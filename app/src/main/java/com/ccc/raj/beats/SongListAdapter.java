package com.ccc.raj.beats;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ccc.raj.beats.model.Song;

import java.util.ArrayList;

/**
 * Created by Raj on 1/20/2018.
 */

public class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.ViewHolder> {
    private ArrayList<Song> songList;
    private Context mContext;
    private OnItemClickListener onItemClickListener;

    public static  class ViewHolder extends RecyclerView.ViewHolder{
        private LinearLayout container;
        public ViewHolder(View itemView) {
            super(itemView);
            container = (LinearLayout) itemView;
        }
    }

    public interface OnItemClickListener{
        public void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
       this.onItemClickListener = onItemClickListener;
    }

    public SongListAdapter(Context context,ArrayList<Song> songList){
       this.songList = songList;
       this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.song_row,parent,false);
        return new ViewHolder(linearLayout);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        LinearLayout container = holder.container;
        TextView songOrder = container.findViewById(R.id.order);
        TextView songTitle = container.findViewById(R.id.titleSong);
        TextView artistTitle = container.findViewById(R.id.titleArtist);
        TextView songDuration = container.findViewById(R.id.duration);
        songOrder.setText("1");
        songTitle.setText(songList.get(position).getTitle());
        artistTitle.setText(songList.get(position).getArtist());
        songDuration.setText("4:50");
        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }
}
