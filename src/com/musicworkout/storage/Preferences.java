package com.musicworkout.storage;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {
	
	public static final String KEY_PLAYLIST = "playlist";
	public static final String KEY_SHUFFLE_MODE = "shuffle_mode";
	public static final String KEY_VOLUME_MODE = "volume_mode";
	public static final String KEY_SONG = "song";
	
	public static final int VOLUME_MODE_QUIET_DURING_REST = 0;
	public static final int VOLUME_MODE_PAUSE_AT_REST = 1;
	public static final int VOLUME_MODE_BELL_AT_REST = 2;
	
	public static final int DEFAULT_SHUFFLE_MODE = 0;
	public static final int DEFAULT_VOLUME_MODE = VOLUME_MODE_PAUSE_AT_REST;
	public static final String DEFAULT_PLAYLIST = "Select";
	
	protected static Preferences instance;
	protected static Context context;
	protected SharedPreferences prefs;
	
	protected Preferences(Context context) {
		prefs = context.getSharedPreferences("com.musicworkout", Context.MODE_PRIVATE);
	}
	
	public static Preferences getInstance(Context context) {
		Preferences.context = context;
		if (instance == null)
			instance = new Preferences(context);
		return instance;
	}
	
	public int getInt(String key, int defaultValue) {
		return prefs.getInt(key, defaultValue);
	}
	
	public void putInt(String key, int value) {
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt(key, value);
		editor.commit();
	}
	
	public String getString(String key, String defaultValue) {
		return prefs.getString(key, defaultValue);
	}
	
	public void putString(String key, String value) {
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(key, value);
		editor.commit();
	}

}
