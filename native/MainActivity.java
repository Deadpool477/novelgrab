package com.novelgrab.app;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.webkit.WebView;

import com.getcapacitor.BridgeActivity;

public class MainActivity extends BridgeActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        registerPlugin(BgPlugin.class);
        super.onCreate(savedInstanceState);
    }

    // While a download runs, tell the WebView it is still visible so Chrome does not throttle or pause its timers.
    private void keepWebViewRunning() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (BgService.on && getBridge() != null) {
                WebView w = getBridge().getWebView();
                w.dispatchWindowVisibilityChanged(View.VISIBLE);
                w.resumeTimers();
            }
        }, 1000);
    }

    @Override
    public void onPause() {
        super.onPause();
        keepWebViewRunning();
    }

    @Override
    public void onStop() {
        super.onStop();
        keepWebViewRunning();
    }
}
