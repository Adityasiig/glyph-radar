package com.adityasingh.glyph;

/** Registers {@link CatRenderer} as a Glyph Toy. */
public class CatToyService extends ToyService {

    @Override
    protected Renderer createRenderer(int size) {
        return new CatRenderer(size);
    }
}
