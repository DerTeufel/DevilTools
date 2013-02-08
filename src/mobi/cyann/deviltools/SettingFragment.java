/**
 * SettingFragment.java
 * Nov 5, 2011 3:12:07 PM
 */
package mobi.cyann.deviltools;

import mobi.cyann.deviltools.PreferenceListFragment.OnPreferenceAttachedListener;
import mobi.cyann.deviltools.services.ObserverService;
import android.content.Intent;
import android.net.Uri;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceScreen;
import android.widget.Toast;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.os.Bundle;

/**
 * @author arif
 *
 */
public class SettingFragment extends PreferenceListFragment implements OnPreferenceAttachedListener, OnPreferenceClickListener, OnPreferenceChangeListener {
	public SettingFragment() {
		super(R.layout.setting);
		setOnPreferenceAttachedListener(this);
	}

 	@Override
    	public void onCreate(Bundle savedInstanceState) {
        	super.onCreate(savedInstanceState);
     
        	MemoryInfo mi = new MemoryInfo();
        	ActivityManager activityManager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        	activityManager.getMemoryInfo(mi);
        	long totalMegs = mi.totalMem / 1048576L;

        	Preference pref = findPreference("system_memory");
        	pref.setSummary(String.valueOf(totalMegs) + " MB");
        
    	}

	@Override
	public void onPreferenceAttached(PreferenceScreen rootPreference, int xmlId) {
		findPreference(getString(R.string.key_deviltools_service)).setOnPreferenceChangeListener(this);
		findPreference(getString(R.string.key_load_settings)).setOnPreferenceChangeListener(this);
		findPreference(getString(R.string.key_save_settings)).setOnPreferenceClickListener(this);
		findPreference(getString(R.string.key_save_settings)).setOnPreferenceChangeListener(this);
		findPreference(getString(R.string.key_delete_settings)).setOnPreferenceChangeListener(this);

	
		Preference about = findPreference(getString(R.string.key_about));
		about.setOnPreferenceClickListener(this);
		about.setTitle(getString(R.string.app_name)+ " " + getString(R.string.app_version));

		Preference donation = findPreference(getString(R.string.key_donation));
		donation.setOnPreferenceClickListener(this);
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		if(preference.getKey().equals(getString(R.string.key_about))) {
			Intent browse = new Intent();
			browse.setAction(Intent.ACTION_VIEW);
			browse.setData(Uri.parse(getString(R.string.deviltools_thread_url)));
			startActivity(browse);
		}else if(preference.getKey().equals(getString(R.string.key_donation))) {
			Intent browse = new Intent();
			browse.setAction(Intent.ACTION_VIEW);
			browse.setData(Uri.parse(getString(R.string.deviltools_donation_url)));
			startActivity(browse);
		}else if(preference.getKey().equals(getString(R.string.key_save_settings))) {
			((EditTextPreference)preference).getEditText().setText("");
		}
		return false;
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		if(preference.getKey().equals(getString(R.string.key_load_settings))) {
			if(newValue != null && newValue.toString().length() > 0) {
				SettingsManager.loadSettings(getActivity(), newValue.toString());
				MainActivity.restart(getActivity());
			}
			return true;
		}else if(preference.getKey().equals(getString(R.string.key_save_settings))) {
			if(newValue != null && newValue.toString().length() > 0) {
				if(SettingsManager.saveSettings(getActivity(), newValue.toString())) {
					Toast.makeText(getActivity(), getString(R.string.save_success_message), Toast.LENGTH_LONG).show();
				}else {
					Toast.makeText(getActivity(), getString(R.string.save_failed_message), Toast.LENGTH_LONG).show();
				}
			}
			return true;
		}else if(preference.getKey().equals(getString(R.string.key_delete_settings))) {
			if(newValue != null && newValue.toString().length() > 0) {
				SettingsManager.deleteSettings(getActivity(), newValue.toString());
			}
			return true;
		}else if(preference.getKey().equals(getString(R.string.key_deviltools_service))) {
			if(newValue != null) {
				if((Boolean)newValue) {
					ObserverService.startService(getActivity(), true);
				}else {
					ObserverService.stopService(getActivity());
				}
			}
			return true;
		}
		return false;
	}
}
