package com.adityasingh.glyph;

/** Registers {@link BloomRenderer} as a Glyph Toy. */
public class BloomToyService extends ToyService {

    @Override
    protected Renderer createRenderer(int size) {
        return new BloomRenderer(size);
    }
}
