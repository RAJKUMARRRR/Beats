package com.ccc.raj.beats;

/**
 * Created by Raj on 1/21/2018.
 */

public class MusicPlayServiceHolder {
    private static MusicPlayService sMusicPlayService;
    public static void setMusicPlayService(MusicPlayService musicPlayService){
        sMusicPlayService = musicPlayService;
    }
    public static MusicPlayService getMusicPlayService(){
        return  sMusicPlayService;
    }
}
