package digital.bauermeister.chromecastdisplay.service_nodejs;

import android.util.Log;

import digital.bauermeister.chromecastdisplay.shell.ShellCommand;

/**
 * Created by pascal on 3/9/16.
 */
public class CommandLauncher extends ShellCommand {

    // su -c "debian.sh mount && /root/ChromecastDisplay/debian-phone-nodejs/chromecast-display.js"

    private static final String TAG = "CommandLauncher";

    public boolean initDebian() {
        return executeOk("su", "-c", "debian.sh mount");
    }

    public boolean hasNodeJsProgram() {
        return executeOk(
                "su", "-c",
                "debian.sh -c 'test -x /root/ChromecastDisplay/debian-phone-nodejs/chromecast-display.js'");
    }

    public boolean runNodeJsProgram() {
        return executeAndHandleLines(
                "su", "-c",
                "debian.sh -c /root/ChromecastDisplay/debian-phone-nodejs/chromecast-display.js"
        );
    }
}
