package com.gerry.myapp.movies.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gerry.myapp.R;
import com.gerry.myapp.movies.object.Config;
import com.gerry.myapp.movies.object.MyReviewRecyclerViewAdapter;
import com.gerry.myapp.movies.object.Reviews;
import com.gerry.myapp.movies.object.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ReviewFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String LOG_TAG = "ReviewFragment";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    private List<Reviews> movieReviewList;
    private String movieId;
    private RecyclerView recyclerView;
    private MyReviewRecyclerViewAdapter reviewListAdapter;
    private Intent intent;
    //if from api or local data
    //flagDataType = 0 --> from popular movies Activty
    //flagDataType = 1 --> from favorites activity
    private int flagDataType;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ReviewFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ReviewFragment newInstance(int columnCount) {
        ReviewFragment fragment = new ReviewFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        intent = getActivity().getIntent();

        if (intent != null) {
            movieId = intent.getStringExtra("movieId");
            flagDataType = intent.getIntExtra("flagData",0);
        }

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            //get movieId from MovieDetailActivity
            movieId = getArguments().getString("movieId");

            List<Reviews> reviews = getArguments().getParcelableArrayList("review_list");
            movieReviewList = reviews;
        }

        if(savedInstanceState!=null) {
            movieId = savedInstanceState.getString("movieId");
            Log.v("savedInstanceState", "movieid = "+ movieId);
        }


        if(flagDataType==0) {
            //call trailers from API
            requestMovieReviews(movieId);
        }
        else {
            Log.v("xxxxxxxx", "trailers not getting from internet");
        }


    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("movieId", movieId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_review_list, container, false);


        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new MyReviewRecyclerViewAdapter(movieReviewList, mListener));
        }
        return view;
    }

    private void getLocalData() {
        movieReviewList = new ArrayList<>();
        List<Reviews> reviews = intent.getParcelableArrayListExtra("reviews");

        movieReviewList = reviews;
        for (Reviews r : movieReviewList) {
            System.out.println("Review===========> " + r.getAuthor());
        }

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Reviews review);
    }

    private void requestMovieReviews(String movieId){
        //http://api.themoviedb.org/3/reviews/293660/videos?api_key=6d369d4e0676612d2d046b7f3e8424bd

        movieReviewList = new ArrayList<>();

        final String BASE_PATH = "http://api.themoviedb.org/3/movie/";
        final String api_key = "?api_key=" + Config.API_KEY;
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
                            String author = "";
                            String content = "";

                            if (results!=null){
                                for (int i = 0; i < results.length(); i++) {

                                    JSONObject obj = results.getJSONObject(i);
                                    author = obj.getString("author");
                                    content = obj.getString("content");

                                    //add to a review object
                                    Reviews reviews = new Reviews(author, content);
                                    movieReviewList.add(reviews);

                                }

                            }

                            //==============================LOGS=================================/


                            if (movieReviewList!=null){
                                for (Reviews review:movieReviewList) {
                                    Log.d("AUTHOR: ",String.valueOf(review.getAuthor()));
                                    Log.d("CONTENT: ",review.getContent());
                                }
                            }
                            //====================================================================/

                            if(movieReviewList.size()==0){
                                author = "No Reviews Available";
                                content = " ";

                                //add to a review object
                                Reviews reviews = new Reviews(author, content);
                                movieReviewList.add(reviews);
                            }

                            Log.d("Review list size ", String.valueOf(movieReviewList.size()));

                            //updates recyclerview once data is fetched from the API call
                            recyclerView.getAdapter().notifyDataSetChanged();
                            reviewListAdapter.setItemList(movieReviewList);
                            reviewListAdapter.notifyDataSetChanged();



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

        reviewListAdapter = new MyReviewRecyclerViewAdapter(movieReviewList,mListener);


    }

}
