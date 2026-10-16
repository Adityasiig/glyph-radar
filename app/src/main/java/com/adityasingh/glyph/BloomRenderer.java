package com.adityasingh.glyph;

/**
 * Diamonds expanding from the centre, one pixel per step.
 *
 * The honest replacement for the old sonar rings. A circle of radius r
 * lands on 8, 12, 16, 32, 28, 40 pixels as r grows, so circles cannot
 * be spaced evenly here; a diamond is exactly 4r pixels at every
 * radius, so these steps are uniform and the growth reads as steady.
 *
 * Rings move outward one whole pixel at a time rather than sliding, so
 * every frame is a clean shape instead of a half lit approximation.
 */
public final class BloomRenderer implements Renderer {

    /** Seconds per one pixel step outward. */
    private static final float STEP = 0.10f;

    private final Grid g;
    private final int maxR;

    public BloomRenderer(int size) {
        this.g = new Grid(size);
        // The furthest diamond that still fits the panel corner to
        // corner is the centre index itself.
        this.maxR = size / 2;
    }

    @Override
    public int[] render(float t) {
        g.clear();
        int c = g.mid();
        int span = maxR + 1;

        // Two fronts in flight, half a span apart, so one is always
        // leaving the centre as the other reaches the edge. Two rather
        // than three: a 6 pixel radius cannot show more without them
        // running together.
        int steps = (int) (t / STEP);
        for (int k = 0; k < 2; k++) {
            int r = ((steps + k * span / 2) % span);
            // Fade with distance so the newest front is brightest and
            // the older one reads as a trailing echo.
            int v = r <= 2 ? Grid.MAX
                    : r <= maxR - 2 ? Grid.HIGH
                    : Grid.MID;
            // Two pixels thick, so the ring is a solid front instead
            // of four dashed diagonals.
            g.diamondBand(c, c, r, 2, v);
        }

        // A permanent core, so the panel never looks empty between
        // fronts and the growth has an origin.
        g.set(c, c, Grid.MAX);

        return g.out();
    }
}
