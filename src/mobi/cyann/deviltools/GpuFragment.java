/**
 * DevilPreference.java
 * Jan 14, 2012 9:36:34 AM
 */
package mobi.cyann.deviltools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mobi.cyann.deviltools.PreferenceListFragment.OnPreferenceAttachedListener;
import mobi.cyann.deviltools.preference.IntegerPreference;
import mobi.cyann.deviltools.SysCommand;
import android.app.ActivityManager;
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
public class GpuFragment extends BasePreferenceFragment implements OnPreferenceChangeListener {
	public GpuFragment() {
		super(R.layout.gpu);
	}
	
	//private final static String LOG_TAG = "DevilTools.tweaks";

	private ListPreference mGpuClock[] = new ListPreference[5];

        private ContentResolver mContentResolver;
	private static SharedPreferences preferences;

    public static final String[] GPU_CLOCK_FILE_PATH = new String[] {
	"/sys/module/mali/parameters/step0_clk",
	"/sys/module/mali/parameters/step1_clk",
	"/sys/module/mali/parameters/step2_clk",
	"/sys/module/mali/parameters/step3_clk",
	"/sys/module/mali/parameters/step4_clk"
	};

	@Override
    	public void onCreate(Bundle savedInstanceState) {
        	super.onCreate(savedInstanceState);

	preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        PreferenceScreen prefSet = getPreferenceScreen();
        mContentResolver = getActivity().getApplicationContext().getContentResolver();
	SysCommand sysCommand = SysCommand.getInstance();

        for (int i = 0; i < GPU_CLOCK_FILE_PATH.length; i++) {
	if (i == 0)
        mGpuClock[i] = (ListPreference) findPreference("key_step0_clk");
	else if (i == 1)
        mGpuClock[i] = (ListPreference) findPreference("key_step1_clk");
	else if (i == 2)
        mGpuClock[i] = (ListPreference) findPreference("key_step2_clk");
	else if (i == 3)
        mGpuClock[i] = (ListPreference) findPreference("key_step3_clk");
	else if (i == 4)
        mGpuClock[i] = (ListPreference) findPreference("key_step4_clk");

        mGpuClock[i].setOnPreferenceChangeListener(this);
	   if(sysCommand.readSysfs(GPU_CLOCK_FILE_PATH[i]) > 0) {
           mGpuClock[i].setSummary(sysCommand.getLastResult(0) + " Mhz");
	   }
	}

    }
	
    public boolean onPreferenceChange(Preference preference, Object objValue) {
	SysCommand sc = SysCommand.getInstance();
	int status, index;
        for (int i = 0; i < GPU_CLOCK_FILE_PATH.length; i++) {
	    if (preference == mGpuClock[i]) {
            status = Integer.valueOf((String) objValue);
            index = mGpuClock[i].findIndexOfValue((String) objValue);
            mGpuClock[i].setSummary(mGpuClock[i].getEntries()[index]);
	    sc.writeSysfs(GPU_CLOCK_FILE_PATH[i], ((String) objValue));
	    if (i == 0)
        	setPreferenceString(getString(R.string.key_step0_clk), ((String) objValue));
	    else if (i == 1)
        	setPreferenceString(getString(R.string.key_step1_clk), ((String) objValue));
	    else if (i == 2)
        	setPreferenceString(getString(R.string.key_step2_clk), ((String) objValue));
	    else if (i == 3)
        	setPreferenceString(getString(R.string.key_step3_clk), ((String) objValue));
	    else if (i == 4)
        	setPreferenceString(getString(R.string.key_step4_clk), ((String) objValue));
            return true;
            }
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
