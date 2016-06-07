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
 * Created by gerry on 17/5/16.
 * code tutorial: https://futurestud.io/blog/picasso-adapter-use-for-listview-gridview-etc
 *
 * code for using picasso
 */
public class ImageListAdapter extends ArrayAdapter {

    private Context context;
    private LayoutInflater inflater;

    private List<String> imageUrls;
    private List<MovieImage> movieImages;

    public ImageListAdapter(Context context, List<String> imageUrls, List<MovieImage> movieImages) {
        super(context, R.layout.gridview_movie_image, imageUrls);

        this.context = context;
        this.imageUrls = imageUrls;
        this.movieImages = movieImages;

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
                .load(movieImages.get(position).getMovie_image())
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
        if (movieImages != null)
            return movieImages.size();
        return 0;
    }

    public MovieImage getItem(int position) {
        if (movieImages != null)
            return movieImages.get(position);
        return null;
    }




    public long getItemId(int position) {
        //for passing the movie_id of the selected movie
        MovieImage movieImage = movieImages.get(position);
        return new Long(movieImage.movie_id);

    }


    public List<MovieImage> getItemList() {
        return movieImages;
    }

    public void setItemList(List<MovieImage> movieImages) {
        this.movieImages = movieImages;
    }
}