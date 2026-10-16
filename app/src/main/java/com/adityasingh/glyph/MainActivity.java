package com.adityasingh.glyph;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.nothing.ketchum.Common;

/**
 * The toy runs from the Glyph Toys list, not from here. This screen
 * only says so.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView status = findViewById(R.id.status);
        // Both panels are supported; anything else cannot show the toy.
        boolean supported = Common.is25111p() || Common.is23112();
        status.setText(supported
                ? getString(R.string.status_connecting)
                : getString(R.string.status_unsupported));
    }
}
