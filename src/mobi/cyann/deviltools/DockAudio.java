package mobi.cyann.deviltools;

import android.app.ActivityManagerNative;
import android.content.Context;
import android.content.Intent;
import android.os.UserHandle;
import android.content.SharedPreferences;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceManager;

public class DockAudio implements OnPreferenceChangeListener {

    public static final String FILE = "/sys/class/switch/dock/state";

    public static boolean isSupported() {
        return Utils.fileExists(FILE);
    }

    /**
     * Restore dockaudio settings from SharedPreferences. (Write to kernel.)
     * @param context       The context to read the SharedPreferences from
     */
    public static void restore(Context context) {
        if (!isSupported()) {
            return;
        }

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean dockAudio = sharedPrefs.getBoolean(FILE, false);
        Intent i = new Intent("com.cyanogenmod.settings.SamsungDock");
        i.putExtra("data", (dockAudio? "1" : "0"));
        ActivityManagerNative.broadcastStickyIntent(i, null, UserHandle.USER_ALL);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
 	String boxValue = (((CheckBoxPreference)preference).isChecked() ? "1" : "0");
        Intent i = new Intent("com.cyanogenmod.settings.SamsungDock");
        i.putExtra("data", boxValue);
        ActivityManagerNative.broadcastStickyIntent(i, null, UserHandle.USER_ALL);
	if(((CheckBoxPreference)preference).isChecked()) {
		DevilTweaksFragment.setPreferenceBoolean(FILE, true);
	} else {
		DevilTweaksFragment.setPreferenceBoolean(FILE, false);
	}
        return true;
    }

}
