package com.adityasingh.glyph;

/**
 * The site's circular level meter: bars radiating from a hub, each its
 * own oscillator.
 *
 * The site uses 48 bars. At 13x13 the rim is only about 38 pixels
 * around, so anything past ~16 bars collapses into a filled disc; 16
 * keeps a visible gap between neighbours.
 */
public final class EqualiserRenderer implements Renderer {

    private static final float TAU = (float) (Math.PI * 2);
    private static final int BARS = 16;

    private final Paint13 p;

    public EqualiserRenderer(int size) {
        this.p = new Paint13(size);
    }

    @Override
    public int[] render(float t) {
        p.clear();
        float c = p.mid();
        // Bars must finish inside the dial; reaching to 0.97*c put the
        // longest tips past the circular clip and flattened them.
        float inner = c * 0.26f, max = c * 0.88f;

        for (int i = 0; i < BARS; i++) {
            float ang = (float) i / BARS * TAU;
            // Two detuned oscillators, so the pattern never visibly
            // repeats the way a single sine would.
            float osc = (float) (Math.sin(t * 2.4f + i * 0.5f)
                    * Math.sin(t * 0.7f + i * 0.13f));
            float mag = Math.abs(osc);
            float len = inner + (max - inner) * (0.22f + mag * 0.78f);

            p.line(c + (float) Math.cos(ang) * inner,
                    c + (float) Math.sin(ang) * inner,
                    c + (float) Math.cos(ang) * len,
                    c + (float) Math.sin(ang) * len,
                    0.22f, 0.28f + mag * 0.72f);
        }

        // The hub, which gives the bars something to grow out of.
        p.ring(c, c, inner * 0.70f, 0.2f, 0.45f);

        p.clipToCircle();
        return p.out();
    }
}
