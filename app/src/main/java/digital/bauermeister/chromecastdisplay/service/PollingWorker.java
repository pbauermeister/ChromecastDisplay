package digital.bauermeister.chromecastdisplay.service;

import android.util.Log;

import java.io.IOException;
import java.net.ConnectException;
import java.security.GeneralSecurityException;

import de.greenrobot.event.EventBus;
import digital.bauermeister.chromecastdisplay.ChromecastInfo;
import digital.bauermeister.chromecastdisplay.Config;
import digital.bauermeister.chromecastdisplay.event.from_worker.ChromecastInfoEvent;
import digital.bauermeister.chromecastdisplay.event.from_worker.StateEvent;
import digital.bauermeister.chromecastdisplay.event.to_worker.PauseEvent;
import digital.bauermeister.chromecastdisplay.event.to_worker.ResumeEvent;
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

    private State state = new State();
    private ChromecastInfo lastInfo = null;

    public PollingWorker() {
        EventBus.getDefault().register(this);
    }

    public void destroy() {
        EventBus.getDefault().unregister(this);
    }

    public void poll() {
        if (!state.paused) {
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
    }

    private void discover() throws IOException {
        Log.d(TAG, ">>> start discovery");

        state.didDiscovery = false;

        post(StateEvent.Discover);
        ChromeCasts.get().clear(); // important! avoids duplicates
        ChromeCasts.restartDiscovery();

        state.didDiscovery = true;
        state.nbDiscoveredNothing = 0;
        state.nbNotConnected = 0;
    }

    private void pollDevices() throws IOException, GeneralSecurityException {
        // Do something here on the main thread
        Log.d(TAG, ">>> poll discovery");

        ChromeCasts chromecasts = ChromeCasts.get();
        int nb = chromecasts.size();
        Log.d(TAG, ">>> poll discovery: " + nb);

        if (nb == 0) {
            post(StateEvent.DiscoveredZero);
            if (++state.nbDiscoveredNothing > Config.REDISCOVER_AFTER_NONE_FOUND_NB) {
                state.didDiscovery = false;
            }
        } else {
            post(StateEvent.DiscoveredSome);
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
                post(StateEvent.Connect);
                try {
                    chromecast.connect();
                } catch (Throwable t) {
                }
            } else state.nbNotConnected = 0;

            // Get device status
            if (chromecast.isConnected()) {
                post(StateEvent.Connected);
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

                    ChromecastInfo info = new ChromecastInfo(
                            chromecast.getName(),
                            app == null ? null : app.name,
                            app == null ? null : app.statusText,
                            status.volume.level,
                            status.volume.muted,
                            status.standBy
                    );
                    if (!info.equals(lastInfo)) {
                        lastInfo = info;
                        post(new ChromecastInfoEvent(info));
                    }
                }
            } else {
                Log.d(TAG, ">>> not connected " + state.nbNotConnected);
                post(StateEvent.NotConnected);
                if (++state.nbNotConnected > Config.REDISCOVER_AFTER_NOT_CONNECTED_NB) {
                    state.didDiscovery = false;
                }
            }
        }
    }

    private void post(Object o) {
        EventBus.getDefault().post(o);
    }
    public void onEventBackgroundThread(PauseEvent event) {
        state.paused = true;
    }

    public void onEventBackgroundThread(ResumeEvent event) {
        state.paused = false;
        lastInfo = null; // will force resending ChromecastInfoEvent
    }

}