package digital.bauermeister.chromecastdisplay.service_nodejs.event_models;

import java.util.Map;

public class Device {
    public String location;
    public String applicationUrl;
    public String name;
    public String model;
    public Info info;

    public static class Info {
        public String deviceType;
        public String friendlyName;
        public String manufacturer;
        public String modelName;
        public String UDN;
        public Map<String, Icon> iconList;

        public static class Icon {
            public String mimetype;
            public int width;
            public int height;
            public int depth;
            public String url;
        }

        public Map<String, Service> serviceList;

        public static class Service {
            public String serviceType;
            public String serviceId;
            public String controlURL;
            public String eventSubURL;
            public String SCPDURL;
        }
    }
}
