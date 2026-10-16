package com.adityasingh.glyph;

/**
 * Columns of light falling with trailing tails.
 *
 * Column aligned motion is native to the grid: a drop is one pixel
 * stepping down one row at a time, so there is nothing to round and
 * the fall is perfectly even. Each column gets its own speed and
 * offset from a fixed hash, giving a non repeating pattern with no
 * stored state, so frames stay reproducible for the checks in tools/.
 */
public final class RainRenderer implements Renderer {

    /** Tail length in whole pixels. */
    private static final int TAIL = 5;

    private final Grid g;

    public RainRenderer(int size) {
        this.g = new Grid(size);
    }

    @Override
    public int[] render(float t) {
        g.clear();
        int S = g.size();
        int span = S + TAIL;

        for (int x = 0; x < S; x++) {
            // Whole pixel steps, so a drop never sits between rows.
            float speed = 6f + hash(x) * 7f;
            int head = (int) (t * speed + hash(x * 7 + 1) * span) % span;

            for (int i = 0; i <= TAIL; i++) {
                int y = head - i;
                if (y < 0 || y >= S) continue;
                int v = i == 0 ? Grid.MAX
                        : i == 1 ? Grid.HIGH
                        : i <= 3 ? Grid.MID : Grid.DIM;
                g.set(x, y, v);
            }
        }

        return g.out();
    }

    /** Stable pseudo random in 0..1, so frames are reproducible. */
    private static float hash(int n) {
        int h = n * 374761393 + 668265263;
        h = (h ^ (h >> 13)) * 1274126177;
        return ((h ^ (h >> 16)) & 0xffff) / 65535f;
    }
}
