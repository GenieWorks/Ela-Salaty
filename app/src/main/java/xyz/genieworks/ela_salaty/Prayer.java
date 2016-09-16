package xyz.genieworks.ela_salaty;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

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

    private int [] numbersList;

    private SimpleDraweeView currentKneelingView;

    private ImageView prayerCover;

    private TextView mainInstructionsText;

    /**
     * called to start  new prayer
     * @param currentReading the current light reading at praying start
     * @param ratio current intensity to max intensity to detect sagda
     * @param activity the calling activity
     */
    public void startPrayer(int currentReading,float ratio, Activity activity){

        Log.d(Tag, "Prayer Started");
        isPrayerStarted = true;
        currentKneeling =1;
        maxLightIntensity = currentReading;
        sagdaCounter = 0;
        sagdaDetectorRatio = ratio;
        isSagdaState = false;

        numbersList = new int[]{R.drawable.number_one,R.drawable.number_two,R.drawable.number_three,R.drawable.number_four};

        currentKneelingView = (SimpleDraweeView) activity.findViewById(R.id.current_kneeling);
        mainInstructionsText = (TextView) activity.findViewById(R.id.main_instructions);
        prayerCover = (ImageView) activity.findViewById(R.id.prayer_cover_image);

        prayerCover.setVisibility(View.GONE);
        currentKneelingView.setVisibility(View.VISIBLE);
        mainInstructionsText.setVisibility(View.GONE);
    }


    /**
     * call this method when sensor value changes to update kneelings and sagdas count
     * @param currentIntensity the new sensor value
     */
    public void updateSensorValue(int currentIntensity){

        if(currentKneeling < 4){
            Log.d(Tag,"current reading = "+ currentIntensity + " max intensity = "+ maxLightIntensity);
            if(currentIntensity > maxLightIntensity ) {
                maxLightIntensity = currentIntensity;
            }
            if(!isSagdaState){
                // sagda was detected based on the given ratio
                if(currentIntensity < sagdaDetectorRatio*maxLightIntensity){
                    Log.d(Tag,"sagda detected");
                    isSagdaState = true;
                    ++sagdaCounter;
                }
            }
            else{
                //sagda ended
                if(currentIntensity >= sagdaDetectorRatio*maxLightIntensity){
                    Log.d(Tag,"sagda finished");
                    isSagdaState = false;

                    //current kneeling has ended
                    if(sagdaCounter == 2){
                        sagdaCounter = 0;
                        ++currentKneeling;
                        //((BitmapDrawable)currentKneelingView.getDrawable()).getBitmap().recycle();
                        //currentKneelingView.setImageResource(numbersList[currentKneeling-1]);
                        ImageRequest imageRequest = ImageRequestBuilder
                                .newBuilderWithResourceId(numbersList[currentKneeling-1])
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
                }
            }
        }
    }

}
