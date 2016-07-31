package app.com.work.shimonaj.helpdx.data;

import android.content.Context;
import android.content.CursorLoader;
import android.net.Uri;

/**
 * Created by shimonaj on 5/17/2016.
 */
public class TicketCommentsLoader   extends CursorLoader {
    public static TicketCommentsLoader getComments(Context context, long itemId) {
        return new TicketCommentsLoader(context, ItemsContract.Comments.buildCommentUri(itemId));
    }
    private TicketCommentsLoader(Context context, Uri uri) {
        super(context, uri, Query.PROJECTION, null, null, ItemsContract.Comments.DEFAULT_SORT);
    }

    public interface Query {
        String[] PROJECTION = {
                ItemsContract.Comments._ID,
                ItemsContract.Comments.TICKETID,
                ItemsContract.Comments.COMMENT,
                ItemsContract.Comments.COMMENTEDON,
                ItemsContract.Comments.COMMENTEDBY,
        };

        int _ID = 0;
        int TICKETID = 1;
        int COMMENT = 2;
        int COMMENTEDON = 3;
        int COMMENTEDBY = 4;


    }
}
