package com.gerry.myapp.movies.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
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
import com.gerry.myapp.movies.object.Config;
import com.gerry.myapp.movies.object.ImageListAdapter;
import com.gerry.myapp.movies.object.MovieImage;
import com.gerry.myapp.movies.object.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class PopularMoviesFragment extends Fragment {

    private List<String> movieImages = new ArrayList<String>();
    private ArrayList<MovieImage> list;
    private ImageListAdapter mAdapter;


    private SharedPreferences mSharedPreferences;
    String sortOption;


    public static PopularMoviesFragment newInstance() {
        PopularMoviesFragment fragment = new PopularMoviesFragment();
        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView =  inflater.inflate(R.layout.fragment_popular_movies, container, false);


        mAdapter =  new ImageListAdapter(getContext(), movieImages, list);

        GridView moviesGridView = (GridView) rootView.findViewById(R.id.gridview_movies);
        moviesGridView.setAdapter(mAdapter);

        // Get the instance of SharedPreferences object
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        sortOption = mSharedPreferences.getString(getString(R.string.pref_sort_key),"popular");

        updateMovies();



        moviesGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent i = new Intent(getActivity(), MovieDetailActivity.class)
                        //pass the selected movie_id to the next Activity
                        .putExtra("movieId", Long.toString(id))
                        .putExtra("flagData", 0)
                        .putExtra("title", mAdapter.getItem(position).getMovie_name());
                startActivity(i);
            }
        });

        return rootView;
    }




    private void updateMovies() {
        // Get the instance of SharedPreferences object
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        // We retrieve foreground and background color value as a string
        sortOption = mSharedPreferences.getString(getString(R.string.pref_sort_key),"popular");

       // getMovies();
        requestMovies();

    }


    @Override
    public void onStart() {
        super.onStart();
        updateMovies();

    }


    private void requestMovies(){
        final String BASE_PATH = "http://api.themoviedb.org/3/movie/";
        final String sort_order = "popular";
        final String api_key = "?api_key=" + Config.API_KEY;;

        String original_url = BASE_PATH + sort_order + api_key;
        System.out.println("ORIGINAL URL >>>>>>>>" + original_url);

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getActivity());



        // Formulate the request and handle the response.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, original_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Do something with the response
                        System.out.println(response);
                        try {

                            //loop through the content of that JSON object

                            list = new ArrayList<>();

                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray results = jsonObject.getJSONArray("results");


                            for (int i = 0; i < results.length(); i++) {
                                JSONObject obj = results.getJSONObject(i);
                                long movie_id = obj.getLong("id");
                                String movie_name = obj.getString("title");
                                String movie_image = obj.getString("poster_path");

                                //save the images to a String array
                                //You will need to append a base path ahead of this relative path to build
                                //the complete url you will need to fetch the image using Picasso.

                                //1.base path
                                final String IMAGE_BASE_PATH = "http://image.tmdb.org/t/p/";
                                //2. Then you will need a ‘size’, which will be one of the following: "w92", "w154", "w185", "w342", "w500", "w780", or "original"
                                String image_size = "w342";
                                //3. And finally the poster path returned by the query : movie_image

                                String posterPath = IMAGE_BASE_PATH + image_size + movie_image;


                                MovieImage movieImage = new MovieImage(movie_id, movie_name,posterPath);
                                list.add(movieImage);
                                Log.v("xxxxx-add", "adding movie: " + movie_name);

                                movieImages.add(posterPath);
                            }



                            for (MovieImage img:list) {
                                Log.d("MOVIE ID: ",String.valueOf(img.getMovie_id()));
                                Log.d("MOVIE NAME: ",img.getMovie_name());
                                Log.d("MOVIE IMAGE: ", img.getMovie_image());

                            }

                            int itemCount = movieImages.size();
                            System.out.println("IMAGE COUNT...>>>>>>>>>" + itemCount);

                            mAdapter.setItemList(list);
                            mAdapter.notifyDataSetChanged();

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
                            Utils.showSuccessDialog(getContext(), R.string.no_connection, R.string.net).show();
                        }
                    }
                });


        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);
    }
}
