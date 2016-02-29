package digital.bauermeister.chromecastdisplay;

/**
 * Created by pascal on 2/23/16.
 */
public abstract class Config {
    public static final int POLLING_DELAY_MS = 500;

    public static final int REDISCOVER_AFTER_NONE_FOUND_NB = 4;
    public static final int REDISCOVER_AFTER_NOT_CONNECTED_NB = 4;

    public final static int HEARTBEAT_DELAY_MS = 1000 / 20;

    public final static int SCROLL_X_DELAY_MS = 1000 / 15;
    public final static int SCROLL_TWEEN_PAUSE = 1500 ;
    public final static int SCROLL_Y_DELAY_MS = 1000 / 20;

    public final static String STATUS_PRIFIX_TO_STRIP = "Now Casting:";
}
