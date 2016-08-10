package app.com.work.shimonaj.helpdx.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class TicketSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static TicketSyncAdapter sTicketSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d("TicketSyncService", "onCreate - TicketSyncService");
        synchronized (sSyncAdapterLock) {
            if (sTicketSyncAdapter == null) {
                sTicketSyncAdapter = new TicketSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sTicketSyncAdapter.getSyncAdapterBinder();
    }
}