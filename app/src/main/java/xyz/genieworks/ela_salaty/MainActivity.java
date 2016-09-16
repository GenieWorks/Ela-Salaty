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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ActionViewTarget;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

public class MainActivity extends AppCompatActivity
        implements SensorEventListener, Observer {

    private final String Tag = this.getClass().getSimpleName();

    private SensorManager mSensorManager;
    private Sensor mLightSensor;

    //holds the last recorder light sensor reading
    private int lightReading;

    private Prayer mPrayer;

    private void showHelp() {
        ShowcaseView view = new ShowcaseView.Builder(this)
                .setTarget(new ViewTarget(findViewById(R.id.start_praying)))
                .withMaterialShowcase()
                //.setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.main_instruction_string))
                .hideOnTouchOutside()
                .setStyle(R.style.CustomShowcaseTheme)

                .build();
        //view.hide();
        view.hideButton();
        //view.show();

    }

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

                    SimpleDraweeView currentKneelingView = (SimpleDraweeView) findViewById(R.id.current_kneeling);
                    TextView mainInstructionsText = (TextView) findViewById(R.id.main_instructions);
                    SimpleDraweeView prayerCover = (SimpleDraweeView) findViewById(R.id.prayer_cover_image);

                    prayerCover.setVisibility(View.GONE);
                    currentKneelingView.setVisibility(View.VISIBLE);
                    mainInstructionsText.setVisibility(View.GONE);

                    mPrayer.startPrayer(lightReading, .5f);

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
        } else if (id == R.id.action_help) {
            showHelp();
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
            mPrayer.updateSensorValue(lightReading, this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //TODO add action when sensor accuracy changes
    }

    @Override
    public void NotifyChanged(int drawable) {

        switch (drawable) {
            case 0:
                drawable = R.drawable.number_one;
                break;
            case 1:
                drawable = R.drawable.number_two;
                break;
            case 2:
                drawable = R.drawable.number_three;
                break;
            case 3:
                drawable = R.drawable.number_four;
                break;

            default:
                throw new IllegalArgumentException("Fuck Microsoft");
        }

        SimpleDraweeView currentKneelingView = (SimpleDraweeView) findViewById(R.id.current_kneeling);
        ImageRequest imageRequest = ImageRequestBuilder
                .newBuilderWithResourceId(drawable)
                .setResizeOptions(
                        new ResizeOptions(50, 50)
                )
                .build();
        DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                .setImageRequest(imageRequest)
                .setOldController(currentKneelingView.getController())
                .setAutoPlayAnimations(true)
                .build();
        currentKneelingView.setController(draweeController);
        currentKneelingView.setImageURI(imageRequest.getSourceUri());
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
