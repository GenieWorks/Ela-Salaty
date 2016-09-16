package xyz.genieworks.ela_salaty;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * xyz.genieworks.ela_salaty
 *
 * Created by mohamed on 16/09/16 - 09:37 م.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Fresco.initialize(getApplicationContext());
    }
}
