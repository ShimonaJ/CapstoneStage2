package app.com.work.shimonaj.helpdx;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import app.com.work.shimonaj.helpdx.data.UpdaterService;
import app.com.work.shimonaj.helpdx.remote.Config;
import app.com.work.shimonaj.helpdx.util.Utility;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReplyActivity extends AppCompatActivity  {
    private static final String TAG = ReplyActivity.class.getName();
    private boolean mIsRefreshing = false;
    public ReplyActivity() {
        // Required empty public constructor

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.reply_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }



        @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:goToTicketDetailView(); return true;
            case R.id.action_reply:
doReply(0);
               // Toast.makeText(getActivity(),"Reply clicked",Toast.LENGTH_SHORT);
                return true;

            case R.id.action_reply_close:doReply(1);
             //   Toast.makeText(getActivity(),"Reply and close clicked",Toast.LENGTH_SHORT);
                return true;
            case R.id.action_cancel:goToTicketDetailView();
                //   Toast.makeText(getActivity(),"Reply and close clicked",Toast.LENGTH_SHORT);
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
    //private String ticketId="";
private void doReply(int actionType){
    Toast.makeText(this, "Reply posted, this may take some time .", Toast.LENGTH_LONG).show();
    Log.v(TAG,"Button click");
    JSONObject userObj = Utility.getUserInfo(this);
    String EmpId="";

    String ticketId = Utility.getValFromSharedPref(this, Config.TicketId);
    try{

       // ticketId = getArguments().getString(TicketDetailFragment.TicketId);
        EmpId = userObj.getString("EmployeeId");
    }catch (JSONException e){
        e.printStackTrace();
    }
    JSONObject json = new JSONObject();

    try {

        json.put("ticketId", ticketId);
        json.put("IsPrivate", 0);
        json.put("actionType", actionType);
        json.put("EmployeeId", EmpId);
        json.put("comment",((TextView)findViewById(R.id.replyText)).getText());
        json.put("tokenKey","");
    } catch (JSONException e) {
        e.printStackTrace();
    }
   // registerReceiver(mRefreshingReceiver,
   //         new IntentFilter(UpdaterService.TICKET_COMMENT_POST));
    Intent intent = new Intent(this, UpdaterService.class);
    intent.putExtra("commentData",json.toString());
    intent.setAction(UpdaterService.TICKET_COMMENT_POST);
    startService(intent);
    goToTicketDetailView();

}
//    private BroadcastReceiver mRefreshingReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (UpdaterService.TICKET_COMMENT_POST.equals(intent.getAction())) {
//                mIsRefreshing = intent.getBooleanExtra(UpdaterService.EXTRA_REFRESHING, false);
//                //updateRefreshingUI();
//                if(!mIsRefreshing) {
//                    unregisterReceiver(mRefreshingReceiver);
//                    //Toast.makeText(getActivity(),"Ticket Created Successfully, This will refresh in a while",Toast.LENGTH_LONG);
////                    ((TicketDetailActivity)).onReplyDone(getArguments().getString(TicketDetailFragment.TicketId));
////                    Snackbar.make(getView(), "Reply Send Successfully, This will refresh in a while", Snackbar.LENGTH_LONG)
////                            .setAction("Action", null).show();
//
//
//                }
//
//
//            }
//        }
//    };
    private void goToTicketDetailView(){
        Intent detailActivityIntent = new Intent(getApplicationContext(), TicketDetailActivity.class);
      //  detailActivityIntent.putExtra(TicketDetailFragment.TicketId,ticketId );

        startActivity(detailActivityIntent);
        this.finish();
    }



    @Override
    public void onBackPressed() {
        goToTicketDetailView();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_reply);
     //   ticketId = getIntent().getStringExtra(TicketDetailFragment.TicketId);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
       //setHasOptionsMenu(true);
    }


//    private void updateOnResponse() {
//
//
//
//        // if cursor is empty, why? do we have an invalid location
//        int message = 0;
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
//
//        int bookstatus = prefs.getInt(Config.TICKET_STATUS, 0);
//
//        Log.v(TAG,"Reply Activity: Response recieved "+bookstatus);
//        if(bookstatus==UpdaterService.TICKET_STATUS_SERVER_DOWN){
//
//            Toast.makeText(getApplicationContext(),"No Network, reply will not be posted.",Toast.LENGTH_LONG).show();
//
//        }else  if(bookstatus==UpdaterService.TICKET_STATUS_INVALID){
//
//            Toast.makeText(getApplicationContext(),"No Network, reply will not be posted.",Toast.LENGTH_LONG).show();
//
//        }else  if(bookstatus==UpdaterService.TICKET_STATUS_OK){
//            Toast.makeText(getApplicationContext(),"Ticket Posted Successfully, This will refresh in a while",Toast.LENGTH_LONG).show();
//
//
//        }else {
//            Toast.makeText(getApplicationContext(),"Unknown error."+bookstatus,Toast.LENGTH_LONG).show();
//        }
//        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
//        sp.unregisterOnSharedPreferenceChangeListener(this);
//    }
//    @Override
//    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
//
//
//        if ( key.equals(Config.TICKET_STATUS) ) {
//            Log.v(TAG,"********************* yup ");
//            updateOnResponse();
//        }
//    }



//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        View rootView =inflater.inflate(R.layout.fragment_reply, container, false);
//// getActivity().setTheme(R.style.ReplyTheme);
//        setHasOptionsMenu(true);
////        TextView titleView =(TextView)getActivity().findViewById(R.id.detail_title);
////        titleView.setText( "");
//
//        TextView subTitleView =(TextView)getActivity().findViewById(R.id.detail_subtitle);
//        subTitleView.setVisibility(View.INVISIBLE);
//        subTitleView.setText(  "");
//
////
//        TextView descView =(TextView)getActivity().findViewById(R.id.detail_desc);
//        descView.setVisibility(View.INVISIBLE);
//        descView.setText("");
//
//        return rootView;
//    }

}
