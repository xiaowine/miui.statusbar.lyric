package miui.statusbar.lyric.activity;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.byyang.choose.ChooseFileUtils;
import miui.statusbar.lyric.Config;
import miui.statusbar.lyric.R;
import miui.statusbar.lyric.utils.ActivityUtils;
import miui.statusbar.lyric.utils.ShellUtils;
import miui.statusbar.lyric.utils.Utils;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Objects;


@SuppressLint("ExportedPreferenceActivity")
public class SettingsActivity extends PreferenceActivity {
    private final Activity activity = this;
    private Config config;

    @SuppressLint("WrongConstant")
    @SuppressWarnings({"deprecation"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.root_preferences);
        ActivityUtils.checkPermissions(activity);
        config = new Config();

        Utils.context = activity;
        Utils.log("Debug开启");

        String tips = "Tips1";
        SharedPreferences preferences = activity.getSharedPreferences(tips, 0);
        if (!preferences.getBoolean(tips, false)) {
            new AlertDialog.Builder(activity)
                    .setTitle("提示")
                    .setIcon(R.mipmap.ic_launcher)
                    .setMessage(getString(R.string.Tips1))
                    .setNegativeButton("我已知晓", (dialog, which) -> {
                        SharedPreferences.Editor a = preferences.edit();
                        a.putBoolean(tips, true);
                        a.apply();
                    })
                    .setPositiveButton("退出", (dialog, which) -> activity.finish())
                    .setCancelable(false)
                    .create()
                    .show();
        } else {
            ActivityUtils.checkConfig(activity, config.getId());
        }

        //版本介绍
        Preference verExplain = findPreference("ver_explain");
        assert verExplain != null;
        verExplain.setOnPreferenceClickListener((preference) -> {
            new AlertDialog.Builder(activity)
                    .setIcon(R.mipmap.ic_launcher)
                    .setTitle("版本说明")
                    .setMessage(" 当前版本 [" + ActivityUtils.getLocalVersion(activity) + "] 适用情况：\n\n " + getString(R.string.ver_explain))
                    .setNegativeButton("我已知晓", null)
                    .create()
                    .show();
            return true;
        });
        // 隐藏桌面图标
        SwitchPreference hIcons = (SwitchPreference) findPreference("hLauncherIcon");
        assert hIcons != null;
        hIcons.setOnPreferenceChangeListener((preference, newValue) -> {
            int mode;
            PackageManager packageManager = Objects.requireNonNull(activity).getPackageManager();
            if ((Boolean) newValue) {
                mode = PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
            } else {
                mode = PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
            }
            packageManager.setComponentEnabledSetting(new ComponentName(activity, "miui.statusbar.lyric.launcher"), mode, PackageManager.DONT_KILL_APP);
            return true;
        });


        // 歌词总开关
        SwitchPreference lyricService = (SwitchPreference) findPreference("lyricService");
        assert lyricService != null;
        lyricService.setChecked(config.getLyricService());
        lyricService.setOnPreferenceChangeListener((preference, newValue) -> {
            config.setLyricService((Boolean) newValue);
            return true;
        });

        // 暂停关闭歌词
        SwitchPreference lyricOff = (SwitchPreference) findPreference("lyricOff");
        assert lyricOff != null;
        lyricOff.setChecked(config.getLyricAutoOff());
        lyricOff.setOnPreferenceChangeListener((preference, newValue) -> {
            config.setLyricAutoOff((Boolean) newValue);
            return true;
        });

        // 歌词动效
        ListPreference anim = (ListPreference) findPreference("lyricAnim");
        anim.setEntryValues(new String[]{
                "off", "top", "lower",
                "left", "right", "random"
        });
        anim.setEntries(new String[]{
                "关闭", "上滑", "下滑",
                "左滑", "右滑", "随机"
        });
        Dictionary<String, String> dict = new Hashtable<>();
        dict.put("off", "关闭");
        dict.put("top", "上滑");
        dict.put("lower", "下滑");
        dict.put("left", "左滑");
        dict.put("right", "右滑");
        dict.put("random", "随机");
        anim.setSummary(dict.get(new Config().getAnim()));
        anim.setOnPreferenceChangeListener((preference, newValue) -> {
            new Config().setAnim(newValue.toString());
            anim.setSummary(dict.get(new Config().getAnim()));
            return true;
        });

        // 歌词最大自适应宽度
        EditTextPreference lyricMaxWidth = (EditTextPreference) findPreference("lyricMaxWidth");
        assert lyricMaxWidth != null;
        lyricMaxWidth.setEnabled(String.valueOf(config.getLyricWidth()).equals("-1"));
        lyricMaxWidth.setSummary((String.valueOf(config.getLyricMaxWidth())));
        if (String.valueOf(config.getLyricMaxWidth()).equals("-1")) {
            lyricMaxWidth.setSummary("关闭");
        }
        lyricMaxWidth.setDialogMessage("(-1~100，-1为关闭，仅在歌词宽度为自适应时生效)，当前:" + lyricMaxWidth.getSummary());
        lyricMaxWidth.setOnPreferenceChangeListener((preference, newValue) -> {
            lyricMaxWidth.setDialogMessage("(-1~100，-1为关闭，仅在歌词宽度为自适应时生效)，当前:自适应");
            lyricMaxWidth.setSummary("自适应");
            config.setLyricMaxWidth(-1);
            try {
                String value = newValue.toString().replaceAll(" ", "").replaceAll("\n", "");
                if (value.equals("-1")) {
                    return true;
                } else if (Integer.parseInt(value) <= 100 && Integer.parseInt(value) >= 0) {
                    config.setLyricMaxWidth(Integer.parseInt(value));
                    lyricMaxWidth.setSummary(value);
                } else {
                    Toast.makeText(activity, "范围输入错误，恢复默认", Toast.LENGTH_LONG).show();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(activity, "范围输入错误，恢复默认", Toast.LENGTH_LONG).show();
            }
            return true;
        });


        // 歌词宽度
        EditTextPreference lyricWidth = (EditTextPreference) findPreference("lyricWidth");
        assert lyricWidth != null;
        lyricWidth.setSummary(String.valueOf(config.getLyricWidth()));
        if (String.valueOf(config.getLyricWidth()).equals("-1")) {
            lyricWidth.setSummary("自适应");
        }
        lyricWidth.setDefaultValue(String.valueOf(config.getLyricWidth()));
        lyricWidth.setDialogMessage("(-1~100，-1为自适应)，当前：" + lyricWidth.getSummary());
        lyricWidth.setOnPreferenceChangeListener((preference, newValue) -> {
            String value = newValue.toString().replaceAll(" ", "").replaceAll("\n", "");
            lyricMaxWidth.setEnabled(true);
            lyricWidth.setSummary("自适应");
            lyricWidth.setDialogMessage("(-1~100，-1为自适应)，当前：自适应");
            config.setLyricWidth(-1);

            try {
                if (value.equals("-1")) {
                    return true;
                } else if (Integer.parseInt(value) <= 100 && Integer.parseInt(value) >= 0) {
                    config.setLyricWidth(Integer.parseInt(value));
                    lyricWidth.setSummary(value);
                    lyricMaxWidth.setEnabled(false);
                    lyricWidth.setDialogMessage("(-1~100，-1为自适应)，当前：" + value);
                } else {
                    Toast.makeText(activity, "范围输入错误，恢复默认", Toast.LENGTH_LONG).show();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(activity, "范围输入错误，恢复默认", Toast.LENGTH_LONG).show();
            }
            return true;
        });


        // 歌词颜色
        EditTextPreference lyricColour = (EditTextPreference) findPreference("lyricColour");
        assert lyricColour != null;
        lyricColour.setSummary(config.getLyricColor());
        if (config.getLyricColor().equals("off")) {
            lyricColour.setSummary("自适应");
        }
        lyricColour.setDefaultValue(String.valueOf(config.getLyricColor()));
        lyricColour.setDialogMessage("请输入16进制颜色代码，例如: #C0C0C0，目前：" + config.getLyricColor());
        lyricColour.setOnPreferenceChangeListener((preference, newValue) -> {
            String value = newValue.toString().replaceAll(" ", "");
            if (value.equals("") | value.equals("关闭") | value.equals("自适应")) {
                config.setLyricColor("off");
                lyricColour.setSummary("自适应");
            } else {
                try {
                    Color.parseColor(newValue.toString());
                    config.setLyricColor(newValue.toString());
                    lyricColour.setSummary(newValue.toString());
                } catch (Exception e) {
                    config.setLyricColor("off");
                    lyricColour.setSummary("自适应");
                    Toast.makeText(activity, "颜色代码不正确!", Toast.LENGTH_LONG).show();
                }
            }
            return true;
        });


        // 歌词图标
        SwitchPreference icon = (SwitchPreference) findPreference("lyricIcon");
        assert icon != null;
        icon.setChecked(config.getIcon());
        icon.setOnPreferenceChangeListener((preference, newValue) -> {
            config.setIcon((Boolean) newValue);
            return true;
        });

        // 歌词仅显示一次
        SwitchPreference lshowonce = (SwitchPreference) findPreference("lshowonce");
        assert lshowonce != null;
        lshowonce.setChecked(config.getLShowOnce());
        lshowonce.setOnPreferenceChangeListener((preference, newValue) -> {
            config.setLShowOnce((Boolean) newValue);
            return true;
        });

        // 歌词时间切换
        SwitchPreference lyricSwitch = (SwitchPreference) findPreference("lyricSwitch");
        assert lyricSwitch != null;
        lyricSwitch.setChecked(config.getLyricSwitch());
        lyricSwitch.setOnPreferenceChangeListener((preference, newValue) -> {
            config.setLyricSwitch((Boolean) newValue);
            return true;
        });


        // 防烧屏
        SwitchPreference antiburn = (SwitchPreference) findPreference("antiburn");
        assert antiburn != null;
        antiburn.setChecked(config.getAntiBurn());
        antiburn.setOnPreferenceChangeListener((preference, newValue) -> {
            config.setAntiBurn((Boolean) newValue);
            return true;
        });


        // 图标路径
        Preference iconPath = findPreference("iconPath");
        assert iconPath != null;
        iconPath.setSummary(config.getIconPath());
        if (config.getIconPath().equals(Utils.PATH)) {
            iconPath.setSummary("默认路径");
        }
        iconPath.setOnPreferenceClickListener(((preBuference) -> {
            new AlertDialog.Builder(activity)
                    .setTitle("图标路径")
                    .setNegativeButton("恢复默认路径", (dialog, which) -> {
                        iconPath.setSummary("默认路径");
                        config.setIconPath(Utils.PATH);
                        ActivityUtils.initIcon(activity);
                    })
                    .setPositiveButton("选择新路径", (dialog, which) -> {
                        ChooseFileUtils chooseFileUtils = new ChooseFileUtils(activity);
                        chooseFileUtils.chooseFolder(new ChooseFileUtils.ChooseListener() {
                            @Override
                            public void onSuccess(String filePath, Uri uri, Intent intent) {
                                super.onSuccess(filePath, uri, intent);
                                config.setIconPath(filePath);
                                iconPath.setSummary(filePath);
                                if (config.getIconPath().equals(Utils.PATH)) {
                                    iconPath.setSummary("默认路径");
                                }
                                ActivityUtils.initIcon(activity);
                            }
                        });
                    })
                    .create()
                    .show();


            return true;
        }));

        // 图标上下位置
        EditTextPreference lyricPosition = (EditTextPreference) findPreference("lyricPosition");
        assert lyricPosition != null;
        lyricPosition.setSummary((String.valueOf(config.getLyricPosition())));
        if (String.valueOf(config.getLyricPosition()).equals("2")) {
            lyricPosition.setSummary("默认");
        }
        lyricPosition.setDialogMessage("-100~100，当前:" + lyricPosition.getSummary());
        lyricPosition.setOnPreferenceChangeListener((preference, newValue) -> {
            lyricPosition.setDialogMessage("-100~100，当前:默认");
            lyricPosition.setSummary("默认");
            try {
                String value = newValue.toString().replaceAll(" ", "").replaceAll("\n", "");
                if (value.equals("2")) {
                    return true;
                } else if (Integer.parseInt(value) <= 100 && Integer.parseInt(value) >= -100) {
                    config.setLyricPosition(Integer.parseInt(value));
                    lyricPosition.setSummary(value);
                } else {
                    Toast.makeText(activity, "范围输入错误，恢复默认", Toast.LENGTH_LONG).show();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(activity, "范围输入错误，恢复默认", Toast.LENGTH_LONG).show();
            }
            return true;
        });

        // 图标反色
        SwitchPreference iconColor = (SwitchPreference) findPreference("iconAutoColor");
        assert iconColor != null;
        iconColor.setSummary("关闭");
        if (config.getIconAutoColor()) {
            iconColor.setSummary("开启");
        }
        iconColor.setOnPreferenceChangeListener((preference, newValue) -> {
            config.setIconAutoColor((boolean) newValue);
            return true;
        });


        // 锁屏隐藏
        SwitchPreference lockScreenOff = (SwitchPreference) findPreference("lockScreenOff");
        assert lockScreenOff != null;
        lockScreenOff.setChecked(config.getLockScreenOff());
        lockScreenOff.setOnPreferenceChangeListener((preference, newValue) -> {
            config.setLockScreenOff((Boolean) newValue);
            return true;
        });

        // 隐藏通知图标
        SwitchPreference hNoticeIcon = (SwitchPreference) findPreference("hNoticeIcon");
        assert hNoticeIcon != null;
        hNoticeIcon.setChecked(config.getHNoticeIco());
        hNoticeIcon.setOnPreferenceChangeListener((preference, newValue) -> {
            config.sethNoticeIcon((Boolean) newValue);
            return true;
        });


        // 隐藏实时网速
        SwitchPreference hNetWork = (SwitchPreference) findPreference("hNetWork");
        assert hNetWork != null;
        hNetWork.setChecked(config.getHNetSpeed());
        hNetWork.setOnPreferenceChangeListener((preference, newValue) -> {
            config.sethNetSpeed((Boolean) newValue);
            return true;
        });


        // 隐藏运营商名称
        SwitchPreference hCUK = (SwitchPreference) findPreference("hCUK");
        assert hCUK != null;
        hCUK.setChecked(config.getHCUK());
        hCUK.setOnPreferenceChangeListener((preference, newValue) -> {
            config.sethCUK((Boolean) newValue);
            return true;
        });

        // 文件传输歌词
        SwitchPreference fileLyric = (SwitchPreference) findPreference("fileLyric");
        assert fileLyric != null;
        fileLyric.setChecked(config.getFileLyric());
        fileLyric.setOnPreferenceChangeListener((preference, newValue) -> {
            config.setFileLyric((Boolean) newValue);
            return true;
        });


        // 隐藏闹钟图标
//        SwitchPreference hAlarm = (SwitchPreference) findPreference("hAlarm");
//        assert hAlarm != null;
//        hAlarm.setChecked(config.getHAlarm());
//        hAlarm.setOnPreferenceChangeListener((preference, newValue) -> {
//            if (!(Boolean) newValue) {
//                setIAlarm("settings delete secure icon_blacklist");
//            }
//            config.setHAlarm((Boolean) newValue);
//            return true;
//        });
        // 自定义Hook
        Preference hook = findPreference("lyricHook");
        assert hook != null;
        hook.setSummary(config.getHook());
        if (config.getHook().equals("")) {
            hook.setSummary("默认Hook点");
        }
        hook.setOnPreferenceClickListener((preBuference) -> {
            EditText editText = new EditText(activity);
            editText.setText(config.getHook());
            new AlertDialog.Builder(activity)
                    .setTitle("自定义Hook点")
                    .setView(editText)
                    .setNegativeButton("恢复默认", (dialog, which) -> {
                        hook.setSummary("默认Hook点");
                        config.setHook("");
                        Utils.showToastOnLooper(activity, "已恢复默认Hook点,请重启SystemUI");
                    })
                    .setPositiveButton("确定", (dialog, which) -> {
                        config.setHook(editText.getText().toString());
                        hook.setSummary(editText.getText().toString());
                        if (config.getHook().equals("")) {
                            hook.setSummary("默认Hook点");
                        }
                        Utils.showToastOnLooper(activity, "已设置Hook点为: " + config.getHook() + " 请重启SystemUI");
                    })
                    .create()
                    .show();
            return true;
        });

        // Debug模式
        SwitchPreference debug = (SwitchPreference) findPreference("debug");
        assert debug != null;
        debug.setChecked(config.getDebug());
        debug.setOnPreferenceChangeListener((preference, newValue) -> {
            config.setDebug((Boolean) newValue);
            return true;
        });

        // 使用统计
        SwitchPreference isUsedCount = (SwitchPreference) findPreference("isusedcount");
        assert isUsedCount != null;
        isUsedCount.setChecked(config.getisUsedCount());
        isUsedCount.setOnPreferenceChangeListener((preference, newValue) -> {
            config.setisUsedCount((Boolean) newValue);
            if (!(Boolean) newValue) {
                setTitle(getString(R.string.app_name));
            }
            return true;
        });

        // 重启SystemUI
        Preference reSystemUI = findPreference("restartUI");
        assert reSystemUI != null;
        reSystemUI.setOnPreferenceClickListener((preference) -> {
            new AlertDialog.Builder(activity)
                    .setTitle("确定重启系统界面吗？")
                    .setMessage("若使用中突然发现不能使用，可尝试重启系统界面。")
                    .setPositiveButton("确定", (dialog, which) -> ShellUtils.voidShell("pkill -f com.android.systemui", true))
                    .setNegativeButton("取消", null)
                    .create()
                    .show();
            return true;
        });

        // 重置插件
        Preference reset = findPreference("reset");
        assert reset != null;
        reset.setOnPreferenceClickListener((preference) -> {
            new AlertDialog.Builder(activity)
                    .setTitle("是否要重置模块")
                    .setMessage("模块没问题请不要随意重置")
                    .setPositiveButton("确定", (dialog, which) -> ActivityUtils.cleanConfig(activity))
                    .setNegativeButton("取消", null)
                    .create()
                    .show();
            return true;
        });


        //检查更新
        Preference checkUpdate = findPreference("CheckUpdate");
        assert checkUpdate != null;
        checkUpdate.setSummary("当前版本: " + ActivityUtils.getLocalVersion(activity));
        checkUpdate.setOnPreferenceClickListener((preference) -> {
            Toast.makeText(activity, "开始检查是否有更新", Toast.LENGTH_LONG).show();
            ActivityUtils.checkUpdate(activity);
            return true;
        });

        // 关于activity
        Preference about = findPreference("about");
        assert about != null;
        about.setOnPreferenceClickListener((preference) -> {
            startActivity(new Intent(activity, AboutActivity.class));
            return true;
        });


//        非MIUI关闭功能
        if (!Utils.hasMiuiSetting) {
            hNoticeIcon.setEnabled(false);
            hNoticeIcon.setChecked(false);
            hNoticeIcon.setSummary(hNoticeIcon.getSummary() + " (您不是MIUI系统)");
            config.sethNoticeIcon(false);
            hNetWork.setEnabled(false);
            hNetWork.setChecked(false);
            hNetWork.setSummary(hNetWork.getSummary() + " (您不是MIUI系统)");
            config.sethNoticeIcon(false);
            hCUK.setEnabled(false);
            hCUK.setChecked(false);
            hCUK.setSummary(hCUK.getSummary() + " (您不是MIUI系统)");
            config.sethNoticeIcon(false);
        }

        Handler titleUpdate = new Handler(Looper.getMainLooper(), message -> {
            setTitle("已获取歌词数句数：" + new Config().getUsedCount());
            return false;
        });
        new Thread(() -> {
            while (true) {
                if (new Config().getisUsedCount()) {
                    titleUpdate.sendEmptyMessage(0);
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        ActivityUtils.setData(activity);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == 0) {
            ActivityUtils.init(activity);
            ActivityUtils.initIcon(activity);
        } else {
            new AlertDialog.Builder(activity)
//                    .setIcon(R.mipmap.ic_launcher)
                    .setTitle("获取存储权限失败")
                    .setMessage("请开通存储权限\n否则无法正常使用本模块\n若不信任本模块,请卸载")
                    .setNegativeButton("重新申请", (dialog, which) -> ActivityUtils.checkPermissions(activity))
                    .setPositiveButton("推出", (dialog, which) -> finish())
                    .setNeutralButton("前往设置授予权限", (dialog, which) -> {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                .setData(Uri.fromParts("package", getPackageName(), null));
                        startActivityForResult(intent, 13131);
                    })
                    .setCancelable(false)
                    .create()
                    .show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 13131) {
            ActivityUtils.checkPermissions(activity);
        }
    }

}
