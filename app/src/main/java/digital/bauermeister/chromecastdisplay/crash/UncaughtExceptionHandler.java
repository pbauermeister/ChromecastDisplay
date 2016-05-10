package digital.bauermeister.chromecastdisplay.crash;

import android.content.Context;
import android.content.Intent;

import org.joda.time.DateTime;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by pascal on 10/13/15.
 * <p/>
 * Works in:
 * - Main(=UI) thread
 * - Async tasks
 *
 * <p/>
 * Does NOT work in:
 * - Service
 * - BackgroundThread
 */
public enum UncaughtExceptionHandler {
    INSTANCE;

    private Thread.UncaughtExceptionHandler defaultUncaughtExceptionHandler;
    private Context context;

    public void init(Context context) {
        this.context = context;
        defaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        // setup handler for uncaught exception
        Thread.setDefaultUncaughtExceptionHandler(myUncaughtExceptionHandler);
    }

    // handler listener
    private Thread.UncaughtExceptionHandler myUncaughtExceptionHandler =
            new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread thread, Throwable exception) {
                    handle(thread, "in thread " + Thread.currentThread().getName(), exception);
                }
            };

    public void handle(Thread thread, String info, Throwable exception) {
        if (context != null) {
            // restore, to avoid spurious loop
            Thread.setDefaultUncaughtExceptionHandler(defaultUncaughtExceptionHandler);

            DateTime dt = new DateTime();
            String crashInfo = "On " + dt + " " + info + ":\n" + getTrace(exception);
            Intent intent = new Intent(context, CrashActivity.class);
            intent.putExtra(CrashActivity.STACKTRACE, crashInfo);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);

            // Process.killProcess(Process.myPid());
            System.exit(10);
        } else {
            // re-throw critical exception further to the os (important)
            //defaultUncaughtExceptionHandler.uncaughtException(thread, exception);
            throw new RuntimeException(exception);
        }
    }

    private static String getTrace(Throwable exception) {
        StringWriter stackTrace = new StringWriter();
        exception.printStackTrace(new PrintWriter(stackTrace));
        String stackStr = stackTrace.toString();
        return stackStr;
    }
}
