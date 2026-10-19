package com.adityasingh.glyph;

/**
 * A single diamond growing and shrinking on a slow breath.
 *
 * One shape, one motion, nothing else. The restraint is the design: at
 * 13x13 a lone symmetric outline expanding over four seconds reads as
 * deliberate, where anything busier reads as noise.
 *
 * Paced to a 4-7-8 style breath rather than a sine, so the motion has
 * the hesitation of something breathing instead of the evenness of a
 * machine.
 */
public final class BreatheRenderer implements Renderer {

    /** Seconds for one full in and out. */
    private static final float CYCLE = 6f;

    private final Grid g;

    public BreatheRenderer(int size) {
        this.g = new Grid(size);
    }

    @Override
    public int[] render(float t) {
        g.clear();
        int c = g.mid();
        int maxR = c;

        float phase = (t / CYCLE) % 1f;
        float open = curve(phase);

        // Quantise to whole pixels: a diamond is only ever an integer
        // radius, so interpolating would just stall on one value then
        // jump, which looks like a stutter rather than a breath.
        // Never fully collapse: at radius 0 the panel is a single lit
        // pixel, which reads as the toy having died rather than as a
        // breath bottoming out.
        int r = 1 + Math.round(open * (maxR - 1));

        // Two pixels thick, so the outline is a solid band rather than
        // four dashed diagonals.
        g.diamondBand(c, c, r, 2, Grid.HIGH);
        // The leading edge brighter, which gives the shape a rim.
        g.diamond(c, c, r, Grid.MAX);

        // A core that fades as the shape opens, so the centre is not a
        // dead spot at full extension.
        g.set(c, c, r <= 1 ? Grid.MAX : Grid.DIM);

        return g.out();
    }

    /**
     * Breath shape in 0..1: a slower draw in, a brief hold, a slower
     * release. Sine would spend equal time everywhere and feel
     * mechanical.
     */
    private static float curve(float p) {
        if (p < 0.35f) {
            // Inhale, easing out towards the top.
            float k = p / 0.35f;
            return (float) Math.sin(k * Math.PI / 2);
        }
        if (p < 0.50f) {
            return 1f;
        }
        if (p < 0.85f) {
            // Exhale, easing in.
            float k = (p - 0.50f) / 0.35f;
            return (float) Math.cos(k * Math.PI / 2);
        }
        return 0f;
    }
}
