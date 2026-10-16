package com.adityasingh.glyph;

/**
 * A tiny software rasteriser for the Glyph Matrix.
 *
 * At 13x13 a shape is only a few pixels across, so hard on/off tests
 * produce visible stair steps and shapes jump between frames instead
 * of moving. Every primitive here takes a distance to the ideal shape
 * and feathers over roughly one pixel, which buys smooth motion and
 * round curves out of a very small grid. Brightness accumulates with
 * max() so overlapping strokes do not blow out.
 */
final class Paint13 {

    private final int size;
    private final float[] buf;

    Paint13(int size) {
        this.size = size;
        this.buf = new float[size * size];
    }

    int size() {
        return size;
    }

    /** Centre coordinate; 6.0 on a 13 wide panel. */
    float mid() {
        return (size - 1) / 2f;
    }

    void clear() {
        java.util.Arrays.fill(buf, 0f);
    }

    /** Adds brightness at a pixel, keeping whichever is brighter. */
    void lite(int x, int y, float v) {
        if (x < 0 || x >= size || y < 0 || y >= size || v <= 0f) return;
        int i = y * size + x;
        if (v > buf[i]) buf[i] = v;
    }

    /**
     * A soft dot. {@code radius} is where brightness reaches zero, so
     * a radius near 0.7 gives a single crisp pixel and larger values
     * spread over neighbours.
     */
    void dot(float cx, float cy, float radius, float v) {
        int x0 = (int) Math.floor(cx - radius - 1), x1 = (int) Math.ceil(cx + radius + 1);
        int y0 = (int) Math.floor(cy - radius - 1), y1 = (int) Math.ceil(cy + radius + 1);
        for (int y = y0; y <= y1; y++) {
            for (int x = x0; x <= x1; x++) {
                float d = (float) Math.hypot(x - cx, y - cy);
                lite(x, y, v * falloff(d, radius));
            }
        }
    }

    /** A line from one point to another, feathered across its width. */
    void line(float ax, float ay, float bx, float by, float width, float v) {
        int x0 = (int) Math.floor(Math.min(ax, bx) - width - 1);
        int x1 = (int) Math.ceil(Math.max(ax, bx) + width + 1);
        int y0 = (int) Math.floor(Math.min(ay, by) - width - 1);
        int y1 = (int) Math.ceil(Math.max(ay, by) + width + 1);
        float dx = bx - ax, dy = by - ay;
        float len2 = dx * dx + dy * dy;
        for (int y = y0; y <= y1; y++) {
            for (int x = x0; x <= x1; x++) {
                float t = len2 == 0f ? 0f
                        : ((x - ax) * dx + (y - ay) * dy) / len2;
                t = Math.max(0f, Math.min(1f, t));
                float d = (float) Math.hypot(x - (ax + dx * t), y - (ay + dy * t));
                lite(x, y, v * falloff(d, width));
            }
        }
    }

    /** A circle outline of the given stroke width. */
    void ring(float cx, float cy, float radius, float width, float v) {
        int x0 = (int) Math.floor(cx - radius - width - 1);
        int x1 = (int) Math.ceil(cx + radius + width + 1);
        int y0 = (int) Math.floor(cy - radius - width - 1);
        int y1 = (int) Math.ceil(cy + radius + width + 1);
        for (int y = y0; y <= y1; y++) {
            for (int x = x0; x <= x1; x++) {
                float d = Math.abs((float) Math.hypot(x - cx, y - cy) - radius);
                lite(x, y, v * falloff(d, width));
            }
        }
    }

    /**
     * Coverage for a point {@code d} away from a shape of half-extent
     * {@code w}, fading out over one pixel past the edge.
     */
    private static float falloff(float d, float w) {
        if (d <= w) return 1f;
        // Feather over half a pixel, not a whole one. A full pixel of
        // bleed on each side turns every 0.5 wide stroke into a 2px
        // smear, which is what merges neighbouring shapes at 13x13.
        if (d >= w + FEATHER) return 0f;
        float k = 1f - (d - w) / FEATHER;
        // Smoothstep, so edges read as round rather than linear ramps.
        return k * k * (3f - 2f * k);
    }

    /** How far past a shape's edge brightness reaches, in pixels. */
    private static final float FEATHER = 0.55f;

    /** Converts the float buffer to the SDK's 0..255 array. */
    int[] out() {
        int[] px = new int[size * size];
        for (int i = 0; i < px.length; i++) {
            px[i] = Math.round(Math.min(1f, Math.max(0f, buf[i])) * 255f);
        }
        return px;
    }

    /** Masks everything outside the circular dial face. */
    void clipToCircle() {
        float c = mid();
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                if (Math.hypot(x - c, y - c) > c + 0.5f) buf[y * size + x] = 0f;
            }
        }
    }
}
