package com.adityasingh.glyph;

/**
 * The one list of toys in this app.
 *
 * The picker and the preview both read this, so adding a toy means
 * adding a row here plus a service in the manifest, and the two cannot
 * drift apart in what they claim to offer.
 */
final class Toys {

    interface Factory {
        Renderer create(int size);
    }

    static final class Toy {
        final int nameRes;
        final Factory factory;

        Toy(int nameRes, Factory factory) {
            this.nameRes = nameRes;
            this.factory = factory;
        }
    }

    static final Toy[] ALL = {
            new Toy(R.string.toy_radar, RadarRenderer::new),
            new Toy(R.string.toy_equaliser, EqualiserRenderer::new),
            new Toy(R.string.toy_orbit, OrbitRenderer::new),
            new Toy(R.string.toy_waveform, WaveformRenderer::new),
            new Toy(R.string.toy_pulse, PulseRenderer::new),
            new Toy(R.string.toy_rain, RainRenderer::new)
    };

    private Toys() {
    }
}
