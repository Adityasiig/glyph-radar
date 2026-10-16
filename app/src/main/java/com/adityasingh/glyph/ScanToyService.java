package com.adityasingh.glyph;

/** Registers {@link ScanRenderer} as a Glyph Toy. */
public class ScanToyService extends ToyService {

    @Override
    protected Renderer createRenderer(int size) {
        return new ScanRenderer(size);
    }
}
