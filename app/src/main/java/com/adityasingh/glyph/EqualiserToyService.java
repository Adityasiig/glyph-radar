package com.adityasingh.glyph;

/** Registers {@link EqualiserRenderer} as a Glyph Toy. */
public class EqualiserToyService extends ToyService {

    @Override
    protected Renderer createRenderer(int size) {
        return new EqualiserRenderer(size);
    }
}
