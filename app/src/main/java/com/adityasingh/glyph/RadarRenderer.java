package com.adityasingh.glyph;

/**
 * The portfolio's hero radar: polar rings, a sweeping arm with a
 * fading trail, and blips that flare as the arm crosses them.
 *
 * The site draws three rings on a large canvas. Two is the most that
 * stays separable at 13x13, so the middle one is dropped rather than
 * letting them merge into a smear.
 */
public final class RadarRenderer implements Renderer {

    private static final float TAU = (float) (Math.PI * 2);
    private static final float SWEEP_RATE = 0.9f;
    /** How far behind the arm the trail reaches, in radians. */
    private static final float TRAIL = 1.7f;

    /** Blips as {radiusFraction, bearingRadians}, as on the site. */
    private static final float[][] BLIPS = {
            {0.72f, 1.1f}, {0.46f, 3.0f}, {0.86f, 4.4f}, {0.32f, 5.6f}
    };

    private final Paint13 p;

    public RadarRenderer(int size) {
        this.p = new Paint13(size);
    }

    @Override
    public int[] render(float t) {
        p.clear();
        float c = p.mid(), R = c;
        float sweep = (t * SWEEP_RATE) % TAU;

        // The dial: two rings plus cross hairs, dim enough to sit
        // under the sweep but bright enough to survive it.
        p.ring(c, c, R, 0.22f, 0.3f);
        p.ring(c, c, R * 0.5f, 0.22f, 0.25f);
        p.line(c - R, c, c + R, c, 0.18f, 0.2f);
        p.line(c, c - R, c, c + R, 0.18f, 0.2f);

        // The trail, as spokes fading behind the arm. Squared falloff
        // keeps the tail dim so the dial stays readable through it.
        for (int i = 1; i <= 12; i++) {
            float k = 1f - i / 12f;
            float ang = sweep - (i / 12f) * TRAIL;
            p.line(c, c,
                    c + (float) Math.cos(ang) * R,
                    c + (float) Math.sin(ang) * R,
                    0.25f, k * k * 0.6f);
        }
        // The arm itself.
        p.line(c, c,
                c + (float) Math.cos(sweep) * R,
                c + (float) Math.sin(sweep) * R,
                0.28f, 1f);

        // Contacts, holding a little longer than the trail so one
        // stays readable after the arm has moved on.
        for (float[] b : BLIPS) {
            float behind = ((sweep - b[1]) % TAU + TAU) % TAU;
            float flare = Math.max(0f, 1f - behind / (TRAIL * 1.5f));
            p.dot(c + (float) Math.cos(b[1]) * R * b[0],
                    c + (float) Math.sin(b[1]) * R * b[0],
                    0.35f + flare * 0.45f,
                    0.2f + flare * 0.8f);
        }

        p.clipToCircle();
        return p.out();
    }
}
