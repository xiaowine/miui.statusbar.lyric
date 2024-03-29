package miui.statusbar.lyric.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.widget.Toast;
import miui.statusbar.lyric.Config;
import miui.statusbar.lyric.R;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class ActivityUtils {

    public static String getLocalVersion(Context context) {
        String localVersion = "";
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            localVersion = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return localVersion;
    }

    public static void checkPermissions(Activity activity) {
        if (checkSelfPermission(activity) == -1) {
            activity.requestPermissions(new String[]{
                    "android.permission.WRITE_EXTERNAL_STORAGE"
            }, 1);
        } else {
            init(activity);
            initIcon(activity);
        }
    }

    private static int checkSelfPermission(Context context) {
        return context.checkPermission("android.permission.WRITE_EXTERNAL_STORAGE", android.os.Process.myPid(), Process.myUid());
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void init(Activity activity) {
        File file = new File(Utils.PATH);
        File file2 = new File(Utils.PATH + "Config.json");
        if (!file.exists()) {
            file.mkdirs();
        }
        if (!file2.exists()) {
            try {
                Config config = new Config();
                file2.createNewFile();
                config.setId(1);
                config.setUsedCount(0);
                config.setLyricService(false);
                config.setLyricAutoOff(true);
                config.setLyricSwitch(false);
                config.setLyricWidth(-1);
                config.setLyricMaxWidth(-1);
                config.setAnim("off");
                config.setLyricColor("off");
                config.setIcon(true);
                config.setLShowOnce(false);
                config.setLyricPosition(2);
                config.setIconPath(Utils.PATH);
                config.setIconAutoColor(true);
                config.setLockScreenOff(false);
                config.sethNoticeIcon(false);
                config.sethNetSpeed(false);
                config.sethCUK(false);
                config.setHAlarm(false);
                config.setDebug(false);
                config.setisUsedCount(true);
                config.setHook("");

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(activity, "初始化失败，请重启软件", Toast.LENGTH_LONG).show();
            }
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void initIcon(Activity activity) {
        String[] IconList = {"kugou.webp", "netease.webp", "qqmusic.webp", "myplayer.webp", "migu.webp"};
        Config config = new Config();
        for (String s : IconList) {
            if (!new File(config.getIconPath(), s).exists()) {
                copyAssets(activity, "icon/" + s, config.getIconPath() + s);
            }
        }
//        if (!new File(config.getIconPath(), "kugou.webp").exists()) {
//            copyAssets(activity, "icon/kugou.webp", config.getIconPath() + "kugou.webp");
//        }
//        if (!new File(config.getIconPath(), "netease.webp").exists()) {
//            copyAssets(activity, "icon/netease.webp", config.getIconPath() + "netease.webp");
//        }
//        if (!new File(config.getIconPath(), "qqmusic.webp").exists()) {
//            copyAssets(activity, "icon/qqmusic.webp", config.getIconPath() + "qqmusic.webp");
//        }
//        if (!new File(config.getIconPath(), "kuwo.webp").exists()) {
//            copyAssets(activity, "icon/kuwo.webp", config.getIconPath() + "kuwo.webp");
//        }
//        if (!new File(config.getIconPath(), "myplayer.webp").exists()) {
//            copyAssets(activity, "icon/myplayer.webp", config.getIconPath() + "myplayer.webp");
//        }
//        if (!new File(config.getIconPath(), "migu.webp").exists()) {
//            copyAssets(activity, "icon/migu.webp", config.getIconPath() + "migu.webp");
//        }
        if (!new File(config.getIconPath(), ".nomedia").exists()) {
            try {
                new File(config.getIconPath(), ".nomedia").createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void copyAssets(Activity activity, String str, String str2) {
        try {
            File file = new File(str2);
            InputStream open = activity.getAssets().open(str);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            byte[] bArr = new byte[1024];
            while (true) {
                int read = open.read(bArr);
                if (read == -1) {
                    fileOutputStream.flush();
                    open.close();
                    fileOutputStream.close();
                    return;
                }
                fileOutputStream.write(bArr, 0, read);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void checkUpdate(Activity activity) {
        Handler handler = new Handler(Looper.getMainLooper(), message -> {
            String data = message.getData().getString("value");
            try {
                JSONObject jsonObject = new JSONObject(data);
                if (!getLocalVersion(activity).equals("")) {
                    if (Integer.parseInt(jsonObject.getString("tag_name").split("v")[1])
                            > Utils.getLocalVersionCode(activity)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        builder.setTitle("发现新版本[" + jsonObject.getString("name") + "]")
                                .setIcon(R.mipmap.ic_launcher)
                                .setMessage(jsonObject.getString("body").replace("#", ""))
                                .setPositiveButton("更新", (dialog, which) -> {
                                    try {
                                        Uri uri = Uri.parse(jsonObject.getJSONArray("assets").getJSONObject(0).getString("browser_download_url"));
                                        Intent intent = new Intent();
                                        intent.setAction("android.intent.action.VIEW");
                                        intent.setData(uri);
                                        activity.startActivity(intent);
                                    } catch (JSONException e) {
                                        Toast.makeText(activity, "获取最新版下载地址失败: " + e, Toast.LENGTH_LONG).show();
                                    }

                                }).setNegativeButton("取消", null).create().show();
                    } else {
                        Toast.makeText(activity, "无新版可更新", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(activity, "检查失败，请稍后再试!", Toast.LENGTH_LONG).show();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Looper.loop();
            return true;
        });

        new Thread(() -> {
            Looper.prepare();
            String value = HttpUtils.Get("https://api.github.com/repos/xiaowine/miui.statusbar.lyric/releases/latest");
            if (!value.equals("")) {
                Message message = handler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putString("value", value);
                message.setData(bundle);
                handler.sendMessage(message);
            } else {
                Toast.makeText(activity, "检查更新失败: ", Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }).start();
    }

    public static void cleanConfig(Activity activity) {
        SharedPreferences userSettings = activity.getSharedPreferences("miui.statusbar.lyric_preferences", 0);
        SharedPreferences.Editor editor = userSettings.edit();
        editor.clear();
        editor.apply();
        new File(Utils.PATH + "Config.json").delete();
        PackageManager packageManager = Objects.requireNonNull(activity).getPackageManager();
        packageManager.setComponentEnabledSetting(new ComponentName(activity, "miui.statusbar.lyric.launcher"), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

        Toast.makeText(activity, "重置成功", Toast.LENGTH_LONG).show();
        activity.finishAffinity();
    }

    public static void checkConfig(final Activity activity, int id) {
        if (id == 0) {
            try {
//                Handler handler = new Handler(Looper.getMainLooper());
//                handler.post(() ->
                new AlertDialog.Builder(activity)
                        .setTitle("警告")
                        .setMessage("配置文件错误\n可能是文件内容丢失或者配置文件有所升级\n可能造成不必要的问题\n是否重置")
                        .setNegativeButton("立即重置", (dialog, which) -> cleanConfig(activity))
                        .setPositiveButton("先不重置", null)
                        .setCancelable(false)
                        .create()
//                        .show());
                        .show();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
    }

    public static void setData(Activity activity) {
        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String data = HttpUtils.Get("https://io.xiaowine.cc/app/data.json");
            try {
                JSONObject jsonObject = new JSONObject(data);
                SharedPreferences preferences = activity.getSharedPreferences("dataId", 0);
                int dataId=preferences.getInt("id",0);
                if (Integer.parseInt(jsonObject.getString("id"))
                        > dataId) {
                    SharedPreferences.Editor preferenceEditor = preferences.edit();
                    preferenceEditor.putInt("id", Integer.parseInt(jsonObject.getString("id")));
                    preferenceEditor.putString("data", jsonObject.getString("data"));
                    preferenceEditor.apply();
                    Looper.prepare();
                    Toast.makeText(activity, jsonObject.getString("id"), Toast.LENGTH_LONG).show();
                    Looper.loop();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                 Looper.prepare();
                    Toast.makeText(activity, e.toString(), Toast.LENGTH_LONG).show();
                    Looper.loop();

            }
        }).start();
    }
}
