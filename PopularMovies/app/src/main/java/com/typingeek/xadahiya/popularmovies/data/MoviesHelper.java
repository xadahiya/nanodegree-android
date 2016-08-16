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
    public static final int DATABASE_VERSION = 5;

    public MoviesHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    final String SQL_CREATE_MOVIES_TABLE = "CREATE TABLE " + MovieContract.MovieEntry.TABLE_NAME + " (" +
            MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY," +
            MovieContract.MovieEntry.COLUMN_MOVIE_ISADULT + " INTEGER NOT NULL, " +
            MovieContract.MovieEntry.COLUMN_BACKDROP_URL + " TEXT NOT NULL, " +
            MovieContract.MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
            MovieContract.MovieEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
            MovieContract.MovieEntry.COLUMN_BACKDROP_IMAGE + " TEXT NOT NULL," +
            MovieContract.MovieEntry.COLUMN_POPULARITY + " REAL NOT NULL,"+
            MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL," +
            MovieContract.MovieEntry.COLUMN_VOTE_COUNT + " INTEGER NOT NULL," +
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL" +
            " );";
    @Override
    public void onCreate(SQLiteDatabase database) {

        database.execSQL(SQL_CREATE_MOVIES_TABLE);
    }




    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.d("table test", SQL_CREATE_MOVIES_TABLE);
        database.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        database.execSQL(SQL_CREATE_MOVIES_TABLE);

    }
    }

