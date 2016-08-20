package com.typingeek.xadahiya.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by xadahiya on 8/16/16.
 */
public class MoviesHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "movies.db";
    public static final int DATABASE_VERSION = 7
            ;

    public MoviesHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    final String SQL_CREATE_MOSTPOPULARMOVIES_TABLE = "CREATE TABLE " + MovieContract.MostPopularMovieEntry.TABLE_NAME + " (" +
            MovieContract.MostPopularMovieEntry._ID + " INTEGER PRIMARY KEY," +
            MovieContract.MostPopularMovieEntry.COLUMN_MOVIE_ISADULT + " INTEGER NOT NULL, " +
            MovieContract.MostPopularMovieEntry.COLUMN_BACKDROP_URL + " TEXT NOT NULL, " +
            MovieContract.MostPopularMovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
            MovieContract.MostPopularMovieEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
            MovieContract.MostPopularMovieEntry.COLUMN_BACKDROP_IMAGE + " TEXT NOT NULL," +
            MovieContract.MostPopularMovieEntry.COLUMN_POPULARITY + " REAL NOT NULL,"+
            MovieContract.MostPopularMovieEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL," +
            MovieContract.MostPopularMovieEntry.COLUMN_VOTE_COUNT + " INTEGER NOT NULL," +
            MovieContract.MostPopularMovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL," +
            MovieContract.MostPopularMovieEntry.COLUMN_IS_FAVOURITE + " INTEGER NOT NULL"+
            " );";

    final String SQL_CREATE_TOPRATEDMOVIES_TABLE = "CREATE TABLE " + MovieContract.TopRatedMovieEntry.TABLE_NAME + " (" +
            MovieContract.TopRatedMovieEntry._ID + " INTEGER PRIMARY KEY," +
            MovieContract.TopRatedMovieEntry.COLUMN_MOVIE_ISADULT + " INTEGER NOT NULL, " +
            MovieContract.TopRatedMovieEntry.COLUMN_BACKDROP_URL + " TEXT NOT NULL, " +
            MovieContract.TopRatedMovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
            MovieContract.TopRatedMovieEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
            MovieContract.TopRatedMovieEntry.COLUMN_BACKDROP_IMAGE + " TEXT NOT NULL," +
            MovieContract.TopRatedMovieEntry.COLUMN_POPULARITY + " REAL NOT NULL,"+
            MovieContract.TopRatedMovieEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL," +
            MovieContract.TopRatedMovieEntry.COLUMN_VOTE_COUNT + " INTEGER NOT NULL," +
            MovieContract.TopRatedMovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL," +
            MovieContract.TopRatedMovieEntry.COLUMN_IS_FAVOURITE + " INTEGER NOT NULL"+
            " );";

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(SQL_CREATE_MOSTPOPULARMOVIES_TABLE);
        database.execSQL(SQL_CREATE_TOPRATEDMOVIES_TABLE);
        Log.d("Database created","Database created" + SQL_CREATE_MOSTPOPULARMOVIES_TABLE+ SQL_CREATE_TOPRATEDMOVIES_TABLE);

    }




    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.d("table test", SQL_CREATE_MOSTPOPULARMOVIES_TABLE);
        Log.d("table test", SQL_CREATE_TOPRATEDMOVIES_TABLE);
        database.execSQL("DROP TABLE IF EXISTS " + MovieContract.MostPopularMovieEntry.TABLE_NAME);
        database.execSQL("DROP TABLE IF EXISTS " + MovieContract.TopRatedMovieEntry.TABLE_NAME);
        database.execSQL(SQL_CREATE_MOSTPOPULARMOVIES_TABLE);
        database.execSQL(SQL_CREATE_TOPRATEDMOVIES_TABLE);

    }
    }

