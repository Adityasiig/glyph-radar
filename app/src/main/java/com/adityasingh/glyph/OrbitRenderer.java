package com.adityasingh.glyph;

/**
 * The site's planetary readout: rings of beads turning at different
 * speeds and directions.
 *
 * Three rings of 6/10/16 beads is right for a large canvas but at
 * 13x13 the inner ring has barely six pixels of circumference. Two
 * rings of 4 and 8 keeps the beads distinct, and the ring guides are
 * dropped because a 2px-radius circle outline just reads as noise.
 */
public final class OrbitRenderer implements Renderer {

    private static final float TAU = (float) (Math.PI * 2);

    /** {radiusFraction, beadCount, radiansPerSecond}. */
    private static final float[][] RINGS = {
            {0.40f, 4f, 0.65f},
            {0.78f, 8f, -0.34f}
    };

    private final Paint13 p;

    public OrbitRenderer(int size) {
        this.p = new Paint13(size);
    }

    @Override
    public int[] render(float t) {
        p.clear();
        float c = p.mid();

        for (float[] ring : RINGS) {
            float R = c * ring[0];
            int n = (int) ring[1];
            for (int i = 0; i < n; i++) {
                float ang = (float) i / n * TAU + t * ring[2];
                // Beads brighten crossing the top, giving the ring an
                // apparent front and back.
                float lift = (float) (Math.cos(ang + Math.PI / 2) + 1) / 2f;
                p.dot(c + (float) Math.cos(ang) * R,
                        c + (float) Math.sin(ang) * R,
                        0.42f, 0.3f + lift * 0.7f);
            }
        }

        // A still centre, so the two directions of travel read against
        // something fixed.
        p.dot(c, c, 0.3f, 0.85f);

        p.clipToCircle();
        return p.out();
    }
}
