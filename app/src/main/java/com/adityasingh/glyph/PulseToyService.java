package com.adityasingh.glyph;

/** Registers {@link PulseRenderer} as a Glyph Toy. */
public class PulseToyService extends ToyService {

    @Override
    protected Renderer createRenderer(int size) {
        return new PulseRenderer(size);
    }
}
