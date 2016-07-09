package com.gerry.myapp.movies.masterdetail;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gerry.myapp.R;
import com.gerry.myapp.movies.activity.MovieDetailActivity;
import com.gerry.myapp.movies.activity.SettingsActivity;
import com.gerry.myapp.movies.carousel.MainCarousel;
import com.gerry.myapp.movies.object.Config;
import com.gerry.myapp.movies.object.MovieImage;
import com.gerry.myapp.movies.object.Utils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * An activity representing a list of Posters. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link MovieDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class PosterListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     *
     * THIS IS THE MAIN ACTIVITY --> LAUNCHER
     */
    private boolean mTwoPane;


    private List<String> movieImages = new ArrayList<>();
    private ArrayList<MovieImage> list;

    private SharedPreferences mSharedPreferences;
    private String sortOption;
    private MovieItemRecyclerViewAdapter mAdapter;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private int progressStatus;
    private Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        // Set the progress status zero on each button click
        progressStatus = 0;
        handler = new Handler();


        // Start the lengthy operation in a background thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(progressStatus < 100){
                    // Update the progress status
                    progressStatus +=1;

                    // Try to sleep the thread for 20 milliseconds
                    try{
                        Thread.sleep(50);
                    }catch(InterruptedException e){
                        e.printStackTrace();
                    }

                    // Update the progress bar
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress(progressStatus);
                        }
                    });
                }
            }
        }).start(); // Start the operation





        recyclerView = (RecyclerView) findViewById(R.id.poster_list);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        int spanCount = 2; // 2 columns
        int spacing = 20; // 20px
        boolean includeEdge = false;
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));

        assert recyclerView != null;
        setupRecyclerView(recyclerView);


        if (findViewById(R.id.poster_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }


        // Get the instance of SharedPreferences object
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        sortOption = mSharedPreferences.getString(getString(R.string.pref_sort_key),"popular");


        updateMovies();


    }


    @Override
    protected void onStart() {
        super.onStart();
        updateMovies();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_popular_movies, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id==R.id.action_settings){
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        if (id==R.id.action_fav){
            //if movies are available in the favoriteList
            //then show FavoritesActivity
            try {
                if (Utils.getFavoriteMovies(this).length()>0){
                    Intent intent = new Intent(this, MainCarousel.class);
                    startActivity(intent);
                }else{
                    //inform user that no movies are added to favorites
                    Utils.showSuccessDialog(this,R.string.title_activity_favor,R.string.no_favorites).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
        return super.onOptionsItemSelected(item);
    }


    private void updateMovies() {
        // Get the instance of SharedPreferences object
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // We retrieve foreground and background color value as a string
        sortOption = mSharedPreferences.getString(getString(R.string.pref_sort_key),"popular");

        // getMovies();
        requestMovies();
    }

    private void requestMovies() {
        final String BASE_PATH = "http://api.themoviedb.org/3/movie/";
        final String sort_order = sortOption;
        final String api_key = "?api_key=" + Config.API_KEY;;

        String original_url = BASE_PATH + sort_order + api_key;
        System.out.println("ORIGINAL URL >>>>>>>>" + original_url);

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(PosterListActivity.this);



        // Formulate the request and handle the response.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, original_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Update the progress status
                        progressStatus = 100;

                        //hide progressbar
                        progressBar.setVisibility(View.GONE);

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
                            recyclerView.getAdapter().notifyDataSetChanged();

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
                            Utils.showSuccessDialog(PosterListActivity.this, R.string.no_connection, R.string.net).show();
                        }
                    }
                });


        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }


    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {

        mAdapter =  new MovieItemRecyclerViewAdapter(this, movieImages, list);
        recyclerView.setAdapter(mAdapter);
    }

    public class MovieItemRecyclerViewAdapter
            extends RecyclerView.Adapter<MovieItemRecyclerViewAdapter.ViewHolder> {

        //private final List<DummyContent.DummyItem> mValues;
        private List<String> imageUrls;
        private List<MovieImage> movieImages;
        private Context context;

        public MovieItemRecyclerViewAdapter(Context context, List<String> imageUrls, List<MovieImage> movieImages) {
            this.context = context;
            this.imageUrls = imageUrls;
            this.movieImages = movieImages;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_movie_poster, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = movieImages.get(position);

            //set image into image view
            Picasso
                    .with(context)
                    .load(movieImages.get(position).getMovie_image())
                    .fit()
                    .error(R.drawable.ic_error)
                    .placeholder(R.drawable.ic_loading)
                    .into(holder.mImageView);

            //set movie name to textView
            holder.mTitleView.setText(movieImages.get(position).getMovie_name());


            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString("movieId", Long.toString(holder.mItem.getMovie_id()));
                        arguments.putInt("flagData", 0);
                        arguments.putString("title", holder.mItem.getMovie_name());

                        PosterDetailFragment fragment = new PosterDetailFragment();
                        fragment.setArguments(arguments);
                        getFragmentManager().beginTransaction()
                                .replace(R.id.poster_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, MovieDetailActivity.class);
                        intent.putExtra("movieId", Long.toString(holder.mItem.getMovie_id()))
                                .putExtra("flagData", 0)
                                .putExtra("title", holder.mItem.getMovie_name());

                        context.startActivity(intent);
                    }
                }
            });
        }


        public void setItemList(List<MovieImage> movieImages) {
            this.movieImages = movieImages;
        }

        @Override
        public int getItemCount() {
            if (movieImages != null)
                return movieImages.size();
            return 0;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final ImageView mImageView;
            public final TextView mTitleView;

            public MovieImage mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mImageView = (ImageView) view.findViewById(R.id.img_movie);
                mTitleView = (TextView) view.findViewById(R.id.txt_name);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mTitleView.getText() + "'";
            }
        }
    }
}
