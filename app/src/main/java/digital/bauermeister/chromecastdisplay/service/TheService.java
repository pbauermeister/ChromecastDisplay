package digital.bauermeister.chromecastdisplay.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import de.greenrobot.event.EventBus;
import digital.bauermeister.chromecastdisplay.Event.PollEvent;

public class TheService extends Service {
    private static final String TAG = "TheService";

    private static final int DELAY_MS = 2000;

    Handler handler = new Handler();
    private boolean enabled = true;

    private PollingWorker pollingWorker;

    private Runnable pollingHandler = new Runnable() {
        @Override
        public void run() {
            EventBus.getDefault().post(new PollEvent());
        }
    };

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

    public void onEventBackgroundThread(PollEvent event) {
        if (enabled) {
            pollingWorker.poll();
            handler.postDelayed(pollingHandler, DELAY_MS);
        }
    }
}
