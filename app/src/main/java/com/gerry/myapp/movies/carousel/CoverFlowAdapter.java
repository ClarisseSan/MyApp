package com.gerry.myapp.movies.carousel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gerry.myapp.R;
import com.gerry.myapp.movies.object.FavoriteMovie;
import com.squareup.picasso.Picasso;

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
        Picasso
                .with(context)
                .load(data.get(position).getMovie_image())
                .fit()
                .error(R.mipmap.error)
                .into(viewHolder.gameImage);

        //set movie name
        viewHolder.gameName.setText(data.get(position).getMovie_name());

        convertView.setOnClickListener(onClickListener(position));

        return convertView;
    }

    private View.OnClickListener onClickListener(final int position) {
        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {
              //TODO: add click listener when movie is clicked
            }
        };
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
