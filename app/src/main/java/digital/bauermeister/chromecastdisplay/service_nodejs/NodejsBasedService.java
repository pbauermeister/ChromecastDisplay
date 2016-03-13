package digital.bauermeister.chromecastdisplay.service_nodejs;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import de.greenrobot.event.EventBus;
import digital.bauermeister.chromecastdisplay.MainActivity;
import digital.bauermeister.chromecastdisplay.R;
import digital.bauermeister.chromecastdisplay.Util;
import digital.bauermeister.chromecastdisplay.shell.ShellCommand;

public class NodejsBasedService extends Service {
    private static final String TAG = "TheService";

    @Override
    public IBinder onBind(Intent intent) {
        // No binding provided
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "*** Service created ***");
        super.onCreate();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "*** Service destroyed ***");
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        EventBus.getDefault().post(new StartEvent()); // fire up
        return START_STICKY;
    }

    public void onEventBackgroundThread(StartEvent event) {
        boolean rooted = ShellCommand.isRooted();
//        toast("Rooted: " + rooted);
        CommandLauncher cmd;

        if (!rooted) {
            showDialog(0, R.string.error_not_rooted_message, null, true);
            return;
        }

        cmd = new CommandLauncher();
        if (!cmd.initDebian()) {
            showDialog(0, R.string.error_init_debian_message, cmd, true);
            return;
        }

        cmd = new CommandLauncher();
        if (!cmd.hasNodeJsProgram()) {
            showDialog(0, R.string.error_no_nodejs_program_message, cmd, true);
            return;
        }

        while (true) {
            cmd = new CommandHandler();
            if (!cmd.runNodeJsProgram()) {
                showDialog(0, R.string.error_nodejs_program_message, cmd, false);
            }
        }

    }

    public void onEventMainThread(RunnableInUiThread runnable) {
        // we are in the UI thread
        runnable.run();
    }

    /*
     * The EventBus allows us to execute code in the UI (aka Main) thread
     */

    private void toast(final String text) {
        // we are still in the service's thread
        EventBus.getDefault().post(new RunnableInUiThread() {
            @Override
            public void run() {
                // EventBus executes this in the UI thread
                Util.toast(NodejsBasedService.this, text);
            }
        });
    }

    public void showDialog(final int titleId, final int messageId,
                           final CommandLauncher cmd, final boolean exit) {
        // we are still in the service's thread
        EventBus.getDefault().post(new MainActivity.RunnableInActivity() {
            @Override
            public void run() {
                // EventBus executes this in the UI thread
                Util.DialogListener listener = exit ?
                        new Util.DialogListener() {
                            @Override
                            public void onDismiss() {
                                activity.finish();
                            }
                        }
                        : null;

                if (cmd == null) {
                    Util.showDialog(activity, titleId, messageId, listener);
                } else {
                    String message = activity.getString(messageId);
                    if (cmd.getError() != null) message += "\n\nError: " + cmd.getError();
                    message += "\n\nReturned: " + cmd.getReturnCode();
                    Util.showDialog(activity, titleId, message, listener);
                }
            }
        });
    }

    private interface RunnableInUiThread extends Runnable {
    }

    public class StartEvent {
    }
}
