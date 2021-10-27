package miui.statusbar.lyric.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import miui.statusbar.lyric.R;
import miui.statusbar.lyric.Utils;

@SuppressLint("ExportedPreferenceActivity")
public class AboutActivity extends PreferenceActivity {
    private final Activity activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.about_preferences);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("关于模块");

        //版本介绍
        Preference verExplain = findPreference("ver_explain");
        assert verExplain != null;
        verExplain.setSummary("当前版本[" + Utils.getLocalVersion(activity) + "]适用于 "+getString(R.string.ver_explain));

    }
}