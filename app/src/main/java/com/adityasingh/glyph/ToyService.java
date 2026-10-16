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
 * Shared plumbing for every Glyph Toy in this app.
 *
 * Nothing's launcher binds one of these when its toy is on screen, so
 * the bound service owns the matrix and must release it on unbind.
 * Subclasses only supply a renderer.
 *
 * The launcher talks to a toy over a {@link Messenger}, posting
 * MSG_GLYPH_TOY with an EVENT_* string saying when the toy became
 * visible or entered always-on display. A bare Binder leaves it unable
 * to say anything, so that contract is implemented here once.
 */
public abstract class ToyService extends Service {

    private static final String TAG = "GlyphToy";

    /**
     * ~11fps. These designs move in whole pixels, so there is nothing
     * between one step and the next to draw; pushing 20fps just sent
     * the same frame twice and burned power doing it.
     */
    private static final long FRAME_MS = 90L;
    /** AOD must be cheap, so slow right down when the screen is off. */
    private static final long FRAME_MS_AOD = 250L;

    /** Builds this toy's animation for a panel {@code size} wide. */
    protected abstract Renderer createRenderer(int size);

    private final Handler handler = new Handler(Looper.getMainLooper());

    /**
     * Receives the launcher's toy events. Held as a field so the
     * Messenger it backs stays alive for the life of the bind.
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

    private volatile Renderer renderer;
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
                String device = Common.is23112()
                        ? Glyph.DEVICE_23112
                        : Glyph.DEVICE_25111p;
                // register() reports whether the system authorised the
                // app key rather than throwing. setMatrixFrame does not
                // consult that flag, so draw either way and let the log
                // name a rejected key instead of going dark.
                if (!manager.register(device)) {
                    Log.w(TAG, "not authorised for " + device
                            + "; check the NothingKey meta-data");
                }
                renderer = createRenderer(matrixLength());
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
     * Panel width in pixels. The SDK answers 0 for a device it does not
     * recognise, which would leave the renderer an empty buffer, so
     * fall back to the (4a) Pro geometry and keep drawing.
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
                Renderer r = renderer;
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
