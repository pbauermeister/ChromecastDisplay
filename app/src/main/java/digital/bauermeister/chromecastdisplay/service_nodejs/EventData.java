package digital.bauermeister.chromecastdisplay.service_nodejs;

import digital.bauermeister.chromecastdisplay.service_nodejs.event_models.ChromecastMessage;
import digital.bauermeister.chromecastdisplay.service_nodejs.event_models.Device;
import digital.bauermeister.chromecastdisplay.service_nodejs.event_models.Error;
import digital.bauermeister.chromecastdisplay.service_nodejs.event_models.EventMessage;
import digital.bauermeister.chromecastdisplay.service_nodejs.event_models.GetStatusRequest;

/**
 * Created by pascal on 3/11/16.
 */
public abstract class EventData {
    public EventMessage.Event type;

    public static class DiscoveringData extends EventData {
        public DiscoveringData() {
            type = EventMessage.Event.DISCOVERING;
        }
    }

    public static class DiscoveredData extends EventData {
        public Device device;

        public DiscoveredData(Device device) {
            type = EventMessage.Event.DISCOVERED;
            this.device = device;
        }
    }

    public static class ConnectedData extends EventData {
        public Device device;

        public ConnectedData(Device device) {
            type = EventMessage.Event.CONNECTED;
            this.device = device;
        }
    }

    public static class MessageData extends EventData {
        public ChromecastMessage message;

        public MessageData(ChromecastMessage message) {
            type = EventMessage.Event.MESSAGE;
            this.message = message;
        }
    }

    public static class GetStatusData extends EventData {
        public GetStatusRequest get_status_request;

        public GetStatusData(GetStatusRequest get_status_request) {
            type = EventMessage.Event.GET_STATUS;
            this.get_status_request = get_status_request;
        }
    }

    public static class ErrorData extends EventData {
        public Error error;

        public ErrorData(Error error) {
            type = EventMessage.Event.ERROR;
            this.error = error;
        }
    }

}
