package com.gerry.myapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.gerry.myapp.movies.activity.PopularMoviesActivity;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);
        /** Called when the user touches the button */
        Button btnMovies = (Button) view.findViewById(R.id.btn_movie);
        Button btnStock = (Button) view.findViewById(R.id.btn_stock);
        Button btnBuild = (Button) view.findViewById(R.id.btn_build);
        Button btnMake = (Button) view.findViewById(R.id.btn_make);
        Button btnGo = (Button) view.findViewById(R.id.btn_go);
        Button btnCapstone = (Button) view.findViewById(R.id.btn_capstone);



        btnMovies.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                //display in short period of time
                Toast.makeText(getActivity(), "This button will launch my Popular Movies app!",
                        Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), PopularMoviesActivity.class);
                startActivity(intent);

            }
        });


        btnStock.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                //display in short period of time
                Toast.makeText(getActivity(), "This button will launch my Stock Hawk app!",
                        Toast.LENGTH_SHORT).show();
            }
        });

        btnBuild.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                //display in short period of time
                Toast.makeText(getActivity(), "This button will launch my Build It Bigger app!",
                        Toast.LENGTH_SHORT).show();
            }
        });


        btnMake.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                //display in short period of time
                Toast.makeText(getActivity(), "This button will launch my Make Your App Material app!",
                        Toast.LENGTH_SHORT).show();
            }
        });

        btnGo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                //display in short period of time
                Toast.makeText(getActivity(), "This button will launch my Go Ubiquitous app!",
                        Toast.LENGTH_SHORT).show();
            }
        });

        btnCapstone.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                //display in short period of time
                Toast.makeText(getActivity(), "This button will launch my Capstone app!",
                        Toast.LENGTH_SHORT).show();
            }
        });


        return view;
    }


}
