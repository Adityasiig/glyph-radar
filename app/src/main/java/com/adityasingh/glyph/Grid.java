package com.adityasingh.glyph;

/**
 * A pixel buffer for designs drawn <em>on</em> the grid rather than
 * sampled onto it.
 *
 * {@link Paint13} rasterises analog shapes with sub pixel coverage,
 * which is the right tool for a big canvas and the wrong one here. A
 * 13 wide panel holds 137 pixels inside the dial, and the number of
 * pixels forming a circle of radius r runs 8, 12, 16, 32, 28, 40: not
 * even monotonic, so concentric rings cannot be spaced evenly and a
 * rotating arm cannot sweep smoothly. Feathered edges spend the panel's
 * tiny brightness budget on half lit pixels that read as mud.
 *
 * So this class offers no curves and no anti aliasing. Everything is
 * addressed by integer pixel, brightness is picked from a few fixed
 * levels, and designs are built from rows, columns, blocks and
 * symmetry, which is what the grid can actually show.
 */
final class Grid {

    /**
     * Fixed brightness levels. Keeping to a handful of well separated
     * values is what makes a 13x13 image read as deliberate; smooth
     * ramps just look dim and muddy.
     */
    static final int OFF = 0;
    static final int DIM = 28;
    static final int MID = 90;
    static final int HIGH = 170;
    static final int MAX = 255;

    private final int size;
    private final int[] px;

    Grid(int size) {
        this.size = size;
        this.px = new int[size * size];
    }

    int size() {
        return size;
    }

    /** Centre index. 6 on a 13 wide panel, so designs can be exact. */
    int mid() {
        return size / 2;
    }

    void clear() {
        java.util.Arrays.fill(px, OFF);
    }

    /** Sets a pixel, keeping whichever value is brighter. */
    void set(int x, int y, int v) {
        if (x < 0 || x >= size || y < 0 || y >= size) return;
        int i = y * size + x;
        if (v > px[i]) px[i] = v;
    }

    /** Overwrites a pixel even if that dims it. */
    void put(int x, int y, int v) {
        if (x < 0 || x >= size || y < 0 || y >= size) return;
        px[y * size + x] = v;
    }

    int get(int x, int y) {
        if (x < 0 || x >= size || y < 0 || y >= size) return OFF;
        return px[y * size + x];
    }

    void row(int y, int v) {
        for (int x = 0; x < size; x++) set(x, y, v);
    }

    void col(int x, int v) {
        for (int y = 0; y < size; y++) set(x, y, v);
    }

    /** A filled rectangle in pixel coordinates, inclusive. */
    void rect(int x0, int y0, int x1, int y1, int v) {
        for (int y = y0; y <= y1; y++) {
            for (int x = x0; x <= x1; x++) set(x, y, v);
        }
    }

    /** A rectangle outline, inclusive. */
    void frame(int x0, int y0, int x1, int y1, int v) {
        for (int x = x0; x <= x1; x++) {
            set(x, y0, v);
            set(x, y1, v);
        }
        for (int y = y0; y <= y1; y++) {
            set(x0, y, v);
            set(x1, y, v);
        }
    }

    /**
     * A diamond outline at Chebyshev-free L1 distance {@code r}.
     *
     * The grid renders |dx|+|dy| exactly, so a diamond is the one
     * closed curve that comes out even at every radius. It is the
     * honest substitute for a circle at this size.
     */
    void diamond(int cx, int cy, int r, int v) {
        if (r <= 0) {
            set(cx, cy, v);
            return;
        }
        // Walk every pixel whose L1 distance is exactly r. Stepping d
        // along one axis and r-d along the other visits each of the 4r
        // pixels once, with no gaps and no duplicates at the tips.
        for (int d = 0; d <= r; d++) {
            int o = r - d;
            set(cx + d, cy + o, v);
            set(cx + d, cy - o, v);
            set(cx - d, cy + o, v);
            set(cx - d, cy - o, v);
        }
    }

    /**
     * A diamond whose edge is solid rather than a dotted outline.
     *
     * Two diamonds whose radii differ by more than one only touch at
     * their tips, so a ring drawn at every other radius reads as four
     * dashed diagonals. Filling the band between r and r-(w-1) closes
     * those gaps and gives a front thick enough to see move.
     */
    void diamondBand(int cx, int cy, int r, int w, int v) {
        for (int k = 0; k < w; k++) {
            int rr = r - k;
            if (rr < 0) break;
            diamond(cx, cy, rr, v);
        }
    }

    /** Mirrors the left half onto the right, for exact symmetry. */
    void mirrorX() {
        int last = size - 1;
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size / 2; x++) {
                put(last - x, y, get(x, y));
            }
        }
    }

    /** Mirrors the top half onto the bottom. */
    void mirrorY() {
        int last = size - 1;
        for (int y = 0; y < size / 2; y++) {
            for (int x = 0; x < size; x++) {
                put(x, last - y, get(x, y));
            }
        }
    }

    int[] out() {
        return px.clone();
    }
}
