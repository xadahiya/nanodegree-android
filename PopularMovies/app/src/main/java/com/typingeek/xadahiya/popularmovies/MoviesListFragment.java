package com.typingeek.xadahiya.popularmovies;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.typingeek.xadahiya.popularmovies.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

public class MoviesListFragment extends Fragment {

    public NetworkChangeReceiver mNetworkChangeReceiver;
    public GridAdapter gridAdapter;

    private static final String[] MOST_POPULAR_PROJECTION = new String[]{
            MovieContract.MostPopularMovieEntry._ID,
            MovieContract.MostPopularMovieEntry.COLUMN_MOVIE_ISADULT,
            MovieContract.MostPopularMovieEntry.COLUMN_BACKDROP_URL,
            MovieContract.MostPopularMovieEntry.COLUMN_TITLE,
            MovieContract.MostPopularMovieEntry.COLUMN_DESCRIPTION,
            MovieContract.MostPopularMovieEntry.COLUMN_BACKDROP_IMAGE,
            MovieContract.MostPopularMovieEntry.COLUMN_POPULARITY,
            MovieContract.MostPopularMovieEntry.COLUMN_VOTE_AVERAGE,
            MovieContract.MostPopularMovieEntry.COLUMN_VOTE_COUNT,
            MovieContract.MostPopularMovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MostPopularMovieEntry.COLUMN_IS_FAVOURITE
    };

    private static final String[] TOP_RATED_PROJECTION = new String[]{
            MovieContract.TopRatedMovieEntry._ID,
            MovieContract.TopRatedMovieEntry.COLUMN_MOVIE_ISADULT,
            MovieContract.TopRatedMovieEntry.COLUMN_BACKDROP_URL,
            MovieContract.TopRatedMovieEntry.COLUMN_TITLE,
            MovieContract.TopRatedMovieEntry.COLUMN_DESCRIPTION,
            MovieContract.TopRatedMovieEntry.COLUMN_BACKDROP_IMAGE,
            MovieContract.TopRatedMovieEntry.COLUMN_POPULARITY,
            MovieContract.TopRatedMovieEntry.COLUMN_VOTE_AVERAGE,
            MovieContract.TopRatedMovieEntry.COLUMN_VOTE_COUNT,
            MovieContract.TopRatedMovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.TopRatedMovieEntry.COLUMN_IS_FAVOURITE
    };

    // these indices must match the projection
    private static final int INDEX_ID = 0;
    private static final int INDEX_MOVIE_ISADULT = 1;
    private static final int INDEX_BACKDROP_URL = 2;
    private static final int INDEX_TITLE = 3;
    private static final int INDEX_DESC = 4;
    private static final int INDEX_BACKDROP_IMAGE = 5;
    private static final int INDEX_POPULARITY = 6;
    private static final int INDEX_VOTE_AVERAGE = 7;
    private static final int INDEX_VOTE_COUNT = 8;
    private static final int INDEX_RELEASE_DATE = 1;
    private static final int INDEX_IS_FAVOURITE = 1;


    public List<Movie> getAllMovies() {
        List<Movie> movieList = new ArrayList<Movie>();
        // Select All Query


//            SQLiteDatabase db = this.getWritableDatabase();
//            Cursor cursor = db.rawQuery(selectQuery, null);
        Uri moviesUri = MovieContract.MostPopularMovieEntry.CONTENT_URI;
        Cursor cursor = getContext().getContentResolver().query(moviesUri, MOST_POPULAR_PROJECTION, null, null, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Boolean isAdult = (cursor.getInt(INDEX_MOVIE_ISADULT) == 1) ? true : false;
                String backdrop_url = cursor.getString(INDEX_BACKDROP_URL);
                String title = cursor.getString(INDEX_TITLE);
                String description = cursor.getString(INDEX_DESC);
                Float popularity = cursor.getFloat(INDEX_POPULARITY);
                Float vote_average = cursor.getFloat(INDEX_VOTE_AVERAGE);
                Integer vote_count = cursor.getInt(INDEX_VOTE_COUNT);
                String backdrop_img = cursor.getString(INDEX_BACKDROP_IMAGE);
                String release_date = cursor.getString(INDEX_RELEASE_DATE);
                Boolean isFavourite = (cursor.getInt(INDEX_IS_FAVOURITE) == 1) ? true : false;
                Movie movie = new Movie(isAdult, backdrop_url, title, description, popularity, vote_average, vote_count, backdrop_img, release_date, isFavourite);
                Log.d("Getting data", cursor.getString(INDEX_BACKDROP_IMAGE));
                movieList.add(movie);
            } while (cursor.moveToNext());
        }

        Log.d("MovieList", movieList.toString());
        return movieList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Movie[] test_movies = {
        };

        List<Movie> movieList = new ArrayList<>(

                Arrays.asList(test_movies)
        );


        View v = inflater.inflate(R.layout.activity_movies_list_fragment, container, false);
        GridView gridview = (GridView) v.findViewById(R.id.movies_gridview);
        gridAdapter = new GridAdapter(getActivity(), movieList);
        gridview.setAdapter(gridAdapter);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                Movie movie = gridAdapter.getItem(position);
                intent.putExtra("Movie", movie);
//                Log.d("Item", gridAdapter.getItem(position).getMpopularity().toString());
//                Toast.makeText(getActivity(), "" + position,
//                        Toast.LENGTH_SHORT).show();

                startActivity(intent);
            }
        });
        return v;

    }

    public void UpdateMovies() {
        FetchMovies fetchMovies = new FetchMovies();
        fetchMovies.execute();
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();

        mNetworkChangeReceiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        getActivity().registerReceiver(mNetworkChangeReceiver, intentFilter);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        getActivity().unregisterReceiver(mNetworkChangeReceiver);
    }

    private List<Movie> getMovieDataFromJson(String movieJsonStr)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String OWM_RESULT = "results";
        final String OWM_ADULT = "adult";
        final String OWM_BACKDROP_PATH = "backdrop_path";
        final String OWM_TITLE = "original_title";
        final String OWM_DESCRIPTION = "overview";
        final String OWM_BACKDROP_IMG = "poster_path";
        final String OWM_POPULARITY = "popularity";
        final String OWM_VOTE_AVERAGE = "vote_average";
        final String OWM_VOTE_COUNT = "vote_count";
        final String OWM_RELEASE_DATE = "release_date";
        try {
            JSONObject forecastJson = new JSONObject(movieJsonStr);
            JSONArray resultArray = forecastJson.getJSONArray(OWM_RESULT);

            // Vector for bulkInserting data
            Vector<ContentValues> cVVector = new Vector<ContentValues>(resultArray.length());
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String sort_mode = pref.getString(getString(R.string.sort_mode), getString(R.string.sort_default));

//            Movie[] movie_list = new Movie[resultArray.length()];
            for (int i = 0; i < resultArray.length(); i++) {
                JSONObject movieObject = resultArray.getJSONObject(i);

                boolean isAdult = movieObject.getBoolean(OWM_ADULT);
                String backdrop_url = "http://image.tmdb.org/t/p/w185/" + movieObject.getString(OWM_BACKDROP_PATH);
                String title = movieObject.getString(OWM_TITLE);
                String description = movieObject.getString(OWM_DESCRIPTION);
                String backdrop_img = "http://image.tmdb.org/t/p/w185/" + movieObject.getString(OWM_BACKDROP_IMG);
                Float popularity = Float.parseFloat(movieObject.get(OWM_POPULARITY).toString());
                Float vote_average = Float.parseFloat(movieObject.get(OWM_VOTE_AVERAGE).toString());
                Integer vote_count = movieObject.getInt(OWM_VOTE_COUNT);
                String release_date = movieObject.getString(OWM_RELEASE_DATE);
                Log.d("movie", backdrop_url);
//                movie_list[i] = new Movie(isAdult, backdrop_url, title, description, popularity, vote_average, vote_count, backdrop_img, release_date);


                ContentValues movieValues = new ContentValues();

                if (sort_mode.equals("popularity")) {
                    movieValues.put(MovieContract.MostPopularMovieEntry.COLUMN_MOVIE_ISADULT, (isAdult) ? 1 : 0);
                    movieValues.put(MovieContract.MostPopularMovieEntry.COLUMN_BACKDROP_URL, backdrop_url);
                    movieValues.put(MovieContract.MostPopularMovieEntry.COLUMN_TITLE, title);
                    movieValues.put(MovieContract.MostPopularMovieEntry.COLUMN_DESCRIPTION, description);
                    movieValues.put(MovieContract.MostPopularMovieEntry.COLUMN_BACKDROP_IMAGE, backdrop_img);
                    movieValues.put(MovieContract.MostPopularMovieEntry.COLUMN_POPULARITY, popularity);
                    movieValues.put(MovieContract.MostPopularMovieEntry.COLUMN_VOTE_AVERAGE, vote_average);
                    movieValues.put(MovieContract.MostPopularMovieEntry.COLUMN_VOTE_COUNT, vote_count);
                    movieValues.put(MovieContract.MostPopularMovieEntry.COLUMN_RELEASE_DATE, release_date);
                    movieValues.put(MovieContract.MostPopularMovieEntry.COLUMN_IS_FAVOURITE, 0);
                } else if (sort_mode.equals("rating")) {

                    movieValues.put(MovieContract.TopRatedMovieEntry.COLUMN_MOVIE_ISADULT, (isAdult) ? 1 : 0);
                    movieValues.put(MovieContract.TopRatedMovieEntry.COLUMN_BACKDROP_URL, backdrop_url);
                    movieValues.put(MovieContract.TopRatedMovieEntry.COLUMN_TITLE, title);
                    movieValues.put(MovieContract.TopRatedMovieEntry.COLUMN_DESCRIPTION, description);
                    movieValues.put(MovieContract.TopRatedMovieEntry.COLUMN_BACKDROP_IMAGE, description);
                    movieValues.put(MovieContract.TopRatedMovieEntry.COLUMN_POPULARITY, popularity);
                    movieValues.put(MovieContract.TopRatedMovieEntry.COLUMN_VOTE_AVERAGE, vote_average);
                    movieValues.put(MovieContract.TopRatedMovieEntry.COLUMN_VOTE_COUNT, vote_count);
                    movieValues.put(MovieContract.TopRatedMovieEntry.COLUMN_RELEASE_DATE, release_date);
                    movieValues.put(MovieContract.TopRatedMovieEntry.COLUMN_IS_FAVOURITE, 0);
                } else {
                    Log.d("database test", sort_mode);
                }

                cVVector.add(movieValues);
            }

            int inserted = 0;

            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                if (sort_mode.equals("popularity")) {
                    inserted = getContext().getContentResolver().bulkInsert(MovieContract.MostPopularMovieEntry.CONTENT_URI, cvArray);
                    Log.d("Database test", "FetchWeatherTask Complete. " + inserted + " Inserted in MostPopularMovies table");
                } else if (sort_mode.equals("rating")) {
                    inserted = getContext().getContentResolver().bulkInsert(MovieContract.TopRatedMovieEntry.CONTENT_URI, cvArray);
                    Log.d("Database test", "FetchWeatherTask Complete. " + inserted + " Inserted in TopRated movies table");
                } else {
                    Log.d("Database test", sort_mode + "insertion to database failed");
                }

            }


        } catch (JSONException e) {
            Log.e("Database test", e.getMessage(), e);
            e.printStackTrace();
        }
        return getAllMovies();
    }


    public class FetchMovies extends AsyncTask<String, Void, List<Movie>> {
        private final String LOG_TAG = FetchMovies.class.getSimpleName();

        protected List<Movie> doInBackground(String... urls) {

            // These two need to be declared outside the try/catch
// so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

// Will contain the raw JSON response as a string.
            String movieJsonstr = null;
            String format = "json";


            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                String sort_mode = pref.getString(getString(R.string.sort_mode), getString(R.string.sort_default));
                final String MOVIE_BASE_URL;
                if (sort_mode.equals("popularity")) {
                    MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie/popular?";
                    ;
                } else if (sort_mode.equals("rating")) {
                    MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie/top_rated?";
                } else {
                    Log.d(LOG_TAG, "Unit type not found" + sort_mode);
                    MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie/top_rated?";
                }


                final String API_KEY_PARAM = "api_key";

                Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                        .appendQueryParameter(API_KEY_PARAM, "b9c5a823d73daf11cb998036021c8c20")
                        .build();

                URL url = new URL(builtUri.toString());

//                Log.v(LOG_TAG, "Built URI " + builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                movieJsonstr = buffer.toString();
                Log.e(LOG_TAG, movieJsonstr);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getMovieDataFromJson(movieJsonstr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;

        }

        public Comparator<Movie> Sort = new Comparator<Movie>() {
            @Override
            public int compare(Movie o1, Movie o2) {
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                String sort_mode = pref.getString(getString(R.string.sort_mode), getString(R.string.sort_default));

                if (sort_mode.equals("popularity")) {
                    return o1.getMpopularity().compareTo(o2.getMpopularity());
                } else if (sort_mode.equals("rating")) {
                    return o1.getMvote_average().compareTo(o2.getMvote_average());
                } else {
                    Log.d(LOG_TAG, "Unit type not found" + sort_mode);
                }
                return 0;
            }
        };


        @Override
        protected void onPostExecute(List<Movie> result) {
//            super.onPostExecute(strings);


//            List<Movie> movieResult = new ArrayList<>(
//
//                    Arrays.asList(result)
//            );
//
////            Collections.sort(movieResult, Sort);
            if (result != null) {
                gridAdapter.clear();
                for (Movie movie : result) {
                    gridAdapter.add(movie);
//                }
//            }

                }
            }
        }

    }

        public class NetworkChangeReceiver extends BroadcastReceiver {

            private static final String LOG_TAG = "NetworkChangeReceiver";

            @Override
            public void onReceive(final Context context, Intent intent) {
                Log.v(LOG_TAG, "Receieved notification about network status");
                isNetworkAvailable(context);
            }

            private void isNetworkAvailable(Context context) {
                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

                if (connectivityManager != null) {

                    NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
                    if (activeNetwork != null) {
                        UpdateMovies();
                        Log.d(LOG_TAG, "Movie list updated using broadcast receiver");
                    }
                }
            }
        }


}


