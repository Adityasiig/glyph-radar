package com.adityasingh.glyph;

/** Registers {@link WaveRenderer} as a Glyph Toy. */
public class WaveToyService extends ToyService {

    @Override
    protected Renderer createRenderer(int size) {
        return new WaveRenderer(size);
    }
}
