package com.adityasingh.glyph;

/**
 * A single point tracing the edge of a diamond, with a short tail.
 *
 * One lit dot moving along a path every pixel of which is exact. The
 * earlier square-track version had two comets and a lattice, which is
 * busy; this keeps one traveller and a faint path, so the eye follows
 * the motion rather than scanning the panel.
 *
 * A diamond is used rather than a square because its perimeter is the
 * one closed loop this grid renders with even spacing at every radius.
 */
public final class OrbitRenderer implements Renderer {

    /** Seconds per pixel step around the loop. */
    private static final float STEP = 0.11f;
    private static final int TAIL = 5;

    private final Grid g;
    private final int[][] path;

    public OrbitRenderer(int size) {
        this.g = new Grid(size);
        this.path = diamondPath(size / 2, size / 2, size / 2);
    }

    /**
     * The diamond's pixels in travel order, clockwise from the top.
     *
     * Walked edge by edge rather than collected by distance test, so
     * consecutive entries are always adjacent and the dot never jumps.
     */
    private static int[][] diamondPath(int cx, int cy, int r) {
        int[][] pts = new int[4 * r][2];
        int i = 0;
        for (int d = 0; d < r; d++) pts[i++] = new int[]{cx + d, cy - r + d};
        for (int d = 0; d < r; d++) pts[i++] = new int[]{cx + r - d, cy + d};
        for (int d = 0; d < r; d++) pts[i++] = new int[]{cx - d, cy + r - d};
        for (int d = 0; d < r; d++) pts[i++] = new int[]{cx - r + d, cy - d};
        return pts;
    }

    @Override
    public int[] render(float t) {
        g.clear();
        int c = g.mid();

        // The path, barely lit, so the route is implied rather than
        // drawn. This is what keeps it from reading as a busy outline.
        for (int[] pt : path) g.set(pt[0], pt[1], Grid.DIM);

        int n = path.length;
        int head = (int) (t / STEP) % n;
        for (int i = 0; i < TAIL; i++) {
            int[] pt = path[((head - i) % n + n) % n];
            int v = i == 0 ? Grid.MAX
                    : i == 1 ? Grid.HIGH
                    : i == 2 ? Grid.MID : Grid.DIM;
            g.put(pt[0], pt[1], v);
        }

        // A still centre, so the travel reads against something fixed.
        g.set(c, c, Grid.MID);

        return g.out();
    }
}
