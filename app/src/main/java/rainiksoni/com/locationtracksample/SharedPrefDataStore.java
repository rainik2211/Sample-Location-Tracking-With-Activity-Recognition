package rainiksoni.com.locationtracksample;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by rainiksoni on 19/02/17.
 */

public class SharedPrefDataStore {

    public static final String SHARED_PREF_NAME = "Location_track_pref";


    // SharedPreferences are singletons and cached process-wide,
    // but, a getSP requires a stat(). so, do it once.
    protected static Map<String, SharedPreferences> sharedPrefs =
            new HashMap<String, SharedPreferences>(5);

    private static String getStoreNameBase() {
        return SHARED_PREF_NAME;
    }

    protected static SharedPreferences getSharedPreferences(Context context,
                                                            String storeName) {
        if (sharedPrefs.get(storeName) == null) {
            synchronized (sharedPrefs) {
                sharedPrefs.put(storeName,
                        context.getSharedPreferences(storeName,
                                Context.MODE_PRIVATE));
            }
        }
        return sharedPrefs.get(storeName);
    }

    // we can support a number of different stores internally

    public static String getKeyValueStoreName() {
        return getStoreNameBase() + "_misc";
    }

    public static void putBoolean(Context context, String key, Boolean value) {
        SharedPreferences s = getSharedPreferences(context, getKeyValueStoreName());
        SharedPreferences.Editor e = s.edit();
        e.putBoolean(key, value);
        e.commit();
    }

    public static boolean getBoolean(Context context, String key, boolean defaultValue) {
        SharedPreferences s = getSharedPreferences(context, getKeyValueStoreName());
        return s.getBoolean(key, defaultValue);
    }
}
