package com.example.android.sunshine.app;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
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
public class ForecastFragment extends Fragment {
    private ForecastAdapter mForecastAdapter;



    public ForecastFragment() {

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
        FetchWeatherTask weatherTask = new FetchWeatherTask(getActivity(), mForecastAdapter);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String location_key = pref.getString(getString(R.string.pref_key),getString(R.string.pref_default));
        String unit = pref.getString(getString(R.string.pref_units_key),getString(R.string.units_default));
        weatherTask.execute(location_key, unit);
    }

    @Override
    public void onStart(){
        super.onStart();
        updateWeather();
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

        String locationSettings = Utility.getPreferredLocation(getActivity());
        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";

        Uri WeatherForecastUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(locationSettings, System.currentTimeMillis());
        Cursor cur = getActivity().getContentResolver().query(WeatherForecastUri,null, null, null, sortOrder);

        mForecastAdapter = new ForecastAdapter(getActivity(),cur, 0);
        return rootView;
    }


}
