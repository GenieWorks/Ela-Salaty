package xyz.genieworks.ela_salaty;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

/**
 * Created by ibrahim_hussiny on 9/9/2016.
 */
public class Prayer {

    public static boolean isPrayerStarted = false;

    private final String Tag = "Prayer";

    private int currentKneeling;

    private int maxLightIntensity;

    private int sagdaCounter;

    private float sagdaDetectorRatio;

    private boolean isSagdaState;

    private TextView currentKneelingText;

    private TextView mainInstructionsText;

    /**
     * called to start  new prayer
     * @param maxIntensity the current max light intensity
     * @param ratio current intensity to max intensity to detect sagda
     * @param activity the calling activity
     */
    public void startPrayer(int maxIntensity,float ratio, Activity activity){

        isPrayerStarted = true;
        currentKneeling =1;
        maxLightIntensity = maxIntensity;
        sagdaCounter = 0;
        sagdaDetectorRatio = ratio;
        isSagdaState = false;

        currentKneelingText = (TextView) activity.findViewById(R.id.current_kneeling);
        mainInstructionsText = (TextView) activity.findViewById(R.id.main_instructions);

        currentKneelingText.setVisibility(View.VISIBLE);
        mainInstructionsText.setVisibility(View.GONE);
    }

    /**
     * call this method when sensor value changes to update kneelings and sagdas count
     * @param currentIntensity the new sensor value
     */
    public void updateSensorValue(int currentIntensity){
        if(currentKneeling < 4){
            if(currentIntensity > maxLightIntensity ) {
                maxLightIntensity = currentIntensity;
            }
            if(!isSagdaState){
                // sagda was detected based on the given ratio
                if(currentIntensity < sagdaDetectorRatio*maxLightIntensity){
                    Log.d(Tag,"sagda detected");
                    isSagdaState = true;
                    if( (++sagdaCounter) == 2){
                        Log.d(Tag,"second sagda in kneeling");
                        currentKneeling++;
                        currentKneelingText.setText(String.valueOf(currentKneeling));
                        sagdaCounter =0;
                    }
                }
            }
            else{
                //sagda ended
                if(currentIntensity >= sagdaDetectorRatio*maxLightIntensity){
                    Log.d(Tag,"sagda finished");
                    isSagdaState = false;
                }
            }
        }
    }

}
