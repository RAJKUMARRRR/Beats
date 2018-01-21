package com.ccc.raj.beats.musiclibrary;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ccc.raj.beats.MusicPlayService;
import com.ccc.raj.beats.R;
import com.ccc.raj.beats.listennow.ListenNowFragment;
import com.ccc.raj.beats.listennow.OfflineFragment;
import com.ccc.raj.beats.listennow.OnlineFragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class MusicLibraryFragment extends Fragment {

    ViewPager viewPager;
    TabLayout tabLayout;
    OnlineFragment onlineFragment = null;
    OfflineFragment offlineFragment = null;
    MusicPlayService mMusicPlayService = null;

    public MusicLibraryFragment() {
        // Required empty public constructor
    }



    public void setMusicPlayService(MusicPlayService musicPlayService){
        this.mMusicPlayService = musicPlayService;
        if(offlineFragment != null) {
            offlineFragment.setMusicPlayService(musicPlayService);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music_library, container, false);
        viewPager = view.findViewById(R.id.pager_view);
        PagerViewAdapter pagerViewAdapter = new PagerViewAdapter(getChildFragmentManager());
        viewPager.setAdapter(pagerViewAdapter);
        tabLayout = view.findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);
        return view;
    }


    private class PagerViewAdapter extends FragmentPagerAdapter {
        public PagerViewAdapter(FragmentManager fm) {
            super(fm);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0:
                    return getString(R.string.playlists);
                case 1:
                    return getString(R.string.artists);
                case 2:
                    return getString(R.string.albums);
                case 3:
                    return getString(R.string.songs);
                case 4:
                    return getString(R.string.genres);
            }
            return null;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return new PlaylistsFragment();
                case 1:
                    return new ArtistsFragment();
                case 2:
                    if(offlineFragment == null) {
                        offlineFragment = new OfflineFragment();
                        /*if(mMusicPlayService != null){
                            offlineFragment.setMusicPlayService(mMusicPlayService);
                        }*/
                    }
                    return offlineFragment;
                case 3:
                    return new SongsFragment();
                case 4:
                    return new GenresFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 5;
        }
    }
}
