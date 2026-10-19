package com.adityasingh.glyph;

/** Registers {@link CornersRenderer} as a Glyph Toy. */
public class CornersToyService extends ToyService {

    @Override
    protected Renderer createRenderer(int size) {
        return new CornersRenderer(size);
    }
}
