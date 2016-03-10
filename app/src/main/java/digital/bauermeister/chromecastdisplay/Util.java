package digital.bauermeister.chromecastdisplay;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * A small collection of useful features.
 *
 * @author pascal
 */
public class Util {

    private static ArrayList<Toast> toasts = new ArrayList<Toast>();

//    public static void delay(int ms) {
//        try {
//            Thread.sleep(1000 * 10);
//        } catch (InterruptedException e) {
//        }
//    }

    public static void toast(Context context, String text) {
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
        toasts.add(toast);
    }

    public static synchronized void cancelToasts() {
        for (Toast t : toasts) {
            t.cancel();
        }
        toasts.clear();
    }

    public static void beep() {
        ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
        tg.startTone(ToneGenerator.TONE_PROP_BEEP, 300);
    }

    public static void showDialog(Context context, int titleId, String message,
                                  final DialogListener listener) {
        AlertDialog.Builder alertBuilder;
        alertBuilder = new AlertDialog.Builder(context);
        Resources res = context.getResources();

        AlertDialog alert = alertBuilder.create();
        if (titleId != 0) alert.setTitle(titleId);
        alert.setMessage(message);
        alert.setButton(AlertDialog.BUTTON_POSITIVE, res.getString(android.R.string.ok),
                (DialogInterface.OnClickListener) null);
        alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (listener != null) {
                    listener.onDismiss();
                }
            }
        });
        alert.show();
    }

    public static void showDialog(Context context, int titleId, int messageId,
                                  final DialogListener listener) {
        showDialog(context, titleId, context.getString(messageId), listener);
    }

    public interface DialogListener {
        void onDismiss();
    }

}
