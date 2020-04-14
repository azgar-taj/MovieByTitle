package com.example.abdulazgartaj.moviebytitle;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class    movieAdapter extends ArrayAdapter {
    public movieAdapter(Context context, ArrayList<new_movie> moves) {
        super(context,0,moves);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View list_movies = convertView;
        if(list_movies==null){
            list_movies = LayoutInflater.from(getContext()).inflate(R.layout.one_movie,parent,false);
        }
        new_movie movie = (new_movie) getItem(position);

        TextView movie_name = (TextView)list_movies.findViewById(R.id.movie_name);
        movie_name.setText(movie.getMovie_name());

        TextView movie_year = (TextView)list_movies.findViewById(R.id.movie_year);
        movie_year.setText(movie.getMovie_year());

        return list_movies;
    }
}
