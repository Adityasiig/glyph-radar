package com.adityasingh.glyph;

/**
 * A single smooth curve rolling across the panel.
 *
 * One lit pixel per column, so the panel can never clutter however the
 * wave moves. The wavelength matters more than it looks: a full wave
 * across 13 columns makes neighbouring columns land three or four rows
 * apart, which reads as scattered dots rather than a line. Half a wave
 * at this amplitude keeps every step within one row, so the curve is
 * continuous and the shape is legible.
 *
 * Gaps are bridged explicitly rather than hoped for, so the line stays
 * unbroken on a 25x25 panel too, where the arithmetic works out
 * differently.
 */
public final class WaveRenderer implements Renderer {

    private static final double TAU = Math.PI * 2;
    /** Seconds for the wave to travel one full cycle. */
    private static final float PERIOD = 3.2f;
    /** Wavelengths visible across the panel. */
    private static final double WAVES = 0.5;

    private final Grid g;

    public WaveRenderer(int size) {
        this.g = new Grid(size);
    }

    @Override
    public int[] render(float t) {
        g.clear();
        int S = g.size();
        int c = g.mid();
        // Short of the full radius, so crests sit inside the panel and
        // consecutive columns stay within a row of each other.
        int amp = Math.max(1, (int) Math.round(c * 0.68));

        // A faint centre line, so the wave has a zero to swing about
        // and the panel reads as an instrument rather than a scatter.
        g.row(c, Grid.DIM);

        int prev = Integer.MIN_VALUE;
        for (int x = 0; x < S; x++) {
            double ang = (double) x / S * TAU * WAVES - (t / PERIOD) * TAU;
            int y = c - (int) Math.round(Math.sin(ang) * amp);

            // Fill any vertical gap left by the previous column, so the
            // curve is a connected line at every size and phase.
            if (prev != Integer.MIN_VALUE && Math.abs(y - prev) > 1) {
                int step = y > prev ? 1 : -1;
                for (int fy = prev + step; fy != y; fy += step) {
                    g.put(x, fy, Grid.MID);
                }
            }

            // Crest and trough brightest: the extremes are what the eye
            // tracks, so letting them lead gives the wave a shape
            // instead of a uniform ribbon.
            boolean extreme = Math.abs(y - c) >= amp - 1;
            g.put(x, y, extreme ? Grid.MAX : Grid.HIGH);
            prev = y;
        }

        return g.out();
    }
}
