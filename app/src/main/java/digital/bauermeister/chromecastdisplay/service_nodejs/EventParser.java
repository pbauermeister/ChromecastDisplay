package digital.bauermeister.chromecastdisplay.service_nodejs;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import digital.bauermeister.chromecastdisplay.service_nodejs.event_models.EventMessage;

/**
 * Created by pascal on 3/11/16.
 */
public class EventParser {
    private static final String TAG = "EventParser";

    public static EventData parseLine(String line) {
        EventMessage message = null;
        try {
            message = new Gson().fromJson(line, EventMessage.class);
        } catch (JsonSyntaxException e) {
            Log.e(TAG, "ERROR parsing JSON: " + e);
            return null;
        }

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
