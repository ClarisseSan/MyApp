package com.gerry.myapp.movies.fragment;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
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
public class OverviewFragment extends Fragment {

    //variables for sharing
    private static final String LOG_TAG = "OverviewFragment.class";
    private static final String STATE_ID = "movie_id" ;

    private String movieId;

    private String mTitle;
    private String mYear;
    private String mDuration;
    private String mRating;
    private String mOverview;
    private String mPoster;
    private String first_trailer_url = "";




    private TextView txtYear;
    private TextView txtDuration;
    private TextView txtRating;
    private TextView txtDescription;
    private ImageView imgPoster;
    private RatingBar ratingBar;

    private float vote_average;

    private List<Trailer> movieTrailersList;

    private List<Reviews>  reviewList;



     //if from api or local data
    //flagDataType = 0 --> from popular movies Activty
    //flagDataType = 1 --> from favorites activity
    private int flagDataType;

    private Intent intent;
    private OnDetailInteractionListener mListener; // this is the activity

    public OverviewFragment() {
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

        if(savedInstanceState!=null) {
            movieId = savedInstanceState.getString("movieId");
            mPoster = savedInstanceState.getString("mPoster");
            flagDataType= savedInstanceState.getInt("flagDataType");
        }


        if(flagDataType==0) {

            requestMovieDetail(movieId);
            requestMovieTrailer(movieId);
            requestMovieReviews(movieId);
        }
        else {
            Log.v("xxxxxxxx", "not getting from internet");
        }

    }


    @Override
    public void onResume() {
        super.onResume();

        setValuesOfView();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(outState);
        // Save the current movieID state
        outState.putString(STATE_ID, movieId);
        outState.putString("mPoster", mPoster);
        outState.putInt("flagDataType", flagDataType);
    }


    private void getLocalData() {

        mTitle = intent.getStringExtra("title");
        mYear = intent.getStringExtra("year");
        mDuration = intent.getStringExtra("duration");
        mRating = intent.getStringExtra("rating");
        vote_average = Float.parseFloat(mRating)/2;
        mOverview = intent.getStringExtra("overview");
        mPoster = intent.getStringExtra("poster" +
                "");


        movieTrailersList = new ArrayList<>();
        List<Trailer> trailers = intent.getParcelableArrayListExtra("trailers");

        movieTrailersList = trailers;
        for (Trailer t:movieTrailersList) {
            System.out.println("TRAILER_URL===========>" + t.getTrailerNumber());
        }


        System.out.println("RATING NYA DAW------------->" + mRating);
        txtYear.setText(mYear);
        txtDuration.setText(mDuration);
        //txtRating.setText(String.valueOf(vote_average));
        txtDescription.setText(mOverview);
        ratingBar.setRating(vote_average);
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
        View rootView = inflater.inflate(R.layout.fragment_overview, container, false);



        //Movie Release Year
         txtYear = (TextView) rootView.findViewById(R.id.txt_year);


        //Movie Duration
         txtDuration = (TextView) rootView.findViewById(R.id.txt_duration);


        //Movie Rating
        // txtRating = (TextView) rootView.findViewById(R.id.txt_rating);


        //Movie Description
         txtDescription = (TextView) rootView.findViewById(R.id.txt_description);


        //Movie poster
         imgPoster = (ImageView) rootView.findViewById(R.id.img_movie);

        //rating
         ratingBar = (RatingBar) rootView.findViewById(R.id.ratingBar);


        if (flagDataType==1){
            //from FAvoritesActivity
            getLocalData();
        }
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
                             vote_average = Float.parseFloat(jsonObject.getString("vote_average"));
                             mOverview = jsonObject.getString("overview");
                             mPoster = IMAGE_BASE_PATH + image_size + jsonObject.getString("poster_path");


                            Log.v("TITLE:>>>>>>>>>>>> ", mTitle);
                            Log.v("mYear:>>>>>>>>>>>> ", mYear);
                            Log.v("mDuration:>>>>>>>>>>> ", mDuration);
                            Log.v("mRating:>>>>>>>>>>>>>> ",mRating);
                            Log.v("mOverview:>>>>>>>>>>>> ",mOverview);
                            Log.v("mPoster:>>>>>>>>>>>>>> ", mPoster);

                            setValuesOfView();


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
                            Utils.showSuccessDialog(getContext(), R.string.no_connection, "This application requires an internet connection.").show();
                        }
                    }
                });


        // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }

    private void setValuesOfView() {
        txtYear.setText(mYear);
        txtDuration.setText(mDuration);
        //txtRating.setText(mRating);
        txtDescription.setText(mOverview);
        ratingBar.setRating(vote_average/2);
        Glide
                .with(getContext())
                .load(mPoster)
                .centerCrop()
                .error(R.mipmap.error)
                .into(imgPoster);
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
                            if (movieTrailersList.size()!=0){
                                first_trailer_url = movieTrailersList.get(0).getTrailerUrl();
                            }else{
                                //no trailer available
                                first_trailer_url = "";
                            }

                            for (Trailer trailer:movieTrailersList
                                 ) {
                                System.out.println("TRAILER NUMBER-----------> " + trailer.getTrailerNumber());

                            }

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
                            Utils.showSuccessDialog(getContext(), R.string.no_connection, "This application requires an internet connection.").show();
                        }
                    }
                });


        // Add the request to the RequestQueue.
        queue.add(stringRequest);





    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnDetailInteractionListener) {
            mListener = (OnDetailInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }




    private void requestMovieReviews(String movieId){
        //http://api.themoviedb.org/3/reviews/293660/videos?api_key=6d369d4e0676612d2d046b7f3e8424bd

        reviewList = new ArrayList<>();

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

                                    Reviews reviews = new Reviews(author, content);
                                   reviewList.add(reviews);

                                }


                                if (reviewList!=null){
                                    for (Reviews review:reviewList) {
                                        Log.d("AUTHOR: ",String.valueOf(review.getAuthor()));
                                        Log.d("CONTENT: ",review.getContent());
                                    }
                                }




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
                            Utils.showSuccessDialog(getContext(), R.string.no_connection, "This application requires an internet connection.").show();
                        }
                    }
                });


        // Add the request to the RequestQueue.
        queue.add(stringRequest);


    }



    public Intent createShareMovieIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        String mMovieString = "Check out this movie, " + mTitle + ": " + first_trailer_url;
        shareIntent.putExtra(Intent.EXTRA_TEXT, mMovieString);

        return shareIntent;
    }




    private JSONArray saveReviews(){
        //generate new JSON Array
        JSONArray reviews = new JSONArray();

        //loop through the trailer list and save each item in the JSONArray
        for (int i = 0; i < reviewList.size() ; i++) {
            String trailer_num = reviewList.get(i).getAuthor();
            String trailer_url = reviewList.get(i).getContent();


            JSONObject reviewObject = new JSONObject();
            try {
                reviewObject.put("review_author", trailer_num);
                reviewObject.put("review_content", trailer_url);

                reviews.put(reviewObject);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return reviews;
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

        }
        return trailers;
    }

    public void markAsFavorite() throws JSONException {


        //generate JSON(itemlist) so php can process it
        JSONObject item = new JSONObject();
        try {
            item.put("movie_id", movieId);
            item.put("movie_name", mTitle);
            item.put("movie_image", mPoster);
            item.put("movie_overview", mOverview);
            item.put("movie_year", mYear);
            item.put("movie_date", mYear);
            item.put("movie_vote", vote_average);
            item.put("movie_duration", mDuration);
            item.put("movie_trailers", saveTrailers());
            item.put("movie_reviews", saveReviews());


            int size = saveTrailers().length();
            System.out.println("TRAILER SIZE " + size);

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
            Toast.makeText(getContext(), R.string.fav_add,
                    Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public String getTitle(){
        return mTitle;
    }


    public interface OnDetailInteractionListener {
        public void markAsFavorite();
    }
}


