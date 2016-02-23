package digital.bauermeister.chromecastdisplay;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import digital.bauermeister.chromecastdisplay.service.TheService;

public class TheApplication extends Application {

    private static final String TAG = "TheApplication";

    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "*** APP onCreate ***");

        startService(new Intent(this, TheService.class));

        Log.d(TAG, "*** APP onCreate done");

    }
}
