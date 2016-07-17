package com.example.android.sunshine.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity {
public final String LOG_TAG = MainActivity.this.getClass().getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ForecastFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onStart(){
        super.onStart();
        Log.d(LOG_TAG, "Activity Started");
    }

    @Override
    public void onStop(){
        super.onStop();
        Log.d(LOG_TAG, "Activity Stopped");
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.d(LOG_TAG,"Activity Resumed");

    }

    @Override
    public void onPause(){
        super.onPause();
        Log.d(LOG_TAG, "Activity Paused");
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.d(LOG_TAG, "Activity Destroyed");
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, settingsActivity.class);
            startActivity(intent);

            return true;
        }
        else if(id == R.id.action_view_on_map){
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            String location = prefs.getString(getString(R.string.pref_key), getString(R.string.pref_default));
            String map = "http://maps.google.co.in/maps?q=" + location ;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(map));
            if(intent.resolveActivity(getPackageManager()) != null){
                startActivity(intent);
            }
            else{
                Log.d("implicit intent", "No app capable of managing this intent");
            }
        }

        return super.onOptionsItemSelected(item);
    }


}
