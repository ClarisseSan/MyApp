package com.gerry.myapp.movies.fragment;


import android.content.Context;
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

    //variables for sharing
    private static final String LOG_TAG = "OverviewFragment";
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

        if (intent != null) {
            movieId = intent.getStringExtra(Intent.EXTRA_TEXT);
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
            movieId = savedInstanceState.getString("movieId");
            mPoster = savedInstanceState.getString("mPoster");
            flagDataType= savedInstanceState.getInt("flagDataType");
        }


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
        outState.putString("mPoster", mPoster);
        outState.putInt("flagDataType", flagDataType);
    }


    private void setValuesOfViewLocalData() {

        txtYear.setText(mYear);
        txtDuration.setText(mDuration);
        txtDescription.setText(mOverview);
        ratingBar.setRating(vote_average);

        //set movie poster
        imgPoster.setImageBitmap(Utils.decodeBase64Image(mPoster));
     }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_overview, container, false);

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
                        .centerCrop()
                        .error(R.mipmap.error)
                        .into(imgPoster);
                break;
            case 1:
                //set movie poster
                imgPoster.setImageBitmap(Utils.decodeBase64Image(mPoster));
                break;

        }

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




    public interface OnDetailInteractionListener {
        public void markAsFavorite();
    }
}


