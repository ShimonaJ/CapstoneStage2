package app.com.work.shimonaj.helpdx.widget;

/**
 * Created by shimonaj on 2/15/2016.
 */
import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;



import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import app.com.work.shimonaj.helpdx.MainActivity;
import app.com.work.shimonaj.helpdx.R;
import app.com.work.shimonaj.helpdx.data.ItemsContract;
import app.com.work.shimonaj.helpdx.data.TicketLoader;


/**
 * RemoteViewsService controlling the data being shown in the scrollable weather detail widget
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class DetailWidgetRemoteViewService extends RemoteViewsService {
    public final String LOG_TAG = DetailWidgetRemoteViewService.class.getSimpleName();




    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;

            @Override
            public void onCreate() {
                // Nothing to do
            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }
                // This method is called by the app hosting the widget (e.g., the launcher)
                // However, our ContentProvider is not exported so it doesn't have access to the
                // data. Therefore we need to clear (and finally restore) the calling identity so
                // that calls use our process and permission
                final long identityToken = Binder.clearCallingIdentity();
                //   String location = Utility.getPreferredLocation(DetailWidgetRemoteViewsService.this);
                Uri ticketUri = ItemsContract.Items.buildDirUri();

                data = getContentResolver().query(ticketUri,
                        TicketLoader.Query.PROJECTION,
                        null
                        , null,
                        ItemsContract.Items.TICKETID + " DESC limit 10");
                Binder.restoreCallingIdentity(identityToken);

            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }
                RemoteViews views = new RemoteViews(getPackageName(),
                        R.layout.widget_item);
//Log.v(LOG_TAG,"check:"+data.getString(TicketLoader.Query.STAGENAME));
                String stagename =data.getString(TicketLoader.Query.STAGENAME);
                views.setTextViewText(R.id.title, "#"+data.getString(TicketLoader.Query.TICKETID)+" "+data.getString(TicketLoader.Query.TITLE));
               // views.setTextViewText(R.id.stagename, data.getString(TicketLoader.Query.STAGENAME));
                if(stagename.equals("Open")){

                    views.setTextColor(R.id.title, getResources().getColor( R.color.red));
                }else if(stagename.equals("Closed")){
                    views.setTextColor(R.id.title, getResources().getColor( R.color.green));
                }
//                views.setTextViewText(R.id.ticketid, data.getString(TicketLoader.Query.TICKETID));
//                views.setTextViewText(R.id.assignedTo, data.getString(TicketLoader.Query.ASSIGNEDTO));
//                views.setTextViewText(R.id.category, data.getString(TicketLoader.Query.CATEGORYNAME));
//                views.setTextViewText(R.id.createdOn, data.getString(TicketLoader.Query.CREATEDON));
//
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
//                    setRemoteContentDescription(views, "Ticket title: " + data.getString(TicketLoader.Query.TITLE) +" is in "+ data.getString(TicketLoader.Query.STAGENAME) +" stage");
//                }


//                final Intent fillInIntent = new Intent();
//
//                Uri weatherUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(
//                        locationSetting,
//                        dateInMillis);
//                fillInIntent.setData(weatherUri);
//                views.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);
                Intent mainActivityIntent = new Intent(getApplicationContext(), MainActivity.class);
                Bundle arguments = new Bundle();
                arguments.putInt(MainActivity.SELECTED_LIST_POS_KEY,position);
                 mainActivityIntent.putExtras(arguments);
                //startActivity(mainActivityIntent);
                views.setOnClickFillInIntent(R.id.widget_item, mainActivityIntent);
                return views;
            }

            @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
            private void setRemoteContentDescription(RemoteViews views, String description) {
                views.setContentDescription(R.id.widget_icon, description);
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (data.moveToPosition(position))
                    return data.getLong(TicketLoader.Query._ID
                    );
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
