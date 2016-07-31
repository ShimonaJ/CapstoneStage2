package app.com.work.shimonaj.helpdx.data;

import android.content.Context;
import android.content.CursorLoader;
import android.net.Uri;

/**
 * Helper for loading a list of articles or a single article.
 */
public class TicketLoader extends CursorLoader {
    public static TicketLoader newAllArticlesInstance(Context context) {
        return new TicketLoader(context, ItemsContract.Items.buildDirUri());
    }

    public static TicketLoader newInstanceForItemId(Context context, long itemId) {
        return new TicketLoader(context, ItemsContract.Items.buildItemUri(itemId));
    }

    private TicketLoader(Context context, Uri uri) {
        super(context, uri, Query.PROJECTION, null, null, ItemsContract.Items.DEFAULT_SORT);
    }

    public interface Query {
        String[] PROJECTION = {
                ItemsContract.Items._ID,
                ItemsContract.Items.TICKETID,
                ItemsContract.Items.TITLE,
                ItemsContract.Items.DESCRIPTION,
                ItemsContract.Items.CREATEDON,
                ItemsContract.Items.STAGENAME,
                ItemsContract.Items.CATEGORYNAME,
                ItemsContract.Items.ASSIGNEDTO,
                ItemsContract.Items.REQUESTEDBY,
                ItemsContract.Items.SOURCE,
        };

        int _ID = 0;
        int TICKETID = 1;
        int TITLE = 2;
        int DESCRIPTION = 3;
        int CREATEDON = 4;
        int STAGENAME = 5;
        int CATEGORYNAME = 6;
        int ASSIGNEDTO = 7;
        int REQUESTEDBY = 8;
        int SOURCE = 9;

    }
}
