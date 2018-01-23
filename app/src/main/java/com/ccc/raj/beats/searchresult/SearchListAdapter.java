package com.ccc.raj.beats.searchresult;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ccc.raj.beats.R;
import com.ccc.raj.beats.Utitlity;
import com.ccc.raj.beats.model.Album;
import com.ccc.raj.beats.model.OfflineDataProvider;
import com.ccc.raj.beats.model.OfflineSong;

import java.util.ArrayList;
import java.util.zip.Inflater;

/**
 * Created by Raj on 1/23/2018.
 */

public class SearchListAdapter extends RecyclerView.Adapter<SearchListAdapter.ViewHolder>{
    ArrayList<SearchRecord> mSearchRecords;
    Context context;
    public  static class ViewHolder extends RecyclerView.ViewHolder{
        public  View itemView;
        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
        }
    }

    public SearchListAdapter(Context context,ArrayList<SearchRecord> searchRecords){
      this.mSearchRecords = searchRecords;
      this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType){
            case SearchRecord.ALBUM_VIEW:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_template,parent,false);
                break;
            case SearchRecord.ARTIST_VIEW:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_template,parent,false);
                break;
            case SearchRecord.SONG_VIEW:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_row,parent,false);
                break;
            case SearchRecord.SECTION_VIEW:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.section_header,parent,false);
                break;
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.section_header,parent,false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SearchRecord searchRecord = mSearchRecords.get(position);
        switch (searchRecord.getViewType()){
            case SearchRecord.ALBUM_VIEW:
                bindAlbumData(holder,searchRecord);
                break;
            case SearchRecord.ARTIST_VIEW:
                bindAlbumData(holder,searchRecord);
                break;
            case SearchRecord.SONG_VIEW:
                bindSongData(holder,searchRecord);
                break;
            case SearchRecord.SECTION_VIEW:
                bindHeaderData(holder,searchRecord);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mSearchRecords.size();
    }

    @Override
    public int getItemViewType(int position) {
        SearchRecord searchRecord = mSearchRecords.get(position);
        return searchRecord.getViewType();
    }

    public void bindAlbumData(ViewHolder viewHolder,SearchRecord searchRecord){
        CardView cardView = (CardView) viewHolder.itemView;
        Album album = searchRecord.getOfflineAlbum();
        TextView textSong = cardView.findViewById(R.id.textSong);
        textSong.setText(Utitlity.formatString(album.getAlbumTitle(),20));
        TextView textArtist = cardView.findViewById(R.id.textArtist);
        textArtist.setText(Utitlity.formatString(album.getComposer()+"",20));
        ImageView imageSong = cardView.findViewById(R.id.imageSong);
        imageSong.setImageBitmap(OfflineDataProvider.getBitmapByAlbumId(context,album.getAlbumId()));
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //onItemClickListener.onItemClick(position);
            }
        });
        ImageButton playButton = cardView.findViewById(R.id.albumPlayButton);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //onItemClickListener.onPlayButtonClick(position);
            }
        });
    }

    public  void bindSongData(ViewHolder viewHolder,SearchRecord searchRecord){
        LinearLayout container = (LinearLayout) viewHolder.itemView;
        TextView songOrder = container.findViewById(R.id.order);
        TextView songTitle = container.findViewById(R.id.titleSong);
        TextView artistTitle = container.findViewById(R.id.titleArtist);
        TextView songDuration = container.findViewById(R.id.duration);
        OfflineSong offlineSong = (OfflineSong) searchRecord.getSong();
        artistTitle.setText(Utitlity.formatString(offlineSong.getArtist()+"",35));
        songDuration.setText(Utitlity.converMillisecondsToMMSS(offlineSong.getDuratio())+"");
        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //onItemClickListener.onItemClick(position);
            }
        });
        /*songOrder.setText(offlineSong.getTrackNumber()+"");
        songTitle.setText(Utitlity.formatString(offlineSong.getDisplayName()+"",35));*/
        songOrder.setBackgroundResource(R.drawable.music);
        songOrder.setBackground(new BitmapDrawable(context.getResources(),OfflineDataProvider.getBitmapBySongId(context,offlineSong.getId())));
        songOrder.setText("");
        songTitle.setText(Utitlity.formatString(offlineSong.getDisplayName()+"",35));
    }
    public  void bindHeaderData(ViewHolder viewHolder,SearchRecord searchRecord){
        LinearLayout container = (LinearLayout) viewHolder.itemView;
        TextView textViewTitle = container.findViewById(R.id.header);
        textViewTitle.setText(searchRecord.getSectionData().getTitleSection());
        TextView textViewSubTitle = container.findViewById(R.id.subHeader);
        textViewSubTitle.setText(searchRecord.getSectionData().getSubTitleSection());
    }
}
