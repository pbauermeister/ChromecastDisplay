package digital.bauermeister.chromecastdisplay.service_nodejs;

import digital.bauermeister.chromecastdisplay.service_nodejs.event_models.ChromecastMessage;
import digital.bauermeister.chromecastdisplay.service_nodejs.event_models.Device;
import digital.bauermeister.chromecastdisplay.service_nodejs.event_models.Error;
import digital.bauermeister.chromecastdisplay.service_nodejs.event_models.GetStatusRequest;

/**
 * Created by pascal on 3/11/16.
 */
public interface EventData {

    public static class DiscoveringData implements EventData {
    }

    public static class DiscoveredData implements EventData {
        public DiscoveredData(Device device) {
            this.device = device;
        }

        public Device device;
    }

    public static class ConnectedData implements EventData {
        public Device device;

        public ConnectedData(Device device) {
            this.device = device;
        }
    }

    public static class MessageData implements EventData {
        public ChromecastMessage message;

        public MessageData(ChromecastMessage message) {
            this.message = message;
        }
    }

    public static class GetStatusData implements EventData {
        public GetStatusRequest get_status_request;

        public GetStatusData(GetStatusRequest get_status_request) {
            this.get_status_request = get_status_request;
        }
    }

    public static class ErrorData implements EventData {
        public Error error;

        public ErrorData(Error error) {
            this.error = error;
        }
    }

}
