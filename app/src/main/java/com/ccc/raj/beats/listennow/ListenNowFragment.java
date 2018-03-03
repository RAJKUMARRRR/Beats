package com.ccc.raj.beats.listennow;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ccc.raj.beats.MusicPlayService;
import com.ccc.raj.beats.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListenNowFragment extends Fragment {

    ViewPager viewPager;
    TabLayout tabLayout;
    OnlineFragment onlineFragment = null;
    OfflineFragment offlineFragment = null;
    MusicPlayService mMusicPlayService = null;



    public ListenNowFragment() {
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_listen_now, container, false);
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
                case 1:
                    return "Now Playing";
                case 0:
                    return "Recent";
            }
            return null;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    if(offlineFragment == null) {
                        offlineFragment = new OfflineFragment();
                        if(mMusicPlayService != null){
                            offlineFragment.setMusicPlayService(mMusicPlayService);
                        }
                    }
                    return offlineFragment;
                case 1:
                    if(onlineFragment == null) {
                        onlineFragment = new OnlineFragment();
                    }
                    return onlineFragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
