package com.example.abdulazgartaj.moviebytitle;

public class new_movie {
    private String movie_name;
    private String movie_year;
    private String movie_id;
    new_movie(String a,String b,String c){
        movie_name = a;
        movie_year = b;
        movie_id = c;
    }
    public String getMovie_name() {
        return movie_name;
    }
    public String getMovie_year() {
        return movie_year;
    }
    public String getMovie_id() {
        return movie_id;
    }
}
