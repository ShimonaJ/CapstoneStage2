package app.com.work.shimonaj.helpdx.data;

import android.app.IntentService;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.annotation.IntDef;
import android.support.v4.content.LocalBroadcastManager;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;

import app.com.work.shimonaj.helpdx.MainActivity;
import app.com.work.shimonaj.helpdx.remote.Config;
import app.com.work.shimonaj.helpdx.remote.RemoteEndpointUtil;
import app.com.work.shimonaj.helpdx.util.Utility;

import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

public class UpdaterService extends IntentService {
    private static final String TAG = UpdaterService.class.getName();
    public static final String ACTION_DATA_UPDATED =
            "helpdesk.ACTION_DATA_UPDATED";
    public static final String BROADCAST_ACTION_STATE_CHANGE
            = "com.example.helpdx.intent.action.STATE_CHANGE";
    public static final String EXTRA_REFRESHING
            = "com.example.helpdx.intent.extra.REFRESHING";
    public static final String ERROR
            = "com.example.helpdx.intent.extra.ERROR";
    public static final String TICKET_COMMENT_POST
            = "com.example.helpdx.intent.extra.CREATECOMMENT";
    public static final String TICKET_POST
            = "com.example.helpdx.intent.extra.CREATETICKET";
    public static final String TICKET_DETAIL
            = "com.example.helpdx.intent.extra.GETTICKETDETAIL";
    public static final String TICKET_COMMENTS
            = "com.example.helpdx.intent.extra.GETTICKETCOMMENTS";
    public static final String VALIDATE_COMPANY
            = "com.example.helpdx.intent.extra.VALIDATECOMPANY";
    public static final String VALIDATE_USER
            = "com.example.helpdx.intent.extra.VALIDATEUSER";
    public static final String LOGOUT
            = "com.example.helpdx.intent.extra.LOGOUT";
    public static final int TICKET_STATUS_OK = 0;
    public static final int TICKET_STATUS_SERVER_DOWN = 1;
    public static final int TICKET_STATUS_SERVER_INVALID = 2;
    public static final int TICKET_STATUS_UNKNOWN = 3;
    public static final int TICKET_STATUS_INVALID = 4;
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({TICKET_STATUS_OK,TICKET_STATUS_SERVER_DOWN,TICKET_STATUS_SERVER_INVALID,TICKET_STATUS_UNKNOWN,TICKET_STATUS_INVALID})
    public @interface TicketStatus{}


    public UpdaterService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Time time = new Time();
        RemoteEndpointUtil.mContext = getApplicationContext();
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null || !ni.isConnected()) {
            Log.w(TAG, "Not online, not refreshing.");
        //    setBookStatus(this,TICKET_STATUS_SERVER_DOWN);
            broadcastMessage("No Network found ");
          //  Toast.makeText(getApplicationContext(),"Not online, not refreshing.",Toast.LENGTH_LONG).show();
            return;
        }
        if (intent.getAction() == TICKET_POST) {
            Log.v(TAG, "************** Posting data.", null);
            String str = intent.getStringExtra("ticketData");
            postTicket(str);
        }else if(intent.getAction() == TICKET_DETAIL) {
            Log.v(TAG, "************** Getting ticket detail & Fetching Comments.. :"+intent.getLongExtra("ticketId",0), null);
            long str = intent.getLongExtra("ticketId",0);
            getTicketDetail(str);
           // getTicketComments(str);
        }
        else if(intent.getAction() == VALIDATE_COMPANY) {
            Log.v(TAG, "************** Validate Company. :"+intent.getStringExtra("companyName"), null);

            validateCompanyName(intent.getStringExtra("companyName"));
        }
        else if(intent.getAction() == VALIDATE_USER) {

            String email = intent.getStringExtra("email");
            String helpdesk_name = intent.getStringExtra("helpdeskname");
            String pwd = intent.getStringExtra("password");
            Log.v(TAG, "************** Validate User. :"+email+","+pwd+","+helpdesk_name, null);
            validateUser(helpdesk_name,email,pwd);
        }
        else if(intent.getAction() == LOGOUT) {

            Log.v(TAG, "************** LOGOUT. :", null);
            doLogout();
        }
        else if(intent.getAction() == BROADCAST_ACTION_STATE_CHANGE) {
            Log.v(TAG, "************** Fetching data.", null);
           fetchTickets();

        }
        else if(intent.getAction() == TICKET_COMMENT_POST) {
            Log.v(TAG, "************** Posting comment data.", null);
            String str = intent.getStringExtra("commentData");
            postComment(str);

        }
//        else if(intent.getAction() == TICKET_COMMENTS) {
//            Log.v(TAG, "************** Fetching Comments.", null);
//            long str =(long)intent.getLongExtra("ticketId",0);
//
// getTicketComments(str);
//        }
    }
    private void broadcastMessage(String message){
        Intent messageIntent = new Intent(MainActivity.MESSAGE_EVENT);
        messageIntent.putExtra(MainActivity.MESSAGE_KEY,message);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(messageIntent);
    }

    private void postTicket(String str){

        try{
            //sendStickyBroadcast(new Intent(TICKET_POST).putExtra(EXTRA_REFRESHING, true));
         RemoteEndpointUtil.postTicket(str);
            //sendStickyBroadcast(new Intent(TICKET_POST).putExtra(EXTRA_REFRESHING, false));
    } catch (IOException  e) {
            broadcastMessage("Network issue, ticket might not have been created.");
         //   setBookStatus(this,TICKET_STATUS_SERVER_DOWN);
        Log.e(TAG, "Error posting ticket.", e);
    }
        broadcastMessage("Ticket Posted Successfully, This will refresh in a while");
      //  setBookStatus(this,TICKET_STATUS_OK);
    }
    private void postComment(String str){

        try{
         //   sendStickyBroadcast(
         //           new Intent(TICKET_COMMENT_POST).putExtra(EXTRA_REFRESHING, true));
            RemoteEndpointUtil.postComment(str);
          //  sendStickyBroadcast(
          //          new Intent(TICKET_COMMENT_POST).putExtra(EXTRA_REFRESHING, false));
        } catch (IOException  e) {
            broadcastMessage("Network issue, reply might not have been posted.");
            Log.e(TAG, "Error posting comment.", e);
         //   setBookStatus(this,TICKET_STATUS_SERVER_DOWN);
        }
        broadcastMessage("Reply Posted Successfully, This will refresh in a while");
       // setBookStatus(this,TICKET_STATUS_OK);
    }
    private void doLogout(){

        int  EmpId =   Utility.getUserEmpId(getApplicationContext());
//            sendStickyBroadcast(
//                    new Intent(LOGOUT).putExtra(EXTRA_REFRESHING, true));
            RemoteEndpointUtil.onLogout(EmpId);
//            sendStickyBroadcast(
//                    new Intent(LOGOUT).putExtra(EXTRA_REFRESHING, false));


    }
    private void fetchTickets(){
     //   sendStickyBroadcast(
     //           new Intent(BROADCAST_ACTION_STATE_CHANGE).putExtra(EXTRA_REFRESHING, true));

        // Don't even inspect the intent, we only do one thing, and that's fetch content.
        ArrayList<ContentProviderOperation> cpo = new ArrayList<ContentProviderOperation>();

        Uri dirUri = ItemsContract.Items.buildDirUri();


        try {




            int EmpId = Utility.getUserEmpId(getApplicationContext());
            JSONArray array = RemoteEndpointUtil.fetchTicketsArray(EmpId);

            if (array == null) {


                throw new JSONException("Invalid parsed item array");
            }
            // Delete all items
            cpo.add(ContentProviderOperation.newDelete(dirUri).build());

            for (int i = 0; i < array.length(); i++) {
                ContentValues values = new ContentValues();
                JSONObject object = array.getJSONObject(i);
                values.put(ItemsContract.Items.TICKETID, object.getString("TicketId"));
                values.put(ItemsContract.Items.DESCRIPTION, object.getString("Description"));
                values.put(ItemsContract.Items.TITLE, object.getString("Title"));
                values.put(ItemsContract.Items.STAGENAME, object.getString("StageName"));
                values.put(ItemsContract.Items.CATEGORYNAME, object.getString("CategoryName"));
                values.put(ItemsContract.Items.ASSIGNEDTO, object.getString("AssignToEmpName"));
                values.put(ItemsContract.Items.REQUESTEDBY, object.getString("RequestedByEmpName"));
                values.put(ItemsContract.Items.SOURCE, object.getString("TicketSource"));

                //  time.parse3339(object.getString("CreatedOn"));
                values.put(ItemsContract.Items.CREATEDON, object.getString("CreatedOn"));
                cpo.add(ContentProviderOperation.newInsert(dirUri).withValues(values).build());
            }

            getContentResolver().applyBatch(ItemsContract.CONTENT_AUTHORITY, cpo);

        } catch (JSONException | RemoteException | OperationApplicationException e) {
           // setBookStatus(this, TICKET_STATUS_SERVER_INVALID);
            broadcastMessage("No Network, latest tickets not fetched.");
            Log.e(TAG, "Error updating content.", e);
        }
        broadcastMessage("Latest tickets fetched.");
        updateWidgets();
     //   setBookStatus(this,TICKET_STATUS_OK);
     //   sendStickyBroadcast(
       //         new Intent(BROADCAST_ACTION_STATE_CHANGE).putExtra(EXTRA_REFRESHING, false));
    }
    private void updateWidgets() {
        Context context = getApplicationContext();
        // Setting the package ensures that only components in our app will receive the broadcast
        Intent dataUpdatedIntent = new Intent(ACTION_DATA_UPDATED)
                .setPackage(context.getPackageName());
        context.sendBroadcast(dataUpdatedIntent);
    }
    private void validateCompanyName(String companyName) {
        sendStickyBroadcast(
                new Intent(VALIDATE_COMPANY).putExtra(EXTRA_REFRESHING, true));
        String dataObj = RemoteEndpointUtil.validateCompanyName(companyName);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Config.HELPDX_NAME, dataObj);
        editor.putString(Config.COMPANY_NAME, companyName);
        editor.commit();
        sendStickyBroadcast(
                new Intent(VALIDATE_COMPANY).putExtra(EXTRA_REFRESHING, false));
    }
    private void validateUser(String helpdesk_name,String email,String password) {
        sendStickyBroadcast(
                new Intent(VALIDATE_USER).putExtra(EXTRA_REFRESHING, false));
        JSONObject dataObj = RemoteEndpointUtil.validateUser(helpdesk_name,email,password);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Config.USER_KEY, dataObj==null?"":dataObj.toString());
        editor.commit();
        sendStickyBroadcast(
                new Intent(VALIDATE_USER).putExtra(EXTRA_REFRESHING, false));
    }
    private void getTicketDetail(long ticketId){
        //sendStickyBroadcast(
          //      new Intent(TICKET_DETAIL).putExtra(EXTRA_REFRESHING, true));

        // Don't even inspect the intent, we only do one thing, and that's fetch content.
        ArrayList<ContentProviderOperation> cpo = new ArrayList<ContentProviderOperation>();

        Uri dirUri = ItemsContract.Items.buildItemUri(ticketId);


        try {

            JSONObject dataObj = RemoteEndpointUtil.getTicketDetailById(ticketId);

            if (dataObj == null) {
                throw new JSONException("Invalid parsed item array");
            }
            // Delete all items
            //  cpo.add(ContentProviderOperation.newDelete(dirUri).build());
            //   cpo.add(ContentProviderOperation.newDelete(dirUri).build());
            //getContentResolver().delete(dirUri);

            // for (int i = 0; i < array.length(); i++) {
            ContentValues values = new ContentValues();

            values.put(ItemsContract.Items.TICKETID, dataObj.getString("TicketId"));

            values.put(ItemsContract.Items.TITLE, dataObj.getString("Title"));

            values.put(ItemsContract.Items.DESCRIPTION, dataObj.getString("Description"));
            values.put(ItemsContract.Items.STAGENAME, dataObj.getString("StageName"));
            values.put(ItemsContract.Items.CATEGORYNAME, dataObj.getString("CategoryName"));
            values.put(ItemsContract.Items.ASSIGNEDTO, dataObj.getString("AssignToEmpName"));
            values.put(ItemsContract.Items.REQUESTEDBY, dataObj.getString("RequestedByEmpName"));
            values.put(ItemsContract.Items.SOURCE, dataObj.getString("TicketSource"));

            //  time.parse3339(object.getString("CreatedOn"));
            values.put(ItemsContract.Items.CREATEDON, dataObj.getString("CreatedOn"));
            //  time.parse3339(object.getString("CreatedOn"));

            //  cpo.add(ContentProviderOperation.newInsert(dirUri).withValues(values).build());
            // }


            getContentResolver().update(dirUri, values,"",null);

        } catch (JSONException e) {
            Log.e(TAG, "Error updating content.", e);
            broadcastMessage("Error Occured while decoding server resposne.");

           // setBookStatus(this,TICKET_STATUS_SERVER_INVALID);
        }

        cpo = new ArrayList<ContentProviderOperation>();
        dirUri = ItemsContract.Comments.buildCommentUri(ticketId);

        try {
            JSONArray array = RemoteEndpointUtil.fetchTicketComments(ticketId);

            if (array == null) {
                throw new JSONException("Invalid parsed item array");
            }
            // Delete all items
            cpo.add(ContentProviderOperation.newDelete(dirUri).build());

            for (int i = 0; i < array.length(); i++) {
                ContentValues values = new ContentValues();
                JSONObject object = array.getJSONObject(i);
                if(object.getInt("IsPrivate")==0){
                    values.put(ItemsContract.Comments.TICKETID,ticketId);
                    values.put(ItemsContract.Comments.COMMENT, object.getString("Comments"));
                    values.put(ItemsContract.Comments.COMMENTEDBY, object.getString("CreatedByEmpName"));
                    values.put(ItemsContract.Comments.COMMENTEDON, object.getString("CreatedOn"));

                    cpo.add(ContentProviderOperation.newInsert(dirUri).withValues(values).build());
                }

            }
            getContentResolver().applyBatch(ItemsContract.CONTENT_AUTHORITY, cpo);

        } catch (JSONException | RemoteException | OperationApplicationException e) {
            Log.e(TAG, "Error updating content.", e);
           // setBookStatus(this,TICKET_STATUS_SERVER_INVALID);
            broadcastMessage("No Network, latest ticket details not fetched.");

        }
        //setBookStatus(this,TICKET_STATUS_OK);
        broadcastMessage("Latest ticket details fetched.");


     //   sendStickyBroadcast(
       //         new Intent(TICKET_DETAIL).putExtra(EXTRA_REFRESHING, false));
    }
//    private void getTicketComments(long ticketId){
//        sendStickyBroadcast(
//                new Intent(TICKET_COMMENTS).putExtra(EXTRA_REFRESHING, true));
//
//        // Don't even inspect the intent, we only do one thing, and that's fetch content.
//        ArrayList<ContentProviderOperation> cpo = new ArrayList<ContentProviderOperation>();
//        Uri dirUri = ItemsContract.Comments.buildCommentUri(ticketId);
//
//        try {
//         JSONArray array = RemoteEndpointUtil.fetchTicketComments(ticketId);
//
//            if (array == null) {
//                throw new JSONException("Invalid parsed item array");
//            }
//            // Delete all items
//
//                cpo.add(ContentProviderOperation.newDelete(dirUri).build());
//
//                for (int i = 0; i < array.length(); i++) {
//                    ContentValues values = new ContentValues();
//                    JSONObject object = array.getJSONObject(i);
//                    if (object.getInt("IsPrivate") == 0) {
//                        values.put(ItemsContract.Comments.TICKETID, ticketId);
//                        values.put(ItemsContract.Comments.COMMENT, object.getString("Comments"));
//                        values.put(ItemsContract.Comments.COMMENTEDBY, object.getString("CreatedByEmpName"));
//                        values.put(ItemsContract.Comments.COMMENTEDON, object.getString("CreatedOn"));
//
//                        cpo.add(ContentProviderOperation.newInsert(dirUri).withValues(values).build());
//                    }
//
//                }
//                getContentResolver().applyBatch(ItemsContract.CONTENT_AUTHORITY, cpo);
//
//
//        } catch (JSONException | RemoteException | OperationApplicationException e) {
//            Log.e(TAG, "Error updating content.", e);
//        }
//
//        sendStickyBroadcast(
//                new Intent(TICKET_COMMENTS).putExtra(EXTRA_REFRESHING, false));
//    }
}
