package digital.bauermeister.chromecastdisplay.bus_event.from_worker;

import digital.bauermeister.chromecastdisplay.ChromecastInfo;

/**
 * Created by pascal on 2/23/16.
 */
public class ChromecastInfoEvent {
    public final ChromecastInfo chromecastInfo;

    public ChromecastInfoEvent(ChromecastInfo chromecastInfo) {
        this.chromecastInfo = chromecastInfo;
    }
}
