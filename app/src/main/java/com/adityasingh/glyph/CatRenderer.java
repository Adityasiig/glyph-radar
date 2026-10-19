package com.adityasingh.glyph;

/**
 * A cat sitting in profile, wiggling its tail.
 *
 * The body is a hand placed bitmap, because a sitting cat is a shape
 * the eye either recognises or does not; there is no formula for it at
 * this size. The tail is drawn per frame instead, so it can move.
 *
 * The silhouette sits left of centre on purpose, leaving the right of
 * the panel clear for the tail to sweep through. The tail root is the
 * first cell outside the body rather than inside it, so the tail
 * visibly grows out of the rump instead of appearing detached.
 */
public final class CatRenderer implements Renderer {

    /** Seconds for one full flick out and back. */
    private static final float SWING_PERIOD = 2.6f;
    /** How far the tip swings either side of rest, in radians. */
    private static final float SWING = 0.95f;
    /** Resting angle of the tail, measured up from horizontal. */
    private static final float REST = 1.05f;
    /** Samples along the tail; finer than one per pixel on purpose. */
    private static final int SAMPLES = 22;

    /**
     * The cat, authored for a 13 wide panel. Rows top to bottom, '#'
     * lit. Gaps at the eye row are the eyes: at this size leaving
     * pixels unlit reads better than trying to draw them.
     */
    private static final String[] ART = {
            "             ",
            "  #   #      ",
            "  ## ##      ",
            "  #####      ",
            "  # # #      ",
            "  #####      ",
            "   ###       ",
            "  #####      ",
            " #######     ",
            " #######     ",
            "#########    ",
            "#########    ",
            " #######     "
    };

    private final Grid g;
    private final int scale;
    private final int offset;

    public CatRenderer(int size) {
        this.g = new Grid(size);
        // Whole number scaling only, so a 25x25 panel gets a crisp
        // double size cat rather than a blurry stretched one.
        this.scale = Math.max(1, size / ART.length);
        this.offset = (size - ART.length * scale) / 2;
    }

    @Override
    public int[] render(float t) {
        g.clear();
        drawBody();

        float phase = t / SWING_PERIOD * (float) (Math.PI * 2);
        // A plain sine flattens at each end of the swing, so the tail
        // sat on the same pixels for several frames and read as a
        // stutter. Cubing speeds it through the extremes while keeping
        // the turn smooth, which looks like a flick rather than a
        // pendulum.
        float s = (float) Math.sin(phase);
        drawTail(s * s * s);

        return g.out();
    }

    private void drawBody() {
        for (int y = 0; y < ART.length; y++) {
            String row = ART[y];
            for (int x = 0; x < row.length(); x++) {
                if (row.charAt(x) != '#') continue;
                block(x, y, Grid.MAX);
            }
        }
    }

    /**
     * Draws the tail for a swing in -1..1.
     *
     * Sampled finely and then joined point to point: the arc can cross
     * a diagonal between samples, and without bridging those the tail
     * breaks into dashes at the far end of the swing.
     */
    private void drawTail(float swing) {
        int rootX = 9, rootY = 11;
        int px = rootX, py = rootY;

        for (int i = 1; i <= SAMPLES; i++) {
            float s = i * 0.28f;
            // The swing grows along the length, so the tip travels far
            // while the root barely moves, the way a real tail does.
            float a = REST + swing * SWING * (s / 5.5f);
            int x = Math.round(rootX + (float) Math.cos(a) * s * 1.15f);
            int y = Math.round(rootY - (float) Math.sin(a) * s);
            line(px, py, x, y);
            px = x;
            py = y;
        }
    }

    /** Lights every cell between two points, inclusive. */
    private void line(int x0, int y0, int x1, int y1) {
        int n = Math.max(Math.abs(x1 - x0), Math.abs(y1 - y0));
        if (n == 0) {
            block(x0, y0, Grid.HIGH);
            return;
        }
        for (int i = 0; i <= n; i++) {
            block(Math.round(x0 + (x1 - x0) * (float) i / n),
                    Math.round(y0 + (y1 - y0) * (float) i / n),
                    Grid.HIGH);
        }
    }

    /** Plots one bitmap cell, honouring the panel scale. */
    private void block(int x, int y, int v) {
        for (int dy = 0; dy < scale; dy++) {
            for (int dx = 0; dx < scale; dx++) {
                g.set(offset + x * scale + dx, offset + y * scale + dy, v);
            }
        }
    }
}
