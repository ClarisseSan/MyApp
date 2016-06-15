package com.gerry.myapp.movies.fragment;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.gerry.myapp.R;
import com.gerry.myapp.movies.activity.TrailerExampleActivity;
import com.gerry.myapp.movies.object.Movie;
import com.gerry.myapp.movies.object.Trailer;
import com.gerry.myapp.movies.object.TrailerAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailActivityFragment extends Fragment {

    //variables for sharing
    private static final String LOG_TAG = "MovieDetailActivityFragment.class";

    private String movieId;
    private ArrayList<Movie> list;


    private String mTitle;
    private String mYear;
    private String mDuration;
    private String mRating;
    private String mOverview;
    private String mPoster;
    private String first_trailer_url = "";

    private TextView txtTitle;
    private TextView txtYear;
    private TextView txtDuration;
    private TextView txtRating;
    private TextView txtDescription;
    private ImageView imgPoster;

    private List<Trailer> movieTrailersList;
    private TrailerAdapter trailerListAdapter;
    private RecyclerView recyclerView;



    private int flagDataType; //if from api or local data
    //flagDataType = 0 --> from popular movies Activty
    //flagDataType = 1 --> from favorites activity
    private Intent intent;




    public MovieDetailActivityFragment() {
        setHasOptionsMenu(true);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            movieId = intent.getStringExtra(Intent.EXTRA_TEXT);
            flagDataType = intent.getIntExtra("flagData",0);
            Toast.makeText(getContext(), "MY ID: " + movieId, Toast.LENGTH_SHORT).show();
        }


        if(flagDataType==0) {

            requestMovieDetail(movieId);
            requestMovieTrailer(movieId);
            // requestMovieReviews(movieId);
        }


    }

    private void getLocalData() {

        mTitle = intent.getStringExtra("title");
        mYear = intent.getStringExtra("year");
        mDuration = intent.getStringExtra("duration");
        mRating = intent.getStringExtra("rating");
        mOverview = intent.getStringExtra("overview");
        mPoster = intent.getStringExtra("poster" +
                "");


        movieTrailersList = new ArrayList<>();
        List<Trailer> trailers = intent.getParcelableArrayListExtra("trailers");

        movieTrailersList = trailers;
        for (Trailer t:movieTrailersList) {
            System.out.println("TRAILER_URL================>" + t.getTrailerNumber());
        }


        txtTitle.setText(mTitle);
        txtYear.setText(mYear);
        txtDuration.setText(mDuration);
        txtRating.setText(mRating);
        txtDescription.setText(mOverview);
        Glide
                .with(getContext())
                .load(mPoster)
                .centerCrop()
                .error(R.mipmap.error)
                .into(imgPoster);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);




        //Movie title
        txtTitle = (TextView) rootView.findViewById(R.id.txt_title);


        //Movie Release Year
         txtYear = (TextView) rootView.findViewById(R.id.txt_year);


        //Movie Duration
         txtDuration = (TextView) rootView.findViewById(R.id.txt_duration);


        //Movie Rating
         txtRating = (TextView) rootView.findViewById(R.id.txt_rating);


        //Movie Description
         txtDescription = (TextView) rootView.findViewById(R.id.txt_description);


        //Movie poster
         imgPoster = (ImageView) rootView.findViewById(R.id.img_movie);

        //favorites button
        Button btnFavorite = (Button) rootView.findViewById(R.id.btnFavorite);
        btnFavorite.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                try {
                    markAsFavorite();
                    //display in short period of time
                    Toast.makeText(getActivity(), mTitle + " added to favorites",
                            Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(LOG_TAG,e.getMessage());
                }

            }
        });



        Button btntrailer = (Button) rootView.findViewById(R.id.btnTrailers);
        btntrailer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), TrailerExampleActivity.class);
                startActivity(i);
            }
        });


        if (flagDataType==1){
            //from FAvoritesActivity
            getLocalData();
        }



        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);

        /*
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(trailerListAdapter);
        */

        //ListView reviewsListView = (ListView) rootView.findViewById(R.id.list_reviews);
        //reviewsListView.setAdapter(trailerListAdapter);

        return rootView;

    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    private void requestMovieDetail(String movieId) {



        final String BASE_PATH = "http://api.themoviedb.org/3/movie/";
        final String api_key = "?api_key=6d369d4e0676612d2d046b7f3e8424bd";
        String id = movieId;


        final String original_url = BASE_PATH + id + api_key;
        Log.v("ORIGINAL URL >>>>>>>>", original_url);

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getActivity());


        //generate url for fetching movie poster
        //1.base path
        final String IMAGE_BASE_PATH = "http://image.tmdb.org/t/p/";
        //2. Then you will need a ‘size’, which will be one of the following: "w92", "w154", "w185", "w342", "w500", "w780", or "original"
        final String image_size = "w185";
        //3. And finally the poster path returned by the query : movie_image



        // Formulate the request and handle the response.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, original_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Do something with the response
                        System.out.println(response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                             mTitle = jsonObject.getString("title");
                             mYear = jsonObject.getString("release_date").substring(0, 4);
                             mDuration = jsonObject.getString("runtime") + "min";
                             mRating = jsonObject.getString("vote_average") + "/10";
                             mOverview = jsonObject.getString("overview");
                             mPoster = IMAGE_BASE_PATH + image_size + jsonObject.getString("poster_path");


                            Log.v("TITLE:>>>>>>>>>>>> ", mTitle);
                            Log.v("mYear:>>>>>>>>>>>> ", mYear);
                            Log.v("mDuration:>>>>>>>>>>> ", mDuration);
                            Log.v("mRating:>>>>>>>>>>>>>> ",mRating);
                            Log.v("mOverview:>>>>>>>>>>>> ",mOverview);
                            Log.v("mPoster:>>>>>>>>>>>>>> ", mPoster);

                            txtTitle.setText(mTitle);
                            txtYear.setText(mYear);
                            txtDuration.setText(mDuration);
                            txtRating.setText(mRating);
                            txtDescription.setText(mOverview);
                            Glide
                                    .with(getContext())
                                    .load(mPoster)
                                    .centerCrop()
                                    .error(R.mipmap.error)
                                    .into(imgPoster);

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
                            showSuccessDialog(getContext(), "No network connection", "This application requires an internet connection.").show();
                        }
                    }
                });


        // Add the request to the RequestQueue.
        queue.add(stringRequest);

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


                            //first trailer to send at share intent
                            if (movieTrailersList!=null){
                                first_trailer_url = movieTrailersList.get(0).getTrailerUrl();
                            }else{
                                //no trailer available
                                first_trailer_url = "";
                            }

                            for (Trailer trailer:movieTrailersList
                                 ) {
                                System.out.println("TRAILER NUMBER-----------> " + trailer.getTrailerNumber());

                            }

                            trailerListAdapter.setItemList(movieTrailersList);
                            trailerListAdapter.notifyDataSetChanged();
                            Log.d("Trailer list size SIZE", String.valueOf(movieTrailersList.size()));

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
                            showSuccessDialog(getContext(), "No network connection", "This application requires an internet connection.").show();
                        }
                    }
                });


        // Add the request to the RequestQueue.
        queue.add(stringRequest);


        //TODO : move this later at the oncreateView when you also fetched the array for the local data
        trailerListAdapter = new TrailerAdapter(movieTrailersList);



    }






    private void requestMovieReviews(String movieId){
        //http://api.themoviedb.org/3/movie/246655/videos?api_key=6d369d4e0676612d2d046b7f3e8424bd

        final String BASE_PATH = "http://api.themoviedb.org/3/movie/";
        final String api_key = "?api_key=6d369d4e0676612d2d046b7f3e8424bd";
        String id = movieId;
        final String vid = "/reviews";
        final String reviews_url = BASE_PATH + id + vid + api_key;

        Log.d("TRAILER URL--------> ", reviews_url);

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getActivity());


        // Formulate the request and handle the response.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, reviews_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Do something with the response
                        System.out.println(response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray results = jsonObject.getJSONArray("results");


                            if (results!=null){
                                for (int i = 0; i < results.length(); i++) {

                                    JSONObject obj = results.getJSONObject(i);
                                    String author = obj.getString("author");
                                    String content = obj.getString("content");

                                    System.out.println("Author>>>>>>" + author);
                                    System.out.println("Content>>>>>>" + content);

                                   // Reviews reviews = new Reviews(author, content);
                                   // reviewList.add(reviews);

                                }

                                /*
                                if (reviewList!=null){
                                    for (Reviews review:reviewList) {
                                        Log.d("AUTHOR: ",String.valueOf(review.getAuthor()));
                                        Log.d("CONTENT: ",review.getContent());
                                    }
                                }
                                */



                            }else {
                                System.out.println(R.string.no_reviews);
                            }






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
                            showSuccessDialog(getContext(), "No network connection", "This application requires an internet connection.").show();
                        }
                    }
                });


        // Add the request to the RequestQueue.
        queue.add(stringRequest);


    }



    private Intent createShareMovieIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        String mMovieString = "Check out this movie, " + mTitle + ": " + first_trailer_url;
        shareIntent.putExtra(Intent.EXTRA_TEXT, mMovieString);

        return shareIntent;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        //inflate the menu; this adds item form the action bar
        inflater.inflate(R.menu.menu_movie_detail,menu);

        //retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        //get the provider and hold unto it to set/change the sharedIntent
        ShareActionProvider mShareActionProvider;
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        //attach an intent to this ShareActionProvider. You can update it anytime
        //like when the users select a pice of data they might like to share
        if (mShareActionProvider!=null){
            mShareActionProvider.setShareIntent(createShareMovieIntent());
        }else{
            Log.d(LOG_TAG,"share action provider is null");
        }

    }

    private AlertDialog showSuccessDialog(final Context context, CharSequence title, CharSequence message) {
        // Creates a popup dialog
        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(context);
        downloadDialog.setTitle(title);
        downloadDialog.setMessage(message);
        downloadDialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            // Method below is the click handler for the YES button on the popup
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        return downloadDialog.show();
    }

    private JSONArray saveTrailers(){
        //generate new JSON Array
        JSONArray trailers = new JSONArray();

        //loop through the trailer list and save each item in the JSONArray
        for (int i = 0; i < movieTrailersList.size() ; i++) {
            String trailer_num = movieTrailersList.get(i).getTrailerNumber();
            String trailer_url = movieTrailersList.get(i).getTrailerUrl();


            JSONObject trailer = new JSONObject();
            try {
                trailer.put("trailer_num", trailer_num);
                trailer.put("trailer_url", trailer_url);

                trailers.put(trailer);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            trailers.put(trailer);
        }
        return trailers;
    }

    private void markAsFavorite() throws JSONException {


        //generate JSON(itemlist) so php can process it
        JSONObject item = new JSONObject();
        try {
            item.put("movie_id", movieId);
            item.put("movie_name", mTitle);
            item.put("movie_image", mPoster);
            item.put("movie_overview", mOverview);
            item.put("movie_year", mYear);
            item.put("movie_date", mYear);
            item.put("movie_vote", mRating);
            item.put("movie_duration", mDuration);
            item.put("movie_trailers", saveTrailers());

            //favorites.put(item);
            //save favoriteMovie in a list
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            JSONArray jsonArray = new JSONArray();
            String json = preferences.getString("favorites", null);

            if (json != null) {
                jsonArray = new JSONArray(json);
            }
            jsonArray.put(item);

            preferences.edit().putString("favorites", jsonArray.toString()).commit();

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }



}


