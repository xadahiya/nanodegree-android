package com.example.android.sunshine.app;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.sunshine.app.data.WeatherContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private ForecastAdapter mForecastAdapter;
    private static final int FORECAST_LOADER = 0;



    public ForecastFragment() {

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(FORECAST_LOADER, null, this);

    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater){
        menuInflater.inflate(R.menu.forecastfragment, menu);
    }
    public void updateWeather(){
        FetchWeatherTask weatherTask = new FetchWeatherTask(getActivity());
//        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
//        String location_key = pref.getString(getString(R.string.pref_key),getString(R.string.pref_default));
//        String unit = pref.getString(getString(R.string.pref_units_key),getString(R.string.units_default));
//        weatherTask.execute(location_key, unit);

        String location = Utility.getPreferredLocation(getActivity());
        weatherTask.execute(location);
    }

    @Override
    public void onStart(){
        super.onStart();
        updateWeather();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle){
        String locationSetting = Utility.getPreferredLocation(getActivity());

        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
        Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(
                locationSetting, System.currentTimeMillis());

        return new CursorLoader(getActivity(), weatherForLocationUri, null, null, null, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor){
            mForecastAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader){
        mForecastAdapter.swapCursor(null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        switch (item.getItemId()) {
            case R.id.action_refresh:
               updateWeather();
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);
////        String[] forecastArray ={
////        };
////
////        List<String> weekForecast = new ArrayList<>(
////
////                Arrays.asList(forecastArray)
////        );
////        forecastAdapter = new ArrayAdapter<>(getActivity(), R.layout.list_item_forecast, R.id.list_item_forecast_textview, weekForecast);
////        ListView listview = (ListView) rootView.findViewById(R.id.listView_forecast);
////        listview.setAdapter(forecastAdapter);
//
//        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Intent intent = new Intent(getActivity(), DetailActivity.class);
//                intent.putExtra("weather_data",((TextView)view).getText());
//                startActivity(intent);
//            }
//        });

//        String locationSettings = Utility.getPreferredLocation(getActivity());
//        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
//
//        Uri WeatherForecastUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(locationSettings, System.currentTimeMillis());
//        Cursor cur = getActivity().getContentResolver().query(WeatherForecastUri,null, null, null, sortOrder);
//
//        mForecastAdapter = new ForecastAdapter(getActivity(),cur, 0);
        mForecastAdapter = new ForecastAdapter(getActivity(), null, 0);
        return rootView;
    }


}
