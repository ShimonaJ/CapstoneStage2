package app.com.work.shimonaj.helpdx.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.annotation.IntDef;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

import app.com.work.shimonaj.helpdx.R;
import app.com.work.shimonaj.helpdx.data.ItemsContract;
import app.com.work.shimonaj.helpdx.remote.RemoteEndpointUtil;
import app.com.work.shimonaj.helpdx.util.Utility;



public class TicketSyncAdapter extends AbstractThreadedSyncAdapter {
    public final String LOG_TAG = TicketSyncAdapter.class.getSimpleName();
    public static final String ACTION_DATA_UPDATED =
            "helpdesk.ACTION_DATA_UPDATED";
    // Interval at which to sync with the weather, in milliseconds.
// 60 seconds (1 minute) * 180 = 3 hours

    public static final int SYNC_INTERVAL = 60*180 ;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;
    private static final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;
    public static final int TICKET_STATUS_OK = 0;
    public static final int TICKET_STATUS_SERVER_DOWN = 1;
    public static final int TICKET_STATUS_SERVER_INVALID = 2;
    public static final int TICKET_STATUS_UNKNOWN = 3;
    public static final int TICKET_STATUS_INVALID = 4;
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({TICKET_STATUS_OK,TICKET_STATUS_SERVER_DOWN,TICKET_STATUS_SERVER_INVALID,TICKET_STATUS_UNKNOWN,TICKET_STATUS_INVALID})
    public @interface TicketStatus{}
    private static final int TICKET_NOTIFICATION_ID = 3004;
    public TicketSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }
    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }
    private void updateWidgets() {
        Context context = getContext();
        // Setting the package ensures that only components in our app will receive the broadcast
        Intent dataUpdatedIntent = new Intent(ACTION_DATA_UPDATED)
                .setPackage(context.getPackageName());
        context.sendBroadcast(dataUpdatedIntent);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG, "onPerformSync Called.");

fetchTickets();

    }
    private void fetchTickets(){
        //   sendStickyBroadcast(
        //           new Intent(BROADCAST_ACTION_STATE_CHANGE).putExtra(EXTRA_REFRESHING, true));

        // Don't even inspect the intent, we only do one thing, and that's fetch content.
        ArrayList<ContentProviderOperation> cpo = new ArrayList<ContentProviderOperation>();

        Uri dirUri = ItemsContract.Items.buildDirUri();


        try {




            int EmpId = Utility.getUserEmpId(getContext());
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

           getContext().getContentResolver().applyBatch(ItemsContract.CONTENT_AUTHORITY, cpo);

        } catch (JSONException | RemoteException | OperationApplicationException e) {
            // setBookStatus(this, TICKET_STATUS_SERVER_INVALID);
            //broadcastMessage("No Network, latest tickets not fetched.");
            Log.e(LOG_TAG, "Error updating content.", e);
        }
        updateWidgets();


      //  setFootballStatus(getContext(), TICKET_STATUS_OK);

        //   broadcastMessage("Latest tickets fetched.");
        //   setBookStatus(this,TICKET_STATUS_OK);
        //   sendStickyBroadcast(
        //         new Intent(BROADCAST_ACTION_STATE_CHANGE).putExtra(EXTRA_REFRESHING, false));
    }



    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,authority, new Bundle(), syncInterval);
        }
    }


    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        TicketSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }



}