package com.gerry.myapp.movies.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gerry.myapp.R;
import com.gerry.myapp.movies.object.Reviews;
import com.gerry.myapp.movies.object.Trailer;
import com.gerry.myapp.movies.object.Utils;

import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class OverviewFragment extends Fragment {


    private static final String LOG_TAG = "OverviewFragment";
    private static final String STATE_ID = "movie_id" ;
    private static final String STATE_DATA = "flagDataType";
    private static final String STATE_TITLE = "title";
    private static final String STATE_YEAR = "year";
    private static final String STATE_DURATION = "duration";
    private static final String STATE_RATING = "rating";
    private static final String STATE_VOTE = "vote_ave";
    private static final String STATE_OVERVIEW = "overview";
    private static final String STATE_POSTER ="poster" ;

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

    public OverviewFragment() {
        setHasOptionsMenu(true);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        intent = getActivity().getIntent();

        if (intent != null) {
            flagDataType = intent.getIntExtra("flagData",0);
           }

        //this is the bundle from MovieDetailActivity
        if (getArguments() != null) {
            //get movieId from MovieDetailActivity
            movieId = getArguments().getString("movieId");

            mTitle = getArguments().getString("title");
            mYear = getArguments().getString("year");
            mDuration = getArguments().getString("duration");
            mRating = getArguments().getString("rating");
            vote_average = getArguments().getFloat("vote_ave");
            mOverview = getArguments().getString("overview");
            mPoster = getArguments().getString("poster");

        }

        if(savedInstanceState!=null) {
            movieId = savedInstanceState.getString(STATE_ID);
            flagDataType = savedInstanceState.getInt(STATE_DATA);
            mTitle = savedInstanceState.getString(STATE_TITLE);
            mYear = savedInstanceState.getString(STATE_YEAR);
            mDuration = savedInstanceState.getString(STATE_DURATION);
            mRating = savedInstanceState.getString(STATE_RATING);
            vote_average = savedInstanceState.getFloat(STATE_VOTE);
            mOverview = savedInstanceState.getString(STATE_OVERVIEW);
            mPoster = savedInstanceState.getString(STATE_POSTER);
        }

        setRetainInstance(true);

        //for allowing access in movie poster
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }



    @Override
    public void onResume() {
        super.onResume();
        setValuesOfView(mYear, mDuration, mOverview, vote_average, mPoster);
    }




    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(outState);
        // Save the current movieID state
        outState.putString(STATE_ID, movieId);
        outState.putInt(STATE_DATA, flagDataType);
        outState.putString(STATE_TITLE, mTitle);
        outState.putString(STATE_YEAR, mYear);
        outState.putString(STATE_DURATION, mDuration);
        outState.putString(STATE_RATING, mRating);
        outState.putFloat(STATE_VOTE, vote_average);
        outState.putString(STATE_OVERVIEW, mOverview);
        outState.putString(STATE_POSTER, mPoster);
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_overview, container, false);

        //Movie Release Year
         txtYear = (TextView) rootView.findViewById(R.id.txt_year);

        //Movie Duration
         txtDuration = (TextView) rootView.findViewById(R.id.txt_duration);

        //Movie Description
         txtDescription = (TextView) rootView.findViewById(R.id.txt_description);

        //Movie poster
         imgPoster = (ImageView) rootView.findViewById(R.id.img_movie);

        //rating
         ratingBar = (RatingBar) rootView.findViewById(R.id.ratingBar);

        setValuesOfView(mYear, mDuration, mOverview, vote_average, mPoster);
        return rootView;

    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }


    public void setValuesOfView(String mYear, String mDuration, String mOverview, float vote_average, String mPoster) {
        txtYear.setText(mYear);
        txtDuration.setText(mDuration);
        txtDescription.setText(mOverview);
        ratingBar.setRating(vote_average/2);

        switch (flagDataType){
            case 0:
                Glide
                        .with(getContext())
                        .load(mPoster)
                        .placeholder(R.drawable.ic_loading)
                        .fitCenter()
                        .error(R.drawable.ic_error)
                        .crossFade()
                        .into(imgPoster);
                break;
            case 1:
                //set movie poster
                imgPoster.setImageBitmap(Utils.decodeBase64Image(mPoster));
                break;

        }

    }


}


