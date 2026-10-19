package com.adityasingh.glyph;

/** Registers {@link ClockRenderer} as a Glyph Toy. */
public class ClockToyService extends ToyService {

    @Override
    protected Renderer createRenderer(int size) {
        return new ClockRenderer(size);
    }
}
