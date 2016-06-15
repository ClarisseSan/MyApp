package com.gerry.myapp.movies.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
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
public class TrailerExampleActivityFragment extends Fragment {

    private List<Trailer> movieTrailersList;
    private TrailerAdapter trailerListAdapter;
    private RecyclerView recyclerView;
    private View rootView;

    public TrailerExampleActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         rootView = inflater.inflate(R.layout.fragment_trailer_example, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_trailer);




        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(trailerListAdapter);
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestMovieTrailer("246655");
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
                                System.out.println("TRAILER NUMBER!!!!!!!!!!!!!!!!" + trailer.getTrailerNumber());

                            }

                            trailerListAdapter.setItemList(movieTrailersList);
                            trailerListAdapter.notifyDataSetChanged();
                            System.out.println("TRAILERLIST SIZE" + movieTrailersList.size());

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
                            showSuccessDialog(getActivity(), "No network connection", "This application requires an internet connection.").show();
                        }
                    }
                });


        // Add the request to the RequestQueue.
        queue.add(stringRequest);


        //TODO : move this later at the oncreateView when you also fetched the array for the local data
        trailerListAdapter = new TrailerAdapter(movieTrailersList);

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

    void initializeRecyclerView() {

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_trailer);
        recyclerView.setAdapter(new TrailerAdapter(movieTrailersList));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
    }
}
