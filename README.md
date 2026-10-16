# Glyph Radar

Six Glyph Toys for the Nothing Phone (4a) Pro, in one app. Four are
the circular instruments from the website hero, redrawn for the Glyph
Matrix; two are designed for the panel itself.

## About Dot HUB

Dot HUB is a *host app*: it bundles toys its own author wrote, plus an
Explore tab linking to other people's. You cannot hand Dot HUB an APK
and have it adopt it. A toy is its own app that registers a service
with Nothing's launcher, which is what this project is.

So the install route is: install this APK and its toys show up in the
system Glyph Toys list alongside anything Dot HUB installed.

## The toys

Each registers its own service, so all six appear separately in the
Glyph Toys list and the Glyph button cycles between them.

| Toy | What it draws |
| --- | --- |
| **Radar** | Polar rings and cross hairs under a sweeping arm, with contacts that flare as it passes. From the site hero. |
| **Equaliser** | Sixteen bars radiating from a hub, each its own detuned oscillator. |
| **Orbit** | Two counter rotating rings of beads that brighten crossing the top. |
| **Waveform** | A closed loop deformed by layered sines, breathing. |
| **Pulse** | Rings expanding from the centre and fading at the rim, like sonar. Built for 13x13. |
| **Rain** | Columns falling with trailing tails, each at its own speed. Built for 13x13. |

Every renderer asks `Common.getDeviceMatrixLength()` at runtime, so the
same code fills a Phone (3) panel at 25x25 without changes.

### Drawing at 13x13

A 13 wide panel gives about six pixels of radius, so the site's designs
do not survive a straight scale down: three rings merge into one, 48
equaliser bars become a filled disc, and hard on/off pixels make shapes
jump rather than move. `Paint13` rasterises dots, lines and rings with
sub pixel coverage and a half pixel feather, which buys smooth motion
and round curves from a very small grid. Element counts come down to
what stays separable, and every design is kept inside the circular clip
so nothing is silently cut off.

See `tools/` for the standalone checks that verify this.

Note the (4a) Pro is AOD-only and has no Glyph Touch, so the toys draw
and the Glyph button cycles between them, but they take no other input.
Each toy slows to 5fps on always-on display to stay cheap.

## Install it

Grab `app-release.apk` from the
[latest release](https://github.com/Adityasiig/glyph-radar/releases/latest)
and open it on the phone. Android will ask for permission to install
from this source, which is expected for a sideloaded app.

No Nothing API key is baked in. Nothing issues those per package name,
and the toy looks like it does not need one: `register()` returns false
when the key is unauthorised, but `setMatrixFrame` never consults that
flag, so frames go to the panel regardless. If it turns out a future
firmware does enforce it, the log line from `RadarToy` will say so and
a key can be dropped into `strings.xml`.

## Or build it yourself

Needs a JDK 17 and the Android SDK. The Glyph SDK is not on Maven and
its licence (clause 1(c)) forbids redistributing it, so it is not
checked in; `fetch-sdk.sh` pulls it from Nothing.

```sh
./fetch-sdk.sh
./gradlew assembleRelease
adb install -r app/build/outputs/apk/release/app-release.apk
```

## Turning it on

1. Install the APK.
2. Settings, **Glyph Interface**, **Glyph Toys**.
3. Tap the icon top left to manage toys.
4. Add any of **Radar**, **Equaliser**, **Orbit**, **Waveform**,
   **Pulse** or **Rain** to the active list.
5. Hold the Glyph button on the back to cycle between them.

Opening the app itself does not drive the panel; the toys run from the
Glyph Toys list. The app reports whether each link in the chain works
and previews every toy on the front screen, which is the quickest way
to tell a broken animation from a broken Glyph connection.

## Status

Builds clean and signed, all six toy services are present in the
shipped manifest with the right `com.nothing.glyph.TOY` filter, and
every renderer passes the checks in `tools/` against a simulated 13x13
panel: never blank, never frozen, nothing clipped by the dial.

It has **not been run on a phone**, so first run may still need
fixing. If a toy appears in the list but the panel stays dark,
`adb logcat -s GlyphToy GlyphMatrixManager` is the place to look.
