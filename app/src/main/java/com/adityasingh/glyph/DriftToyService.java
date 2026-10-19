package com.adityasingh.glyph;

/** Registers {@link DriftRenderer} as a Glyph Toy. */
public class DriftToyService extends ToyService {

    @Override
    protected Renderer createRenderer(int size) {
        return new DriftRenderer(size);
    }
}
