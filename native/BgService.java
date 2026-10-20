package com.novelgrab.app;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;

// Keeps the app process alive (foreground service + CPU/Wi-Fi locks) while chapters are downloading.
public class BgService extends Service {
    public static volatile boolean on = false;
    private PowerManager.WakeLock wl;
    private WifiManager.WifiLock wf;

    @Override
    public IBinder onBind(Intent i) {
        return null;
    }

    @Override
    public int onStartCommand(Intent i, int flags, int startId) {
        String text = (i != null && i.getStringExtra("text") != null) ? i.getStringExtra("text") : "Downloading chapters";
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= 26) {
            nm.createNotificationChannel(new NotificationChannel("ng", "NovelGrab downloads", NotificationManager.IMPORTANCE_LOW));
        }
        Intent open = getPackageManager().getLaunchIntentForPackage(getPackageName());
        PendingIntent pi = PendingIntent.getActivity(this, 0, open, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder b = Build.VERSION.SDK_INT >= 26 ? new Notification.Builder(this, "ng") : new Notification.Builder(this);
        Notification n = b.setContentTitle("NovelGrab").setContentText(text)
                .setSmallIcon(android.R.drawable.stat_sys_download).setContentIntent(pi).setOngoing(true).build();
        if (Build.VERSION.SDK_INT >= 29) {
            startForeground(1, n, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC);
        } else {
            startForeground(1, n);
        }
        if (wl == null) {
            try {
                PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "novelgrab:download");
                wl.acquire(6 * 60 * 60 * 1000L);
                WifiManager wm = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                wf = wm.createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF, "novelgrab:wifi");
                wf.acquire();
            } catch (Exception e) {
                // locks are a bonus; the foreground service alone already keeps the app alive
            }
        }
        on = true;
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        on = false;
        try {
            if (wl != null && wl.isHeld()) wl.release();
            if (wf != null && wf.isHeld()) wf.release();
        } catch (Exception e) {
        }
        wl = null;
        wf = null;
        super.onDestroy();
    }
}
