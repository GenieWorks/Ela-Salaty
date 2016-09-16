package xyz.genieworks.ela_salaty;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private final String Tag = this.getClass().getSimpleName();

    private SensorManager mSensorManager;
    private Sensor mLightSensor;

    //holds the last recorder light sensor reading
    private int lightReading;

    private Prayer mPrayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //  Declare a new thread to do a preference check
        Thread t = new Thread(new RunAppIntroTask());

        // Start the thread
        t.start();

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.start_praying);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lightReading < 15) {
                    Toast.makeText(MainActivity.this, "please put your phone in a more lighty room and try again", Toast.LENGTH_LONG).show();
                }
                //start praying observing
                else {
                    //disable screen lock when praying starts
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

                    mPrayer.startPrayer(lightReading, .5f, MainActivity.this);
                    fab.setVisibility(View.GONE);
                }
            }
        });



        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mLightSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        mPrayer = new Prayer();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            //TODO add About Activity
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume(){
        super.onResume();
        //register light sensor listener
        mSensorManager.registerListener(this,mLightSensor,SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onPause(){
        super.onPause();
        //unregister sensor when activity pauses
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
         lightReading = (int) event.values[0];

         if(Prayer.isPrayerStarted)
            mPrayer.updateSensorValue(lightReading);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //TODO add action when sensor accuracy changes
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     *
     */
    private class RunAppIntroTask implements Runnable {

        @Override
        public void run() {
            //  Initialize SharedPreferences
            SharedPreferences getPrefs = PreferenceManager
                    .getDefaultSharedPreferences(getBaseContext());

            //  Create a new boolean and preference and set it to true
            boolean isFirstStart = getPrefs.getBoolean("firstStart", true);

            //  If the activity has never started before...
            if (isFirstStart) {

                //  Launch app intro
                Intent i = new Intent(MainActivity.this, Intro.class);
                startActivity(i);

                // TODO: activate the following code in production.
                //  Make a new preferences editor
                //SharedPreferences.Editor e = getPrefs.edit();

                //  Edit preference to make it false because we don't want this to run again
                //e.putBoolean("firstStart", false);

                //  Apply changes
                //e.apply();
            }
        }
    }

}
