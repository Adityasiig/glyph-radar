package com.adityasingh.glyph;

/** Registers {@link HeartRenderer} as a Glyph Toy. */
public class HeartToyService extends ToyService {

    @Override
    protected Renderer createRenderer(int size) {
        return new HeartRenderer(size);
    }
}
