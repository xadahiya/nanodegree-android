package com.typingeek.xadahiya.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Created by xadahiya on 8/16/16.
 */
public class MovieProvider extends ContentProvider{

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    static final int MOSTPOPULARMOVIE = 100;
    static final int TOPRATEDMOVIE =200;
    private MoviesHelper mMoviesHelper;

    @Override
    public String getType(Uri uri){
        final int match = sUriMatcher.match(uri);

        switch (match){
            case MOSTPOPULARMOVIE:
                return MovieContract.MostPopularMovieEntry.CONTENT_TYPE;
            case TOPRATEDMOVIE:
                return MovieContract.TopRatedMovieEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

    }

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;


        matcher.addURI(authority, MovieContract.PATH_TRMOVIE, TOPRATEDMOVIE);
        matcher.addURI(authority, MovieContract.PATH_MPMOVIE, MOSTPOPULARMOVIE);
        return matcher;
        }

    @Override
    public boolean onCreate() {
        mMoviesHelper = new MoviesHelper(getContext());
        return true;
    }


    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs){
        final SQLiteDatabase db = mMoviesHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case MOSTPOPULARMOVIE:
                rowsUpdated = db.update(MovieContract.MostPopularMovieEntry.TABLE_NAME, contentValues, selection, selectionArgs);
                break;
            case TOPRATEDMOVIE:
                rowsUpdated = db.update(MovieContract.TopRatedMovieEntry.TABLE_NAME, contentValues, selection,selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
                if (rowsUpdated != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsUpdated;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values){
        final SQLiteDatabase db = mMoviesHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match){
            case MOSTPOPULARMOVIE:
                long _id = db.insert(MovieContract.MostPopularMovieEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MovieContract.MostPopularMovieEntry.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;

            case TOPRATEDMOVIE:
                _id = db.insert(MovieContract.TopRatedMovieEntry.TABLE_NAME, null, values);
                if( _id > 0){
                    returnUri = MovieContract.TopRatedMovieEntry.buildMovieUri(_id);
                }
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs){
        final SQLiteDatabase db = mMoviesHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case MOSTPOPULARMOVIE:
                rowsDeleted = db.delete(
                        MovieContract.MostPopularMovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TOPRATEDMOVIE:
                rowsDeleted = db.delete(MovieContract.TopRatedMovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder){
        Cursor retCursor;

        switch (sUriMatcher.match(uri)){
            case MOSTPOPULARMOVIE: {
                retCursor = mMoviesHelper.getReadableDatabase().query(
                        MovieContract.MostPopularMovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            case TOPRATEDMOVIE: {
                retCursor = mMoviesHelper.getReadableDatabase().query(
                        MovieContract.TopRatedMovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

            }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;

    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mMoviesHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOSTPOPULARMOVIE:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.MostPopularMovieEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;

            case TOPRATEDMOVIE:
                db.beginTransaction();
                returnCount = 0;
                try{
                    for (ContentValues value : values){
                        long _id = db.insert(MovieContract.TopRatedMovieEntry.TABLE_NAME, null, value);
                        if(_id != -1){
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;

            default:
                return super.bulkInsert(uri, values);
        }
    }
}
