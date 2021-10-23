package miui.statusbar.lyric;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

import de.robv.android.xposed.XposedBridge;


@SuppressWarnings("ResultOfMethodCallIgnored")
public class Utils {
    public static String PATH = Environment.getExternalStorageDirectory() + "/Android/media/miui.statusbar.lyric/";

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

    public static int getLocalVersionCode(Context context) {
        int localVersion = 0;
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            localVersion = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return localVersion;
    }

    public static String getMIUIVer() {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader
                    (Runtime.getRuntime().exec("getprop ro.miui.ui.version.name").getInputStream()), 1024);
            String ver = bufferedReader.readLine();
            bufferedReader.close();
            return ver;
        } catch (Exception e) {
            return "";
        }
    }

    public static void checkPermission(Activity activity) {
        if (checkSelfPermission(activity) != 0) {
            if (shouldShowRequestPermissionRationale(activity)) {
                Toast.makeText(activity, "请开通相关权限，否则无法正常使用本应用！", Toast.LENGTH_LONG).show();
            }
            String[] strArr = new String[1];
            strArr[0] = "android.permission.WRITE_EXTERNAL_STORAGE";
            activity.requestPermissions(strArr, 1);

        }
    }

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
                config.setHideLauncherIcon(false);
                config.setLyricService(true);
                config.setLyricAutoOff(true);
                config.setLyricSwitch(false);
                config.setLyricWidth(-1);
                config.setLyricMaxWidth(-1);
                config.setLyricColor("off");
                config.setIcon(true);
                config.setIconPath(PATH);
                config.setIconAutoColor(true);
                config.setHideNoticeIcon(false);
                config.setHideNetSpeed(true);
                config.setHideCUK(false);
                config.setDebug(false);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(activity, "初始化失败，请重启软件", Toast.LENGTH_LONG).show();
            }
        }
    }

    public static void initIcon(Context context) {
        if (!new File(new Config().getIconPath(), "kugou.webp").exists()) {
            copyAssets(context, "icon/kugou.webp", new Config().getIconPath() + "kugou.webp");
        }
        if (!new File(new Config().getIconPath(), "netease.webp").exists()) {
            copyAssets(context, "icon/netease.webp", new Config().getIconPath() + "netease.webp");
        }
        if (!new File(new Config().getIconPath(), "qqmusic.webp").exists()) {
            copyAssets(context, "icon/qqmusic.webp", new Config().getIconPath() + "qqmusic.webp");
        }
        if (!new File(new Config().getIconPath(), "kuwo.webp").exists()) {
            copyAssets(context, "icon/kuwo.webp", new Config().getIconPath() + "kuwo.webp");
        }
        if (!new File(new Config().getIconPath(), "myplayer.webp").exists()) {
            copyAssets(context, "icon/myplayer.webp", new Config().getIconPath() + "myplayer.webp");
        }
        if (!new File(new Config().getIconPath(), ".nomedia").exists()) {
            try {
                new File(new Config().getIconPath(), ".nomedia").createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void copyAssets(Context context, String str, String str2) {
        try {
            File file = new File(str2);
            InputStream open = context.getAssets().open(str);
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

    public static void log(String text) {
        if (new Config().getDebug()) {
            XposedBridge.log("MIUI状态栏歌词： " + text);
        }
    }

    public static void checkUpdate(Activity activity) {
        Handler handler = new Handler(Looper.getMainLooper(), message -> {
            String data = message.getData().getString("value");
            try {
                JSONObject jsonObject = new JSONObject(data);
                if (!getLocalVersion(activity).equals("")) {
//                    if (Integer.parseInt(jsonObject.getString("tag_name").split(" ")[1])
                    if (Integer.parseInt(jsonObject.getString("tag_name").split("v")[1])
                            > getLocalVersionCode(activity)) {
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
            Toast.makeText(activity, "开始检查是否有更新", Toast.LENGTH_LONG).show();
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL("https://api.github.com/repos/xiaowine/miui.statusbar.lyric/releases/latest").openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                InputStream in = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                Message message = handler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putString("value", reader.readLine());
                message.setData(bundle);
                handler.sendMessage(message);
            } catch (Exception e) {
                Toast.makeText(activity, "检查更新失败: " + e, Toast.LENGTH_LONG).show();
                Log.d("checkUpdate: ", e + "");
                e.printStackTrace();
            }
            Looper.loop();
        }).start();
    }

    private static int checkSelfPermission(Context context) {
        return context.checkPermission("android.permission.WRITE_EXTERNAL_STORAGE", android.os.Process.myPid(), Process.myUid());
    }

    private static boolean shouldShowRequestPermissionRationale(Activity activity) {
        return activity.shouldShowRequestPermissionRationale("android.permission.WRITE_EXTERNAL_STORAGE");
    }


}

