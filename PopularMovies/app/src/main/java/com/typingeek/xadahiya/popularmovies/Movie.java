package com.typingeek.xadahiya.popularmovies;

/**
 * Created by xadahiya on 7/16/16.
 */
public class Movie {

    private boolean misAdult;
    private String mbackdrop_url;

    public String getMbackdrop_url() {
        return mbackdrop_url;
    }

    public String getMbackdrop_img() {
        return mbackdrop_img;
    }

    private String mtitle;
    private String msummary;
    private Float mpopularity;
    private Float mvote_average;
    private Integer mvote_count;
    private String mbackdrop_img;

    public Movie(boolean isAdult, String backdrop_url, String title, String summary, Float popularity, Float vote_average, Integer vote_count, String backdrop_img){

        misAdult = isAdult;
        mbackdrop_url = backdrop_url;
        mtitle = title;
        msummary = summary;
        mpopularity = popularity;
        mvote_average = vote_average;
        mvote_count = vote_count;
        mbackdrop_img = backdrop_img;
    }

    public Float getMpopularity() {
        return mpopularity;
    }

    public Float getMvote_average() {
        return mvote_average;
    }
}
