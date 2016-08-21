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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
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
import java.util.List;

/**
 * Created by xadahiya on 8/20/16.
 */
public class DetailFragment extends Fragment {

    public NetworkChangeReceiver mNetworkChangeReceiver;

    public TextView movie_title;
    public TextView movie_description;
    public TextView release_date;
    public TextView user_rating;
    public ImageView movie_poster;
    public String movie_id  = "278";
    public Button trailer1;
    public Button trailer2;
    public Button favourite_btn;
    public TextView review_txt;
    public Movie mMovie;
    public String review = "";
    public String trailer1_uri = "";
    public String trailer2_uri = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        movie_title = (TextView) view.findViewById(R.id.movie_title);
        movie_description = (TextView) view.findViewById(R.id.movie_description);
        movie_poster = (ImageView) view.findViewById(R.id.movie_poster);
        release_date = (TextView) view.findViewById(R.id.release_date);
        user_rating = (TextView) view.findViewById(R.id.user_rating);
        review_txt = (TextView) view.findViewById(R.id.read_review);
        favourite_btn = (Button) view.findViewById(R.id.favourite_btn);
        trailer1 = (Button) view.findViewById(R.id.trailer1);
        trailer1.setEnabled(false);
        trailer2 = (Button) view.findViewById(R.id.trailer2);
        trailer2.setEnabled(false);

        trailer1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(trailer1_uri)));
            }
        });

        trailer2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(trailer2_uri)));
            }
        });
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sort_mode = pref.getString(getString(R.string.sort_mode), getString(R.string.sort_default));
        if(sort_mode.equals("favorites")){
         favourite_btn.setEnabled(false);
        }
        favourite_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri moviesUri = MovieContract.FavouriteMovieEntry.CONTENT_URI;
                Cursor cursor = getContext().getContentResolver().query(moviesUri,null, MovieContract.FavouriteMovieEntry.COLUMN_MOVIE_ID+ "="+ mMovie.getId(), null, null);
                if(cursor.getCount() != 0){
                    Log.d("data", "contains data");
                    Toast.makeText(getContext(), "Movie already in Favorites!", Toast.LENGTH_SHORT).show();
                }
                else {
                    ContentValues movieValues = new ContentValues();
                    movieValues.put(MovieContract.FavouriteMovieEntry.COLUMN_MOVIE_ID, mMovie.getId());
                    movieValues.put(MovieContract.FavouriteMovieEntry.COLUMN_MOVIE_ISADULT, (mMovie.isMisAdult()) ? 1 : 0);
                    movieValues.put(MovieContract.FavouriteMovieEntry.COLUMN_BACKDROP_URL, mMovie.getMbackdrop_url());
                    movieValues.put(MovieContract.FavouriteMovieEntry.COLUMN_TITLE, mMovie.getMtitle());
                    movieValues.put(MovieContract.FavouriteMovieEntry.COLUMN_DESCRIPTION, mMovie.getMsummary());
                    movieValues.put(MovieContract.FavouriteMovieEntry.COLUMN_BACKDROP_IMAGE, mMovie.getMbackdrop_img());
                    movieValues.put(MovieContract.FavouriteMovieEntry.COLUMN_POPULARITY, mMovie.getMpopularity());
                    movieValues.put(MovieContract.FavouriteMovieEntry.COLUMN_VOTE_AVERAGE, mMovie.getMvote_average());
                    movieValues.put(MovieContract.FavouriteMovieEntry.COLUMN_VOTE_COUNT, mMovie.getMvote_count());
                    movieValues.put(MovieContract.FavouriteMovieEntry.COLUMN_RELEASE_DATE, mMovie.getMrelease_date());
                    Uri inserted = getContext().getContentResolver().insert(MovieContract.FavouriteMovieEntry.CONTENT_URI, movieValues);
                    Log.d("favourite", inserted.toString());
                    Toast.makeText(getContext(), "Added to Favorites", Toast.LENGTH_SHORT).show();
                }
            }
        });
        Bundle arguments = getArguments();
        if (arguments != null) {
            mMovie = arguments.getParcelable("Movie");
//            Log.d("testing id", movie.getId());
            movie_id = mMovie.getId();
            movie_title.setText(mMovie.getMtitle());
            movie_description.setText(mMovie.getMsummary());
            release_date.setText(mMovie.getMrelease_date());
            user_rating.setText(mMovie.getMvote_average().toString());
            Picasso.with(getActivity())
                    .load(mMovie.getMbackdrop_img())
                    .into(movie_poster);
        }

        return view;
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
                    UpdateDetails();
                    Log.d(LOG_TAG, "Movie list updated using broadcast receiver");
                }
            }
        }
    }

    public void UpdateDetails() {
        FetchTrailer trailerDetails = new FetchTrailer();
        trailerDetails.execute();
        FetchReviews reviews = new FetchReviews();
        reviews.execute();
    }

    private List<String> getTrailerDataFromJson(String trailerJsonStr)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String OWM_RESULT = "results";
        final String OWM_KEY = "key";

        JSONObject forecastJson = new JSONObject(trailerJsonStr);
        JSONArray resultArray = forecastJson.getJSONArray(OWM_RESULT);


        List<String> trailer_list = new ArrayList<String>();
        for (int i = 0; i < resultArray.length(); i++) {
            JSONObject trailerObject = resultArray.getJSONObject(i);

            String key = trailerObject.getString(OWM_KEY);
            trailer_list.add(key);
//
        }
        return trailer_list;
    }

    private List<String> getReviewDataFromJson(String trailerJsonStr)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.

        final String OWM_RESULT = "results";
        final String OWM_CONTENT = "content";

        JSONObject forecastJson = new JSONObject(trailerJsonStr);
        JSONArray resultArray = forecastJson.getJSONArray(OWM_RESULT);


        List<String> review_list = new ArrayList<String>();
        for (int i = 0; i < resultArray.length(); i++) {
            JSONObject trailerObject = resultArray.getJSONObject(i);

            String key = trailerObject.getString(OWM_CONTENT);
            review_list.add(key);
//
        }
        return review_list;
    }

    public class FetchTrailer extends AsyncTask<String, Void, List<String>> {
        private final String LOG_TAG = DetailFragment.class.getSimpleName();

        protected List<String> doInBackground(String... urls) {

            // These two need to be declared outside the try/catch
// so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

// Will contain the raw JSON response as a string.
            String trailerJsonstr = null;
            String format = "json";


            try {

                String TRAILER_BASE_URL = "http://api.themoviedb.org/3/movie/";


                final String API_KEY_PARAM = "api_key";

                Uri builtUri = Uri.parse(TRAILER_BASE_URL).buildUpon().appendPath(movie_id).appendPath("videos")
                        .appendQueryParameter(API_KEY_PARAM, "b9c5a823d73daf11cb998036021c8c20")
                        .build();

                URL url = new URL(builtUri.toString());

                Log.v(LOG_TAG, "Built URI " + builtUri.toString());

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
                trailerJsonstr = buffer.toString();
                Log.e(LOG_TAG, trailerJsonstr);
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
                return getTrailerDataFromJson(trailerJsonstr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;

        }


        @Override
        protected void onPostExecute(List<String> result) {
            try{
                trailer1.setEnabled(true);
                trailer1_uri = "https://www.youtube.com/watch?v=" + result.get(0);
                Log.d("trailer", trailer1_uri);
                trailer2.setEnabled(true);
                trailer2_uri = "https://www.youtube.com/watch?v=" + result.get(1);
                Log.d("trailer", trailer2_uri);
            }
            catch (Exception e){
                Log.d("trailer", "error generating trailer uris");
            }

        }
    }

    public class FetchReviews extends AsyncTask<String, Void, List<String>> {
        private final String LOG_TAG = DetailFragment.class.getSimpleName();

        protected List<String> doInBackground(String... urls) {

            // These two need to be declared outside the try/catch
// so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

// Will contain the raw JSON response as a string.
            String reviewJsonstr = null;
            String format = "json";


            try {

                String TRAILER_BASE_URL = "http://api.themoviedb.org/3/movie/";


                final String API_KEY_PARAM = "api_key";

                Uri builtUri = Uri.parse(TRAILER_BASE_URL).buildUpon().appendPath(movie_id).appendPath("reviews")
                        .appendQueryParameter(API_KEY_PARAM, "b9c5a823d73daf11cb998036021c8c20")
                        .build();

                URL url = new URL(builtUri.toString());

                Log.v(LOG_TAG, "Built URI " + builtUri.toString());

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
                reviewJsonstr = buffer.toString();
                Log.e(LOG_TAG, reviewJsonstr);
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
                return getReviewDataFromJson(reviewJsonstr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPostExecute(List<String> result) {
            trailer1.setEnabled(true);
            try {
//                read_review.setEnabled(true);
                review = result.get(0);
                review_txt.setText(review);
                Log.d("review", review);

            } catch (Exception e) {
                Log.d("review", "error generating review uris");
            }

        }
    }
}
