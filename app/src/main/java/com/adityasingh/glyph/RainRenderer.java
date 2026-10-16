package com.adityasingh.glyph;

/**
 * Columns of light falling down the panel with trailing tails.
 *
 * Built for 13x13: it works per column on the pixel grid instead of
 * drawing a shape and sampling it, so every pixel is deliberate and
 * nothing is lost to rounding. Each column gets its own speed and
 * offset from a fixed hash, which gives a non repeating pattern with
 * no stored state and no randomness, so frames stay reproducible.
 */
public final class RainRenderer implements Renderer {

    /** Tail length in pixels. */
    private static final float TAIL = 4.5f;

    private final int size;
    private final Paint13 p;

    public RainRenderer(int size) {
        this.size = size;
        this.p = new Paint13(size);
    }

    @Override
    public int[] render(float t) {
        p.clear();
        float span = size + TAIL;

        for (int x = 0; x < size; x++) {
            // Deterministic per column variation, so the columns drift
            // apart over time rather than marching in step.
            float speed = 2.6f + hash(x) * 3.4f;
            float offset = hash(x * 7 + 1) * span;
            float head = ((t * speed + offset) % span);

            for (int y = 0; y < size; y++) {
                float behind = head - y;
                if (behind < 0f || behind > TAIL) continue;
                // Brightest at the head, fading along the tail.
                float v = 1f - behind / TAIL;
                p.lite(x, y, v * v);
            }
        }

        p.clipToCircle();
        return p.out();
    }

    /** Stable pseudo random in 0..1, so frames are reproducible. */
    private static float hash(int n) {
        int h = n * 374761393 + 668265263;
        h = (h ^ (h >> 13)) * 1274126177;
        return ((h ^ (h >> 16)) & 0xffff) / 65535f;
    }
}
