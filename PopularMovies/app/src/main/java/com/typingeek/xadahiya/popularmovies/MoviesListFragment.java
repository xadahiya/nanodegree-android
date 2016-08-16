package com.typingeek.xadahiya.popularmovies;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
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
    public  GridAdapter gridAdapter ;
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

    public void UpdateMovies(){
        FetchMovies fetchMovies = new FetchMovies();
        fetchMovies.execute();
    }



    @Override
    public void onStart(){
        super.onStart();
    }

    @Override
    public void onResume(){
        super.onResume();

        mNetworkChangeReceiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        getActivity().registerReceiver(mNetworkChangeReceiver, intentFilter);


    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        getActivity().unregisterReceiver(mNetworkChangeReceiver);
    }

    private Movie[] getMovieDataFromJson(String movieJsonStr)
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

            Movie[] movie_list = new Movie[resultArray.length()];
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
                movie_list[i] = new Movie(isAdult, backdrop_url, title, description, popularity, vote_average, vote_count, backdrop_img, release_date);


                ContentValues movieValues = new ContentValues();

                movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ISADULT, (isAdult) ? 1 : 0);
                movieValues.put(MovieContract.MovieEntry.COLUMN_BACKDROP_URL, backdrop_url);
                movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, title);
                movieValues.put(MovieContract.MovieEntry.COLUMN_DESCRIPTION, description);
                movieValues.put(MovieContract.MovieEntry.COLUMN_BACKDROP_IMAGE, description);
                movieValues.put(MovieContract.MovieEntry.COLUMN_POPULARITY, popularity);
                movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, vote_average);
                movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_COUNT, vote_count);
                movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, release_date);

                cVVector.add(movieValues);
            }

            int inserted = 0;

            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                inserted = getContext().getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, cvArray);

            }

            Log.d("Database test", "FetchWeatherTask Complete. " + inserted + " Inserted");

        } catch (JSONException e) {
            Log.e("Database test", e.getMessage(), e);
            e.printStackTrace();
    }
        Movie[] movie_list = new Movie[4];
        return movie_list;
    }


    public class FetchMovies extends AsyncTask<String, Void, Movie[]> {
        private final String LOG_TAG = FetchMovies.class.getSimpleName();

        protected Movie[] doInBackground(String... urls) {

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
                String sort_mode = pref.getString(getString(R.string.sort_mode),getString(R.string.sort_default));
                final String MOVIE_BASE_URL;
                if(sort_mode.equals("popularity")){
                    MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie/popular?";;
                }
                else if (sort_mode.equals("rating")){
                    MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie/top_rated?";
                }
                else{
                    Log.d(LOG_TAG,"Unit type not found"+ sort_mode);
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
            } finally{
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

            try{
                return getMovieDataFromJson(movieJsonstr);
            }
            catch(JSONException e){
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;

        }

        public Comparator<Movie> Sort = new Comparator<Movie>() {
            @Override
            public int compare(Movie o1, Movie o2) {
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                String sort_mode = pref.getString(getString(R.string.sort_mode),getString(R.string.sort_default));

                if(sort_mode.equals("popularity")){
                    return o1.getMpopularity().compareTo(o2.getMpopularity());
                }
                else if (sort_mode.equals("rating")){
                    return o1.getMvote_average().compareTo(o2.getMvote_average());
                }
                else{
                    Log.d(LOG_TAG,"Unit type not found"+ sort_mode);
                }
                return 0;
            }
        };

        @Override
        protected void onPostExecute(Movie[] result) {
//            super.onPostExecute(strings);



            List<Movie> movieResult = new ArrayList<>(

                    Arrays.asList(result)
            );

//            Collections.sort(movieResult, Sort);
            if (result != null){
                gridAdapter.clear();
                for (Movie movie : movieResult){
                    gridAdapter.add(movie);
                }
            }

        }
        }

    public class NetworkChangeReceiver extends BroadcastReceiver {

        private static final String LOG_TAG = "NetworkChangeReceiver";
        @Override
        public void onReceive(final Context context, Intent intent){
            Log.v(LOG_TAG, "Receieved notification about network status");
            isNetworkAvailable(context);
        }

        private void isNetworkAvailable(Context context){
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            if(connectivityManager != null){

                NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
                if(activeNetwork != null){
                    UpdateMovies();
                    Log.d(LOG_TAG, "Movie list updated using broadcast receiver");
                }
            }
        }
    }

}


