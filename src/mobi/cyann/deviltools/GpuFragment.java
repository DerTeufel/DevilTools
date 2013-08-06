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

    private static final String[] KEY_GPU_CLOCK = new String[] {
        "key_step0_clk",
        "key_step1_clk",
        "key_step2_clk",
        "key_step3_clk",
        "key_step4_clk",
    };

	@Override
    	public void onCreate(Bundle savedInstanceState) {
        	super.onCreate(savedInstanceState);

	preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        PreferenceScreen prefSet = getPreferenceScreen();
        mContentResolver = getActivity().getApplicationContext().getContentResolver();
	SysCommand sysCommand = SysCommand.getInstance();

	boolean supported = isSupported();

	int i = 0;
	if (supported) {
	   for (String filePath : GPU_CLOCK_FILE_PATH) {
           mGpuClock[i] = (ListPreference) findPreference(KEY_GPU_CLOCK[i]);
	   	if (mGpuClock[i] != null) {
        	   mGpuClock[i].setOnPreferenceChangeListener(this);
           	   String value = preferences.getString(filePath, "-1");
		   if(!value.equals("-1")) {
		   sysCommand.writeSysfs(filePath, value);
		   setPreferenceString(filePath, value);
		   }
	   	   if(sysCommand.readSysfs(filePath) > 0) {
           	   mGpuClock[i].setSummary(sysCommand.getLastResult(0) + " Mhz");
		   }
	   	}
	   i++;
	   }
	} else {
        PreferenceCategory category = (PreferenceCategory) getPreferenceScreen().findPreference("gpu_speed_category");
            getPreferenceScreen().removePreference(category);
	}

    }
	
    public boolean onPreferenceChange(Preference preference, Object objValue) {
	SysCommand sc = SysCommand.getInstance();
	int status, index, i = 0;
	for (String filePath : GPU_CLOCK_FILE_PATH) {
	    if (preference == mGpuClock[i] && Utils.fileExists(filePath)) {
            status = Integer.valueOf((String) objValue);
            index = mGpuClock[i].findIndexOfValue((String) objValue);
            //mGpuClock[i].setSummary(mGpuClock[i].getEntries()[index]);
	    sc.writeSysfs(filePath, ((String) objValue));
            setPreferenceString(filePath, ((String) objValue));
	   	if(sc.readSysfs(filePath) > 0) {
           	mGpuClock[i].setSummary(sc.getLastResult(0) + " Mhz");
		}
            return true;
            }
	i++;
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

    public static boolean isSupported() {
        boolean exists = true;
        for (String filePath : GPU_CLOCK_FILE_PATH) {
            if (!Utils.fileExists(filePath)) {
                exists = false;
            }
        }
	return exists;
   }

}
