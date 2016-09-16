package xyz.genieworks.ela_salaty;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;

/**
 * xyz.genieworks.ela_salaty
 *
 * Introduction to the app.
 *
 * Created by mohamed on 16/09/16 - 04:25 م.
 */
public class Intro extends AppIntro2 {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Add your slide's fragments here.
        // AppIntro will automatically generate the dots indicator and buttons.
        //addSlide(first_fragment);
        //addSlide(second_fragment);
        //addSlide(third_fragment);
        //addSlide(fourth_fragment);

        // Instead of fragments, you can also use our default slide
        // Just set a title, description, background and image. AppIntro will do the rest.
        addSlide(
                AppIntroFragment.
                        newInstance(getString(R.string.app_name),
                                getString(R.string.intro_slide1),
                                R.drawable.indicator_dot_grey,
                                Color.TRANSPARENT)
        );

        addSlide(
                AppIntroFragment.
                        newInstance(getString(R.string.app_name),
                                getString(R.string.intro_slide2),
                                R.drawable.indicator_dot_grey,
                                Color.TRANSPARENT));

        // SHOW or HIDE the statusbar
        showStatusBar(false );

        // Edit the color of the nav bar on Lollipop+ devices
        //setNavBarColor(Color.parseColor("#3F51B5"));

        // Turn vibration on and set intensity
        // NOTE: you will need to ask VIBRATE permission in Manifest if you haven't already
        //setVibrate(true);
        //setVibrateIntensity(30);

        // Animations -- use only one of the below. Using both could cause errors.
        //setFadeAnimation(); // OR
        //setZoomAnimation(); // OR
        //setFlowAnimation(); // OR
        setSlideOverAnimation(); // OR
        //setDepthAnimation(); // OR
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        // Do something when users tap on Skip button.
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        //super.onDonePressed(currentFragment);
        // Do something when users tap on Done button.
        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }
}
