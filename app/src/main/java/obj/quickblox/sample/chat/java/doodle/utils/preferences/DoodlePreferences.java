package com.doodle.utils.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class DoodlePreferences {
	private SharedPreferences		mPreferences;
	private SharedPreferences     mPrefrencesTuts;
	private static DoodlePreferences droolyPrefrence;
	private static DoodlePreferences droolTuts;
	private Editor					mEditor;
	private Editor					mEditorTut;
	private Context					mContext;
	private String					DOODLE_PREFS						= "doodle_pref";
	private static String  PREFS_DOODLE_TEXT=							"doodle_text";
	private static String  PREFS_DOODLE_DRAW_COLOR=							"doodle_draw_color";
	private static String  PREFS_DOODLE_DRAW_COLOR_INT=							"doodle_draw_color_int";
	private DoodlePreferences(Context context) {
		mContext = context;
		mPreferences = context.getSharedPreferences(DOODLE_PREFS, Context.MODE_PRIVATE);
		mEditor = mPreferences.edit();
	}

	public static DoodlePreferences getInstance(Context context) {
		if (droolyPrefrence == null) {
			droolyPrefrence = new DoodlePreferences(context);
		}
		return droolyPrefrence;
	}
	public SharedPreferences getPrefrences() {
		return mPreferences;
	}


	public String getdoodleText() {
		return mPreferences.getString(PREFS_DOODLE_TEXT, "");
	}

	public void setdoodleText(String doodleText) {
		mEditor.putString(PREFS_DOODLE_TEXT, doodleText).apply();
	}






	public String getDoodleDrawColor() {
		return mPreferences.getString(PREFS_DOODLE_DRAW_COLOR, "");
	}

	public void setDoodleDrawColor(String doodleText) {
		mEditor.putString(PREFS_DOODLE_DRAW_COLOR, doodleText).apply();
	}




	public Integer getDoodleDrawColorInt() {
		return mPreferences.getInt(PREFS_DOODLE_DRAW_COLOR_INT, 0);
	}

	public void setDoodleDrawColorInt(Integer doodleText) {
		mEditor.putInt(PREFS_DOODLE_DRAW_COLOR_INT, doodleText).apply();
	}
}
