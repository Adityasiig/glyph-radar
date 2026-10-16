package com.adityasingh.glyph;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.nothing.ketchum.Common;
import com.nothing.ketchum.Glyph;

/**
 * The toys run from the Glyph Toys list, not from here. This screen
 * answers "I installed it and nothing happened": it reports each link
 * in the chain and previews every toy on the front display.
 */
public class MainActivity extends AppCompatActivity {

    /** The system app GlyphMatrixManager.init() binds to. */
    private static final String GLYPH_PKG = "com.nothing.thirdparty";

    private MatrixPreview preview;
    private TextView toyLabel;
    private final TextView[] rows = new TextView[Toys.ALL.length];
    private int selected = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((TextView) findViewById(R.id.status)).setText(diagnose());

        preview = findViewById(R.id.preview);
        toyLabel = findViewById(R.id.toy_label);
        buildPicker();
        select(0);
    }

    private int panelSize() {
        int len = Common.getDeviceMatrixLength();
        return len > 0 ? len : Glyph.DEVICE_25111p_MATRIX_LENGTH;
    }

    private void buildPicker() {
        LinearLayout picker = findViewById(R.id.picker);
        for (int i = 0; i < Toys.ALL.length; i++) {
            TextView row = new TextView(this);
            row.setText(getString(Toys.ALL[i].nameRes));
            row.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
            row.setTypeface(android.graphics.Typeface.MONOSPACE);
            row.setGravity(Gravity.CENTER);
            row.setLetterSpacing(0.12f);
            int pad = Math.round(getResources().getDisplayMetrics().density * 12);
            row.setPadding(pad, pad, pad, pad);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.bottomMargin = Math.round(
                    getResources().getDisplayMetrics().density * 8);
            row.setLayoutParams(lp);

            final int index = i;
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    select(index);
                }
            });

            rows[i] = row;
            picker.addView(row);
        }
    }

    private void select(int index) {
        selected = index;
        for (int i = 0; i < rows.length; i++) {
            boolean on = i == selected;
            rows[i].setTextColor(on ? Color.BLACK : Color.WHITE);
            rows[i].setBackgroundColor(on ? Color.WHITE : 0xFF141414);
        }
        toyLabel.setText(getString(Toys.ALL[index].nameRes));
        preview.show(Toys.ALL[index], panelSize());
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

        int toys = toyServicesFound();
        b.append(toys == Toys.ALL.length ? "[ok] " : "[!!] ")
                .append("toys registered ")
                .append(toys).append('/').append(Toys.ALL.length)
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

    /** How many of our toy services survived into the installed APK. */
    private int toyServicesFound() {
        Intent i = new Intent("com.nothing.glyph.TOY");
        i.setPackage(getPackageName());
        return getPackageManager()
                .queryIntentServices(i, PackageManager.MATCH_ALL)
                .size();
    }
}
