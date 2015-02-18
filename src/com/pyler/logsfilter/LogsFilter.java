package com.pyler.logsfilter;

import java.util.HashSet;
import java.util.Set;

import android.util.Log;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;

public class LogsFilter implements IXposedHookZygoteInit {
	public static final String LOGS_FILTER_MANAGE = "logs_filter_manage";
	public static final String LOGS_FILTER = "logs_filter";
	public static final String LOGS = "logs";
	public static final String EMPTY = "";
	public XSharedPreferences prefs;
	public XC_MethodHook logsHook;

	@Override
	public void initZygote(StartupParam startupParam) throws Throwable {
		prefs = new XSharedPreferences(LogsFilter.class.getPackage().getName());
		prefs.makeWorldReadable();

		logsHook = new XC_MethodHook() {
			@Override
			protected void beforeHookedMethod(MethodHookParam param)
					throws Throwable {
				prefs.reload();
				boolean logs = prefs.getBoolean(LOGS, true);
				boolean logsFilter = prefs.getBoolean(LOGS_FILTER, false);
				if (!(param.args[1] instanceof String)) {
					return;
				}
				if (!logs) {
					param.args[1] = EMPTY;
					return;
				}
				if (!logsFilter) {
					return;
				}
				String log = (String) param.args[1];
				param.args[1] = filterLog(log);

			}
		};

		XposedBridge.hookAllMethods(Log.class, "d", logsHook);
		XposedBridge.hookAllMethods(Log.class, "e", logsHook);
		XposedBridge.hookAllMethods(Log.class, "w", logsHook);
		XposedBridge.hookAllMethods(Log.class, "i", logsHook);
		XposedBridge.hookAllMethods(Log.class, "wtf", logsHook);
	}

	public String filterLog(String log) {
		String filteredLog = log;
		prefs.reload();
		Set<String> items = prefs.getStringSet(LOGS_FILTER_MANAGE,
				new HashSet<String>());
		if (items.isEmpty()) {
			return filteredLog;
		}
		for (String item : items) {
			if (filteredLog.contains(item)) {
				filteredLog = filteredLog.replaceAll(item, EMPTY);
			}
		}
		return filteredLog;
	}
}