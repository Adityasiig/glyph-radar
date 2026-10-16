package com.adityasingh.glyph;

/**
 * A heart drawn pixel by pixel, beating.
 *
 * Proof that the grid is better at icons than at instruments: this is
 * legible at a glance where a sampled curve of the same size is not.
 * The shape is a hand placed bitmap, mirrored for exact symmetry, and
 * it pulses on a double thump rhythm rather than a sine so it reads as
 * a heartbeat instead of a throb.
 */
public final class HeartRenderer implements Renderer {

    /** Seconds per beat. */
    private static final float BEAT = 1.1f;

    /**
     * The left half of the heart, including the centre column. Rows
     * are top to bottom, columns left to right, '#' lit. Mirroring
     * the right half guarantees symmetry no formula would give.
     */
    private static final String[] HALF = {
            "       ",
            "  ###  ",
            " ######",
            "#######",
            "#######",
            "#######",
            " ######",
            "  #####",
            "   ####",
            "    ###",
            "     ##",
            "      #",
            "       "
    };

    private final Grid g;

    public HeartRenderer(int size) {
        this.g = new Grid(size);
    }

    @Override
    public int[] render(float t) {
        g.clear();
        int S = g.size();

        // Two quick thumps then a rest, the shape of a real pulse.
        float phase = (t / BEAT) % 1f;
        float pulse = thump(phase);

        // The beat changes brightness, not size: growing a 13 pixel
        // heart by a pixel distorts the silhouette, while brightness
        // scales cleanly. This is the one place a continuous ramp is
        // right, because it is the whole shape lifting together rather
        // than an edge being approximated, so it is not quantised to
        // the fixed levels.
        int bright = Grid.MID + Math.round(pulse * (Grid.MAX - Grid.MID));
        int edge = Grid.DIM + Math.round(pulse * (Grid.MID - Grid.DIM));

        // The bitmap is authored for a 13 wide panel. Scale it by a
        // whole number and centre it so a 25x25 Phone (3) panel gets a
        // crisp double size heart rather than a small one wedged into
        // the corner; fractional scaling would reintroduce exactly the
        // blurry edges this design exists to avoid.
        // The art occupies 12 of its 13 rows, so scale against that
        // rather than the array length: at 25 wide, 25/13 floors to 1
        // and the heart would stay single size on a panel with room
        // for double.
        int art = 12;
        int scale = Math.max(1, S / art);
        int offset = (S - art * scale) / 2;

        for (int y = 0; y < art; y++) {
            String line = HALF[y];
            for (int x = 0; x < line.length(); x++) {
                if (line.charAt(x) != '#') continue;
                for (int dy = 0; dy < scale; dy++) {
                    for (int dx = 0; dx < scale; dx++) {
                        g.set(offset + x * scale + dx,
                                offset + y * scale + dy, bright);
                    }
                }
            }
        }
        g.mirrorX();

        // Dim whichever lit pixels sit on the silhouette, so the heart
        // has an edge rather than reading as a flat blob. Done after
        // mirroring, against the finished shape, because a pixel on
        // the centre column is interior only once both halves exist.
        for (int y = 0; y < S; y++) {
            for (int x = 0; x < S; x++) {
                if (g.get(x, y) == 0) continue;
                boolean interior = g.get(x - 1, y) > 0 && g.get(x + 1, y) > 0
                        && g.get(x, y - 1) > 0 && g.get(x, y + 1) > 0;
                if (!interior) g.put(x, y, edge);
            }
        }

        return g.out();
    }

    /** Double thump: a strong beat, a weaker one, then quiet. */
    private static float thump(float p) {
        if (p < 0.12f) return (float) Math.sin(p / 0.12f * Math.PI);
        if (p < 0.20f) return 0f;
        if (p < 0.32f) return 0.55f * (float) Math.sin((p - 0.20f) / 0.12f * Math.PI);
        return 0f;
    }
}
