package com.adityasingh.glyph;

/**
 * Four brackets easing in towards the centre and back out, like a lens
 * focusing.
 *
 * Twenty four lit pixels in perfect four fold symmetry and one slow
 * move. Brackets are made of straight runs meeting at a right angle,
 * which the grid renders exactly at any position, so the motion is
 * clean at every step.
 */
public final class CornersRenderer implements Renderer {

    /** Seconds for one full close and open. */
    private static final float CYCLE = 4.5f;
    /** Length of each bracket arm, in pixels. */
    private static final int ARM = 3;

    private final Grid g;

    public CornersRenderer(int size) {
        this.g = new Grid(size);
    }

    @Override
    public int[] render(float t) {
        g.clear();
        int S = g.size();
        int c = g.mid();

        float phase = (t / CYCLE) % 1f;
        // Triangle wave eased at both ends, so the brackets settle
        // rather than reversing abruptly at the extremes.
        float tri = phase < 0.5f ? phase * 2f : (1f - phase) * 2f;
        float ease = tri * tri * (3f - 2f * tri);

        // Travel from the panel edge to just short of the centre.
        int inset = Math.round(ease * (c - ARM));
        int lo = inset;
        int hi = S - 1 - inset;
        if (hi - lo < ARM) return g.out();

        // Brightest when closed, which gives the focus a payoff.
        int v = ease > 0.75f ? Grid.MAX : ease > 0.4f ? Grid.HIGH : Grid.MID;

        // Draw one corner, then mirror, so all four are identical by
        // construction rather than by four hand written cases.
        for (int i = 0; i < ARM; i++) {
            g.set(lo + i, lo, v);
            g.set(lo, lo + i, v);
        }
        for (int i = 0; i < ARM; i++) {
            g.set(hi - i, lo, v);
            g.set(hi, lo + i, v);
        }
        g.mirrorY();

        // A single centre pixel as the thing being focused on.
        g.set(c, c, ease > 0.75f ? Grid.MAX : Grid.DIM);

        return g.out();
    }
}
