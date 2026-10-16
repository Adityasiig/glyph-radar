package com.adityasingh.glyph;

/** Registers {@link WaveformRenderer} as a Glyph Toy. */
public class WaveformToyService extends ToyService {

    @Override
    protected Renderer createRenderer(int size) {
        return new WaveformRenderer(size);
    }
}
