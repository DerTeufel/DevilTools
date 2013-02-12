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
import android.app.AlertDialog;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.ListPreference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

/**
 * @author arif
 *
 */
public class DevilTweaksFragment extends PreferenceListFragment implements OnPreferenceChangeListener {
	public DevilTweaksFragment() {
		super(R.layout.devil_tweak);
		//setOnPreferenceAttachedListener(this);
	}
	
	private ListPreference mBigmem;

        private ContentResolver mContentResolver;
	
	@Override
    	public void onCreate(Bundle savedInstanceState) {
        	super.onCreate(savedInstanceState);

        PreferenceScreen prefSet = getPreferenceScreen();
        mContentResolver = getActivity().getApplicationContext().getContentResolver();

        mBigmem = (ListPreference) findPreference("key_bigmem");
        mBigmem.setOnPreferenceChangeListener(this);

        MemoryInfo mi = new MemoryInfo();
        ActivityManager activityManager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);
        long totalMegs = mi.totalMem / 1048576L;

        mBigmem.setSummary(String.valueOf("Current Ram: " + totalMegs) + " MB");

	}
	
	/*@Override
	public void onPreferenceAttached(PreferenceScreen rootPreference, int xmlId) {
        mBigmem = (ListPreference) findPreference("key_bigmem");
        mBigmem.setOnPreferenceChangeListener(this);
	}*/

    public boolean onPreferenceChange(Preference preference, Object objValue) {
	SysCommand sc = SysCommand.getInstance();
	if (preference == mBigmem) {
            int status = Integer.valueOf((String) objValue);
            int index = mBigmem.findIndexOfValue((String) objValue);
    	    //private static final String bigmem_string = ((String) objValue);
            mBigmem.setSummary("After Reboot: " + mBigmem.getEntries()[index]);
	    SysCommand.getInstance().writeSysfs("/sys/kernel/bigmem/enable", ((String) objValue));
            return true;
        }

        return false;
    }

	/*@Override
	protected void onBindView(View view) {
		super.onBindView(view);
		// Sync the summary view
        TextView summaryView = (TextView) view.findViewById(android.R.id.summary);
        if (summaryView != null) {
        	if(value == null) {
        		summaryView.setText(R.string.status_not_available);
        	}else if(listValues != null && listValues.length > 0) {
        		int idx = selectedIndex();
        		if(idx >= 0) {
        			summaryView.setText(listLabels[idx]);
        		}else {
        			// currently don't know what to do :D
        			summaryView.setText("?");
        		}
        	}
        }
	}*/
}
