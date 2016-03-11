package digital.bauermeister.chromecastdisplay.service_nodejs;

import com.google.gson.Gson;

import digital.bauermeister.chromecastdisplay.service_nodejs.event_models.EventMessage;

/**
 * Created by pascal on 3/11/16.
 */
public class EventParser {
    public static EventData parseLine(String line) {
        EventMessage message = new Gson().fromJson(line, EventMessage.class);

        switch (message.event) {
            case DISCOVERING:
                return new EventData.DiscoveringData();

            case DISCOVERED:
                return new EventData.DiscoveredData(message.device);

            case CONNECTED:
                return new EventData.ConnectedData(message.device);

            case MESSAGE:
                return new EventData.MessageData(message.message);

            case GET_STATUS:
                return new EventData.GetStatusData(message.get_status_request);

            case ERROR:
                return new EventData.ErrorData(message.error);
        }
        return null;
    }
}
