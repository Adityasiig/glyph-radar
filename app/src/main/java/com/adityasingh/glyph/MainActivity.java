package com.adityasingh.glyph;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.nothing.ketchum.Common;
import com.nothing.ketchum.Glyph;

/**
 * The toy itself runs from the Glyph Toys list, not from here. This
 * screen exists to answer "I installed it and nothing happened", so it
 * reports each link in the chain instead of a fixed instruction.
 */
public class MainActivity extends AppCompatActivity {

    /** The system app GlyphMatrixManager.init() binds to. */
    private static final String GLYPH_PKG = "com.nothing.thirdparty";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView status = findViewById(R.id.status);
        status.setText(diagnose());

        MatrixPreview preview = findViewById(R.id.preview);
        int len = Common.getDeviceMatrixLength();
        preview.setGrid(len > 0 ? len : Glyph.DEVICE_25111p_MATRIX_LENGTH);
    }

    private String diagnose() {
        StringBuilder b = new StringBuilder();

        boolean known = Common.is25111p() || Common.is23112();
        b.append(known ? "[ok] " : "[--] ")
                .append("device ")
                .append(android.os.Build.MODEL)
                .append(known ? " supported" : " has no Glyph Matrix")
                .append('\n');

        int len = Common.getDeviceMatrixLength();
        b.append(len > 0 ? "[ok] " : "[--] ")
                .append("panel ")
                .append(len > 0 ? len + "x" + len : "unknown")
                .append('\n');

        boolean visible = glyphServiceVisible();
        b.append(visible ? "[ok] " : "[!!] ")
                .append("glyph service ")
                .append(visible ? "reachable" : "NOT reachable")
                .append('\n');

        boolean toy = toyRegistered();
        b.append(toy ? "[ok] " : "[!!] ")
                .append("toy service ")
                .append(toy ? "registered" : "missing")
                .append('\n');

        b.append('\n');
        if (!known) {
            b.append(getString(R.string.status_unsupported));
        } else if (!visible) {
            // The bind is silent when it fails, so say what to check.
            b.append(getString(R.string.status_no_service));
        } else {
            b.append(getString(R.string.status_connecting));
        }
        return b.toString();
    }

    /**
     * Whether the Glyph system service can actually be bound. A missing
     * {@code <queries>} entry or a non-Nothing ROM both show up here,
     * which is the failure {@code init()} swallows.
     */
    private boolean glyphServiceVisible() {
        Intent i = new Intent("com.nothing.thirdparty.bind_glyphservice");
        i.setComponent(new ComponentName(
                GLYPH_PKG, "com.nothing.thirdparty.GlyphService"));
        return !getPackageManager()
                .queryIntentServices(i, PackageManager.MATCH_ALL)
                .isEmpty();
    }

    /** Whether our own toy service survived into the installed APK. */
    private boolean toyRegistered() {
        Intent i = new Intent("com.nothing.glyph.TOY");
        i.setPackage(getPackageName());
        return !getPackageManager()
                .queryIntentServices(i, PackageManager.MATCH_ALL)
                .isEmpty();
    }
}
