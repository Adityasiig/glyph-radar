package com.adityasingh.glyph;

/**
 * One Glyph Matrix animation.
 *
 * Implementations are pure functions of time so a frame can be
 * reproduced for testing, and so a toy can be paused and resumed
 * without carrying state.
 */
public interface Renderer {

    /**
     * Renders one frame.
     *
     * @param tSeconds elapsed time, the only state the animation needs
     * @return size*size brightnesses, 0..255, row major
     */
    int[] render(float tSeconds);
}
