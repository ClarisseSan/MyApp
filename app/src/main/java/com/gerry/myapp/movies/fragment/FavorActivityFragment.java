package com.gerry.myapp.movies.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gerry.myapp.R;
import com.gerry.myapp.movies.activity.MovieDetailActivity;
import com.gerry.myapp.movies.object.FavoriteListAdapter;
import com.gerry.myapp.movies.object.FavoriteMovie;
import com.gerry.myapp.movies.object.Trailer;
import com.gerry.myapp.movies.object.TrailerListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class FavorActivityFragment extends Fragment {



    private List<Trailer> movieTrailersList;
    private TrailerListAdapter trailerListAdapter;

    private FavoriteListAdapter mAdapter;
    private ArrayList<FavoriteMovie> list;

    public FavorActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestMovieTrailer("293660");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_favor, container, false);

        //trailerListAdapter = new TrailerListAdapter(getActivity(), movieTrailersList);
        //ListView listView = (ListView) rootView.findViewById(R.id.list_hiro);
        //listView.setAdapter(trailerListAdapter);
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
                        .putParcelableArrayListExtra("trailers", (ArrayList<? extends Parcelable>) mAdapter.getItem(position).getMovie_trailerList());

                startActivity(i);


            }
        });



        return rootView;
    }






    private void requestMovieTrailer(String movieId){
        //http://api.themoviedb.org/3/movie/246655/videos?api_key=6d369d4e0676612d2d046b7f3e8424bd
        movieTrailersList = new ArrayList<>();


        final String BASE_PATH = "http://api.themoviedb.org/3/movie/";
        final String api_key = "?api_key=6d369d4e0676612d2d046b7f3e8424bd";
        String id = movieId;
        final String vid = "/videos";
        String trailer_url = BASE_PATH + id + vid + api_key;

        Log.d("TRAILER URL----------> ", trailer_url);

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getActivity());


        // Formulate the request and handle the response.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, trailer_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Do something with the response
                        System.out.println(response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray results = jsonObject.getJSONArray("results");


                            for (int i = 0; i < results.length(); i++) {

                                JSONObject obj = results.getJSONObject(i);
                                String trailer_key = obj.getString("key");
                                String youtube_trailer = "https://www.youtube.com/watch?v=" + trailer_key;
                                String trailer_num = "Trailer " + (i+1);

                                System.out.println("TRAILER NUMBER --------->" + trailer_num);
                                System.out.println("TRAILER URL --------->" + youtube_trailer);

                                Trailer trailer = new Trailer(trailer_num, youtube_trailer);

                                //save trailers in a list
                                movieTrailersList.add(trailer);

                            }


                            for (Trailer trailer:movieTrailersList
                                    ) {
                                System.out.println("TRAILER NUMBER------->" + trailer.getTrailerNumber());

                            }



                            trailerListAdapter.setItemList(movieTrailersList);
                            trailerListAdapter.notifyDataSetChanged();
                            System.out.println("Trailer SIZE: " + movieTrailersList.size());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //other catches
                        if(error instanceof NoConnectionError) {
                            //show dialog no net connection
                            // showSuccessDialog(getContext(), "No network connection", "This application requires an internet connection.").show();
                        }
                    }
                });


        // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }

    public JSONArray getFavoriteMovies() throws JSONException {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
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

                movieTrailersList = new ArrayList<>();
                for (int j = 0; j < trailers.length(); j++) {

                    JSONObject trailer = trailers.getJSONObject(i);
                    String trailer_num = trailer.getString("trailer_num");
                    String trailer_url = trailer.getString("trailer_url");

                    Trailer t = new Trailer(trailer_num, trailer_url);
                    //save trailers in a list
                    movieTrailersList.add(t);
                }
                Log.d("xxxxx-add", "adding movie: " + movie_name);
                System.out.println("TRAILERs SIZE---------->" + movieTrailersList.size());

                System.out.println("MOVIE NAME = " + movie_name);
                for (Trailer t:movieTrailersList
                     ) {
                    System.out.println("TRAILER: " + t.getTrailerUrl());

                }



                FavoriteMovie fav = new FavoriteMovie(movie_id,movie_name,movie_image,movie_overview,movie_date,movie_vote,movie_duration, movieTrailersList);
                list.add(fav);



            }

            System.out.println("FAVORITE MOVIES SIZE---------> " + list.size());

            mAdapter.setItemList(list);
            mAdapter.notifyDataSetChanged();


        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("ERROR", e.getMessage());
        }

    }


}
