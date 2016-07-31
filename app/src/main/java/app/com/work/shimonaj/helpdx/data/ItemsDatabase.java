package app.com.work.shimonaj.helpdx.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static app.com.work.shimonaj.helpdx.data.ItemsProvider.Tables;

public class ItemsDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "tickets.db";
    private static final int DATABASE_VERSION = 5;

    public ItemsDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + Tables.ITEMS + " ("
                + ItemsContract.ItemsColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + ItemsContract.ItemsColumns.TICKETID + " INTEGER,"
                + ItemsContract.ItemsColumns.TITLE + " TEXT  NULL,"
                + ItemsContract.ItemsColumns.DESCRIPTION + " TEXT  NULL,"
                + ItemsContract.ItemsColumns.STAGENAME + " TEXT  NULL,"
                + ItemsContract.ItemsColumns.CATEGORYNAME + " TEXT  NULL,"
                + ItemsContract.ItemsColumns.ASSIGNEDTO + " TEXT  NULL,"
                + ItemsContract.ItemsColumns.REQUESTEDBY + " TEXT  NULL,"
                + ItemsContract.ItemsColumns.SOURCE + " TEXT  NULL,"
                + ItemsContract.ItemsColumns.CREATEDON + " TEXT NULL "
                + ")" );
        final String SQL_CREATE_COMMENT_TABLE = "CREATE TABLE " + Tables.COMMENTS + " (" +
                // Why AutoIncrement here, and not above?
                // Unique keys will be auto-generated in either case.  But for weather
                // forecasting, it's reasonable to assume the user will want information
                // for a certain date and all dates *following*, so the forecast data
                // should be sorted accordingly.
                ItemsContract.Comments._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                // the ID of the location entry associated with this weather data
                ItemsContract.Comments.COMMENT + " TEXT  NULL, " +
                ItemsContract.Comments.COMMENTEDBY + " TEXT  NULL, " +
                ItemsContract.Comments.COMMENTEDON + " TEXT  NULL, " +
                ItemsContract.Comments.TICKETID + " INTEGER NOT NULL ," +

                // Set up the location column as a foreign key to location table.
                " FOREIGN KEY (" +  ItemsContract.Comments.TICKETID
                + ") REFERENCES " +
                Tables.ITEMS + " (" + ItemsContract.ItemsColumns._ID + ")); " ;
        db.execSQL(SQL_CREATE_COMMENT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Tables.ITEMS);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.COMMENTS);
        onCreate(db);
    }
}
