package com.ccc.raj.beats.searchresult;

import com.ccc.raj.beats.model.Album;
import com.ccc.raj.beats.model.Song;

/**
 * Created by Raj on 1/23/2018.
 */

public class SearchRecord {
    public static class Section{
        public String titleSection;
        public String subTitleSection;
        public int sectionType;
        public Section(String titleSection,String subTitleSection,int sectionType){
            this.titleSection = titleSection;
            this.subTitleSection = subTitleSection;
            this.sectionType = sectionType;
        }

        public int getSectionType(){
            return sectionType;
        }

        public String getTitleSection() {
            return titleSection;
        }

        public void setTitleSection(String titleSection) {
            this.titleSection = titleSection;
        }

        public String getSubTitleSection() {
            return subTitleSection;
        }

        public void setSubTitleSection(String subTitleSection) {
            this.subTitleSection = subTitleSection;
        }
    }
    private int viewType;//0|1|2|3
    public  static  final int SECTION_VIEW = 0;
    public  static  final int SONG_VIEW = 1;
    public  static  final int ALBUM_VIEW = 2;
    public  static  final int ARTIST_VIEW = 3;

    private Section sectionData = null;
    private Song song = null;
    private Album offlineAlbum = null;
    private Album artistAlbum = null;

    public  SearchRecord(int viewType){
        this.viewType = viewType;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public Section getSectionData() {
        return sectionData;
    }

    public void setSectionData(Section sectionData) {
        this.sectionData = sectionData;
    }

    public Song getSong() {
        return song;
    }

    public void setSong(Song song) {
        this.song = song;
    }

    public Album getOfflineAlbum() {
        return offlineAlbum;
    }

    public void setOfflineAlbum(Album offlineAlbum) {
        this.offlineAlbum = offlineAlbum;
    }

    public Album getArtistAlbum() {
        return artistAlbum;
    }

    public void setArtistAlbum(Album artistAlbum) {
        this.artistAlbum = artistAlbum;
    }
}
