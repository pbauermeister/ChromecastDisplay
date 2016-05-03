package digital.bauermeister.chromecastdisplay;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 * Created by pascal on 5/2/16.
 */


public enum PreferencesManager {
    INSTANCE;

    private static final String TAG = "PreferencesManager";
    private static final String CHOSEN_DEVICE_KEY = "cachedChosenDevice";

    private SharedPreferences pref;
    private ChromecastInfo cachedChosenDevice = null;

    public void init(Context context) {
        pref = context.getSharedPreferences("MainPreference", Context.MODE_PRIVATE);
        cachedChosenDevice = null;
    }

    public ChromecastInfo getChosenDevice() {
        if (cachedChosenDevice != null) return cachedChosenDevice;

        String json = pref.getString(CHOSEN_DEVICE_KEY, null);
        if (json == null) return null;
        Gson gson = new Gson();
        try {
            ChromecastInfo info = gson.fromJson(json, ChromecastInfo.class);
            Log.d(TAG, "** Get chosen device: " + info.chromecastName);
            cachedChosenDevice = info;
            return info;
        } catch (JsonSyntaxException e) {
            Log.d(TAG, "** Get chosen device: -none-");
            return null;
        }
    }

    public String getChosenDeviceUdn() {
        getChosenDevice();
        if (cachedChosenDevice == null) return null;
        return cachedChosenDevice.udn;
    }

    public void putChosenDevice(ChromecastInfo info) {
        Log.d(TAG, "** Put chosen device: " + info.chromecastName);
        cachedChosenDevice = info;
        Gson gson = new Gson();
        String json = gson.toJson(info);
        pref.edit().putString(CHOSEN_DEVICE_KEY, json).commit();
    }
}
