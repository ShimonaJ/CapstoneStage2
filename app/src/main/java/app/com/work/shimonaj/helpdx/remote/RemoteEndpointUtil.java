package app.com.work.shimonaj.helpdx.remote;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.net.UrlQuerySanitizer;
import android.preference.PreferenceManager;
import android.util.Log;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.net.URL;

import app.com.work.shimonaj.helpdx.util.Utility;
import okio.BufferedSink;


public class RemoteEndpointUtil {
    private static final String TAG = "RemoteEndpointUtil";
    public static Context mContext;
    private RemoteEndpointUtil() {

    }

    public static JSONArray fetchTicketsArray(int empId) {
        String itemsJson = null;
        try {
            Uri builtUri = Uri.parse(Config.TICKET_URL.toString()+"UP_GetAllTicketByRequestorId").buildUpon()
                    .appendQueryParameter("Rid",String.valueOf(empId) )
                    .appendQueryParameter("Did","1" )
                    .appendQueryParameter("StartIndex","0" )
                    .appendQueryParameter("EndIndex","100" )
                    .appendQueryParameter("stageid","0" ).build();


            itemsJson = getResponse(builtUri);
            Log.v(TAG, itemsJson.toString(), null);
            if(itemsJson==""){
                return null;
            }

        } catch (IOException e) {
            Log.e(TAG, "Error fetching items JSON", e);
            return null;
        }

        // Parse JSON
        try {
            JSONTokener tokener = new JSONTokener(itemsJson);
            JSONObject val = (JSONObject) tokener.nextValue();

            JSONArray retVal = (JSONArray) val.get("DataList");

            if (!(retVal instanceof JSONArray)) {
                throw new JSONException("Expected JSONArray");
            }
            return (JSONArray) retVal;
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing items JSON", e);
        }

        return null;
    }
    public static JSONArray fetchTicketComments(long ticketId) {
        String itemsJson = null;
        try {
            Uri builtUri = Uri.parse(Config.TICKET_URL.toString()+"FindTicketComments").buildUpon()
                    .appendQueryParameter("ticketId",String.valueOf(ticketId) )
                    .build();
            itemsJson = getResponse(builtUri);
            Log.v(TAG, itemsJson.toString(), null);
            if(itemsJson==""){
                return null;
            }

        } catch (IOException e) {
            Log.e(TAG, "Error fetching items JSON", e);
            return null;
        }

        // Parse JSON
        try {
            JSONTokener tokener = new JSONTokener(itemsJson);
            JSONObject val = (JSONObject) tokener.nextValue();

            JSONArray retVal = (JSONArray) val.get("DataList");

            if (!(retVal instanceof JSONArray)) {
                throw new JSONException("Expected JSONArray");
            }
            return (JSONArray) retVal;
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing items JSON", e);
        }

        return null;
    }
    static String getResponseWithNoToken(Uri builtUri) throws IOException {
        OkHttpClient client = new OkHttpClient();




        URL finalU = new URL(builtUri.toString());
        Request request = new Request.Builder().url(finalU)
                .addHeader("Authorization", "")
                .addHeader("UserId", "")
                .addHeader("AppLoginFlag", "true")
                .build();
        // .header("User-Agent", "OkHttp Headers.java")
        //        .addHeader("Accept", "application/json; q=0.5")
        Response response = client.newCall(request).execute();
        return response.body().string();
    }
    static String getResponse(Uri builtUri) throws IOException {
        OkHttpClient client = new OkHttpClient();

        JSONObject userObj = Utility.getUserInfo(mContext);
        if(userObj!=null) {
            String AccessToken = "";
            String EmpId = "";

            try {
                AccessToken = userObj.getString("AccessToken");
                EmpId = userObj.getString("EmployeeId");
            } catch (JSONException e) {
                e.printStackTrace();
            }


            URL finalU = new URL(builtUri.toString());
            Request request = new Request.Builder().url(finalU)
                    .addHeader("Authorization", AccessToken)
                    .addHeader("UserId", EmpId)
                    .addHeader("AppLoginFlag", "")
                    .build();
            // .header("User-Agent", "OkHttp Headers.java")
            //        .addHeader("Accept", "application/json; q=0.5")
            Response response = client.newCall(request).execute();
            return response.body().string();
        }
        return "";
    }
    static String postResponse(Uri builtUri,String postJson) throws IOException {
        OkHttpClient client = new OkHttpClient();

        JSONObject userObj = Utility.getUserInfo(mContext);
        if(userObj!=null) {
            String AccessToken = "";
            String EmpId = "";

            try {
                AccessToken = userObj.getString("AccessToken");
                EmpId = userObj.getString("EmployeeId");
            } catch (JSONException e) {
                e.printStackTrace();
            }


            URL finalU = new URL(builtUri.toString());
            Request request = new Request.Builder().url(finalU)
                    .addHeader("Authorization", AccessToken)
                    .addHeader("UserId", EmpId)
                    .addHeader("AppLoginFlag", "")
                    .post(RequestBody
                            .create(MediaType
                                            .parse("application/json"),
                                    postJson.toString()
                            ))
                    .build();
            // .header("User-Agent", "OkHttp Headers.java")
            //        .addHeader("Accept", "application/json; q=0.5")
            Response response = client.newCall(request).execute();
            return response.body().string();
        }
        return "";
    }
    public static String postTicket(String ticketJson) throws IOException {
        OkHttpClient client = new OkHttpClient();


        Uri builtUri = Uri.parse(Config.TICKET_URL+"CreateTicket").buildUpon()
//                .appendQueryParameter("EmployeeId",String.valueOf(EmpId) )
//                .appendQueryParameter("ticket",ticketJson )
//                .appendQueryParameter("hostname","" )
//                .appendQueryParameter("tokenKey","" )
                .build();
return postResponse(builtUri,ticketJson);

//        URL finalU = new URL(builtUri.toString());
//
//
//
//
//        Request request = new Request.Builder()
//                .addHeader("Authorization", AccessToken)
//                .addHeader("UserId", EmpId)
//                .addHeader("AppLoginFlag", "")
//                .post(RequestBody
//                        .create(MediaType
//                                        .parse("application/json"),
//                                ticketJson.toString()
//                        ))
//                .url(finalU).build();
//
//        Response response = client.newCall(request).execute();
//            return response.body().string();




    }
    public static String postComment(String commentJson) throws IOException {
        OkHttpClient client = new OkHttpClient();


        Uri builtUri = Uri.parse(Config.TICKET_URL+"InsertTicketComment").buildUpon()
//                .appendQueryParameter("EmployeeId",String.valueOf(EmpId) )
//                .appendQueryParameter("ticket",ticketJson )
//                .appendQueryParameter("hostname","" )
//                .appendQueryParameter("tokenKey","" )
                .build();

        return postResponse(builtUri,commentJson);
//        URL finalU = new URL(builtUri.toString());
//
//        Request request = new Request.Builder()
//                .post(RequestBody
//                        .create(MediaType
//                                        .parse("application/json"),
//                                commentJson.toString()
//                        ))
//                .url(finalU).build();
//
//        Response response = client.newCall(request).execute();
//        return response.body().string();




    }
    public static JSONObject getTicketDetailById(long TicketId) {
        String itemsJson = null;
        try {
            Uri builtUri = Uri.parse(Config.TICKET_URL.toString()+"FindTicketDetailsById").buildUpon()
                    .appendQueryParameter("TicketId",String.valueOf(TicketId) )
                    .appendQueryParameter("tokenKey","" )
                 .build();


            itemsJson = getResponse(builtUri);
            Log.v(TAG, itemsJson.toString(), null);
        } catch (IOException e) {
            Log.e(TAG, "Error fetching ticket detail ID:"+TicketId, e);
            return null;
        }

        // Parse JSON
        try {
            JSONTokener tokener = new JSONTokener(itemsJson);
            JSONObject val = (JSONObject) tokener.nextValue();

            JSONObject retVal = (JSONObject) val.get("Data");

            if (!(retVal instanceof JSONObject)) {
                throw new JSONException("Expected JSONArray");
            }
            return (JSONObject) retVal;
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing items JSON", e);
        }

        return null;
    }
    public static String validateCompanyName(String companyName) {
        String itemsJson = null;
        try {
            Uri builtUri = Uri.parse(Config.COMMON_URL.toString()+"ValidateCompanyName").buildUpon()
                    .appendQueryParameter("companyName",companyName )
                    .build();


            itemsJson = getResponseWithNoToken(builtUri);
            Log.v(TAG, itemsJson.toString(), null);
        } catch (IOException e) {
            Log.e(TAG, "Error validating Company Name:"+companyName, e);
            return "";
        }

        // Parse JSON
        try {
            JSONTokener tokener = new JSONTokener(itemsJson);
            JSONObject val = (JSONObject) tokener.nextValue();
            String retVal = (String) val.get("Data");


            return retVal;
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing items JSON", e);
        }

        return "";
    }
    public static void onLogout(int EmpId) {


        try {
            Uri builtUri = Uri.parse(Config.COMMON_URL.toString()+"UpdateUserToken").buildUpon()
                    .appendQueryParameter("userid",String.valueOf(EmpId ))
                    .build();

            getResponse(builtUri);

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
if(prefs!=null) {
    prefs.edit().clear().commit();
}
        } catch (IOException e) {
            Log.e(TAG, "Error onLogout", e);
            return ;
        }


        return ;
    }
    public static JSONObject validateUser(String helpdesk_name,String email,String password) {
        String itemsJson = null;
        try {
            Uri builtUri = Uri.parse(Config.COMMON_URL.toString()+"ValidateAppUser").buildUpon()
                    .appendQueryParameter("emailId",email )
                    .appendQueryParameter("Password",password )
                    .appendQueryParameter("helpdeskname",helpdesk_name )
                    .build();


            itemsJson = getResponseWithNoToken(builtUri);
            Log.v(TAG, itemsJson.toString(), null);
        } catch (IOException e) {
            Log.e(TAG, "Error validating User: "+email+" "+password+" "+helpdesk_name, e);
            return null;
        }

        // Parse JSON
        try {
            JSONTokener tokener = new JSONTokener(itemsJson);
            JSONObject val = (JSONObject) tokener.nextValue();
            JSONObject dataItem =null;
            if(val.getInt("Result")==1){
                JSONArray retVal = (JSONArray) val.get("DataList");
                if(retVal.length()==1){
                    dataItem = retVal.getJSONObject(0);
                }
            }

            return dataItem;
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing items JSON", e);
        }

        return null;
    }
}
