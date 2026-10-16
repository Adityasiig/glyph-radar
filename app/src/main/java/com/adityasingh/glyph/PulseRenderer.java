package com.adityasingh.glyph;

/**
 * Rings expanding from the centre and fading at the rim, like sonar.
 *
 * Designed for 13x13 rather than scaled down to it. A 13 wide panel
 * has only about six pixels of radius, which is too few for detail but
 * plenty for motion, so this spends everything on a clean moving edge:
 * three evenly spaced rings, each fading as it grows, which keeps a
 * bright front travelling outward at all times.
 */
public final class PulseRenderer implements Renderer {

    /** Seconds for one ring to travel centre to rim. */
    private static final float PERIOD = 2.2f;
    private static final int RINGS = 3;

    private final Paint13 p;

    public PulseRenderer(int size) {
        this.p = new Paint13(size);
    }

    @Override
    public int[] render(float t) {
        p.clear();
        float c = p.mid();

        for (int i = 0; i < RINGS; i++) {
            // Stagger the ring phases so one is always near the centre
            // as another reaches the rim.
            float phase = ((t / PERIOD) + (float) i / RINGS) % 1f;
            float radius = phase * c;
            // Fade in quickly, then out towards the rim, so nothing
            // pops into existence at the centre.
            float fade = Math.min(1f, phase * 5f) * (1f - phase * 0.85f);
            p.ring(c, c, radius, 0.25f, fade);
        }

        // A steady emitter at the centre.
        p.dot(c, c, 0.32f, 1f);

        p.clipToCircle();
        return p.out();
    }
}
