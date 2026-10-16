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
            new Toy(R.string.toy_bars, BarsRenderer::new),
            new Toy(R.string.toy_bloom, BloomRenderer::new),
            new Toy(R.string.toy_scan, ScanRenderer::new),
            new Toy(R.string.toy_heart, HeartRenderer::new),
            new Toy(R.string.toy_spin, SpinRenderer::new),
            new Toy(R.string.toy_rain, RainRenderer::new)
    };

    private Toys() {
    }
}
