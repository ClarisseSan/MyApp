package com.gerry.myapp.movies.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.gerry.myapp.R;
import com.gerry.myapp.movies.fragment.TrailerFragment;
import com.gerry.myapp.movies.object.Trailer;

public class TrailerExampleActivity extends FragmentActivity implements TrailerFragment.OnListFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trailer_example);

        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }
            TrailerFragment firstFragment = new TrailerFragment();
            firstFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, firstFragment).commit();
        }
    }




    @Override
    public void onListFragmentInteraction(Trailer trailer) {

    }
}
