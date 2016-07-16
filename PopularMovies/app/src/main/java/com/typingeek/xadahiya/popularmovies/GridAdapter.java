package com.typingeek.xadahiya.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class GridAdapter extends ArrayAdapter {
    private Context context;
    private LayoutInflater inflater;

    private List<Movie> movies;

    public GridAdapter(Context context, List<Movie> movies) {
        super(context, R.layout.gridview_item_image, movies);

        this.context = context;
        this.movies = movies;

        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.gridview_item_image, parent, false);
        }

        Picasso
                .with(context)
                .load(movies.get(position).getMbackdrop_img())
                .fit() // will explain later
                .into((ImageView) convertView);

        return convertView;
    }
}