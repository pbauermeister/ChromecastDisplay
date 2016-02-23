package digital.bauermeister.chromecastdisplay;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import de.greenrobot.event.EventBus;
import digital.bauermeister.chromecastdisplay.event.from_worker.ChromecastInfoEvent;
import digital.bauermeister.chromecastdisplay.event.to_worker.PauseEvent;
import digital.bauermeister.chromecastdisplay.event.to_worker.ResumeEvent;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);

        View mContentView = findViewById(R.id.fullscreen_content);

        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        EventBus.getDefault().register(this);
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
        TextView tv;
        tv = (TextView) findViewById(R.id.chromecast_name);
        tv.setText(event.chromecastInfo.chromecastName);

        tv = (TextView) findViewById(R.id.app_name);
        tv.setText(event.chromecastInfo.appName);

        tv = (TextView) findViewById(R.id.status_text);
        tv.setText(mkText(event.chromecastInfo.statusText));

        tv = (TextView) findViewById(R.id.audio_level);
        tv.setText(event.chromecastInfo.audioLevel.toString());

        tv = (TextView) findViewById(R.id.audio_muted);
        tv.setText(event.chromecastInfo.audioMuted.toString());

        tv = (TextView) findViewById(R.id.stand_by);
        tv.setText(event.chromecastInfo.standBy.toString());
    }

    private String mkText(String text) {
        return (text == null || text.length() == 0) ? "---" : text;
    }

}
