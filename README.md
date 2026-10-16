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

## Build it

No prebuilt APK is published. The Glyph SDK licence grants a
non-transferable right to use the SDK and clause 1(c) forbids
redistributing it, and a built APK contains the SDK's classes. So the
APK has to be built locally, which takes three steps.

**1. Fetch the SDK.**

```sh
./fetch-sdk.sh
```

**2. Add an API key.** Nothing issues these per package name. Request
one for `com.adityasingh.glyph` from Nothing's developer programme and
put it in `app/src/main/res/values/strings.xml`:

```xml
<string name="nothing_api_key">your-key-here</string>
```

Worth trying without one first. `register()` returns false when the
key is not authorised, but `setMatrixFrame` does not check that flag,
so the toy may well draw anyway; the log will say if the key was
rejected.

**3. Build and install.** Needs a JDK 17 and the Android SDK.

```sh
./gradlew assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## Turning it on

1. Install the APK.
2. Settings, **Glyph Interface**, **Glyph Toys**.
3. Tap the icon top left to manage toys.
4. Find **Radar** and add it to the active list.
5. It now appears in the toy carousel on the back panel.

## Status

Compiles clean and the toy service registers correctly in the built
manifest. The renderer was verified by simulating frames, but this has
**not been run on a phone**, so expect to fix small things on first
run, most likely around the service lifecycle or the API key.
