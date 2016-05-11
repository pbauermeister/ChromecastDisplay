package digital.bauermeister.chromecastdisplay;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;
import digital.bauermeister.chromecastdisplay.bus_event.from_worker.ChromecastInfoEvent;
import digital.bauermeister.chromecastdisplay.bus_event.from_worker.HeartBeatEvent;
import digital.bauermeister.chromecastdisplay.bus_event.from_worker.StateEvent;
import digital.bauermeister.chromecastdisplay.bus_event.to_worker.PauseEvent;
import digital.bauermeister.chromecastdisplay.bus_event.to_worker.ResumeEvent;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private View contentView;
    private View decorView;
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

    private float audioLevel = -1f;
    private Boolean audioMuted = null;
    private Boolean standBy = null;
    private StateEvent state = null;
    private boolean first = true;
    private PowerManager.WakeLock wakeLock;

    // Media players
    private MediaPlayer heartbeatMp = null;
    private MediaPlayer noiseMp = null;
    private MediaPlayer discoverMp = null;
    private MediaPlayer connectingMp = null;
    private MediaPlayer connectedMp = null;
    private MediaPlayer notConnectedMp = null;

    private String xcurrentUdn = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "********** MainActivity onCreate");

        setContentView(R.layout.activity_fullscreen);
        contentView = findViewById(R.id.fullscreen_content);
        initAudio();

        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        decorView = getWindow().getDecorView();
        setImmersive();

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

        // backlight
        final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        this.wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "My Tag");
        this.wakeLock.acquire();

        // long press
        contentView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                cycleChosenDevice();
                setImmersive();
                return true;
            }
        });
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setImmersive();
            }
        });
    }

    private void setImmersive() {
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Log.d(TAG, "onWindowFocusChanged: " + hasFocus);
        if (hasFocus) {
            setImmersive();
        }
    }

    private void initAudio() {
        heartbeatMp = MediaPlayer.create(this, R.raw.radio_beep);
        noiseMp = MediaPlayer.create(this, R.raw.snd23503__percy_duke__radio_static);
        discoverMp = MediaPlayer.create(this, R.raw.snd70299__kizilsungur__sonar);
        connectingMp = MediaPlayer.create(this, R.raw.connecting);
        connectedMp = MediaPlayer.create(this, R.raw.snd42796__digifishmusic__sonar_ping);
        notConnectedMp = MediaPlayer.create(this, R.raw.disconnected);

        // tweaks
        discoverMp.setVolume(0.03f, 0.03f);
        connectedMp.setVolume(0.4f, 0.4f);

        // bg initial noise
        noiseMp.setLooping(true);
        noiseMp.setVolume(0.05f, 0.05f);
        noiseMp.start();
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
        this.wakeLock.release();
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private boolean isSelectedDevice(ChromecastInfo info) {
        String thisUdn = info.udn;
        if (thisUdn == null) {
            return false;
        }

        // none selected -> compare to the first one of all seen devices
        String chosenUdn = PreferencesManager.INSTANCE.getChosenDeviceUdn();
        if (chosenUdn == null) {
            PreferencesManager.INSTANCE.putChosenDevice(info);
            chosenUdn = PreferencesManager.INSTANCE.getChosenDeviceUdn();
        }

        // is it the one selected?
        return thisUdn.equals(chosenUdn);
    }

    public void onEventMainThread(ChromecastInfoEvent event) {
        resetIfFirstTime();
        ChromecastInfo selected = PreferencesManager.INSTANCE.getChosenDevice();
        // int a = 0; a = a / a; // test crash handling

        Log.d(TAG, "??? is=" + isSelectedDevice(event.chromecastInfo) + " sel=" + selected + " has=" + DeviceManager.INSTANCE.has(event.chromecastInfo));

        if (!isSelectedDevice(event.chromecastInfo) && selected != null
                && !DeviceManager.INSTANCE.has(selected))
            updateDisplay(selected, false); // display wanted but unseen device
        else
            updateDisplay(event.chromecastInfo, DeviceManager.INSTANCE.has(event.chromecastInfo));
    }

    public void updateDisplay(ChromecastInfo info, boolean sure) {
        // devices
        Map<String, ChromecastInfo> devices = DeviceManager.INSTANCE.get();
        nbIv.setImageResource(devices.size() > 1 ? R.drawable.ic_nb_many : R.drawable.ic_nb);
        for (Map.Entry<String, ChromecastInfo> each : devices.entrySet()) {
            Log.d(TAG, each.getKey() + " --> " + each.getValue().chromecastName);
        }

        if (!isSelectedDevice(info))
            return;

        // texts
        chromecastNameTv.setText2(info.chromecastName); //+ " - The quick brown fox jumps over the lazy dog");
        appNameNameTv.setText2(!sure ? "???" : info.appName);
        statusTextTv.setText2(!sure ? "???" : mkText(info.statusText));

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

    private void stopNoiseMp() {
        if (noiseMp.isPlaying()) {
            noiseMp.stop();
        }
    }

    private void playEventSound(MediaPlayer mp) {
        if (heartbeatMp.isPlaying()) {
            heartbeatMp.stop();
        }
        mp.start();
    }

    public void onEventMainThread(StateEvent event) {
        resetIfFirstTime();

        if (state == event) return;
        state = event;

        switch (event) {
            case Discover:
                discoverIv.setImageResource(R.drawable.ic_discover_ongoing);
                playEventSound(discoverMp);
                break;
            case Connect:
                stateIv.setImageResource(R.drawable.ic_state_connect);
                playEventSound(connectingMp);
                break;
            case Connected:
                stateIv.setImageResource(R.drawable.ic_state_connected);
                stopNoiseMp();
                playEventSound(connectedMp);
                break;
            case NotConnected:
                stateIv.setImageResource(R.drawable.ic_state_connected_not);
                playEventSound(notConnectedMp);
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

        if (!heartbeatMp.isPlaying()) {
            heartbeatMp.release();
            heartbeatMp = MediaPlayer.create(this, R.raw.radio_beep);
            heartbeatMp.start();
        }
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

    private void cycleChosenDevice() {
        Log.d(TAG, "*cycleChosenDevice*");
        HashMap<String, ChromecastInfo> devices = DeviceManager.INSTANCE.get();
        List<String> udns = DeviceManager.getUdns(devices);

        if (udns.size() == 0) return;

        String udn = PreferencesManager.INSTANCE.getChosenDeviceUdn();

        // no chosen device, or chosen device not present => take first present
        if (udn == null || !udns.contains(udn)) {
            udn = udns.get(0);
        }

        // take next present device
        else if (udn != null && udns.contains(udn)) {
            int index = udns.indexOf(udn);
            index = (index + 1) % udns.size();
            Log.d(TAG, "  OLD: " + udn);
            udn = udns.get(index);
        }

        // choose and display
        if (udn != null && udns.contains(udn)) {
            ChromecastInfo info = devices.get(udn);
            PreferencesManager.INSTANCE.putChosenDevice(info);
            updateDisplay(info, false);
            Log.d(TAG, "  NEW: " + udn + " " + info.chromecastName);
        }
    }
}
