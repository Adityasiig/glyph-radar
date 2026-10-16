package com.adityasingh.glyph;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

import com.nothing.ketchum.Common;
import com.nothing.ketchum.Glyph;
import com.nothing.ketchum.GlyphException;
import com.nothing.ketchum.GlyphMatrixManager;
import com.nothing.ketchum.GlyphToy;

/**
 * The Glyph Toy. Nothing's launcher binds this when the toy is shown,
 * so it owns the matrix while bound and must give it back on unbind.
 *
 * The launcher talks to a toy over a {@link Messenger}, not a plain
 * Binder: it posts MSG_GLYPH_TOY carrying an EVENT_* string telling the
 * toy when it became visible (EVENT_CHANGE) or entered always-on
 * display (EVENT_AOD). Returning a bare Binder here would leave the
 * launcher unable to say anything, so the toy would draw over whatever
 * else is on the panel.
 */
public class RadarToyService extends Service {

    private static final String TAG = "RadarToy";

    /** ~20fps. The panel cannot usefully go faster and it costs power. */
    private static final long FRAME_MS = 50L;
    /** AOD must be cheap, so slow right down when the screen is off. */
    private static final long FRAME_MS_AOD = 200L;

    private final Handler handler = new Handler(Looper.getMainLooper());

    /**
     * Receives the launcher's toy events on the main thread. Held as a
     * field so the Messenger it backs stays alive as long as the bind.
     */
    private final Handler toyEvents = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what != GlyphToy.MSG_GLYPH_TOY) {
                super.handleMessage(msg);
                return;
            }
            String event = msg.getData() == null
                    ? null
                    : msg.getData().getString(GlyphToy.MSG_GLYPH_TOY_DATA);
            if (GlyphToy.EVENT_AOD.equals(event)) {
                frameMs = FRAME_MS_AOD;
            } else if (GlyphToy.EVENT_CHANGE.equals(event)) {
                frameMs = FRAME_MS;
            }
        }
    };

    private final Messenger messenger = new Messenger(toyEvents);

    private volatile RadarRenderer renderer;
    private volatile long frameMs = FRAME_MS;

    private GlyphMatrixManager manager;
    private Runnable tick;
    private long startedAt;

    @Override
    public IBinder onBind(Intent intent) {
        startedAt = System.currentTimeMillis();
        frameMs = FRAME_MS;

        manager = GlyphMatrixManager.getInstance(getApplicationContext());
        manager.init(new GlyphMatrixManager.Callback() {
            @Override
            public void onServiceConnected(ComponentName name) {
                // The Phone (3) is a 25x25 panel, the (4a) Pro a 13x13.
                String device = Common.is23112()
                        ? Glyph.DEVICE_23112
                        : Glyph.DEVICE_25111p;
                // register() returns whether the system authorised the
                // app key rather than throwing. setMatrixFrame does not
                // consult that flag, so draw either way and let the log
                // report a rejected key instead of going dark.
                if (!manager.register(device)) {
                    Log.w(TAG, "not authorised for " + device
                            + "; check the NothingKey meta-data");
                }
                renderer = new RadarRenderer(matrixLength());
                startAnimating();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                stopAnimating();
            }
        });

        return messenger.getBinder();
    }

    /**
     * Panel width in pixels. The SDK answers 0 on a device it does not
     * recognise, which would give the renderer an empty buffer, so fall
     * back to the (4a) Pro's geometry and keep drawing something.
     */
    private int matrixLength() {
        int len = Common.getDeviceMatrixLength();
        if (len <= 0) {
            Log.w(TAG, "unknown device; assuming 13x13");
            return Glyph.DEVICE_25111p_MATRIX_LENGTH;
        }
        return len;
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
                handler.postDelayed(this, frameMs);
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
}
