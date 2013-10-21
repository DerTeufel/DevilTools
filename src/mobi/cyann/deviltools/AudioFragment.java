package mobi.cyann.deviltools;

import mobi.cyann.deviltools.PreferenceListFragment.OnPreferenceAttachedListener;
import mobi.cyann.deviltools.EqTuningPreference;
import mobi.cyann.deviltools.preference.IntegerPreference;
import mobi.cyann.deviltools.preference.StatusPreference;
import mobi.cyann.deviltools.SysCommand;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.ListPreference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.view.View;
import android.widget.TextView;


public class AudioFragment extends BasePreferenceFragment {
	//private final static String LOG_TAG = "DevilTools.AudioActivity";
	
	public AudioFragment() {
		super(R.layout.audio);
	}

    public static final String[] SCOOBYDOO_EQ_KEY = new String[] {
        "key_scoobydoo_sound_headphone_eq_b1_gain",
        "key_scoobydoo_sound_headphone_eq_b2_gain",
        "key_scoobydoo_sound_headphone_eq_b3_gain",
        "key_scoobydoo_sound_headphone_eq_b4_gain",
        "key_scoobydoo_sound_headphone_eq_b5_gain"
    };

    public static final String[] SCOOBYDOO_FILE_PATH = new String[] {
        "/sys/devices/virtual/misc/scoobydoo_sound/headphone_eq_b1_gain",
        "/sys/devices/virtual/misc/scoobydoo_sound/headphone_eq_b2_gain",
        "/sys/devices/virtual/misc/scoobydoo_sound/headphone_eq_b3_gain",
        "/sys/devices/virtual/misc/scoobydoo_sound/headphone_eq_b4_gain",
        "/sys/devices/virtual/misc/scoobydoo_sound/headphone_eq_b5_gain"
    };

    public static final String[] VOODOO_EQ_KEY = new String[] {
        "key_voodoo_sound_headphone_eq_b1_gain",
        "key_voodoo_sound_headphone_eq_b2_gain",
        "key_voodoo_sound_headphone_eq_b3_gain",
        "key_voodoo_sound_headphone_eq_b4_gain",
        "key_voodoo_sound_headphone_eq_b5_gain"
    };

    public static final String[] VOODOO_FILE_PATH = new String[] {
        "/sys/devices/virtual/misc/voodoo_sound/headphone_eq_b1_gain",
        "/sys/devices/virtual/misc/voodoo_sound/headphone_eq_b2_gain",
        "/sys/devices/virtual/misc/voodoo_sound/headphone_eq_b3_gain",
        "/sys/devices/virtual/misc/voodoo_sound/headphone_eq_b4_gain",
        "/sys/devices/virtual/misc/voodoo_sound/headphone_eq_b5_gain"
    };
}
