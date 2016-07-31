package app.com.work.shimonaj.helpdx;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import app.com.work.shimonaj.helpdx.data.UpdaterService;
import app.com.work.shimonaj.helpdx.remote.Config;
import app.com.work.shimonaj.helpdx.util.Utility;

public class CreateTicketActivity extends AppCompatActivity  {
    private static final String TAG = CreateTicketActivity.class.getName();
    private boolean mIsRefreshing = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.putKeyValInSharedPref(this,Config.TicketId,"0");
        Utility.putKeyValInSharedPref(this,MainActivity.SELECTED_LIST_POS_KEY,"0");
        setContentView(R.layout.activity_create_ticket);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Button btn =(Button)this.findViewById(R.id.create_ticket_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCreateTicketClick(view);
            }

        });
    }

    public void onCreateTicketClick(View view) {
        Toast.makeText(this, "Ticket posted, this may take some time .", Toast.LENGTH_LONG).show();
        Log.v(TAG,"Button click");
        JSONObject userObj = Utility.getUserInfo(getApplicationContext());
        String EmpId="";
        String CompanyId="";

        try{
            CompanyId = userObj.getString("CompanyId");
            EmpId = userObj.getString("EmployeeId");
        }catch (JSONException e){
            e.printStackTrace();
        }

        JSONObject json = new JSONObject();
        JSONObject ticket = new JSONObject();
        try {
            ticket.putOpt("Title",((TextView)this.findViewById(R.id.addTicketTitle)).getText());
            ticket.putOpt("Description",((TextView)this.findViewById(R.id.addTicketDesc)).getText());
            ticket.putOpt("TicketSource","Android App");
            ticket.putOpt("CompanyId",CompanyId);
            ticket.put("RequestorId", EmpId);

            json.put("EmployeeId", EmpId);
            json.putOpt("ticket",ticket);
            json.put("hostname","");
            json.put("tokenKey","");
        } catch (JSONException e) {
            e.printStackTrace();
        }



//        registerReceiver(mRefreshingReceiver,
//                    new IntentFilter(UpdaterService.TICKET_POST));
        Intent intent = new Intent(this, UpdaterService.class);
        intent.putExtra("ticketData",json.toString());
        intent.setAction(UpdaterService.TICKET_POST);
        startService(intent);

        Intent mainActivityIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(mainActivityIntent);
    }


//
//    private void updateEmptyView() {
//
//
//
//            // if cursor is empty, why? do we have an invalid location
//            int message = 0;
//            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
//
//            int bookstatus = prefs.getInt(Config.TICKET_STATUS, 0);
//         if(bookstatus==UpdaterService.TICKET_STATUS_SERVER_DOWN){
//
//             Toast.makeText(getApplicationContext(),"No Network, ticket will not be created.",Toast.LENGTH_LONG).show();
//
//        }else  if(bookstatus==UpdaterService.TICKET_STATUS_OK){
//
//
//             makeSnackbar();
//         } else {
//             Toast.makeText(getApplicationContext(),"Unknown error."+bookstatus,Toast.LENGTH_LONG).show();
//         }
//    }



//    private void makeSnackbar(){
//        Toast.makeText(getApplicationContext(),"Ticket Posted Successfully, This will refresh in a while",Toast.LENGTH_LONG).show();;
////        Snackbar.make(getCurrentFocus(), "Ticket Created Successfully, This will refresh in a while", Snackbar.LENGTH_INDEFINITE)
////                .setAction("Action", null).show();
//    }
//    private BroadcastReceiver mRefreshingReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (UpdaterService.TICKET_POST.equals(intent.getAction())) {
//
//                mIsRefreshing = intent.getBooleanExtra(UpdaterService.EXTRA_REFRESHING, false);
//                //updateRefreshingUI();
//                if(!mIsRefreshing) {
//                    unregisterReceiver(mRefreshingReceiver);
//                  //  Toast.makeText(getApplicationContext(),"Ticket Created Successfully, This will refresh in a while",Toast.LENGTH_LONG);
//
//                    Intent mainActivityIntent = new Intent(getApplicationContext(), MainActivity.class);
//                    startActivity(mainActivityIntent);
//                    makeSnackbar();
//
//                }
//
//
//            }
//        }
//    };
}
