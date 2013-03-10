/**
 * DevilPreference.java
 * Jan 14, 2012 9:36:34 AM
 */
package mobi.cyann.deviltools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mobi.cyann.deviltools.PreferenceListFragment.OnPreferenceAttachedListener;
import mobi.cyann.deviltools.ColorTuningPreference;
import mobi.cyann.deviltools.preference.IntegerPreference;
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

/**
 * @DerTeufel1980
 *
 */
public class DevilTweaksFragment extends BasePreferenceFragment implements OnPreferenceChangeListener {
	public DevilTweaksFragment() {
		super(R.layout.devil_tweak);
	}
	
	//private final static String LOG_TAG = "DevilTools.tweaks";

	private ListPreference mBigmem;
    	private ColorTuningPreference mColor;
    	private ListPreference mMdnie;
    	private ListPreference mVoodooPre;

        private ContentResolver mContentResolver;
	private static SharedPreferences preferences;

    	private static final String FILE_PATH = "/sys/kernel/bigmem/enable";
	
	@Override
    	public void onCreate(Bundle savedInstanceState) {
        	super.onCreate(savedInstanceState);

	preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        PreferenceScreen prefSet = getPreferenceScreen();
        mContentResolver = getActivity().getApplicationContext().getContentResolver();

        mMdnie = (ListPreference) findPreference("mdnie");
        mMdnie.setEnabled(Mdnie.isSupported());

  	mColor = (ColorTuningPreference) findPreference("color_tuning");
        mColor.setEnabled(ColorTuningPreference.isSupported());

    	mVoodooPre = (ListPreference) findPreference("key_voodoo_gamma_presets");
	if (ColorTuningPreference.isVoodoo()) {
    	mVoodooPre.setOnPreferenceChangeListener(this);
	}
        mVoodooPre.setEnabled(ColorTuningPreference.isVoodoo());

        if (!Mdnie.isSupported() && !ColorTuningPreference.isSupported()) {
            PreferenceCategory category = (PreferenceCategory) getPreferenceScreen().findPreference("screen_category");
            category.removePreference(mMdnie);
            category.removePreference(mColor);
            getPreferenceScreen().removePreference(category);	
        } else if (Mdnie.isSupported()) {
        mMdnie.setOnPreferenceChangeListener(new Mdnie());
        }

	// get current available ram
        MemoryInfo mi = new MemoryInfo();
        ActivityManager activityManager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);
        long totalMegs = mi.totalMem / 1048576L;

        mBigmem = (ListPreference) findPreference("key_bigmem");
	if (!Utils.fileExists(FILE_PATH)) {
            PreferenceCategory category = (PreferenceCategory) getPreferenceScreen().findPreference("bigmem_category");
            category.removePreference(mBigmem);
            getPreferenceScreen().removePreference(category);
	} else {
        mBigmem.setOnPreferenceChangeListener(this);
        mBigmem.setSummary(String.valueOf("Current Ram: " + totalMegs) + " MB");
	}
    }
	
    public boolean onPreferenceChange(Preference preference, Object objValue) {
	SysCommand sc = SysCommand.getInstance();
	int status, index;
	if (preference == mBigmem) {
            status = Integer.valueOf((String) objValue);
            index = mBigmem.findIndexOfValue((String) objValue);
            mBigmem.setSummary("After Reboot: " + mBigmem.getEntries()[index]);
	    sc.writeSysfs("/sys/kernel/bigmem/enable", ((String) objValue));
	    sc.writeSysfs("/data/local/devil/bigmem", ((String) objValue));
	    setPreferenceString(getString(R.string.key_bigmem), ((String) objValue));
            return true;
        } else if (preference == mVoodooPre) {
            status = Integer.valueOf((String) objValue);
            index = mVoodooPre.findIndexOfValue((String) objValue);
            mVoodooPre.setSummary(mVoodooPre.getEntries()[index]);
            switch(status){
            	case 0:
                    ColorTuningPreference.Preset(0);
                    break;
            	case 1:
                    ColorTuningPreference.Preset(1);
                    break;
            	case 2:
                    ColorTuningPreference.Preset(2);
                    break;
            	default:
                    ColorTuningPreference.Preset(0);
                    break;
	    }
            return true;
        }

        return false;
    }

    public static void setPreferenceString(String key, String value) {
	Editor ed = preferences.edit();
	ed.putString(key, value);
	ed.commit();
    }

    public static void setPreferenceInteger(String key, int value) {
	Editor ed = preferences.edit();
	ed.putInt(key, value);
	ed.commit();
    }

}
