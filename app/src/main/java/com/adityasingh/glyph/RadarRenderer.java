package com.adityasingh.glyph;

/**
 * The portfolio's hero radar, rendered for the Glyph Matrix.
 *
 * This is a direct port of the canvas renderer on the website: polar
 * rings, a rotating arm with a fading trail, and blips that light as
 * the arm passes. The only change is resolution. The site draws 25x25;
 * the Phone (4a) Pro panel is 13x13, so the ring count drops from
 * three to two and the blips move inward to stay on the face.
 */
public final class RadarRenderer {

    private static final float TAU = (float) (Math.PI * 2);
    /** The SDK takes 0..255 per pixel. */
    private static final int MAX = 255;

    /** One full revolution, matching the 0.9 rad/s of the web version. */
    private static final float SWEEP_RATE = 0.9f;

    /** Blips as {radiusFraction, bearingRadians}, as on the site. */
    private static final float[][] BLIPS = {
            {0.70f, 1.1f}, {0.45f, 3.0f}, {0.85f, 4.4f}, {0.30f, 5.6f}
    };

    /** 13 on the Phone (4a) Pro, 25 on the Phone (3). */
    private final int size;
    private final int[] pixels;

    public RadarRenderer(int size) {
        this.size = size;
        this.pixels = new int[size * size];
    }

    /**
     * Renders one frame.
     *
     * @param tSeconds elapsed time; the only state the animation needs
     * @return a size*size array of 0..255 brightnesses, row-major
     */
    public int[] render(float tSeconds) {
        final float c = (size - 1) / 2f;
        final float maxR = c;
        final float sweep = (tSeconds * SWEEP_RATE) % TAU;

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                float dx = x - c, dy = y - c;
                float dist = (float) Math.hypot(dx, dy);

                // Outside the dial face stays dark.
                if (dist > maxR + 0.5f) {
                    pixels[y * size + x] = 0;
                    continue;
                }

                // Faint grid: two concentric rings plus cross hairs, so
                // the face reads as an instrument when the arm is away.
                float v = 0.06f;
                for (int r = 1; r <= 2; r++) {
                    float ringR = maxR * r / 2f;
                    if (Math.abs(dist - ringR) < 0.5f) v = Math.max(v, 0.16f);
                }
                if (Math.abs(dx) < 0.5f || Math.abs(dy) < 0.5f) v = Math.max(v, 0.14f);

                // The sweep: brightest at the arm, fading behind it.
                if (dist > 0.6f) {
                    float ang = (float) Math.atan2(dy, dx);
                    float behind = sweep - ang;
                    behind = ((behind % TAU) + TAU) % TAU;
                    if (behind < 1.6f) {
                        v = Math.max(v, (1f - behind / 1.6f) * 0.95f);
                    }
                }

                pixels[y * size + x] = Math.round(Math.min(v, 1f) * MAX);
            }
        }

        // Blips sit on top, flaring as the arm crosses their bearing.
        for (float[] blip : BLIPS) {
            float bx = c + (float) Math.cos(blip[1]) * maxR * blip[0];
            float by = c + (float) Math.sin(blip[1]) * maxR * blip[0];
            int px = Math.round(bx), py = Math.round(by);
            if (px < 0 || px >= size || py < 0 || py >= size) continue;

            float behind = sweep - blip[1];
            behind = ((behind % TAU) + TAU) % TAU;
            float flare = Math.max(0f, 1f - behind / 1.5f);

            int lit = Math.round((0.12f + flare * 0.88f) * MAX);
            int idx = py * size + px;
            if (lit > pixels[idx]) pixels[idx] = lit;
        }

        return pixels;
    }
}
