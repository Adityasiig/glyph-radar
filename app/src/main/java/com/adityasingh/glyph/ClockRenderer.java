package com.adityasingh.glyph;

import java.util.Calendar;

/**
 * The real time, as a minimal analog face.
 *
 * Four quarter marks, an hour hand and a minute hand. Nothing else: no
 * ring, no numerals, no second hand. At 13x13 a hand is only six pixels
 * long, so the dial has to give those six pixels the whole panel to
 * speak in.
 *
 * Hands are drawn by stepping outward from the centre and rounding each
 * step to a pixel, rather than rasterising a line, so a hand is always
 * a connected run with no gaps or stray pixels at any angle.
 */
public final class ClockRenderer implements Renderer {

    private static final double TAU = Math.PI * 2;

    private final Grid g;

    public ClockRenderer(int size) {
        this.g = new Grid(size);
    }

    @Override
    public int[] render(float t) {
        g.clear();
        int c = g.mid();
        int r = c;

        Calendar now = Calendar.getInstance();
        int hour = now.get(Calendar.HOUR);
        int min = now.get(Calendar.MINUTE);

        // 12 o'clock is up, and clockwise is positive, which is a
        // quarter turn back from the usual atan2 convention.
        double hourAngle = (hour + min / 60.0) / 12.0 * TAU - Math.PI / 2;
        double minAngle = min / 60.0 * TAU - Math.PI / 2;

        hand(c, minAngle, r - 1, Grid.MAX);
        hand(c, hourAngle, r - 3, Grid.HIGH);

        // Quarter marks only; twelve would ring the whole edge and
        // leave the hands nothing to stand out against. Drawn last but
        // only onto empty pixels: set() would lose a mark under a hand
        // lying along an axis, and put() would dim that hand's own tip.
        mark(c, c - r);
        mark(c, c + r);
        mark(c - r, c);
        mark(c + r, c);

        // The pivot last, so it always wins over a hand crossing it.
        g.put(c, c, Grid.MAX);

        return g.out();
    }

    /** Places a dial mark only where nothing is already lit. */
    private void mark(int x, int y) {
        if (g.get(x, y) == Grid.OFF) g.put(x, y, Grid.DIM);
    }

    /**
     * Draws a hand from the centre outward.
     *
     * Stepping along the angle in half pixel increments and rounding
     * guarantees a connected line of pixels; drawing the ideal line and
     * testing distance would leave holes at shallow angles.
     */
    private void hand(int c, double angle, int len, int v) {
        double dx = Math.cos(angle), dy = Math.sin(angle);
        for (double d = 1.0; d <= len; d += 0.5) {
            g.set(c + (int) Math.round(dx * d),
                    c + (int) Math.round(dy * d), v);
        }
    }
}
