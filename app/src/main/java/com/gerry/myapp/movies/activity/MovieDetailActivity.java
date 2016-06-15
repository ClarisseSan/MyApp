package com.gerry.myapp.movies.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Switch;

import com.gerry.myapp.R;
import com.gerry.myapp.movies.object.FavoriteMovie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MovieDetailActivity extends AppCompatActivity {
    short flag;
    private Intent intent;
    private String movieId;

    private FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        intent = this.getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            movieId = intent.getStringExtra(Intent.EXTRA_TEXT);
         }


        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if button is clicked
                //determine the value of flag




                switch (flag){
                    case 0:
                        //if flag = 0, then highlight the star...
                        fab.setImageResource(R.mipmap.ic_action_fav);

                        //TODO: add MOVIEID to the list

                        flag = 1;
                        break;

                    case 1:
                        //if flag = 1 , then unhighlight the star...
                        fab.setImageResource(R.mipmap.ic_action_unfav);

                        //TODO: delete MOVIEID from the list

                        flag = 0;
                        break;
                }


            }
        });

        checkMovieID();
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

    private void checkMovieID(){
        //if movieID is included in the list then flag = 1, highlight star
        //else  star has no highlight flag = 0
        ArrayList<String> favMovies = getListOfFavMovies();
        if(favMovies.contains(movieId)){
            flag = 1;
            fab.setImageResource(R.mipmap.ic_action_fav);
       }else{
            flag = 0;
            fab.setImageResource(R.mipmap.ic_action_unfav);
        }
    }


    }


