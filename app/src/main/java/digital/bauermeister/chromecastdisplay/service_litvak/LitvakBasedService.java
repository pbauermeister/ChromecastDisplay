package digital.bauermeister.chromecastdisplay.service_litvak;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import de.greenrobot.event.EventBus;
import digital.bauermeister.chromecastdisplay.Config;

public class LitvakBasedService extends Service {
    private static final String TAG = "TheService";

    Handler handler = new Handler();
    private boolean enabled = true;
    private PollingWorker pollingWorker;

    public class PollEvent {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // No binding provided
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "*** Service created ***");
        super.onCreate();

        pollingWorker = new PollingWorker();

        EventBus.getDefault().register(this);
        EventBus.getDefault().post(new PollEvent()); // fire up polling
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "*** Service destroyed ***");

        EventBus.getDefault().unregister(this);
        pollingWorker.destroy();

        enabled = false;
        handler.removeCallbacks(pollingHandler);

        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        return START_STICKY;
    }

    /*
     * Scheduling and execution of periodic task
     */

    private Runnable pollingHandler = new Runnable() {
        @Override
        public void run() {
            if (enabled) {
                EventBus.getDefault().post(new PollEvent());
            }
        }
    };

    public void onEventBackgroundThread(PollEvent event) {
        if (enabled) {
            pollingWorker.poll();
            handler.postDelayed(pollingHandler, Config.POLLING_DELAY_MS);
        }
    }
}
