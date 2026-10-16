package com.adityasingh.glyph;

/** Registers {@link SpinRenderer} as a Glyph Toy. */
public class SpinToyService extends ToyService {

    @Override
    protected Renderer createRenderer(int size) {
        return new SpinRenderer(size);
    }
}
