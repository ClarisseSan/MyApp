package com.gerry.myapp.movies.carousel;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.gerry.myapp.R;
import com.gerry.myapp.movies.object.FavoriteMovie;
import com.gerry.myapp.movies.object.Reviews;
import com.gerry.myapp.movies.object.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import it.moondroid.coverflow.components.ui.containers.FeatureCoverFlow;

/**
 * code tutorial from :
 * http://www.devexchanges.info/2015/11/making-carousel-layout-in-android.html
 */

public class MainCarousel extends AppCompatActivity {

    private FeatureCoverFlow coverFlow;
    private CoverFlowAdapter adapter;

    private ArrayList<FavoriteMovie> list;
    private List<Trailer> movieTrailersList;
    private List<Reviews> movieReviewsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_carousel);
        coverFlow = (FeatureCoverFlow) findViewById(R.id.coverflow);

        displayFavoriteMovies();

        coverFlow.setAdapter(adapter);
        coverFlow.setOnScrollPositionListener(onScrollListener());
    }

    private FeatureCoverFlow.OnScrollPositionListener onScrollListener() {
        return new FeatureCoverFlow.OnScrollPositionListener() {
            @Override
            public void onScrolledToPosition(int position) {
                Log.v("MainActiivty", "position: " + position);
            }

            @Override
            public void onScrolling() {
                Log.i("MainCarousel", "scrolling");
            }
        };
    }


    public JSONArray getFavoriteMovies() throws JSONException {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String items = preferences.getString("favorites", "");
        return new JSONArray(items);
    }

    private void displayFavoriteMovies() {



        list = new ArrayList<>();

        try {
            // this calls the json
            JSONArray arr = getFavoriteMovies();

            //TODO: check JSON in Jsonlint
            Log.e("xxxxx-add", "called JSON(" + arr.length() + "): " + arr);


            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                Long movie_id = obj.getLong("movie_id");
                String movie_name = obj.getString("movie_name");
                String movie_image = obj.getString("movie_image");
                String movie_overview = obj.getString("movie_overview");
                String movie_date = obj.getString("movie_date");
                String movie_vote = obj.getString("movie_vote");
                String movie_duration = obj.getString("movie_duration");
                JSONArray trailers = obj.getJSONArray("movie_trailers");
                JSONArray reviews = obj.getJSONArray("movie_reviews");

                //FETCH TRAIlERS
                if (trailers.length()>0){
                    //if there are trailers available
                    movieTrailersList = new ArrayList<>();
                    for (int j = 0; j < trailers.length(); j++) {

                        JSONObject trailer = trailers.getJSONObject(j);
                        String trailer_num = trailer.getString("trailer_num");
                        String trailer_url = trailer.getString("trailer_url");

                        Trailer t = new Trailer(trailer_num, trailer_url);
                        //save trailers in a list
                        movieTrailersList.add(t);


                    }
                }

                //FETCH REVIEWS
                if (reviews.length()>=0){
                    //if there are trailers available
                    movieReviewsList = new ArrayList<>();
                    for (int j = 0; j < reviews.length(); j++) {

                        JSONObject reviewObj = reviews.getJSONObject(j);

                        String review_author = reviewObj.getString("review_author");
                        String review_content = reviewObj.getString("review_content");

                        Reviews rev = new Reviews(review_author, review_content);
                        //save reviews in a list
                        movieReviewsList.add(rev);
                    }
                }

                //**********LOGS************//
                Log.d("xxxxx-add", "adding movie: " + movie_name);
                System.out.println("TRAILER SIZE---------->" + movieTrailersList.size());

                System.out.println("MOVIE NAME = " + movie_name);
                for (Trailer t:movieTrailersList
                        ) {
                    System.out.println("TRAILER: " + t.getTrailerUrl());
                }
                //**************************//


                FavoriteMovie fav = new FavoriteMovie(movie_id,movie_name,movie_image,movie_overview,movie_date,movie_vote,movie_duration, movieTrailersList, movieReviewsList);
                list.add(fav);

                System.out.println("FAVORITE MOVIES SIZE---------> " + list.size());

                adapter = new CoverFlowAdapter(this, list);
                //adapter.setItemList(list);
                adapter.notifyDataSetChanged();

            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("ERROR", e.getMessage());
        }

    }

}
