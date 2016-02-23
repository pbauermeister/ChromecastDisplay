package digital.bauermeister.chromecastdisplay;

import android.util.Log;

/**
 * Created by pascal on 2/23/16.
 */
public class ChromecastInfo implements Comparable<ChromecastInfo> {
    public final String chromecastName;
    public final String appName;
    public final String statusText;
    public final Float audioLevel;
    public final Boolean audioMuted;
    //public final boolean activeInput;
    public final Boolean standBy;

    public ChromecastInfo(
            String chromecastName,
            String appName,
            String statusText,
            float audioLevel,
            boolean audioMuted,
            boolean standBy) {
        this.chromecastName = chromecastName;
        this.appName = appName;
        this.statusText = statusText;
        this.audioLevel = audioLevel;
        this.audioMuted = audioMuted;
        this.standBy = standBy;
    }

    private static final String TAG = "ChromecastInfo";

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (!(o instanceof ChromecastInfo)) return false;
        ChromecastInfo other = (ChromecastInfo) o;
        return compareTo(other) == 0;
    }

    @Override
    public int compareTo(ChromecastInfo other) {
        if (other == null) return -1;

        int comp;

        comp = this.chromecastName.compareTo(other.chromecastName);
        if (comp != 0) return comp;

        comp = this.appName.compareTo(other.appName);
        if (comp != 0) return comp;

        comp = this.statusText.compareTo(other.statusText);
        if (comp != 0) return comp;

        comp = this.audioLevel.compareTo(other.audioLevel);
        if (comp != 0) return comp;

        comp = this.audioMuted.compareTo(other.audioMuted);
        if (comp != 0) return comp;

        comp = this.standBy.compareTo(other.standBy);
        if (comp != 0) return comp;

        return comp;
    }
}
