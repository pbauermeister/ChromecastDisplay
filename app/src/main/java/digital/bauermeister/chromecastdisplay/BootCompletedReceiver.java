package digital.bauermeister.chromecastdisplay;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by pascal on 4/30/16.
 */

public class BootCompletedReceiver extends BroadcastReceiver {
    private static final String TAG = "BootCompletedReceiver";

    // This will just create TheApplication., and stay in background

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Received notification of boot completed");

        // now this will launch the UI
        Intent i = new Intent(context, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}
