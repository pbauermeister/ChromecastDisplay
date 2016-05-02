package digital.bauermeister.chromecastdisplay;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by pascal on 5/2/16.
 */
public enum DeviceManager {

    INSTANCE;

    private static final int RESTART_PERIOD = 1000 * 15;

    private Date sampleStartDate;
    private HashMap<String, String> listedDevices;
    private HashMap<String, String> accDevices;

    private void initSampling() {
        listedDevices = accDevices;
        accDevices = null;
        sampleStartDate = null;
    }

    public void add(String udn, String name) {
        Date now = new Date();
        long delta = sampleStartDate == null ? 0 : now.getTime() - sampleStartDate.getTime();
        if (delta > RESTART_PERIOD) {
            initSampling();
        }

        if (accDevices == null) {
            accDevices = new HashMap();
            sampleStartDate = now;
        }

        accDevices.put(udn, name);
    }

    public HashMap<String, String> get() {
        return listedDevices == null ? new HashMap() : listedDevices;
    }

}
