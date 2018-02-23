package com.ccc.raj.beats;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;

import com.ccc.raj.beats.listennow.ListenNowFragment;
import com.ccc.raj.beats.listennow.OfflineFragment;
import com.ccc.raj.beats.listennow.OnlineFragment;
import com.ccc.raj.beats.model.Song;
import com.ccc.raj.beats.musiclibrary.MusicLibraryFragment;
import com.ccc.raj.beats.searchresult.CursorSuggestionAdapter;
import com.ccc.raj.beats.searchresult.CustomSuggestionsProvider;
import com.ccc.raj.beats.searchresult.SearchSuggestionProvider;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;

public class MainActivity extends MediaControlBaseActivity implements NavigationView.OnNavigationItemSelectedListener,SearchView.OnSuggestionListener,SearchView.OnQueryTextListener{
    Toolbar toolbar;
    public  MusicPlayService musicPlayService;
    private ArrayList<Song> songsList;
    DrawerLayout drawerLayout;
    ListenNowFragment listenNowFragment;
    FrameLayout mediaViewContainer;

    SearchView searchView;
    CursorSuggestionAdapter mCursorSuggestionAdapter;
    SearchSuggestionProvider mSearchSuggestionProvider;

    SlidingUpPanelLayout mSlidingUpPanelLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onMusicServiceBind(MusicPlayService musicPlayService) {
        this.musicPlayService = musicPlayService;
        listenNowFragment.setMusicPlayService(musicPlayService);
    }

    @Override
    protected void setControllerAnchorView(MusicController musicController) {
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar_main);

        setSupportActionBar(toolbar);

        FragmentManager fragmentManager = getSupportFragmentManager();
        listenNowFragment = new ListenNowFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.main_container,listenNowFragment);
        fragmentTransaction.commit();

        setNavigationDrawer();

        mediaViewContainer = findViewById(R.id.media_container);
        musicController.setAnchorView(mediaViewContainer);

        setSearchBar();
        setSlidingLayout();
    }

    public void setSlidingLayout() {
        mSlidingUpPanelLayout = findViewById(R.id.sliding_layout);
        mSlidingUpPanelLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                if (SlidingUpPanelLayout.PanelState.COLLAPSED.name().equalsIgnoreCase(newState.name())) {
                    Log.i("SlideUp", newState.name());
                    MainActivity.super.onSlideDown();
                } else if (SlidingUpPanelLayout.PanelState.EXPANDED.name().equalsIgnoreCase(newState.name())) {
                    Log.i("SlideUp", newState.name());
                    MainActivity.super.onSlideUp();
                }
            }
        });
    }


    public void setNavigationDrawer(){
        drawerLayout = findViewById(R.id.drawer_view);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,drawerLayout,toolbar,R.string.nav_open_drawer,R.string.nav_close_drawer
        );
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Fragment fragment = null;
        Intent intent = null;
        switch (id){
            case R.id.nav_listen_now:
                fragment = new ListenNowFragment();
                //((ListenNowFragment)fragment).setMusicPlayService(musicPlayService);
                break;
            case R.id.nav_music_library:
                fragment = new MusicLibraryFragment();
                //((MusicLibraryFragment)fragment).setMusicPlayService(musicPlayService);
                break;
            /*case R.id.nav_sent:
                fragment = new InboxFragment();
                break;
            case R.id.nav_trash:
                fragment = new InboxFragment();
                break;
            case R.id.help:
                intent = new Intent(this,HelpActivity.class);
                break;
            case R.id.feedback:
                intent = new Intent(this,FeedbackActivity.class);
                break;*/
        }
        if(fragment!=null){
            FragmentTransaction fragmentTransaction= getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.main_container,fragment);
            fragmentTransaction.commit();
        }else{
            //startActivity(intent);
        }
        DrawerLayout drawerLayout = findViewById(R.id.drawer_view);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.options_menu,menu);
        /*SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setBackgroundResource(R.drawable.rectangle);*/
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Toast.makeText(this,"Search Clickd",Toast.LENGTH_SHORT).show();
        searchBarRevealAnimation(item.getActionView());
        return true;
    }

    public void setSearchBar(){
        searchView = findViewById(R.id.search_bar_edit_text);
        searchView.setOnQueryTextListener(this);
        searchView.setOnSuggestionListener(this);
        ArrayList<String> list = new ArrayList<>();
        mCursorSuggestionAdapter = new CursorSuggestionAdapter(this,null,true);
        mCursorSuggestionAdapter.setOnSuggessionClickListener(new CursorSuggestionAdapter.OnSuggessionClickListener() {
            @Override
            public void onSuggessionSelect(String text,String action) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                intent.setAction(action);
                intent.setData(Uri.parse(text));
                startActivity(intent);
            }
        });
        searchView.setSuggestionsAdapter(mCursorSuggestionAdapter);
        mSearchSuggestionProvider = new SearchSuggestionProvider(this);
        formattSuggestionView();
    }
    public void searchBarRevealAnimation(View view) {
        final LinearLayout searchBar = findViewById(R.id.search_container_main);
        int centerX = (searchBar.getLeft() + searchBar.getRight());
        int centerY = (searchBar.getTop() + searchBar.getBottom())/2;
        float radius = Math.max(searchBar.getWidth(), searchBar.getHeight()) * 2.0f;
        if (searchBar.getVisibility() == View.INVISIBLE) {
            searchBar.setVisibility(View.VISIBLE);
            Animator reveal = ViewAnimationUtils.createCircularReveal(searchBar, centerX, centerY, 0, radius);
            reveal.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    searchView.setFocusable(true);
                }
            });
            reveal.setDuration(300);
            reveal.setInterpolator(new AccelerateInterpolator());
            reveal.start();
        } else {
            Animator reveal = ViewAnimationUtils.createCircularReveal(searchBar, centerX, centerY, radius, 0);
            reveal.setDuration(500);
            reveal.setInterpolator(new AccelerateInterpolator());
            reveal.addListener(new AnimatorListenerAdapter(){
                public void onAnimationEnd(Animator animation) {
                    searchBar.setVisibility(View.INVISIBLE);
                }
            });
            reveal.start();
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Intent intent = new Intent(this,SearchActivity.class);
        intent.setAction(Intent.ACTION_SEARCH);
        intent.putExtra(SearchManager.QUERY,query);
        startActivity(intent);
        searchView.clearFocus();
        searchView.setQuery("",false);
        searchBarRevealAnimation(new View(this));
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Cursor cursor = mSearchSuggestionProvider.getSearchSuggestionCursor(newText); //getCursor(list);
        mCursorSuggestionAdapter.changeCursor(cursor);
        return true;
    }

    @Override
    public boolean onSuggestionSelect(int position) {
        Toast.makeText(this,"select",Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public boolean onSuggestionClick(int position) {
        Toast.makeText(this,"click",Toast.LENGTH_SHORT).show();
        return false;
    }

    private void formattSuggestionView(){
        int searchEditTextId = R.id.search_src_text;
        final AutoCompleteTextView searchEditText = (AutoCompleteTextView) searchView.findViewById(searchEditTextId);
        final View dropDownAnchor = searchView.findViewById(searchEditText.getDropDownAnchor());

        if (dropDownAnchor != null) {
            dropDownAnchor.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                           int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    int screenWidthPixel = MainActivity.this.getResources().getDisplayMetrics().widthPixels;
                    final LinearLayout searchBar = findViewById(R.id.search_container_main);
                    searchEditText.setDropDownWidth(screenWidthPixel);
                }
            });
        }
    }
}

