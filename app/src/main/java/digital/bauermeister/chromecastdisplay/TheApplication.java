package digital.bauermeister.chromecastdisplay;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import digital.bauermeister.chromecastdisplay.service_litvak.LitvakBasedService;
import digital.bauermeister.chromecastdisplay.service_nodejs.NodejsBasedService;

public class TheApplication extends Application {

    private static final String TAG = "TheApplication";

    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "*** APP onCreate ***");

//        startService(new Intent(this, LitvakBasedService.class));
        startService(new Intent(this, NodejsBasedService.class));

        Log.d(TAG, "*** APP onCreate done");

    }
}
