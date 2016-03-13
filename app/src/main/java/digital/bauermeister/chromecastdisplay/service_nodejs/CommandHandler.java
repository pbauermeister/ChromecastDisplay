package digital.bauermeister.chromecastdisplay.service_nodejs;

import android.util.Log;

import de.greenrobot.event.EventBus;
import digital.bauermeister.chromecastdisplay.ChromecastInfo;
import digital.bauermeister.chromecastdisplay.bus_event.from_worker.ChromecastInfoEvent;
import digital.bauermeister.chromecastdisplay.bus_event.from_worker.HeartBeatEvent;
import digital.bauermeister.chromecastdisplay.bus_event.from_worker.StateEvent;

/**
 * Created by pascal on 3/11/16.
 */
public class CommandHandler extends CommandLauncher {
    private static final String TAG = "CommandHandler";

    protected void handleLine(String line) {
        super.handleLine(line);
        EventData event = EventParser.parseLine(line);

        post(HeartBeatEvent.Beat);

        switch (event.type) {
            case DISCOVERING:
                post(StateEvent.Discover);
                break;
            case DISCOVERED:
                post(StateEvent.DiscoveredSome);
                post(StateEvent.Connect);
                break;
            case CONNECTED:
                post(StateEvent.Connected);
                break;
            case MESSAGE:
                post(StateEvent.Connected);
                EventData.MessageData data = (EventData.MessageData) event;
                String appName = "???";
                String statusText = "---";
                try {
                    appName = data.message.receiver_status.status.applications[0].displayName;
                } catch (Exception e) {
                }
                try {
                    statusText = data.message.receiver_status.status.applications[0].statusText;
                } catch (Exception e) {
                }
                ChromecastInfo info = new ChromecastInfo(
                        data.message.device.name,
                        appName,
                        statusText,
                        data.message.receiver_status.status.volume.level,
                        data.message.receiver_status.status.volume.muted,
                        false
                );
                post(new ChromecastInfoEvent(info));
                break;

            case GET_STATUS:
            case ERROR:
        }
    }

    private void post(Object o) {
        EventBus.getDefault().post(o);
    }

}
