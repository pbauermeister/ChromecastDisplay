package digital.bauermeister.chromecastdisplay.shell;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * Executes a shell command.
 *
 * @author pascal
 */
public class ShellCommand {

    private static final String TAG = "ShellCommand";
    protected String command = "";
    protected DataOutputStream stdinWriteStream = null;
    protected DataInputStream stderrReadStream = null;
    protected DataInputStream stdoutReadStream = null;
    protected Process process = null;
    protected BufferedReader stdinReader;
    protected BufferedReader stderrReader;
    protected String error = null;
    protected Integer returnCode = null;

    private static int cmdNr = 0;

    public static boolean isRooted() {
        return new ShellCommand().executeOk("su", "-c", "true");
    }

    public String toString() {
        return "cmd=[" + cmdNr + "]";
    }

    public int execute(String... cmd) {
         start(cmd);
        return finish();
    }

    public boolean executeOk(String... cmd) {
        return execute(cmd) == 0;
    }

    public String getError() {
        return error;
    }

    public int getReturnCode() {
        return returnCode==null?0 : returnCode;
    }

    private boolean start(String... cmd) {
        // for debug
        cmdNr++;
        for (String e : cmd) {
            if (command.length() > 0) command += "|";
            command += e;
        }
        Log.d(TAG, "Launching shell " + toString() + " > [" + command + "]");

        // run
        try {
            process = new ProcessBuilder().command(cmd).redirectErrorStream(true).start();

            stdinWriteStream = new DataOutputStream(process.getOutputStream());
            stdoutReadStream = new DataInputStream(process.getInputStream());
            stderrReadStream = new DataInputStream(process.getErrorStream());
            stdinReader = new BufferedReader(new InputStreamReader(stdoutReadStream));
            stderrReader = new BufferedReader(new InputStreamReader(stderrReadStream));

        } catch (IOException e) {
            Log.e(TAG, toString() + " ERROR execute(): " + e);
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean executeAndHandleLines(String... cmd) {
        if (start(cmd)) {
            while (true) {
                String line;
                try {
                    line = stdinReader.readLine();
                    if (line == null) {
                        break;
                    }
                } catch (IOException e) {
                    Log.e(TAG, toString() + " ERROR executeUntil() readLine: " + e);
                    e.printStackTrace();
                    break;
                }

                try {
                    handleLine(line);
                } catch (Exception e) {
                    Log.e(TAG, toString() + " ERROR parsing line " + line);
                    e.printStackTrace();
                }
            }
            return finish() == 0;
        } else {
            return false;
        }
    }

    public void destroy() {
        try {
            process.destroy();
        } catch (Exception e) {
            Log.e(TAG, toString() + " ERROR destroy(): " + e);
            e.printStackTrace();
        }
    }

    public int finish() {
        if (process == null) return -100;

        // wait dead
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            Log.e(TAG, toString() + " ERROR finish() waitFor: " + e);
            e.printStackTrace();
            return -101;
        }

        // return code
        for (int i = 10; i > 0; --i) {
            try {
                returnCode = process.exitValue();
                break;
            } catch (IllegalThreadStateException e) {
                if (i == 1) return -102;
                delay(100);
            }
        }

        // error stream
        error = "";
        try {
            while (stderrReader.ready()) {
                error += stderrReader.readLine() + "\n";
            }
        } catch (IOException e) {
            Log.e(TAG, toString() + " ERROR finish() readLine: " + e);
            e.printStackTrace();
        }
        if(error.length()==0) error = null;
        Log.v(TAG, toString() + " err: " + error);
        Log.v(TAG, toString() + " ret: " + returnCode);

        // close all
        tryCloseOutput(stdinWriteStream);
        tryCloseInput(stdoutReadStream);
        tryCloseInput(stderrReadStream);

        return returnCode;
    }

    private void tryCloseInput(InputStream s) {
        try {
            if (s != null) s.close();
        } catch (IOException e) {
        }
    }

    private void tryCloseOutput(OutputStream s) {
        try {
            if (s != null) s.close();
        } catch (IOException e) {
        }
    }

    private void delay(int ms) {
        try {
            Thread.sleep(1000 * 10);
        } catch (InterruptedException e) {
        }
    }

    // Default implementation
    protected void handleLine(String line) {
        Log.v(TAG, toString() + " >>> " + line);
    }
}
