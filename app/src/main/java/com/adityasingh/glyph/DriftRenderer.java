package com.adityasingh.glyph;

/**
 * A few points drifting slowly, each fading in and out on its own
 * cycle. A quiet starfield.
 *
 * The calmest thing in the set and the least like a gadget. It works
 * because it never asks the grid to draw a shape: single pixels are
 * the one thing a 13x13 panel renders perfectly, so sparse and slow
 * is exactly where it looks best.
 *
 * Positions come from a fixed hash rather than a random source, so the
 * field is the same every run and the checks in tools/ stay
 * reproducible.
 */
public final class DriftRenderer implements Renderer {

    private static final double TAU = Math.PI * 2;
    private static final int STARS = 7;

    private final Grid g;

    public DriftRenderer(int size) {
        this.g = new Grid(size);
    }

    @Override
    public int[] render(float t) {
        g.clear();
        int S = g.size();

        for (int i = 0; i < STARS; i++) {
            // Each star drifts along its own slow diagonal and wraps.
            float sx = 0.07f + hash(i) * 0.1f;
            float sy = 0.05f + hash(i * 3 + 1) * 0.09f;
            float x0 = hash(i * 5 + 2) * S;
            float y0 = hash(i * 7 + 3) * S;

            int x = (int) ((x0 + t * sx * S) % S);
            int y = (int) ((y0 + t * sy * S) % S);

            // Independent fade cycles, so the field never pulses as a
            // group, which would read as a flash rather than a drift.
            float period = 3.5f + hash(i * 11 + 4) * 4f;
            double ph = (t / period + hash(i * 13 + 5)) % 1.0;
            float lum = (float) ((Math.sin(ph * TAU) + 1) / 2);

            int v = lum > 0.85f ? Grid.MAX
                    : lum > 0.55f ? Grid.HIGH
                    : lum > 0.25f ? Grid.MID : Grid.DIM;
            g.set(x, y, v);
        }

        return g.out();
    }

    /** Stable pseudo random in 0..1, so frames are reproducible. */
    private static float hash(int n) {
        int h = n * 374761393 + 668265263;
        h = (h ^ (h >> 13)) * 1274126177;
        return ((h ^ (h >> 16)) & 0xffff) / 65535f;
    }
}
