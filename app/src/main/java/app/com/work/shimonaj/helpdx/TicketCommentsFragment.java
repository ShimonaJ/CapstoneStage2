package app.com.work.shimonaj.helpdx;


import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import app.com.work.shimonaj.helpdx.data.TicketAdapter;
import app.com.work.shimonaj.helpdx.data.TicketCommentsAdapter;
import app.com.work.shimonaj.helpdx.data.TicketCommentsLoader;
import app.com.work.shimonaj.helpdx.data.TicketLoader;
import app.com.work.shimonaj.helpdx.data.UpdaterService;
import app.com.work.shimonaj.helpdx.remote.Config;
import app.com.work.shimonaj.helpdx.util.Utility;


/**
 * A simple {@link Fragment} subclass.
 */
public class TicketCommentsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>  {

    //private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private static final int TICKET_COMMENTS_LOADER=101;
    private boolean mIsRefreshing = false;
    public TicketCommentsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_ticket_comments, container, false);
      //  mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.commentsList);

//        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
//        {
//            @Override
//            public void onRefresh()
//            {
//                refresh();
//            }
//        });
       // setHasOptionsMenu(true);

        if (savedInstanceState == null) {

            getLoaderManager().initLoader(TICKET_COMMENTS_LOADER, null, this);
        }
     //  getActivity().registerReceiver(mRefreshingReceiver,
     //           new IntentFilter(UpdaterService.TICKET_COMMENTS));
        return rootView;
    }
    private void refresh() {
        Intent intent = new Intent(getActivity(), UpdaterService.class);
        intent.setAction(UpdaterService.TICKET_COMMENTS);
        getActivity().startService(intent);
        //  startService(new Intent(this, UpdaterService.class));
    }
//    private BroadcastReceiver mRefreshingReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (UpdaterService.TICKET_COMMENTS.equals(intent.getAction())) {
//                mIsRefreshing = intent.getBooleanExtra(UpdaterService.EXTRA_REFRESHING, false);
//                //updateRefreshingUI();
//if(!mIsRefreshing){
//    getActivity().unregisterReceiver(mRefreshingReceiver);
//}
//
//            }
//        }
//    };
//    private void updateRefreshingUI() {
//        mSwipeRefreshLayout.setRefreshing(mIsRefreshing);
//    }
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String ticketId = Utility.getValFromSharedPref(getActivity(), Config.TicketId);
      //String ticketId =  getArguments().getString(TicketDetailFragment.TicketId);
        return TicketCommentsLoader.getComments(getActivity(),Long.valueOf(ticketId));
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        TicketCommentsAdapter adapter = new TicketCommentsAdapter(cursor,getActivity());
        adapter.setHasStableIds(true);
        mRecyclerView.setAdapter(adapter);

        LinearLayoutManager lm = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(lm);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mRecyclerView.setAdapter(null);
    }
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        if (id == R.id.action_reply) {
//            Intent replyActivityIntent = new Intent(getActivity(), ReplyActivity.class);
//            replyActivityIntent.putExtra(TicketDetailFragment.TicketId,getArguments().getString(TicketDetailFragment.TicketId) );
//
//            startActivity(replyActivityIntent);
//
////            Intent intent = new Intent(this, MainActivity.class);
////            intent.putExtra("ticketId",Long.parseLong(ticketId));
////            intent.setAction(UpdaterService.TICKET_DETAIL);
////            startService(intent);
////            navigateUpTo(new Intent(this, MainActivity.class));
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
}
