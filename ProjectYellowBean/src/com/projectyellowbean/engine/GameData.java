package com.projectyellowbean.engine;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class GameData {
	private Context context;
	public static final String KEY_USER_SCORE = "user_score";
	private static final float START_SCORE = 10000;
	
	public GameData(Context context) {
		this.context = context;
	}
	
	public double getUserScore() {
		if(context != null) {
			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
			return sp.getFloat(KEY_USER_SCORE, START_SCORE);
		}
		return 0f;
	}
	
	public boolean setUserScore(float userScore) {
		if(context != null) {
			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
			SharedPreferences.Editor edit = sp.edit();
			
			edit.putFloat(KEY_USER_SCORE, userScore);
			return edit.commit();
		}
		return false;
	}
	
	

}
