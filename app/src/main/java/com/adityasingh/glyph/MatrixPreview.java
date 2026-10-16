package com.adityasingh.glyph;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Draws a {@link Renderer} on screen as a grid of dots.
 *
 * The Glyph panel is on the back of the phone and only lights once the
 * whole chain works, which makes "nothing happened" impossible to
 * diagnose. This runs the identical renderer on the front display, so
 * a broken animation and a broken Glyph connection look different.
 */
public class MatrixPreview extends View {

    /** Fallback when the SDK cannot name the panel: the (4a) Pro's. */
    private static final int DEFAULT_SIZE = 13;

    private final Paint dot = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Renderer renderer;
    private int size = DEFAULT_SIZE;
    private long startedAt = System.currentTimeMillis();

    public MatrixPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        renderer = new RadarRenderer(size);
    }

    /** Switches which toy is previewed, restarting its clock. */
    void show(Toys.Toy toy, int panelSize) {
        size = panelSize > 0 ? panelSize : DEFAULT_SIZE;
        renderer = toy.factory.create(size);
        startedAt = System.currentTimeMillis();
        invalidate();
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
                // Floor the unlit dots so the panel's shape stays
                // visible rather than vanishing into the background.
                int a = Math.max(px[y * size + x], 18);
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
