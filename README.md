# Glyph Toys

Thirteen Glyph Toys for the Nothing Phone (4a) Pro, in one app, drawn
for a 13x13 panel rather than scaled down to one.

## About Dot HUB

Dot HUB is a *host app*: it bundles toys its own author wrote, plus an
Explore tab linking to other people's. You cannot hand Dot HUB an APK
and have it adopt it. A toy is its own app that registers a service
with Nothing's launcher, which is what this project is.

So the install route is: install this APK and its toys show up in the
system Glyph Toys list alongside anything Dot HUB installed.

## The toys

Each registers its own service, so all thirteen appear separately in the
Glyph Toys list and the Glyph button cycles between them. Add only the
ones you want; the list is yours to curate.

The bold set fills the panel:

| Toy | What it draws |
| --- | --- |
| **Bars** | A level meter: every column a solid bar, bright tip, filling the panel |
| **Bloom** | Diamond fronts expanding from the centre, one pixel per step |
| **Scan** | A full width bar sweeping down over a lattice, with a graded trail |
| **Heart** | A hand placed bitmap heart on a double thump beat |
| **Spin** | Comets running opposite ways around two square tracks |
| **Rain** | Columns falling with trailing tails, each at its own speed |

The quiet set is sparser and slower, for when the back of the phone
should not be shouting:

| Toy | What it draws |
| --- | --- |
| **Clock** | The real time: two hands, four quarter marks, nothing else |
| **Breathe** | One diamond opening and closing on a slow 6 second breath |
| **Orbit** | A single point tracing a diamond, with a short tail |
| **Wave** | One smooth curve rolling across, a single pixel per column |
| **Corners** | Four brackets easing in and out, like a lens focusing |
| **Drift** | Seven points drifting and fading on their own cycles |
| **Cat** | A cat sitting in profile, flicking its tail |

## Why these, and not the website's instruments

The first version ported the site's circular instruments directly: a
radar sweep, a 48 bar equaliser, orbiting beads, a waveform ring. They
look right on a large canvas and wrong here, and the reason is
measurable rather than a matter of taste.

Count the pixels forming a circle of radius r on this grid and you get
8, 12, 16, 32, 28, 40. Not even monotonic. Concentric rings therefore
cannot be spaced evenly, and a rotating arm cannot sweep smoothly: at
30 degrees it is a staircase that reshuffles on every frame. The panel
holds 169 pixels total, so there is no room to hide an approximation
behind resolution, and no room for shading either. A half lit pixel
does not read as a soft edge at this size, it reads as grey mud.

So this version drops sub pixel rasterising entirely. `Grid` offers no
curves and no anti aliasing: pixels are addressed as integers,
brightness comes from five fixed well separated levels, and designs
are built from the things the grid renders exactly, which are rows,
columns, blocks, diamonds and symmetry. A diamond is exactly 4r pixels
at every radius, which makes it the honest substitute for a circle.

The frame rate dropped with it. Whole pixel motion has nothing to draw
between steps, so 20fps was sending the same frame twice; the service
now draws at ~11fps and spends less power doing it.

Every design is still resolution independent: each works off the panel
width directly, except `Heart`, which scales its bitmap by a whole
number, so a 25x25 Phone (3) panel gets a crisp double size heart
rather than a blurry stretched one.

The quiet set leans on one more constraint worth naming. A curve has
to stay connected pixel to pixel or it reads as scattered dots, and
whether it does is arithmetic, not taste: a full sine wave across 13
columns puts neighbouring columns three or four rows apart, so `Wave`
shows half a wavelength and bridges any remaining gap explicitly.
`Clock` steps along each hand's angle and rounds, rather than
rasterising a line, so a hand is a connected run at every angle.

See `tools/` for the standalone checks that verify all of this.

Note the (4a) Pro is AOD-only and has no Glyph Touch, so the toys draw
and the Glyph button cycles between them, but they take no other input.
Each toy slows to 4fps on always-on display to stay cheap.

## Install it

Grab `app-release.apk` from the
[latest release](https://github.com/Adityasiig/glyph-radar/releases/latest)
and open it on the phone. Android will ask for permission to install
from this source, which is expected for a sideloaded app.

No Nothing API key is baked in. Nothing issues those per package name,
and the toys look like they do not need one: `register()` returns false
when the key is unauthorised, but `setMatrixFrame` never consults that
flag, so frames go to the panel regardless. If a future firmware does
enforce it, the warning from the `GlyphToy` log tag will say so and a
key can be dropped into `strings.xml`.

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
4. Add whichever toys you want to the active list.
5. Hold the Glyph button on the back to cycle between them.

Opening the app itself does not drive the panel; the toys run from the
Glyph Toys list. The app reports whether each link in the chain works
and previews every toy on the front screen, which is the quickest way
to tell a broken animation from a broken Glyph connection.

## Status

Builds clean and signed, all thirteen toy services are present in the
shipped manifest with the right `com.nothing.glyph.TOY` filter, and
every renderer passes the checks in `tools/` at both 13x13 and 25x25:
never blank, never muddy, and never stalling except where holding
still is the design, which is Clock, Breathe and Corners.

It has **not been run on a phone**, so first run may still need
fixing. If a toy appears in the list but the panel stays dark,
`adb logcat -s GlyphToy GlyphMatrixManager` is the place to look.
