package com.typingeek.xadahiya.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    public TextView movie_title;
    public TextView movie_description;
    public TextView release_date;
    public TextView user_rating;
    public ImageView movie_poster;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        movie_title = (TextView) findViewById(R.id.movie_title);
        movie_description = (TextView) findViewById(R.id.movie_description);
        movie_poster = (ImageView) findViewById(R.id.movie_poster);
        release_date = (TextView) findViewById(R.id.release_date);
        user_rating = (TextView) findViewById(R.id.user_rating);
        Intent details = getIntent();
        Movie movie = details.getParcelableExtra("Movie");

        movie_title.setText(movie.getMtitle());
        movie_description.setText(movie.getMsummary());
        release_date.setText(movie.getMrelease_date());
        user_rating.setText(movie.getMvote_average().toString());
        Picasso.with(this)
                .load(movie.getMbackdrop_img())
                .into(movie_poster);
    }
}
