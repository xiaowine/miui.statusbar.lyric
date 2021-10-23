package miui.statusbar.lyric.activity;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
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
        verExplain.setSummary("当前版本: " + Utils.getLocalVersion(activity));
        verExplain.setOnPreferenceClickListener((preference) -> {
            new AlertDialog.Builder(activity)
                    .setTitle("当前版本[" + Utils.getLocalVersion(activity) + "]适用于 \n（部分其余版本也支持）")
                    .setMessage("酷狗音乐:v10.8.4 （需打开蓝牙歌词）\n" +
                            "酷我音乐:v9.4.6.2 （需打开蓝牙歌词）\n" +
                            "网易云音乐:v8.6.0 （完美适配）\n" +
                            "Aplayer:v1.5.7.9 （完美适配）\n" +
                            "QQ音乐:v10.17.0.11 （需打开蓝牙歌词和戴耳机）\n\n\n" +
                            "每个版本需要hook内容不同,所以需要适配")
                    .setPositiveButton("确定", null)
                    .create()
                    .show();
            return true;
        });


    }
}