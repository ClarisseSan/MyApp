package com.gerry.myapp.movies.carousel;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcelable;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gerry.myapp.R;
import com.gerry.myapp.movies.activity.MovieDetailActivity;
import com.gerry.myapp.movies.object.FavoriteMovie;

import java.util.ArrayList;

/**
 * Created by isse on 29 Jun 2016.
 */
public class CoverFlowAdapter extends BaseAdapter {


    private ArrayList<FavoriteMovie> data;
    private Context context;

    public CoverFlowAdapter(Context context, ArrayList<FavoriteMovie> objects) {
        this.context = context;
        this.data = objects;
    }


    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public FavoriteMovie getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_flow_view, null, false);

            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        //set movie poster
        viewHolder.gameImage.setImageBitmap(decodeBase64Image(position));


        //set movie name
        viewHolder.gameName.setText(data.get(position).getMovie_name());

        convertView.setOnClickListener(onClickListener(position));

        return convertView;
    }

    private Bitmap decodeBase64Image(int position) {
        byte[] decodedString = Base64.decode(data.get(position).getMovie_image(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        return decodedByte;
    }

    private View.OnClickListener onClickListener(final int position) {
        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                        Intent i = new Intent(context, MovieDetailActivity.class)
                        //pass the selected movie_id to the next Activity
                        .putExtra("flagData", 1)
                        .putExtra("movieId", data.get(position).getMovie_id())
                        .putExtra("title", data.get(position).getMovie_name())
                        .putExtra("year", data.get(position).getMovie_date())
                        .putExtra("rating", data.get(position).getMovie_vote())
                        .putExtra("overview", data.get(position).getMovie_overview())
                        .putExtra("poster", data.get(position).getMovie_image())
                        .putExtra("duration", data.get(position).getMovie_duration())
                        .putParcelableArrayListExtra("trailers", (ArrayList<? extends Parcelable>) data.get(position).getMovie_trailerList())
                        .putParcelableArrayListExtra("reviews", (ArrayList<? extends Parcelable>) data.get(position).getReviewsList());

                context.startActivity(i);


            }
        };
    }

    public void setItemList(ArrayList<FavoriteMovie> list) {
        this.data = list;
    }


    private static class ViewHolder {
        private TextView gameName;
        private ImageView gameImage;

        public ViewHolder(View v) {
            gameImage = (ImageView) v.findViewById(R.id.image);
            gameName = (TextView) v.findViewById(R.id.name);
        }
    }
}
