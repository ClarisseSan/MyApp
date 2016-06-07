package com.gerry.myapp.movies.object;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.gerry.myapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by gerry on 27/5/16.
 */
public class FavoriteListAdapter extends ArrayAdapter {

    private Context context;
    private LayoutInflater inflater;

    private List<String> imageUrls;
    private List<FavoriteMovie> favoriteMovies;

    public FavoriteListAdapter(Context context, List<FavoriteMovie> favoriteMovies) {
        super(context, R.layout.gridview_movie_image, favoriteMovies);

        this.context = context;
        this.favoriteMovies = favoriteMovies;

        inflater = LayoutInflater.from(context);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        //check to see if we have a view
        if (convertView == null){
            //no view so create a new one
            imageView = (ImageView) inflater.inflate(R.layout.gridview_movie_image, parent, false);
        }else{
            //use the recycled view object
            imageView = (ImageView) convertView;
        }


        Picasso
                .with(context)
                .load(favoriteMovies.get(position).getMovie_image())
                .fit()
                .error(R.mipmap.error)
                .into(imageView);

        /*
        Glide
                .with(context)
                .load(movieImages.get(position).getMovie_image())
                .centerCrop()
                .crossFade()
                .error(R.mipmap.error)
                .into(imageView);
        */
        return imageView;
    }



    public int getCount() {
        if (favoriteMovies != null)
            return favoriteMovies.size();
        return 0;
    }

    public FavoriteMovie getItem(int position) {
        if (favoriteMovies != null)
            return favoriteMovies.get(position);
        return null;
    }




    public long getItemId(int position) {
        //for passing the movie_id of the selected movie
        FavoriteMovie favoriteMovie = favoriteMovies.get(position);
        return new Long(favoriteMovie.movie_id);

    }


    public List<FavoriteMovie> getItemList() {
        return favoriteMovies;
    }

    public void setItemList(List<FavoriteMovie> movieImages) {
        this.favoriteMovies = movieImages;
    }

}
