package com.gerry.myapp.movies.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.gerry.myapp.R;
import com.gerry.myapp.movies.carousel.MainCarousel;
import com.gerry.myapp.movies.fragment.OverviewFragment;
import com.gerry.myapp.movies.fragment.TrailerFragment;
import com.gerry.myapp.movies.object.Trailer;
import com.gerry.myapp.movies.object.Utils;

import org.json.JSONArray;
import org.json.JSONException;

public class PopularMoviesActivity extends AppCompatActivity  implements
        OverviewFragment.OnDetailInteractionListener,
        TrailerFragment.OnListFragmentInteractionListener{

    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /**
         * To know what case it is, have your MainActivity check whether or not the layout
         * contains a view with the id weather_detail_container. If it does, it’s a two pane
         * layout so set mTwoPane to true, otherwise you can set it to false. Also, if it’s a
         * two pane layout, you’ll need to add a new DetailFragment
         * */

        if (findViewById(R.id.movie_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new OverviewFragment(), DETAILFRAGMENT_TAG)
                        .replace(R.id.movie_detail_container1, new TrailerFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        OverviewFragment overviewFragment = (OverviewFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_detail);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_popular_movies, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id==R.id.action_settings){
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        if (id==R.id.action_fav){
            //if movies are available in the favoriteList
            //then show FavoritesActivity
            try {
                if (getFavoriteMovies().length()>0){
                    Intent intent = new Intent(this, MainCarousel.class);
                    startActivity(intent);
                }else{
                    //inform user that no movies are added to favorites
                    Utils.showSuccessDialog(this,R.string.title_activity_favor,R.string.no_favorites).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
        return super.onOptionsItemSelected(item);
    }

    private JSONArray getFavoriteMovies() throws JSONException {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String items = preferences.getString("favorites", null);
        JSONArray movies = items!=null ? new JSONArray(items) : new JSONArray();
        return movies;
    }


    @Override
    public void markAsFavorite() {

    }

    @Override
    public void onListFragmentInteraction(Trailer trailer) {

    }
}
