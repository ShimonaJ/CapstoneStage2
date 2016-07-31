package app.com.work.shimonaj.helpdx.data;

import android.net.Uri;

public class ItemsContract {
	public static final String CONTENT_AUTHORITY = "com.example.helpdx";
	public static final Uri BASE_URI = Uri.parse("content://com.example.helpdx");

	interface ItemsColumns {
		/** Type: INTEGER PRIMARY KEY AUTOINCREMENT */
		String _ID = "_id";
		String TICKETID ="TicketId";

		/** Type: TEXT NOT NULL */
		String TITLE = "Title";
		String DESCRIPTION = "Description";
		/** Type: TEXT NOT NULL */
		String STAGENAME = "StageName";
		String CATEGORYNAME = "Category";
		String ASSIGNEDTO = "AssignedTo";
		String REQUESTEDBY= "RequestedBy";
		String SOURCE= "Source";
		String CREATEDON ="CreatedOn";
	}
	interface CommentsColumns {
		/** Type: INTEGER PRIMARY KEY AUTOINCREMENT */
		String _ID = "_id";
		String TICKETID ="TicketId";

		/** Type: TEXT  NULL */
		String COMMENT = "Comment";
		String COMMENTEDBY = "CommentedByName";

		String COMMENTEDON ="CommentedOn";
	}
	public static class Items implements ItemsColumns {
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.example.helpdx.items";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.example.helpdx.items";

        public static final String DEFAULT_SORT = TICKETID + " DESC";

		/** Matches: /items/ */
		public static Uri buildDirUri() {
			return BASE_URI.buildUpon().appendPath("items").build();
		}

		/** Matches: /items/[_id]/ */
		public static Uri buildItemUri(long _id) {
			return BASE_URI.buildUpon().appendPath("items").appendPath(Long.toString(_id)).build();
		}

        /** Read item ID item detail URI. */
        public static long getItemId(Uri itemUri) {
            return Long.parseLong(itemUri.getPathSegments().get(1));
        }
	}
	public static class Comments implements CommentsColumns {
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.example.helpdx.comments";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.example.helpdx.comments";

		public static final String DEFAULT_SORT = _ID + " ASC";

		/** Matches: /items/ */
		public static Uri buildDirUri() {
			return BASE_URI.buildUpon().appendPath("comments").build();
		}

		/** Matches: /items/[_id]/ */
		public static Uri buildCommentUri(long _id) {
			return BASE_URI.buildUpon().appendPath("comments").appendPath(Long.toString(_id)).build();
		}

		/** Read item ID item detail URI. */
		public static long getItemIdFromUri(Uri itemUri) {
			return Long.parseLong(itemUri.getPathSegments().get(1));
		}
	}
	private ItemsContract() {
	}
}
