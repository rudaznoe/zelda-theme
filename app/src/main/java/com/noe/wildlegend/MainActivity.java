package com.noe.wildlegend;

import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences("wildlegend_prefs", MODE_PRIVATE);

        TextView subtitle = findViewById(R.id.subtitle);
        Switch readableSwitch = findViewById(R.id.switchReadable);
        Switch particlesSwitch = findViewById(R.id.switchParticles);
        Button applyButton = findViewById(R.id.buttonApply);
        Button settingsButton = findViewById(R.id.buttonSettings);

        subtitle.setText(getString(R.string.subtitle));

        readableSwitch.setChecked(prefs.getBoolean("readable", true));
        particlesSwitch.setChecked(prefs.getBoolean("particles", true));

        readableSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                prefs.edit().putBoolean("readable", isChecked).apply());

        particlesSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                prefs.edit().putBoolean("particles", isChecked).apply());

        applyButton.setOnClickListener(v -> openWallpaperChooser());
        settingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
            startActivity(intent);
        });
    }

    private void openWallpaperChooser() {
        Intent intent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
            intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                    new ComponentName(this, WildLegendWallpaperService.class));
        } else {
            intent = new Intent(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER);
        }
        startActivity(intent);
    }
}
