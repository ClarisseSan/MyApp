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
import com.gerry.myapp.movies.object.Utils;

import org.json.JSONArray;
import org.json.JSONException;

public class PopularMoviesActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popular_movies);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


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

}
