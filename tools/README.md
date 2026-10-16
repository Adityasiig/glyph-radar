# Renderer checks

The Glyph panel is on the back of the phone and only lights once the
whole SDK chain works, so a bad animation is easy to mistake for a bad
connection. These run the renderers with no Android involved and print
what the panel would show.

The renderers depend only on `Renderer` and `Grid`, so they compile
standalone once the `package` line is stripped:

```sh
mkdir -p /tmp/sim && cd /tmp/sim
for f in Renderer Grid BarsRenderer BloomRenderer ScanRenderer \
         HeartRenderer SpinRenderer RainRenderer; do
    sed 's/^package .*//' \
        ../app/src/main/java/com/adityasingh/glyph/$f.java > $f.java
done
cp ../tools/CheckRenderers.java Chk.java
cp ../tools/PreviewRenderers.java Sim.java
javac -encoding UTF-8 -d . *.java

java -cp . Chk        # pass/fail per renderer
java -cp . Sim        # frames as ASCII art
java -cp . Sim 25     # the same designs on a Phone (3) panel
```

`Chk` samples each animation at 0.09s, the interval the service draws
at, and fails a renderer that goes blank, drops below a handful of
bright pixels, stalls for more than two frames in a row, or spends
more pixels on in between values than on lit ones.

That last check is the important one at this size. A 13x13 panel has
169 pixels and no room for shading: half lit pixels read as grey mud
rather than as a soft edge, so the designs stick to a few well
separated brightness levels from `Grid`.

The stall check allows a single repeated frame, which is unavoidable
when a design steps in whole pixels and the service draws faster than
one step per frame. Heart is exempt: it is deliberately still between
beats.
