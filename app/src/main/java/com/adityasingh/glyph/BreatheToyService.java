package com.adityasingh.glyph;

/** Registers {@link BreatheRenderer} as a Glyph Toy. */
public class BreatheToyService extends ToyService {

    @Override
    protected Renderer createRenderer(int size) {
        return new BreatheRenderer(size);
    }
}
