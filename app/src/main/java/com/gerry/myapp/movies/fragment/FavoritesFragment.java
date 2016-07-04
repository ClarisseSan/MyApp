package com.gerry.myapp.movies.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.gerry.myapp.R;
import com.gerry.myapp.movies.activity.MovieDetailActivity;
import com.gerry.myapp.movies.object.FavoriteListAdapter;
import com.gerry.myapp.movies.object.FavoriteMovie;
import com.gerry.myapp.movies.object.Reviews;
import com.gerry.myapp.movies.object.Trailer;
import com.gerry.myapp.movies.object.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class FavoritesFragment extends Fragment {



    private List<Trailer> movieTrailersList;

    private List<Reviews> movieReviewsList;


    private FavoriteListAdapter mAdapter;
    private ArrayList<FavoriteMovie> list;

    public FavoritesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_favor, container, false);

        mAdapter =  new FavoriteListAdapter(getActivity(), list);
        GridView moviesGridView = (GridView) rootView.findViewById(R.id.gridview_favorites);
        moviesGridView.setAdapter(mAdapter);



        displayFavoriteMovies();



        moviesGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent i = new Intent(getActivity(), MovieDetailActivity.class)
                        //pass the selected movie_id to the next Activity
                        .putExtra(Intent.EXTRA_TEXT, Long.toString(id))
                        .putExtra("flagData", 1)
                        .putExtra("movieId", mAdapter.getItem(position).getMovie_id())
                        .putExtra("title", mAdapter.getItem(position).getMovie_name())
                        .putExtra("year", mAdapter.getItem(position).getMovie_date())
                        .putExtra("rating", mAdapter.getItem(position).getMovie_vote())
                        .putExtra("overview", mAdapter.getItem(position).getMovie_overview())
                        .putExtra("poster", mAdapter.getItem(position).getMovie_image())
                        .putExtra("duration", mAdapter.getItem(position).getMovie_duration())
                        .putParcelableArrayListExtra("trailers", (ArrayList<? extends Parcelable>) mAdapter.getItem(position).getMovie_trailerList())
                .putParcelableArrayListExtra("reviews", (ArrayList<? extends Parcelable>) mAdapter.getItem(position).getReviewsList());
                startActivity(i);
            }
        });
        return rootView;
    }




    private void displayFavoriteMovies() {



        list = new ArrayList<>();

        try {
            // this calls the json
            JSONArray arr = Utils.getFavoriteMovies(getActivity());

            //TODO: check JSON in Jsonlint
            Log.e("xxxxx-add", "called JSON(" + arr.length() + "): " + arr);


            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                String movie_id = obj.getString("movie_id");
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


                mAdapter.setItemList(list);
                mAdapter.notifyDataSetChanged();

            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("ERROR", e.getMessage());
        }

    }


}
