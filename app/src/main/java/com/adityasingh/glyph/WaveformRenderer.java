package com.adityasingh.glyph;

/**
 * The site's audio ring: a closed loop deformed by layered sine waves.
 *
 * The site stacks three loops at different alphas. At 13x13 they would
 * overlap into a blob, so this draws one loop and puts the detail into
 * its shape instead, sampling finely enough that the outline stays
 * continuous rather than dashed.
 */
public final class WaveformRenderer implements Renderer {

    private static final float TAU = (float) (Math.PI * 2);
    /** Samples around the loop; enough that adjacent points overlap. */
    private static final int STEPS = 120;

    private final Paint13 p;

    public WaveformRenderer(int size) {
        this.p = new Paint13(size);
    }

    @Override
    public int[] render(float t) {
        p.clear();
        float c = p.mid();
        // The two sine terms can sum to 1.38*amp, so base + 1.38*amp
        // has to stay inside the dial or the peaks get clipped flat.
        float base = c * 0.56f, amp = c * 0.20f;

        float px = 0f, py = 0f;
        for (int i = 0; i <= STEPS; i++) {
            float ang = (float) i / STEPS * TAU;
            // Three lobes plus a faster sixth harmonic, the same pair
            // of terms the site uses, so the silhouette matches.
            float r = base
                    + (float) Math.sin(ang * 3 + t * 1.8f) * amp
                    + (float) Math.sin(ang * 6 - t * 1.1f) * amp * 0.38f;
            float x = c + (float) Math.cos(ang) * r;
            float y = c + (float) Math.sin(ang) * r;
            if (i > 0) p.line(px, py, x, y, 0.2f, 0.95f);
            px = x;
            py = y;
        }

        // A dim core, so the loop reads as enclosing something.
        p.dot(c, c, 0.3f, 0.28f);

        p.clipToCircle();
        return p.out();
    }
}
