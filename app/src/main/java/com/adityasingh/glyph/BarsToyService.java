package com.adityasingh.glyph;

/** Registers {@link BarsRenderer} as a Glyph Toy. */
public class BarsToyService extends ToyService {

    @Override
    protected Renderer createRenderer(int size) {
        return new BarsRenderer(size);
    }
}
