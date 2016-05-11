package digital.bauermeister.chromecastdisplay.service_nodejs;

import de.greenrobot.event.EventBus;
import digital.bauermeister.chromecastdisplay.ChromecastInfo;
import digital.bauermeister.chromecastdisplay.DeviceManager;
import digital.bauermeister.chromecastdisplay.bus_event.from_worker.ChromecastInfoEvent;
import digital.bauermeister.chromecastdisplay.bus_event.from_worker.HeartBeatEvent;
import digital.bauermeister.chromecastdisplay.bus_event.from_worker.StateEvent;

/**
 * Created by pascal on 3/11/16.
 */
public class CommandHandler extends CommandLauncher {
    private static final String TAG = "CommandHandler";

    public CommandHandler(IsActiveProvider isActiveProvider) {
        super(isActiveProvider);
    }

    protected void handleLine(String line) {
        super.handleLine(line);
        post(HeartBeatEvent.Beat);
        EventData event = EventParser.parseLine(line);
        if (event == null) return;

        switch (event.type) {
            case DISCOVERING:
                post(StateEvent.Discover);
                break;
            case DISCOVERED:
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
                        data.message.device.info.UDN,
                        appName,
                        statusText,
                        data.message.receiver_status.status.volume.level,
                        data.message.receiver_status.status.volume.muted,
                        false);

                String udn = data.message.device.info.UDN;
                DeviceManager.INSTANCE.add(udn, info);

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
