package digital.bauermeister.chromecastdisplay.service_nodejs.event_models;

public class EventMessage {

    /*
     * Event type
     */

    public enum Event {
        DISCOVERING, DISCOVERED, CONNECTED, MESSAGE, GET_STATUS, ERROR
    }

    public Event event;

    /*
     * Event payloads
     */

    // When event is DISCOVERING
    //   no payload

    // When event is DISCOVERED, CONNECTED
    public Device device;

    // When event is MESSAGE
    public ChromecastMessage message;

    // When event is GET_STATUS
    public GetStatusRequest get_status_request;

    // When event is ERROR
    public Error error;

}
