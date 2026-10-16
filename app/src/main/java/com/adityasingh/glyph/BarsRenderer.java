package com.adityasingh.glyph;

/**
 * A level meter: thirteen columns, each a solid bar rising from the
 * bottom row.
 *
 * This is the shape the grid is best at. A bar is an exact run of
 * pixels in one column, so there is nothing to round off and nothing
 * to anti alias; height is the only variable and the eye reads it
 * instantly. Columns are one pixel wide with no gaps, which turns the
 * whole panel into the display rather than leaving it mostly dark.
 */
public final class BarsRenderer implements Renderer {

    private final Grid g;

    public BarsRenderer(int size) {
        this.g = new Grid(size);
    }

    @Override
    public int[] render(float t) {
        g.clear();
        int S = g.size();

        for (int x = 0; x < S; x++) {
            // Two detuned waves per column so the pattern breathes
            // instead of marching in step.
            double a = Math.sin(t * 2.1 + x * 0.55);
            double b = Math.sin(t * 1.3 - x * 0.31);
            float level = (float) ((a * 0.6 + b * 0.4) * 0.5 + 0.5);

            // At least one pixel, so the floor of the meter is always
            // a visible baseline rather than a gap.
            int h = 1 + Math.round(level * (S - 2));

            for (int i = 0; i < h; i++) {
                int y = S - 1 - i;
                // The top pixel of each bar is brightest, so the
                // silhouette stays crisp against its neighbours.
                g.put(x, y, i == h - 1 ? Grid.MAX
                        : i >= h - 3 ? Grid.HIGH : Grid.MID);
            }
        }

        return g.out();
    }
}
