package vell.bibi.vsigner.config;

import android.content.Context;
import android.content.SharedPreferences;

public class Conf {
	public static final String mSharedPreferencesKey = "setting";
	public static final String mIsNotifyVibrateKey = "isNotifyVibrate";
	public static final String mIsNotifyKey = "isNotify";
	public static final String mIsNotifySoundKey = "isNotifySound";
	
	public static boolean isNotifyVibrate = true;
	public static boolean isNotify = true;
	public static boolean isNotifySound = true;
	
	public static void load(Context context) {
		SharedPreferences settings = context.getSharedPreferences(mSharedPreferencesKey, Context.MODE_PRIVATE);
		isNotifyVibrate = settings.getBoolean(mIsNotifyVibrateKey, true);
		isNotify = settings.getBoolean(mIsNotifyKey, true);
		isNotifySound = settings.getBoolean(mIsNotifySoundKey, true);
	}
	
	public static void save(Context context) {
		SharedPreferences.Editor editor = context.getSharedPreferences(mSharedPreferencesKey, Context.MODE_PRIVATE).edit();
		editor.putBoolean(mIsNotifyKey, isNotify);
		editor.putBoolean(mIsNotifyVibrateKey, isNotifyVibrate);
		editor.putBoolean(mIsNotifySoundKey, isNotifySound);
		editor.commit();
	}
}
