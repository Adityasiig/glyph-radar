package com.adityasingh.glyph;

/**
 * A cat sitting in profile, flicking its tail.
 *
 * The first version drew the cat as a flat silhouette and it read as a
 * lump. The fix was to stop treating the panel as one bit per pixel:
 * the matrix takes a brightness per dot, so the body is shaded in
 * bands, dim for the haunch, brighter for the chest, brightest for the
 * head. That is what separates the parts of the animal and gives the
 * form depth, and it is the only way to get detail out of a shape this
 * small.
 *
 * Proportions matter more than outline at 13 wide. A sitting cat is
 * roughly one head to two and a half body, with a narrow neck and a
 * broad haunch, so the head here is three pixels across and the haunch
 * eight. Making the head any bigger reads as a rabbit.
 *
 * The tail is drawn per frame so it can move, and is brighter than the
 * body so it stays readable when it sweeps back across the haunch.
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

    /** Where the tail leaves the body: first free cell by the haunch. */
    private static final int ROOT_X = 9;
    private static final int ROOT_Y = 11;

    /**
     * The cat, authored for a 13 wide panel. Digits are brightness
     * bands rather than an on/off mask: '2' haunch, '3' chest and
     * neck, '4' head. Gaps in the head row are the eyes; at this size
     * leaving pixels unlit reads better than trying to draw them.
     */
    private static final String[] ART = {
            "             ",
            "   4 4       ",
            "   444       ",
            "   4 4       ",
            "    3        ",
            "   333       ",
            "   3333      ",
            "  33333      ",
            "  233333     ",
            "  2222222    ",
            " 22222222    ",
            " 22222222    ",
            "  2222222    "
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
                int v = level(row.charAt(x));
                if (v != Grid.OFF) block(x, y, v);
            }
        }
    }

    /** Maps a bitmap digit to one of the panel's brightness bands. */
    private static int level(char c) {
        switch (c) {
            case '2': return Grid.MID;
            case '3': return Grid.HIGH;
            case '4': return Grid.MAX;
            default: return Grid.OFF;
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
        int px = ROOT_X, py = ROOT_Y;
        int drawn = 0;

        for (int i = 1; i <= SAMPLES; i++) {
            float s = i * 0.28f;
            // The swing grows along the length, so the tip travels far
            // while the root barely moves, the way a real tail does.
            float a = REST + swing * SWING * (s / 5.5f);
            int x = Math.round(ROOT_X + (float) Math.cos(a) * s * 1.15f);
            int y = Math.round(ROOT_Y - (float) Math.sin(a) * s);
            // The first stretch sits against the haunch, so keep it a
            // band dimmer; past that the tail is in open space and
            // goes full brightness to stay clear of the body behind it.
            drawn = line(px, py, x, y, drawn);
            px = x;
            py = y;
        }
    }

    /**
     * Lights every cell between two points, inclusive.
     *
     * @param drawn how many tail pixels precede this segment
     * @return the running count, so brightness can follow the length
     */
    private int line(int x0, int y0, int x1, int y1, int drawn) {
        int n = Math.max(Math.abs(x1 - x0), Math.abs(y1 - y0));
        for (int i = 0; i <= n; i++) {
            int x = n == 0 ? x0 : Math.round(x0 + (x1 - x0) * (float) i / n);
            int y = n == 0 ? y0 : Math.round(y0 + (y1 - y0) * (float) i / n);
            block(x, y, drawn > 2 ? Grid.MAX : Grid.HIGH);
            drawn++;
            if (n == 0) break;
        }
        return drawn;
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
