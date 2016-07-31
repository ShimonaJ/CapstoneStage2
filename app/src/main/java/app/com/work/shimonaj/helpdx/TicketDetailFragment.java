//package app.com.work.shimonaj.helpdx;
//
//import android.app.LoaderManager;
//import android.content.Intent;
//import android.content.Loader;
//import android.database.Cursor;
//import android.os.Bundle;
//import android.app.Fragment;
//import android.support.design.widget.TabLayout;
//import android.support.v4.view.ViewPager;
//import android.support.v7.app.ActionBar;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.Toolbar;
//import android.transition.TransitionInflater;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import app.com.work.shimonaj.helpdx.data.TicketAdapter;
//import app.com.work.shimonaj.helpdx.data.TicketDetailViewHolder;
//import app.com.work.shimonaj.helpdx.data.TicketLoader;
//import app.com.work.shimonaj.helpdx.data.UpdaterService;
//import app.com.work.shimonaj.helpdx.util.Utility;
//
//
///**
// * A fragment representing a single Ticket detail screen.
// * This fragment is either contained in a {@link MainActivity}
// * in two-pane mode (on tablets) or a {@link TicketDetailActivity}
// * on handsets.
// */
//public class TicketDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>  {
//    private static final String TAG = UpdaterService.class.getName();
//    /**
//     * The fragment argument representing the item ID that this fragment
//     * represents.
//     */
//
//
//
//
//    /**
//     * The dummy content this fragment is presenting.
//     */
//   // private DummyContent.DummyItem mItem;
//
//    /**
//     * Mandatory empty constructor for the fragment manager to instantiate the
//     * fragment (e.g. upon screen orientation changes).
//     */
//    public TicketDetailFragment() {
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setHasOptionsMenu(true);
//
//
//        //if (getArguments().containsKey(TicketId)) {
//            // Load the dummy content specified by the fragment
//            // arguments. In a real-world scenario, use a Loader
//            // to load content from a content provider.
//         //   Log.v(TAG,getArguments().getString(TicketId));
//        Intent intent = new Intent(getActivity(), UpdaterService.class);
//        intent.putExtra("ticketId", Long.parseLong(ticketId));
//        intent.setAction(UpdaterService.TICKET_DETAIL);
//        startService(intent);
//            getLoaderManager().initLoader(TICKET_DETAIL_LOADER, null, this);
//     //   }
//
//
//    }
//
////    @Override
////    public View onCreateView(LayoutInflater inflater, ViewGroup container,
////                             Bundle savedInstanceState) {
////        View rootView = inflater.inflate(R.layout.ticket_detail, container, false);
////        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
////
////        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
////        ActionBar actionBar =((AppCompatActivity)getActivity()).getSupportActionBar();
////        if(actionBar!=null) {
////            actionBar.setDisplayHomeAsUpEnabled(true);
////
////        }
////        // Show the dummy content as text in a TextView.
//////        if (mItem != null) {
//////            ((TextView) rootView.findViewById(R.id.detail_title)).setText(mItem.details);
//////        }
////
////        return rootView;
////    }
//
//
//    @Override
//    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
//        String ticketId = Utility.getValFromSharedPref(getActivity(),TicketDetailFragment.TicketId);
//        if(ticketId!="") {
//            long l = Long.parseLong(ticketId);
//            return TicketLoader.newInstanceForItemId(getActivity(), l);
//        }
//        return null;
//    }
//
//    @Override
//    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
//        if(!cursor.moveToFirst()){return ;}
//
//
//
//        TextView titleView =(TextView)getActivity().findViewById(R.id.detail_title);
//        titleView.setText(  cursor.getString(TicketLoader.Query.TICKETID)+" "+ cursor.getString(TicketLoader.Query.TITLE));
//
//        TextView subTitleView =(TextView)getActivity().findViewById(R.id.detail_subtitle);
//        String subtitle = cursor.getString(TicketLoader.Query.REQUESTEDBY)+" reported on "+ cursor.getString(TicketLoader.Query.CREATEDON)+" via "+ cursor.getString(TicketLoader.Query.SOURCE);
//        subTitleView.setText(  subtitle);
//        subTitleView.setVisibility(View.VISIBLE);
////
//        TextView descView =(TextView)getActivity().findViewById(R.id.detail_desc);
//        descView.setText(  cursor.getString(TicketLoader.Query.DESCRIPTION));
//        descView.setVisibility(View.VISIBLE);
////        final TicketDetailViewHolder viewHolder = new TicketDetailViewHolder(getView());
////        viewHolder.titleView.setText(  cursor.getString(TicketLoader.Query.TICKETID)+" "+ cursor.getString(TicketLoader.Query.TITLE));
////
////
////        viewHolder.subTitleView.setText(  subtitle);
//        //viewHolder.descView.setText(  cursor.getString(TicketLoader.Query.DESCRIPTION));
//      //  ((Toolbar)viewHolder.mView.findViewById(R.id.toolbar)).setTitle("Ticket Detail");
//
//        //viewHolder.descView.setText( cursor.getString(TicketLoader.Query.STAGENAME));
//       // setTitle(cursor.getString(TicketLoader.Query.TICKETID)+" "+cursor.getString(TicketLoader.Query.TITLE));
//
//    }
//
//    @Override
//    public void onLoaderReset(Loader<Cursor> loader) {
//
//    }
//
//}
