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
public class PerformanceFragment extends BasePreferenceFragment implements OnPreferenceChangeListener {
	public PerformanceFragment() {
		super(R.layout.performance);
	}
    private ContentResolver mContentResolver;
    private static SharedPreferences preferences;

    private static final String GPU_OC = "gpu_oc";

    private PreferenceScreen mGpuOc;
    private ListPreference mGpuClock[] = new ListPreference[5];

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

    public static final String[] GPU_THRESHOLD_FILE_PATH = new String[] {
	"/sys/module/mali/parameters/step0_up",
	"/sys/module/mali/parameters/step1_up",
	"/sys/module/mali/parameters/step2_up",
	"/sys/module/mali/parameters/step3_up",
	"/sys/module/mali/parameters/step1_down",
	"/sys/module/mali/parameters/step2_down",
	"/sys/module/mali/parameters/step3_down",
	"/sys/module/mali/parameters/step4_down",
	"/sys/module/mali/parameters/mali_gpu_utilization_timeout",
	};


	@Override
    	public void onCreate(Bundle savedInstanceState) {
        	super.onCreate(savedInstanceState);

	preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        PreferenceScreen prefSet = getPreferenceScreen();
        mContentResolver = getActivity().getApplicationContext().getContentResolver();
	SysCommand sysCommand = SysCommand.getInstance();
	mGpuOc = (PreferenceScreen) prefSet.findPreference(GPU_OC);

	if(!IsSupported()) {
	mGpuOc.setEnabled(false);
	} else {
	mGpuOc.setEnabled(true);
	}
	if (ocIsSupported()) {
	int i = 0;
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

    public static boolean ocIsSupported() {
        boolean exists = true;
        for (String filePath : GPU_CLOCK_FILE_PATH) {
            if (!Utils.fileExists(filePath)) {
                exists = false;
            }
        }
	return exists;
   }

    public static boolean thresholdIsSupported() {
        boolean exists = false;
        for (String filePath : GPU_THRESHOLD_FILE_PATH) {
            if (Utils.fileExists(filePath)) {
                exists = true;
            }
        }
	return exists;
   }

    public static boolean IsSupported() {
	    boolean ocsupported = ocIsSupported();
	    boolean thresholdsupported = thresholdIsSupported();
	    return (ocsupported && thresholdsupported);
    }

}
