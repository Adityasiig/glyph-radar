package com.adityasingh.glyph;

/**
 * A bright row sweeping down the panel over a dim grid, with a short
 * trail behind it.
 *
 * The replacement for the rotating radar arm. Rotation is the one thing
 * this grid cannot do: an arm at 30 degrees is a staircase that
 * reshuffles every frame. A row is a perfect line at every position, so
 * the motion is dead clean, and a full width bar is the boldest mark
 * the panel can make.
 */
public final class ScanRenderer implements Renderer {

    /** Seconds the bar takes to cross the panel once. */
    private static final float SWEEP = 1.2f;
    private static final int TRAIL = 4;

    private final Grid g;

    public ScanRenderer(int size) {
        this.g = new Grid(size);
    }

    @Override
    public int[] render(float t) {
        g.clear();
        int S = g.size();

        // A static lattice, so the sweep has something to reveal and
        // the panel is never close to empty.
        for (int y = 0; y < S; y += 3) {
            for (int x = 0; x < S; x += 3) {
                g.set(x, y, Grid.DIM);
            }
        }

        // Bounce rather than wrap: a bar that reappears at the top
        // reads as a glitch at this size, a bounce reads as intent.
        float cycle = (t / SWEEP) % 2f;
        float pos = cycle <= 1f ? cycle : 2f - cycle;
        int head = Math.round(pos * (S - 1));
        boolean down = cycle <= 1f;

        for (int i = 0; i <= TRAIL; i++) {
            int y = down ? head - i : head + i;
            if (y < 0 || y >= S) continue;
            int v = i == 0 ? Grid.MAX
                    : i == 1 ? Grid.HIGH
                    : i == 2 ? Grid.MID : Grid.DIM;
            g.row(y, v);
        }

        return g.out();
    }
}
