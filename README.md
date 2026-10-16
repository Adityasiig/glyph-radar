# Glyph Radar

The portfolio's hero radar as a Glyph Toy for the Nothing Phone (4a)
Pro. The same sweep that runs in the website hero, drawn on the Glyph
Matrix on the back of the phone.

## About Dot HUB

Dot HUB is a *host app*: it bundles toys its own author wrote, plus an
Explore tab linking to other people's. You cannot hand Dot HUB an APK
and have it adopt it. A toy is its own app that registers a service
with Nothing's launcher, which is what this project is.

So the install route is: build this APK, install it, and the toy shows
up in the system Glyph Toys list alongside anything Dot HUB installed.

## What it draws

A direct port of the canvas renderer on the site: two polar rings plus
cross hairs, an arm sweeping once every ~7 seconds with a trail that
fades behind it, and four blips that flare as the arm crosses them.

The site's panel is 25x25. The (4a) Pro is **13x13**, so the renderer
asks `Common.getDeviceMatrixLength()` at runtime and sizes itself; the
same code fills a Phone (3) panel at 25x25 without changes.

Note the (4a) Pro is AOD-only and has no Glyph Touch, so the toy draws
but does not respond to the Glyph button.

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
4. Find **Radar** and add it to the active list.
5. Hold the Glyph button on the back to cycle to it.

Opening the app itself only shows a status line; the toy runs from the
Glyph Toys list, not from the launcher icon.

## Status

Builds clean and signed, the toy service is present in the shipped
manifest with the right `com.nothing.glyph.TOY` filter, and the
renderer was verified frame by frame against a simulated 13x13 panel.

It has **not been run on a phone**, so first run may still need
fixing. If the toy appears in the list but the panel stays dark,
`adb logcat -s RadarToy GlyphMatrixManager` is the place to look.
