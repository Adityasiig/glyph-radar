package com.adityasingh.glyph;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Draws {@link RadarRenderer} on screen as a grid of dots.
 *
 * The Glyph panel is on the back of the phone and only lights up once
 * the whole chain works, which makes "nothing happened" impossible to
 * diagnose. This runs the identical renderer on the front display, so
 * a broken animation and a broken Glyph connection look different.
 */
public class MatrixPreview extends View {

    private final Paint dot = new Paint(Paint.ANTI_ALIAS_FLAG);
    private RadarRenderer renderer;
    private int size;
    private long startedAt;

    public MatrixPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        setGrid(Glyph25111pLength());
    }

    /**
     * The preview must work on any phone, including one with no Glyph
     * panel, so it does not ask the SDK for a size.
     */
    private static int Glyph25111pLength() {
        return 13;
    }

    void setGrid(int n) {
        size = n;
        renderer = new RadarRenderer(n);
        startedAt = System.currentTimeMillis();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float t = (System.currentTimeMillis() - startedAt) / 1000f;
        int[] px = renderer.render(t);

        float cell = Math.min(getWidth(), getHeight()) / (float) size;
        float r = cell * 0.36f;
        float ox = (getWidth() - cell * size) / 2f;
        float oy = (getHeight() - cell * size) / 2f;

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                int v = px[y * size + x];
                // Floor the unlit dots so the panel's shape stays
                // visible rather than vanishing into the background.
                int a = Math.max(v, 18);
                dot.setColor(0xFFFFFF | (a << 24));
                canvas.drawCircle(
                        ox + cell * x + cell / 2f,
                        oy + cell * y + cell / 2f,
                        r, dot);
            }
        }

        postInvalidateOnAnimation();
    }
}
