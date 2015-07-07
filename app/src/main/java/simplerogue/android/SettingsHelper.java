package simplerogue.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import simplerogue.android.activity.SettingsActivity;

/**
 * @author Adam Wyłuda
 */
public class SettingsHelper
{
    public static int getFontSize(Context context)
    {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return Integer.parseInt(sharedPref.getString(SettingsActivity.KEY_FONT_SIZE, "24"));
    }

    public static boolean isAntialiasing(Context context)
    {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getBoolean(SettingsActivity.KEY_FONT_ANTIALIASING, true);
    }
}
