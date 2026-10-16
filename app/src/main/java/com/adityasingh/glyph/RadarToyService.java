package com.adityasingh.glyph;

/** Registers {@link RadarRenderer} as a Glyph Toy. */
public class RadarToyService extends ToyService {

    @Override
    protected Renderer createRenderer(int size) {
        return new RadarRenderer(size);
    }
}
