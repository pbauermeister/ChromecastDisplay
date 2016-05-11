package digital.bauermeister.chromecastdisplay.crash;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
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

        final String versionName = getVersionName();
        stackTextView.append("Application (v " + versionName
                + ") has crashed, sorry. \n\n");
        stackTextView.append(stackTrace != null ? stackTrace : "-Stack trace missing-");

        findViewById(R.id.dump).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, stackTrace);
                Toast.makeText(getApplicationContext(),
                        "Stack trace dumped to logs", Toast.LENGTH_LONG).show();
            }
        });

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


        getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {
                killMyServices();
            }
        }, 500);
    }

    private void killMyServices() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

        String prefix = getPackageName() + ".";

        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            String path = service.service.getClassName();
            if (path.startsWith(prefix)) {
                Class clz = null;
                try {
                    clz = Class.forName(path);
                } catch (ClassNotFoundException e) {
                }
                if (clz != null) {
                    Log.d(TAG, "### stopping service " + path);
                    stopService(new Intent(this, clz));
                    Log.d(TAG, "### stopped service " + path);
                }
            }
        }

    }
}
