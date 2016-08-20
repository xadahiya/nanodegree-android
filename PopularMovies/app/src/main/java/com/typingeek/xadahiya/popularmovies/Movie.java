package com.typingeek.xadahiya.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by xadahiya on 7/16/16.
 */
public class Movie implements Parcelable {

    private boolean misAdult;
    private String mbackdrop_url;

    public String getMrelease_date() {
        return mrelease_date;
    }

    public String getMtitle() {
        return mtitle;
    }

    public String getMsummary() {
        return msummary;
    }

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
    private String mrelease_date;
    private Boolean misFavourite;

    public Boolean getMisFavourite() {
        return misFavourite;
    }

    public void setMisFavourite(Boolean misFavourite) {
        this.misFavourite = misFavourite;
    }

    public Movie(boolean isAdult, String backdrop_url, String title, String summary, Float popularity, Float vote_average, Integer vote_count, String backdrop_img, String release_date, boolean isFavourite){

        misAdult = isAdult;
        mbackdrop_url = backdrop_url;
        mtitle = title;
        msummary = summary;
        mpopularity = popularity;
        mvote_average = vote_average;
        mvote_count = vote_count;
        mbackdrop_img = backdrop_img;
        mrelease_date = release_date;

        misFavourite = isFavourite;
    }

    public Float getMpopularity() {
        return mpopularity;
    }

    public Float getMvote_average() {
        return mvote_average;
    }



    protected Movie(Parcel in) {
        misAdult = in.readByte() != 0x00;
        mbackdrop_url = in.readString();
        mtitle = in.readString();
        msummary = in.readString();
        mpopularity = in.readByte() == 0x00 ? null : in.readFloat();
        mvote_average = in.readByte() == 0x00 ? null : in.readFloat();
        mvote_count = in.readByte() == 0x00 ? null : in.readInt();
        mbackdrop_img = in.readString();
        mrelease_date = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (misAdult ? 0x01 : 0x00));
        dest.writeString(mbackdrop_url);
        dest.writeString(mtitle);
        dest.writeString(msummary);
        if (mpopularity == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeFloat(mpopularity);
        }
        if (mvote_average == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeFloat(mvote_average);
        }
        if (mvote_count == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(mvote_count);
        }
        dest.writeString(mbackdrop_img);
        dest.writeString(mrelease_date);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}