package digital.bauermeister.chromecastdisplay.service_nodejs.event_models;

public class ChromecastMessage {
    public enum MessageType {RECEIVER_STATUS}

    public MessageType type;

    public static class ReceiverStatus {
        public int requestId;

        public static class Status {
            public class Volume {
                public float level;
                public boolean muted;
            }

            public Volume volume;

            public static class Application {
                public String displayName;
                public String sessionId;

                public static class Namespace {
                    public String name;
                }

                public Namespace[] namespaces;
                public String statusText;
                public String appId;
                public String transportId;
            }

            public Application[] applications;
        }

        public Status status;
        public String type;
    }

    public ReceiverStatus receiver_status; // when type is RECEIVER_STATUS
    public Device device;
}
