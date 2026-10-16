#!/bin/sh
# Fetches the Glyph Matrix SDK, which cannot be redistributed here.
set -e
URL="https://github.com/Nothing-Developer-Programme/GlyphMatrix-Developer-Kit/raw/main/glyph-matrix-sdk-2.0.aar"
mkdir -p app/libs
curl -fL -o app/libs/glyph-matrix-sdk-2.0.aar "$URL"
echo "SDK placed in app/libs/."
