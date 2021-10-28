package miui.statusbar.lyric.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import miui.statusbar.lyric.R;

@SuppressLint("ExportedPreferenceActivity")
public class AboutActivity extends PreferenceActivity {
    private final Activity activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.about_preferences);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("关于模块");


    }
}