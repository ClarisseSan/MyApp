package com.gerry.myapp.movies;

import android.os.Bundle;
import android.app.Activity;

import com.gerry.myapp.R;

public class FavorActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favor);
        //getActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
