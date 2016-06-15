package com.gerry.myapp.movies.object;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.gerry.myapp.R;
import com.gerry.myapp.movies.object.Trailer;

import java.util.List;

/**
 * Created by gerry on 26/5/16.
 */
public class TrailerListAdapter extends ArrayAdapter {

    private  List<Trailer> trailers;

    public TrailerListAdapter(Context context, List<Trailer> trailers) {
        // Here, we initialize the ArrayAdapter's internal storage for the context and the list.
        // the second argument is used when the ArrayAdapter is populating a single TextView.
        // Because this is a custom adapter for two TextViews and an ImageView, the adapter is not
        // going to use this second argument, so it can be any value. Here, we used 0.

        super(context, 0, trailers);
        this.trailers = trailers;
}


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Gets the trailer object from the ArrayAdapter at the appropriate position
        Trailer trailer = getItem(position);

        // Adapters recycle views to AdapterViews.
        // If this is a new View object we're getting, then inflate the layout.
        // If not, this view already has the layout inflated from a previous call to getView,
        // and we modify the View widgets as usual.
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_trailers, parent, false);
        }

        TextView versionNameView = (TextView) convertView.findViewById(R.id.txt_trailer);
        versionNameView.setText(trailer.getTrailerNumber());

        return convertView;

    }

    public List<Trailer> getItemList() {
        return trailers;
    }

    public void setItemList(List<Trailer> trailers) {
        this.trailers = trailers;
    }



    public int getCount() {
        if (trailers != null)
            return trailers.size();
        return 0;
    }

    public Trailer getItem(int position) {
        if (trailers != null)
            return trailers.get(position);
        return null;
    }



}
