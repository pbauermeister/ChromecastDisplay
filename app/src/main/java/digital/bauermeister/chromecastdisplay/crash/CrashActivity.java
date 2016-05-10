package digital.bauermeister.chromecastdisplay.crash;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.TextView;
import android.widget.Toast;

import digital.bauermeister.chromecastdisplay.R;

public class CrashActivity extends Activity {
    public static final String STACKTRACE = "stacktrace";
    protected static final String TAG = "CrashActivity";

    private String getVersionName() {
        try {
            return getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (Exception e) {
            return "???";
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crash);

        final String stackTrace = getIntent().getStringExtra(STACKTRACE);
        Log.e(TAG, stackTrace);

        final TextView stackTextView = (TextView) findViewById(R.id.stack_text);
        stackTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
        stackTextView.setClickable(false);
        stackTextView.setLongClickable(true);
        stackTextView.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.e(TAG, stackTrace);
                Toast.makeText(getApplicationContext(),
                        "Stack trace dumped to logs", Toast.LENGTH_LONG).show();
                return true;
            }
        });

        final String versionName = getVersionName();
        stackTextView.append("Application (v " + versionName
                + ") has crashed, sorry. \n\n");
        stackTextView.append(stackTrace != null ? stackTrace : "-Stack trace missing-");

        findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = getPackageManager().getLaunchIntentForPackage(
                        getPackageName());
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
                Process.killProcess(Process.myPid());
                System.exit(10);
            }
        });
    }
}
