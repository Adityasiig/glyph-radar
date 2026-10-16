package com.adityasingh.glyph;

/** Registers {@link RainRenderer} as a Glyph Toy. */
public class RainToyService extends ToyService {

    @Override
    protected Renderer createRenderer(int size) {
        return new RainRenderer(size);
    }
}
