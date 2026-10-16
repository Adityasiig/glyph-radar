# Renderer checks

The Glyph panel is on the back of the phone and only lights up once the
whole SDK chain works, so a bad animation is easy to mistake for a bad
connection. These run the renderers on their own, with no Android, and
print what the panel would show.

The renderers depend on nothing but `Renderer` and `Paint13`, so they
compile standalone once the `package` line is stripped:

```sh
mkdir -p /tmp/sim && cd /tmp/sim
for f in Renderer Paint13 RadarRenderer EqualiserRenderer OrbitRenderer \
         WaveformRenderer PulseRenderer RainRenderer; do
    sed 's/^package .*//' \
        ../app/src/main/java/com/adityasingh/glyph/$f.java > $f.java
done
cp ../tools/CheckRenderers.java Chk.java
cp ../tools/PreviewRenderers.java Sim.java
javac -encoding UTF-8 -d . *.java

java -cp . Chk   # pass/fail per renderer
java -cp . Sim   # frames as ASCII art
```

`Chk` sweeps 8 seconds of each animation and fails a renderer that goes
blank, stops moving, drops below a few lit pixels, or presses bright
pixels against the circular clip. That last one catches a design whose
geometry overflows the dial and gets silently cut off, which is what
made the equaliser and waveform look lopsided at 13x13.

Radar and Rain are exempt from the clip check: the radar's outer dial
ring and Rain's falling columns are supposed to reach the rim.
