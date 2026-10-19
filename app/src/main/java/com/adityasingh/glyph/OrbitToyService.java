package com.adityasingh.glyph;

/** Registers {@link OrbitRenderer} as a Glyph Toy. */
public class OrbitToyService extends ToyService {

    @Override
    protected Renderer createRenderer(int size) {
        return new OrbitRenderer(size);
    }
}
