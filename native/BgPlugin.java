package com.novelgrab.app;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

// JS side: Capacitor.Plugins.Bg.start({text}) / update({text}) / stop() / battery()
@CapacitorPlugin(name = "Bg")
public class BgPlugin extends Plugin {
    private void send(PluginCall call) {
        Intent i = new Intent(getContext(), BgService.class);
        i.putExtra("text", call.getString("text", "Downloading chapters"));
        ContextCompat.startForegroundService(getContext(), i);
    }

    @PluginMethod
    public void start(PluginCall call) {
        if (Build.VERSION.SDK_INT >= 33 && getActivity() != null
                && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.POST_NOTIFICATIONS}, 77);
        }
        send(call);
        call.resolve();
    }

    @PluginMethod
    public void update(PluginCall call) {
        if (BgService.on) send(call);
        call.resolve();
    }

    @PluginMethod
    public void stop(PluginCall call) {
        getContext().stopService(new Intent(getContext(), BgService.class));
        call.resolve();
    }

    // asks Android once to exempt the app from battery optimisation (system dialog)
    @PluginMethod
    public void battery(PluginCall call) {
        try {
            PowerManager pm = (PowerManager) getContext().getSystemService(Context.POWER_SERVICE);
            String pkg = getContext().getPackageName();
            if (!pm.isIgnoringBatteryOptimizations(pkg)) {
                Intent i = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS, Uri.parse("package:" + pkg));
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(i);
            }
        } catch (Exception e) {
        }
        call.resolve();
    }
}
