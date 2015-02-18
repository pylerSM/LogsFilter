package com.pyler.logsfilter;

import java.util.HashSet;
import java.util.Set;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class Settings extends PreferenceActivity {
	public static final String LOGS_FILTER_ADD = "logs_filter_add";
	public static final String LOGS_FILTER_MANAGE = "logs_filter_manage";
	public static final String LOGS_FILTER_CLEAR = "logs_filter_clear";

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getPreferenceManager().setSharedPreferencesMode(MODE_WORLD_READABLE);
		addPreferencesFromResource(R.xml.settings);
		EditTextPreference addLogsFilter = (EditTextPreference) findPreference(LOGS_FILTER_ADD);
		Preference clearLogsFilter = findPreference(LOGS_FILTER_CLEAR);
		addLogsFilter
				.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						String text = (String) newValue;
						addLogsFilter(text);
						reloadLogsFilter();
						return false;
					}
				});
		clearLogsFilter
				.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference preference) {
						clearLogsFilter();
						reloadLogsFilter();
						return false;
					}
				});
		reloadLogsFilter();
	}

	@SuppressWarnings("deprecation")
	public void reloadLogsFilter() {
		MultiSelectListPreference logsFilter = (MultiSelectListPreference) findPreference("logs_filter_manage");
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		Set<String> items = prefs.getStringSet(LOGS_FILTER_MANAGE,
				new HashSet<String>());
		CharSequence[] logsFilterItems = items.toArray(new CharSequence[items
				.size()]);
		logsFilter.setEntries(logsFilterItems);
		logsFilter.setEntryValues(logsFilterItems);
	}

	public void addLogsFilter(String text) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		Set<String> logsFilterItems = prefs.getStringSet(LOGS_FILTER_MANAGE,
				new HashSet<String>());
		logsFilterItems.add(text);
		SharedPreferences.Editor prefsEditor = prefs.edit();
		prefsEditor.remove(LOGS_FILTER_MANAGE).apply();
		prefsEditor.putStringSet(LOGS_FILTER_MANAGE, logsFilterItems).apply();
	}

	public void clearLogsFilter() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		SharedPreferences.Editor prefsEditor = prefs.edit();
		prefsEditor.remove(LOGS_FILTER_MANAGE).apply();
	}
}