package com.gerry.myapp.movies.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.gerry.myapp.R;
import com.gerry.myapp.movies.fragment.BlankFragment;
import com.gerry.myapp.movies.fragment.TrailerFragment;
import com.gerry.myapp.movies.fragment.OverviewFragment;
import com.gerry.myapp.movies.object.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.karim.MaterialTabs;


public class MovieDetailActivity extends AppCompatActivity implements
        OverviewFragment.OnDetailInteractionListener,
        TrailerFragment.OnListFragmentInteractionListener,
        BlankFragment.OnFragmentInteractionListener {
    private static final String LOG_TAG = "MovieDetailActivity.class" ;
    short flag;
    private String movieId;
    private String movieName;
    private int flagData;

    private FloatingActionButton fab;
    private static final String TAG = MovieDetailActivity.class.getSimpleName();
    private ViewPager mViewPager;
    private MaterialTabs mMaterialTabs;
    private OverviewFragment detailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Intent intent = this.getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            movieId = intent.getStringExtra(Intent.EXTRA_TEXT);
            movieName = intent.getStringExtra("title");
            flagData = intent.getIntExtra("flagData", 0);

        }

        getSupportActionBar().setTitle(movieName);


        FragmentManager fm = getSupportFragmentManager();

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if button is clicked
                //determine the value of flag

                switch (flag) {
                    case 0:
                        //if flag = 0, then highlight the star...
                        fab.setImageResource(R.mipmap.ic_action_fav);

                        //add MOVIEID to the list
                        try {
                            detailFragment.markAsFavorite();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        flag = 1;
                        break;

                    case 1:


                        switch (flagData){
                            case 0:
                                //flagData = 0 --> from popular movies Activty
                                //if flag = 1 , then unhighlight the star...
                                fab.setImageResource(R.mipmap.ic_action_unfav);
                                break;
                            case 1:
                                //flagData = 1 --> from favorites activity
                                //if flag = 1 , then hide the floating action button...
                                fab.setVisibility(View.INVISIBLE);
                                break;
                        }


                        // delete MOVIEID from the list
                        try {
                            removeFromFavorites();
                            Toast.makeText(getApplication(), R.string.fav_remove,
                                    Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        flag = 0;
                        break;
                }


            }
        });

        checkMovieID();

        int numberOfTabs = 3;
        mViewPager = (ViewPager) findViewById(R.id.view_pager);

        // Assigning ViewPager View and setting the adapter
        SamplePagerAdapter adapter = new SamplePagerAdapter(getSupportFragmentManager(), numberOfTabs);
        mViewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //inflate the menu; this adds item form the action bar

        getMenuInflater().inflate(R.menu.menu_movie_detail,menu);;
        //retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        //get the provider and hold unto it to set/change the sharedIntent
        ShareActionProvider mShareActionProvider;
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        //attach an intent to this ShareActionProvider. You can update it anytime
        //like when the users select a pice of data they might like to share
        if (mShareActionProvider!=null){
            mShareActionProvider.setShareIntent(detailFragment.createShareMovieIntent());
        }else{
            Log.d(LOG_TAG,"share action provider is null");
        }

        return true;
    }

    private void removeFromFavorites() throws JSONException {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Prepare the blank array
        JSONArray blank = new JSONArray();

        // Get the favorites from preferences
        String json = preferences.getString("favorites", null);

        JSONArray lines = new JSONArray(json);
        for (int i = 0; i < lines.length(); i++) {
            // This is one favorites on the preferences
            JSONObject line = lines.getJSONObject(i);

            System.out.println("THIS IS A LINE---> " + line);

            String id = line.getString("movie_id");
            if (!id.equals(movieId)) {
                blank.put(line);
            }
        }


        // At this point, the blank array only contains the favorite movies lines we wanted.
        // Save it again on the preferences
        preferences.edit().putString("favorites", blank.toString()).apply();
    }


    public JSONArray getFavoriteMovies() throws JSONException {
        //1. initialization of aactivity.... button is not yet clicked
        //screen shows, determine if movie ID is included in a list of favorite movies
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String items = preferences.getString("favorites", "");
        return new JSONArray(items);
    }

    private ArrayList<String> getListOfFavMovies() {

        ArrayList<String> list = new ArrayList<>();

        try {
            // this calls the json
            JSONArray arr = getFavoriteMovies();


            Log.e("xxxxx-add", "called(" + arr.length() + "): " + arr);


            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                Long movie_id = obj.getLong("movie_id");
                String movie_name = obj.getString("movie_name");
                list.add(String.valueOf(movie_id));
                Log.d("xxxxx-add", "adding movie: " + movie_name);
            }

            System.out.println("FAVORITES SIZE---------> " + list.size());

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("ERROR", e.getMessage());
        }

        return list;
    }

    private void checkMovieID() {
        //if movieID is included in the list then flag = 1, highlight star
        //else  star has no highlight flag = 0
        ArrayList<String> favMovies = getListOfFavMovies();
        if (favMovies.contains(movieId)) {
            flag = 1;
            fab.setImageResource(R.mipmap.ic_action_fav);
        } else {
            flag = 0;
            fab.setImageResource(R.mipmap.ic_action_unfav);
        }
    }


    @Override
    public void onListFragmentInteraction(Trailer trailer) {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void markAsFavorite() {

    }


    public class SamplePagerAdapter extends FragmentPagerAdapter {

        private final String[] TITLES = {"Overview", "Trailers", "Reviews"};

        private final ArrayList<String> mTitles;

        public SamplePagerAdapter(FragmentManager fm, int numberOfTabs) {
            super(fm);
            mTitles = new ArrayList<>();
            for (int i = 0; i < numberOfTabs; i++) {
                mTitles.add(TITLES[i]);
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles.get(position);
        }

        @Override
        public int getCount() {
            return mTitles.size();
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            //create bundle to pass movieId to the fragments
            Bundle bundle = new Bundle();
            bundle.putString("movieId", movieId);
            Context context = MovieDetailActivity.this;

            if (position == 0) {
                fragment = Fragment.instantiate(context, OverviewFragment.class.getName());
                detailFragment = (OverviewFragment) fragment;
            }

            if (position == 1) {

                fragment = Fragment.instantiate(context, TrailerFragment.class.getName(),bundle);
            }
            if (position == 2) {
                fragment = Fragment.instantiate(context, BlankFragment.class.getName());
            }

            return fragment;
        }
    }

}


