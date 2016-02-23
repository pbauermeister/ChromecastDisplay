package digital.bauermeister.chromecastdisplay.service;

import android.util.Log;

import java.io.IOException;
import java.net.ConnectException;
import java.security.GeneralSecurityException;

import de.greenrobot.event.EventBus;
import su.litvak.chromecast.api.v2.Application;
import su.litvak.chromecast.api.v2.ChromeCast;
import su.litvak.chromecast.api.v2.ChromeCasts;
import su.litvak.chromecast.api.v2.Status;

/**
 * Created by pascal on 2/23/16.
 * <p/>
 * See https://github.com/vitalidze/chromecast-java-api-v2
 */

public class PollingWorker {
    private static final String TAG = "PollingWorker";

    private static final int NB_NOTHING_TO_REDISCOVER = 4;
    private static final int NB_NOT_CONNECTED_TO_REDISCOVER = 4;

    private State state = new State();

    public PollingWorker() {
//        EventBus.getDefault().register(this);
    }

    public void destroy() {
//        EventBus.getDefault().unregister(this);
    }

    public void poll() {
        try {
            if (state.didDiscovery) pollDevices();
            else discover();
        } catch (ConnectException e) {
            Log.e(TAG, "### " + e);
        } catch (IOException e) {
            Log.e(TAG, "### " + e);
        } catch (Throwable t) {
            Log.e(TAG, "### " + t);
            t.printStackTrace();
        }

    }

    public void discover() throws IOException {
        Log.d(TAG, ">>> start discovery");

        state.didDiscovery = false;

        ChromeCasts.get().clear(); // important! avoids duplicates
        ChromeCasts.restartDiscovery();

        state.didDiscovery = true;
        state.nbDiscoveredNothing = 0;
        state.nbNotConnected = 0;
    }

    public void pollDevices() throws IOException, GeneralSecurityException {
        // Do something here on the main thread
        Log.d(TAG, ">>> poll discovery");

        ChromeCasts chromecasts = ChromeCasts.get();
        int nb = chromecasts.size();
        Log.d(TAG, ">>> poll discovery: " + nb);

        if (nb == 0) {
            if (++state.nbDiscoveredNothing > NB_NOTHING_TO_REDISCOVER) {
                state.didDiscovery = false;
            }
        } else {
            for (ChromeCast chromecast : chromecasts) {
                Log.d(TAG, "- " + chromecast.getName());
            }

            ChromeCast chromecast = chromecasts.get(0);
            // Connect
            if (!chromecast.isConnected()) {
                try {
                    Log.d(TAG, ">>> disconnect");
                    chromecast.disconnect();
                } catch (IOException e) {
                    Log.d(TAG, ">>> ## " + e);
                }

                Log.d(TAG, ">>> connect");
                chromecast.connect();
            } else state.nbNotConnected = 0;

            // Get device status
            if (chromecast.isConnected()) {
                Log.d(TAG, ">>> get status");
                Status status = chromecast.getStatus();
                if (status != null) {
                    Application app = status.getRunningApp();
                    Log.i(TAG, ">>> +++++++++++++++++++ " + (app == null ? "None" : app.name + " - " + app.statusText));
                    Log.i(TAG, ">>> +++++++ addr        " + chromecast.getAddress());
                    Log.i(TAG, ">>> +++++++ app         " + chromecast.getApplication());
                    Log.i(TAG, ">>> +++++++ appsUrl     " + chromecast.getAppsURL());
//                    Log.i(TAG, ">>> +++++++ runningApp  " + chromecast.getRunningApp());
                    Log.i(TAG, ">>> +++++++ port        " + chromecast.getPort());

                    Log.i(TAG, ">>> +++++++ volume      " + status.volume);
                    Log.i(TAG, ">>> +++++++ standBy     " + status.standBy);

                    Log.i(TAG, ">>> +++++++ app.id      " + app.id);
                    Log.i(TAG, ">>> +++++++ app.name    " + app.name);
                }
            } else {
                if (++state.nbNotConnected > NB_NOT_CONNECTED_TO_REDISCOVER)
                    state.didDiscovery = false;
            }
        }
    }
}
