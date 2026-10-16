package com.adityasingh.glyph;

/**
 * A block of light running around the edge of a square ring.
 *
 * Rotation done the way the grid allows: instead of rotating a line
 * through arbitrary angles, which staircases, a marker walks the
 * perimeter one pixel at a time. Every frame is exact, the motion is
 * perfectly even, and it still reads as something going round.
 */
public final class SpinRenderer implements Renderer {

    /** Seconds per pixel step around the ring. */
    private static final float STEP = 0.085f;
    /** Length of the bright comet, in pixels of perimeter. */
    private static final int COMET = 13;

    private final Grid g;
    /** Outer ring, and an inner one running the other way. */
    private final int[][] path;
    private final int[][] inner;

    public SpinRenderer(int size) {
        this.g = new Grid(size);
        // Inset by one. The SDK hands out a full square array, but the
        // panel's corner pixels may not be visible on the phone, and a
        // track one pixel in reads the same either way.
        this.path = perimeter(1, 1, size - 2, size - 2);
        this.inner = perimeter(4, 4, size - 5, size - 5);
    }

    /**
     * The ring's pixels in travel order, clockwise from the top left.
     * Precomputed so each frame is a lookup rather than a shape test,
     * which is what keeps the spacing exactly even.
     */
    private static int[][] perimeter(int x0, int y0, int x1, int y1) {
        int n = 2 * (x1 - x0) + 2 * (y1 - y0);
        int[][] pts = new int[n][2];
        int i = 0;
        for (int x = x0; x < x1; x++) pts[i++] = new int[]{x, y0};
        for (int y = y0; y < y1; y++) pts[i++] = new int[]{x1, y};
        for (int x = x1; x > x0; x--) pts[i++] = new int[]{x, y1};
        for (int y = y1; y > y0; y--) pts[i++] = new int[]{x0, y};
        return pts;
    }

    @Override
    public int[] render(float t) {
        g.clear();
        int c = g.mid();

        // Both tracks, so each comet has a visible path to run on.
        for (int[] pt : path) g.set(pt[0], pt[1], Grid.DIM);
        for (int[] pt : inner) g.set(pt[0], pt[1], Grid.DIM);

        int steps = (int) (t / STEP);
        comet(path, steps, COMET, false);
        // The inner ring runs backwards and a little slower, which
        // gives the two tracks a shifting relationship instead of a
        // single mark going round an empty panel.
        comet(inner, (int) (steps * 0.7f), 7, true);

        // A still centre mark, so the rotation has a pivot to read
        // against and the middle of the panel is not dead space.
        g.set(c, c, Grid.HIGH);

        return g.out();
    }

    /** Draws a fading comet of {@code len} pixels along {@code track}. */
    private void comet(int[][] track, int steps, int len, boolean reverse) {
        int n = track.length;
        int head = ((reverse ? -steps : steps) % n + n) % n;
        for (int i = 0; i < len; i++) {
            int idx = reverse
                    ? (head + i) % n
                    : ((head - i) % n + n) % n;
            int[] pt = track[idx];
            float k = 1f - (float) i / len;
            int v = k > 0.85f ? Grid.MAX
                    : k > 0.6f ? Grid.HIGH
                    : k > 0.3f ? Grid.MID : Grid.DIM;
            g.put(pt[0], pt[1], v);
        }
    }
}
