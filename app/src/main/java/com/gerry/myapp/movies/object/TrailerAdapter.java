package com.gerry.myapp.movies.object;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gerry.myapp.R;

import java.util.List;

/**
 * Created by gerry on 27/5/16.
 *
 * TUTORIAL: http://www.androidhive.info/2016/01/android-working-with-recycler-view/
 */
public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.MyViewHolder>{
    private List<Trailer> trailerList;



    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView trailerNumber, trailerURL;

        public MyViewHolder(View view) {
            super(view);
            trailerNumber = (TextView) view.findViewById(R.id.txt_trailer);

        }
    }


    public TrailerAdapter(List<Trailer> trailerList) {
        this.trailerList = trailerList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_trailers, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Trailer trailer = trailerList.get(position);
        holder.trailerNumber.setText(trailer.getTrailerNumber());
      //  holder.trailerURL.setText(trailer.getTrailerUrl());
    }

    @Override
    public int getItemCount() {
        return trailerList.size();
    }

    public List<Trailer> getItemList() {
        return trailerList;
    }

    public void setItemList(List<Trailer> trailers) {
        this.trailerList = trailers;
    }


}

