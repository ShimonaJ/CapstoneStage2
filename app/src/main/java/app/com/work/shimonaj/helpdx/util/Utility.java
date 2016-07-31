package app.com.work.shimonaj.helpdx.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import app.com.work.shimonaj.helpdx.MainActivity;
import app.com.work.shimonaj.helpdx.remote.Config;

/**
 * Created by shimonaj on 5/12/2016.
 */
public class Utility {
    public static void putKeyValInSharedPref(Context mContext,String key,String value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
    }
    public static String getValFromSharedPref(Context mContext,String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
      return prefs.getString(key,"");

    }
    public static JSONObject getUserInfo(Context mContext) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        String userInfo = prefs.getString(Config.USER_KEY, "");
        JSONObject userObj = null;
if(userInfo!="") {

    try {
        userObj = new JSONObject(userInfo);

    } catch (JSONException e) {
        e.printStackTrace();
    }
}
        return userObj;
    }
    public static int getUserEmpId(Context mContext) {
        JSONObject object =getUserInfo(mContext);
        int EmpId =0;
        if(object!=null){
            try {
                EmpId = object.getInt("EmployeeId");
            }catch (JSONException e){
                e.printStackTrace();
            }

        }
        return EmpId;
    }
    public static  Typeface regularRobotoFont;
    public static  Typeface mediumRobotoFont;
    public static void initAllFonts(Context mContext){
        regularRobotoFont =  Typeface.createFromAsset(mContext.getResources().getAssets(), "Roboto-Regular.ttf");
        mediumRobotoFont =  Typeface.createFromAsset(mContext.getResources().getAssets(), "Roboto-Medium.ttf");
    }
}
