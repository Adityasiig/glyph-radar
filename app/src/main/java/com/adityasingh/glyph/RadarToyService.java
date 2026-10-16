package com.adityasingh.glyph;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import com.nothing.ketchum.Common;
import com.nothing.ketchum.Glyph;
import com.nothing.ketchum.GlyphException;
import com.nothing.ketchum.GlyphMatrixManager;

/**
 * The Glyph Toy. Nothing's launcher binds this when the toy is selected,
 * so it owns the matrix for as long as it is bound and must give it
 * back on unbind.
 */
public class RadarToyService extends Service {

    private static final String TAG = "RadarToy";

    /** ~20fps. The panel cannot usefully go faster and it costs power. */
    private static final long FRAME_MS = 50L;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private volatile RadarRenderer renderer;

    private GlyphMatrixManager manager;
    private Runnable tick;
    private long startedAt;

    @Override
    public IBinder onBind(Intent intent) {
        startedAt = System.currentTimeMillis();

        manager = GlyphMatrixManager.getInstance(getApplicationContext());
        manager.init(new GlyphMatrixManager.Callback() {
            @Override
            public void onServiceConnected(ComponentName name) {
                // register returns false rather than throwing; the
                // Phone (3) is a 25x25 panel, the (4a) Pro a 13x13 one.
                String device = Common.is23112()
                        ? Glyph.DEVICE_23112
                        : Glyph.DEVICE_25111p;
                if (!manager.register(device)) {
                    Log.e(TAG, "register failed for " + device);
                    return;
                }
                // Ask the SDK rather than hardcoding, so one renderer
                // fills either panel.
                renderer = new RadarRenderer(Common.getDeviceMatrixLength());
                startAnimating();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                stopAnimating();
            }
        });

        // Nothing to call back into; the toy only draws.
        return new RadarBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        stopAnimating();
        if (manager != null) {
            manager.unInit();
            manager = null;
        }
        return false;
    }

    private void startAnimating() {
        stopAnimating();
        tick = new Runnable() {
            @Override
            public void run() {
                RadarRenderer r = renderer;
                GlyphMatrixManager gm = manager;
                if (r != null && gm != null) {
                    float t = (System.currentTimeMillis() - startedAt) / 1000f;
                    try {
                        gm.setMatrixFrame(r.render(t));
                    } catch (GlyphException e) {
                        Log.e(TAG, "setMatrixFrame failed", e);
                    }
                }
                handler.postDelayed(this, FRAME_MS);
            }
        };
        handler.post(tick);
    }

    private void stopAnimating() {
        if (tick != null) {
            handler.removeCallbacks(tick);
            tick = null;
        }
    }

    static class RadarBinder extends android.os.Binder {
    }
}
