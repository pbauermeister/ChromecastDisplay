package digital.bauermeister.chromecastdisplay;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import de.greenrobot.event.EventBus;
import digital.bauermeister.chromecastdisplay.bus_event.from_worker.ChromecastInfoEvent;
import digital.bauermeister.chromecastdisplay.bus_event.from_worker.HeartBeatEvent;
import digital.bauermeister.chromecastdisplay.bus_event.from_worker.StateEvent;
import digital.bauermeister.chromecastdisplay.bus_event.to_worker.PauseEvent;
import digital.bauermeister.chromecastdisplay.bus_event.to_worker.ResumeEvent;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private View contentView;
    private TextAutoscrollView chromecastNameTv;
    private TextAutoscrollView appNameNameTv;
    private TextAutoscrollView statusTextTv;
    private ImageView audioLevelIv;
    private ImageView audioMutedIv;
    private ImageView standByIv;
    private ImageView discoverIv;
    private ImageView stateIv;
    private ImageView eventIv;
    private ImageView nbIv;

    float audioLevel = -1f;
    Boolean audioMuted = null;
    Boolean standBy = null;
    StateEvent state = null;
    boolean first = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);
        contentView = findViewById(R.id.fullscreen_content);

        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        contentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        EventBus.getDefault().register(this);

        chromecastNameTv = (TextAutoscrollView) findViewById(R.id.chromecast_name);
        appNameNameTv = (TextAutoscrollView) findViewById(R.id.app_name);
        statusTextTv = (TextAutoscrollView) findViewById(R.id.status_text);
        audioLevelIv = (ImageView) findViewById(R.id.audio_level);
        audioMutedIv = (ImageView) findViewById(R.id.audio_muted);
        standByIv = (ImageView) findViewById(R.id.stand_by);
        discoverIv = (ImageView) findViewById(R.id.discover);
        stateIv = (ImageView) findViewById(R.id.state);
        eventIv = (ImageView) findViewById(R.id.event);
        nbIv = (ImageView) findViewById(R.id.nb);
    }


    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().post(new ResumeEvent());
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().post(new PauseEvent());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void onEventMainThread(ChromecastInfoEvent event) {
        resetIfFirstTime();
        updateDisplay(event.chromecastInfo);
    }

    public void updateDisplay(ChromecastInfo info) {
        // texts
        chromecastNameTv.setText2(info.chromecastName); //+ " - The quick brown fox jumps over the lazy dog");
        appNameNameTv.setText2(info.appName);
        statusTextTv.setText2(mkText(info.statusText));

        // volume
        if (info.audioLevel != audioLevel) {
            audioLevel = info.audioLevel;
            int level = Math.round(audioLevel * 10);
            switch (level) {
                case 0:
                    audioLevelIv.setImageResource(R.drawable.ic_volume_00);
                    break;
                case 1:
                    audioLevelIv.setImageResource(R.drawable.ic_volume_01);
                    break;
                case 2:
                    audioLevelIv.setImageResource(R.drawable.ic_volume_02);
                    break;
                case 3:
                    audioLevelIv.setImageResource(R.drawable.ic_volume_03);
                    break;
                case 4:
                    audioLevelIv.setImageResource(R.drawable.ic_volume_04);
                    break;
                case 5:
                    audioLevelIv.setImageResource(R.drawable.ic_volume_05);
                    break;
                case 6:
                    audioLevelIv.setImageResource(R.drawable.ic_volume_06);
                    break;
                case 7:
                    audioLevelIv.setImageResource(R.drawable.ic_volume_07);
                    break;
                case 8:
                    audioLevelIv.setImageResource(R.drawable.ic_volume_08);
                    break;
                case 9:
                    audioLevelIv.setImageResource(R.drawable.ic_volume_09);
                    break;
                case 10:
                    audioLevelIv.setImageResource(R.drawable.ic_volume_10);
                    break;
            }
        }

        // standby
        standByIv.setImageResource(R.drawable.ic_play);

        // mute
        if (audioMuted == null || !audioMuted.equals(info.audioMuted)) {
            audioMuted = info.audioMuted;
            audioMutedIv.setImageResource(audioMuted ?
                    R.drawable.ic_volume_off : R.drawable.ic_volume_on);
        }

        // standbye
        if (standBy == null || !standBy.equals(info.standBy)) {
            standBy = info.standBy;
            standByIv.setImageResource(standBy ?
                    R.drawable.ic_pause : R.drawable.ic_play);
        }
    }

    public void resetIfFirstTime() {
        if (!first) return;
        first = false;
        audioLevelIv.setImageResource(R.drawable.ic_volume_00);
        audioMutedIv.setImageResource(R.drawable.ic_volume_on);
        standByIv.setImageResource(R.drawable.ic_play_pause);
        discoverIv.setImageResource(R.drawable.ic_discover_idle);
        stateIv.setImageResource(R.drawable.ic_state_unknown);
        nbIv.setImageResource(R.drawable.ic_nb);
    }

    public void onEventMainThread(StateEvent event) {
        resetIfFirstTime();

        if (state == event) return;
        state = event;

        switch (event) {
            case Discover:
                discoverIv.setImageResource(R.drawable.ic_discover_ongoing);
                break;
            case Connect:
                stateIv.setImageResource(R.drawable.ic_state_connect);
                break;
            case Connected:
                stateIv.setImageResource(R.drawable.ic_state_connected);
                break;
            case NotConnected:
                stateIv.setImageResource(R.drawable.ic_state_connected_not);
                break;
        }

        if (event != StateEvent.Discover) {
            discoverIv.setImageResource(R.drawable.ic_discover_idle);
        }
    }

    private String mkText(String text) {
        return (text == null || text.length() == 0) ? "---" : text;
    }

    public void onEventMainThread(HeartBeatEvent beat) {
        resetIfFirstTime();

        eventIv.setImageResource(R.drawable.ic_event);
        contentView.removeCallbacks(backToIdle);
        contentView.postDelayed(backToIdle, Config.HEARTBEAT_DELAY_MS);
    }

    private Runnable backToIdle = new Runnable() {
        @Override
        public void run() {
            eventIv.setImageResource(R.drawable.ic_idle);
        }
    };

    /*
     * The EventBus allows other processes to execute code in the UI (aka Main) thread
     */

    public static abstract class RunnableInActivity implements Runnable {
        public Activity activity;
    }

    public void onEventMainThread(RunnableInActivity runnable) {
        // we are in the UI thread
        runnable.activity = MainActivity.this;
        runnable.run();
    }
}
