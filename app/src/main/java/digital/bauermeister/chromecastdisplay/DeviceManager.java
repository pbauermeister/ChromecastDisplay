package digital.bauermeister.chromecastdisplay;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

/**
 * Created by pascal on 5/2/16.
 */
public enum DeviceManager {

    INSTANCE;

    private static final int RESTART_PERIOD = 1000 * 15;

    private Date sampleStartDate;
    private HashMap<String, ChromecastInfo> listedDevices;
    private HashMap<String, ChromecastInfo> accDevices;

    private void initSampling() {
        listedDevices = accDevices;
        accDevices = null;
        sampleStartDate = null;
    }

    public void add(String udn, ChromecastInfo info) {
        Date now = new Date();
        long delta = sampleStartDate == null ? 0 : now.getTime() - sampleStartDate.getTime();
        if (delta > RESTART_PERIOD) {
            initSampling();
        }

        if (accDevices == null) {
            accDevices = new HashMap();
            sampleStartDate = now;
        }

        accDevices.put(udn, info);
    }

    public HashMap<String, ChromecastInfo> get() {
        return listedDevices == null ? new HashMap() : listedDevices;
    }

    public static List<String> getUdns(HashMap<String, ChromecastInfo> devices) {
        return devices == null
                ? new ArrayList()
                : new ArrayList(new TreeSet(devices.keySet()));
    }

    public boolean has(ChromecastInfo info) {
        return info != null && info.udn != null && get().get(info.udn) != null;
    }

}
