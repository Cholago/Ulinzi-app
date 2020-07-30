package com.cholago.ulinziapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class SessionManager {
	// LogCat tag
	private static String TAG = SessionManager.class.getSimpleName();

	// Shared Preferences
	SharedPreferences pref;

	Editor editor;
	Context _context;

	// Shared pref mode
	int PRIVATE_MODE = 0;

	// Shared preferences file name
	private static final String PREF_NAME = "ulinzi_app";
	
	private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
	private static final String NAME = "name";
	private static final String PHONE_NUMBER = "phone";
	private static final String ID_NUMBER = "id_number";


	public SessionManager(Context context) {
		this._context = context;
		pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
		editor = pref.edit();
	}

	public void setLogin(boolean isLoggedIn) {

		editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);

		// commit changes
		editor.commit();

		Log.d(TAG, "User login session modified!");
	}


	//adding device name
	public void setUser(String name, String phone, String id_number){

		editor.putString(NAME, name); // Storing string
		editor.putString(PHONE_NUMBER, phone); // Storing string
		editor.putString(ID_NUMBER, id_number); // Storing string
		//apply changes
		editor.commit();
		Log.d(TAG, "User successfully stored");

	}

	public boolean isLoggedIn(){
		//get data from sherd pref
		return pref.getBoolean(KEY_IS_LOGGED_IN, false);
	}

	public String getName(){

		return pref.getString(NAME, null); // getting device name
	}

	public String getPhoneNumber(){

		return pref.getString(PHONE_NUMBER, null); // getting device name
	}
	public String getIdNumber(){

		return pref.getString(ID_NUMBER, null); // getting device name
	}

}




