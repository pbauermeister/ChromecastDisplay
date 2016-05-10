package digital.bauermeister.chromecastdisplay.service_nodejs;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.SubscriberExceptionEvent;
import digital.bauermeister.chromecastdisplay.MainActivity;
import digital.bauermeister.chromecastdisplay.R;
import digital.bauermeister.chromecastdisplay.Util;
import digital.bauermeister.chromecastdisplay.crash.UncaughtExceptionHandler;
import digital.bauermeister.chromecastdisplay.shell.ShellCommand;

public class NodejsBasedService extends Service {
    private static final String TAG = "TheService";

    final static int INIT_RETRY_DELAY = 3;
    final static int INIT_RETRY_MAX_TIME = 60;

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
        CommandLauncher cmd;

        // check for rooted
        boolean rooted = ShellCommand.isRooted();
        //toast("Rooted: " + rooted);111
        if (!rooted) {
            showDialog(0, R.string.error_not_rooted_message, null, true);
            return;
        }

        // try init Debian for a couple of attempts, may be necessary at boot time
        cmd = new CommandLauncher();
        int t = 0;
        while (!cmd.initDebian()) {
            sleep(INIT_RETRY_DELAY);
            t += INIT_RETRY_DELAY;
            if (t >= INIT_RETRY_MAX_TIME) {
                showDialog(0, R.string.error_init_debian_message, cmd, true);
                return;
            }
        }
        Log.d(TAG, "********** initDebian: ok in " + t);

        // check for nodejs script
        cmd = new CommandLauncher();
        t = 0;
        while (!cmd.hasNodeJsProgram()) {
            sleep(INIT_RETRY_DELAY);
            t += INIT_RETRY_DELAY;
            if (t >= INIT_RETRY_MAX_TIME) {
                showDialog(0, R.string.error_no_nodejs_program_message, cmd, true);
                return;
            }
            cmd.initDebian();
        }
        Log.d(TAG, "********** hasNodeJsProgram: ok in " + t);

        // run nodejs script
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
        Log.d(TAG, "Toasting: " + text);
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
        Log.d(TAG, "Dialog: " + getString(messageId));
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

    public void showDialog(final int titleId, final String message) {
        // we are still in the service's thread
        Log.d(TAG, "Dialog: " + message);
        EventBus.getDefault().post(new MainActivity.RunnableInActivity() {
            @Override
            public void run() {
                // EventBus executes this in the UI thread
                Util.showDialog(activity, titleId, message, null);
            }
        });
    }

    private interface RunnableInUiThread extends Runnable {
    }

    public class StartEvent {
    }

    private void sleep(int seconds) {
        try {
            Thread.sleep(1000 * seconds);
        } catch (InterruptedException e) {
        }
    }

    public void onEvent(final SubscriberExceptionEvent event) {
        // EventBus catches exceptions. We wanht to handle them so that we can fix them.
        // See https://github.com/greenrobot/EventBus/issues/55.
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                UncaughtExceptionHandler.INSTANCE.handle(
                        Thread.currentThread(),
                        event.causingSubscriber.getClass().getSimpleName() + " while handling " + event.causingEvent.getClass().getSimpleName(),
                        event.throwable);
            }
        });
    }
}