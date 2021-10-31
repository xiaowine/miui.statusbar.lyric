package miui.statusbar.lyric;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.MiuiStatusBarManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;
import de.robv.android.xposed.XposedBridge;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

@SuppressWarnings("unused")
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

    public static String getMiuiVer() {
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

    public static void setIAlarm(String s) {
        try {
            java.lang.Process p = Runtime.getRuntime().exec("su");
            OutputStream outputStream = p.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
            dataOutputStream.writeBytes(s);
            dataOutputStream.flush();
            dataOutputStream.close();
            outputStream.close();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @SuppressWarnings("unused")
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

    @SuppressWarnings("unused")
    private static boolean shouldShowRequestPermissionRationale(Activity activity) {
        return activity.shouldShowRequestPermissionRationale("android.permission.WRITE_EXTERNAL_STORAGE");
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
                config.setUsedCount(0);
                config.setLyricService(false);
                config.setLyricAutoOff(true);
                config.setLyricSwitch(false);
                config.setLyricWidth(-1);
                config.setLyricMaxWidth(-1);
                config.setLyricColor("off");
                config.setIcon(true);
                config.setLShowOnce(false);
                config.setIconPath(PATH);
                config.setIconAutoColor(true);
                config.sethNoticeIcon(false);
                config.sethNetSpeed(true);
                config.sethCUK(false);
                config.setHAlarm(true);
                config.setDebug(false);
                config.setisUsedCount(true);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(activity, "初始化失败，请重启软件", Toast.LENGTH_LONG).show();
            }
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void initIcon(Activity activity) {
        Config config = new Config();
        if (!new File(config.getIconPath(), "kugou.webp").exists()) {
            copyAssets(activity, "icon/kugou.webp", config.getIconPath() + "kugou.webp");
        }
        if (!new File(config.getIconPath(), "netease.webp").exists()) {
            copyAssets(activity, "icon/netease.webp", config.getIconPath() + "netease.webp");
        }
        if (!new File(config.getIconPath(), "qqmusic.webp").exists()) {
            copyAssets(activity, "icon/qqmusic.webp", config.getIconPath() + "qqmusic.webp");
        }
        if (!new File(config.getIconPath(), "kuwo.webp").exists()) {
            copyAssets(activity, "icon/kuwo.webp", config.getIconPath() + "kuwo.webp");
        }
        if (!new File(config.getIconPath(), "myplayer.webp").exists()) {
            copyAssets(activity, "icon/myplayer.webp", config.getIconPath() + "myplayer.webp");
        }
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


    //    MainHook
    @SuppressWarnings("unused")
    public static void log(String text) {
        if (new Config().getDebug()) {
            XposedBridge.log("MIUI状态栏歌词： " + text);
        }
    }

    @SuppressWarnings("unused")
    public static boolean isServiceRunning(Context context, String str) {
        List<ActivityManager.RunningServiceInfo> runningServices = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getRunningServices(200);
        if (runningServices.size() <= 0) {
            return false;
        }
        for (ActivityManager.RunningServiceInfo runningServiceInfo : runningServices) {
            if (runningServiceInfo.service.getClassName().contains(str)) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unused")
    public static void setStatusBar(Context application, Boolean isOpen) {
        Config config = new Config();
        int isCarrier = 1;
        int notCarrier = 0;
        if (isOpen) {
            isCarrier = 0;
            notCarrier = 1;
        }
        if (config.getHAlarm() && !isOpen) {
            setIAlarm("settings put secure icon_blacklist alarm_clock");
        } else {
            setIAlarm("settings delete secure icon_blacklist");
        }
        if (config.getHNoticeIco() && MiuiStatusBarManager.isShowNotificationIcon(application) != isOpen) {
            MiuiStatusBarManager.setShowNotificationIcon(application, isOpen);
        }
        if (config.getHNetSpeed() && MiuiStatusBarManager.isShowNetworkSpeed(application) != isOpen) {
            MiuiStatusBarManager.setShowNetworkSpeed(application, isOpen);
        }
        if (config.getHCUK() && Settings.System.getInt(application.getContentResolver(), "status_bar_show_carrier_under_keyguard", 1) == isCarrier) {
            Settings.System.putInt(application.getContentResolver(), "status_bar_show_carrier_under_keyguard", notCarrier);
        }
    }

    @SuppressWarnings("unused")
    public static Drawable reverseColor(Drawable icon, Boolean black) {
        ColorMatrix cm = new ColorMatrix();
        if (black) {
            cm.set(new float[]{
                    -1f, 0f, 0f, 0f, 255f,
                    0f, -1f, 0f, 0f, 255f,
                    0f, 0f, -1f, 0f, 255f,
                    0f, 0f, 0f, 1f, 0f
            });
        }
        icon.setColorFilter(new ColorMatrixColorFilter(cm));
        return icon;
    }

    @SuppressWarnings("unused")
    public static boolean isDark(int color) {
        return ColorUtils.calculateLuminance(color) < 0.5;
    }

    @SuppressWarnings("unused")
    public static void sendLyric(Context context, String lyric, String icon) {
        context.sendBroadcast(new Intent().setAction("Lyric_Server").putExtra("Lyric_Data", lyric).putExtra("Lyric_Icon", icon).putExtra("Lyric_Type", "hook"));
        if (new Config().getisUsedCount()) {
            new Config().setUsedCount(new Config().getUsedCount() + 1);
        }
    }

    // 获取线程名称
    public static String getCurrentProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (mActivityManager != null) {
            for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager.getRunningAppProcesses()) {
                if (appProcess.pid == pid) {
                    return appProcess.processName;
                }
            }
        }
        return "";
    }

    // 弹出toast
    @SuppressWarnings("unused")
    public static void showToastOnLooper(final Context context, final String message) {
        try {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(() -> Toast.makeText(context, message, Toast.LENGTH_LONG).show());
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unused")
    public static Bitmap stringToBitmap(String string) {
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray = Base64.decode(string, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }


}

