package digital.bauermeister.chromecastdisplay;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import de.greenrobot.event.EventBus;
import digital.bauermeister.chromecastdisplay.event.from_worker.ChromecastInfoEvent;
import digital.bauermeister.chromecastdisplay.event.from_worker.StateEvent;
import digital.bauermeister.chromecastdisplay.event.to_worker.PauseEvent;
import digital.bauermeister.chromecastdisplay.event.to_worker.ResumeEvent;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private Typeface tf;
    private TextAutoscrollView chromecastNameTv;
    private TextAutoscrollView appNameNameTv;
    private TextAutoscrollView statusTextTv;
    private TextAutoscrollView audioLevelTv;
    private TextAutoscrollView audioMutedTv;
    private TextAutoscrollView standByTv;
    private TextAutoscrollView stateTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);

        View contentView = findViewById(R.id.fullscreen_content);

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

        tf = Typeface.createFromAsset(getAssets(),
//                "fonts/MUSICNET.ttf" // KO
//                "fonts/PICHSIM_.ttf" // KO
//                "fonts/TRANGA__.TTF" // KO
//                "fonts/PICGRID_.ttf" // KO
//                "fonts/PICHXPL_.ttf" // No
//                "fonts/PICMOR__.ttf" // No
//                "fonts/Digital Dust.otf" // sososo
//                "fonts/EHSMB.TTF" // sososo
//                "fonts/LEDBDREV.TTF" // soso
//                "fonts/LEDBOARD.TTF" // soso
//                "fonts/Crashed Scoreboard.ttf" // sososo
//                "fonts/PICAHMS_.ttf" // soso
//                "fonts/LEDSimulator.ttf" // Soso
//                "fonts/TPF Display.ttf" // Soso
//                "fonts/PICAHMR_.ttf" // so

                "fonts/LLPIXEL3.ttf" // ***-
//                "fonts/Clubland.ttf" // ****
//                "fonts/Famirids.ttf" // ***-
//                "fonts/TRANA___.TTF" // **--
//                "fonts/PICHABS_.ttf" // *---
        );

        chromecastNameTv = (TextAutoscrollView) findViewById(R.id.chromecast_name);
        appNameNameTv = (TextAutoscrollView) findViewById(R.id.app_name);
        statusTextTv = (TextAutoscrollView) findViewById(R.id.status_text);
        audioLevelTv = (TextAutoscrollView) findViewById(R.id.audio_level);
        audioMutedTv = (TextAutoscrollView) findViewById(R.id.audio_muted);
        standByTv = (TextAutoscrollView) findViewById(R.id.stand_by);
        stateTv = (TextAutoscrollView) findViewById(R.id.state);
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
        chromecastNameTv.setText2(event.chromecastInfo.chromecastName); //+ " - The quick brown fox jumps over the lazy dog");
        appNameNameTv.setText2(event.chromecastInfo.appName);
        statusTextTv.setText2(mkText(event.chromecastInfo.statusText));
        audioLevelTv.setText2(event.chromecastInfo.audioLevel.toString());
        audioMutedTv.setText2(event.chromecastInfo.audioMuted ? "Mute" : "<)");
        standByTv.setText2(event.chromecastInfo.standBy ? "II" : "[>]");
    }

    public void onEventMainThread(StateEvent event) {
        String s = null;
        switch (event) {
            case Discover:
                s = "d...";
                break;
            case DiscoveredZero:
                s = "d?";
                break;
            case DiscoveredSome:
                s = "d";
                break;
            case Connect:
                s = "c...";
                break;
            case Connected:
                s = "c";
                break;
            case NotConnected:
                s = "c!";
                break;
        }
        stateTv.setText(s);
    }

    private String mkText(String text) {
        return (text == null || text.length() == 0) ? "---" : text;
    }

}
