package app.com.work.shimonaj.helpdx;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.app.LoaderManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import app.com.work.shimonaj.helpdx.data.TicketAdapter;
import app.com.work.shimonaj.helpdx.data.TicketLoader;
import app.com.work.shimonaj.helpdx.data.UpdaterService;
import app.com.work.shimonaj.helpdx.remote.Config;
import app.com.work.shimonaj.helpdx.remote.RemoteEndpointUtil;
import app.com.work.shimonaj.helpdx.sync.TicketSyncAdapter;
import app.com.work.shimonaj.helpdx.util.Utility;

public class MainActivity extends AppCompatActivity
        implements
        LoaderManager.LoaderCallbacks<Cursor>{
    private static final String TAG = "MainActivity";
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    public static  String SELECTED_LIST_POS_KEY="SelectedListPos";
    public  int mPosition;
    private BroadcastReceiver messageReciever;

    public static final String MESSAGE_EVENT = "MESSAGE_EVENT";
    public static final String MESSAGE_KEY = "MESSAGE_EXTRA";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
       // ((TextView)findViewById(R.id.toolbar_title)).setTypeface(Utility.mediumRobotoFont);

        TicketSyncAdapter.initializeSyncAdapter(this);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();


                Context context = getApplicationContext();
                Intent intent = new Intent(context, CreateTicketActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

//                    host.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(
//                            host, holder.thumbnailView, holder.thumbnailView.getTransitionName()).toBundle());
            }
        });
        messageReciever = new MessageReciever();
        IntentFilter filter = new IntentFilter(MESSAGE_EVENT);
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReciever, filter);
        if (findViewById(R.id.ticket_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            Config.mTwoPane = true;
        }

//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.setDrawerListener(toggle);
//        toggle.syncState();
//
//        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
               refresh();
            }
        });
        refresh();
        getLoaderManager().initLoader(0, null, this);

        if(savedInstanceState!=null && savedInstanceState.containsKey(SELECTED_LIST_POS_KEY)){
            mPosition=savedInstanceState.getInt(SELECTED_LIST_POS_KEY);
        }
        else{
            String position =Utility.getValFromSharedPref(this,MainActivity.SELECTED_LIST_POS_KEY);
            if(position!=""){
                mPosition=  Integer.parseInt(position);
            }else
            if(getIntent().getExtras()!=null){
                Bundle params =getIntent().getExtras();
                mPosition=  params.getInt(MainActivity.SELECTED_LIST_POS_KEY);
            }
        }
    }
    @Override
    public void onResume() {

        super.onResume();
    }

    @Override
    public void onPause() {

        super.onPause();
    }
//    private void updateOnResponse() {
//
//
//
//        // if cursor is empty, why? do we have an invalid location
//       // int message = 0;
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
//
//        int bookstatus = prefs.getInt(Config.TICKET_STATUS, 0);
//        mIsRefreshing = false;
//        updateRefreshingUI();
//        if(bookstatus==UpdaterService.TICKET_STATUS_OK){
//            Toast.makeText(getApplicationContext(),"Latest tickets fetched.",Toast.LENGTH_LONG).show();
////            Intent mainActivityIntent = new Intent(getApplicationContext(), MainActivity.class);
////            startActivity(mainActivityIntent);
////            makeSnackbar();
//        }else
//        if(bookstatus==UpdaterService.TICKET_STATUS_SERVER_DOWN){
//
//            Toast.makeText(getApplicationContext(),"No Network, latest tickets not fetched.",Toast.LENGTH_LONG).show();
//
//        }
//        if(bookstatus==UpdaterService.TICKET_STATUS_SERVER_INVALID){
//
//            Toast.makeText(getApplicationContext(),"Server Down.",Toast.LENGTH_LONG).show();
//
//        }
//        else {
//            Toast.makeText(getApplicationContext(),"Unknown error."+bookstatus,Toast.LENGTH_LONG).show();
//        }
//    }
    @Override
    protected void onDestroy() {

        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReciever);
        super.onDestroy();
    }

    private void refresh() {
//        registerReceiver(mRefreshingReceiver,
//                new IntentFilter(UpdaterService.BROADCAST_ACTION_STATE_CHANGE));

        mIsRefreshing=true;
        updateRefreshingUI();

        Intent intent = new Intent(this, UpdaterService.class);

        intent.setAction(UpdaterService.BROADCAST_ACTION_STATE_CHANGE);
        startService(intent);

      //  startService(new Intent(this, UpdaterService.class));
    }
//    @Override
//    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
//        if ( key.equals(Config.TICKET_STATUS) ) {
//            updateOnResponse();
//        }
//    }

//    private BroadcastReceiver mRefreshingReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (UpdaterService.BROADCAST_ACTION_STATE_CHANGE.equals(intent.getAction())) {
//                mIsRefreshing = intent.getBooleanExtra(UpdaterService.EXTRA_REFRESHING, false);
//                updateRefreshingUI();
//if(!mIsRefreshing){
//    unregisterReceiver(mRefreshingReceiver);
//}
//
//            }
//        }
//    };
    private void updateRefreshingUI() {
        mSwipeRefreshLayout.setRefreshing(mIsRefreshing);
    }


    private boolean mIsRefreshing = false;
//    @Override
//    public void onBackPressed() {
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }
//    }
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return TicketLoader.newAllArticlesInstance(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        TicketAdapter adapter = new TicketAdapter(cursor,this);
        adapter.setHasStableIds(true);
        mRecyclerView.setAdapter(adapter);
        LinearLayoutManager lm = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(lm);
        if(mPosition!= RecyclerView.NO_POSITION){

            mRecyclerView.scrollToPosition(mPosition);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mRecyclerView.setAdapter(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(mPosition!=RecyclerView.NO_POSITION) {
            outState.putInt(SELECTED_LIST_POS_KEY, mPosition);
        }
        //  outState.putParcelableArrayList(MOVIE_KEY, (ArrayList<? extends Parcelable>) movies);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_logout) {
           // RemoteEndpointUtil.onLogout();
            Intent intent = new Intent(this, UpdaterService.class);

            intent.setAction(UpdaterService.LOGOUT);
            startService(intent);

            Intent loginscreen=new Intent(this,LoginPageActivity.class);
            loginscreen.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(loginscreen);
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class MessageReciever extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getStringExtra(MESSAGE_KEY) != null) {
                Snackbar.make(getCurrentFocus(),  intent.getStringExtra(MESSAGE_KEY), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            //    Toast.makeText(MainActivity.this, intent.getStringExtra(MESSAGE_KEY), Toast.LENGTH_LONG).show();
             //   Snackbar.make(findViewById(R.id.toolbar), intent.getStringExtra(MESSAGE_KEY), Snackbar.LENGTH_INDEFINITE);
//                .setAction("Action", null).show();
            }
        }
    }
}
