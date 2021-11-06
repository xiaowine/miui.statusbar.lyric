package miui.statusbar.lyric;

import android.annotation.SuppressLint;
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
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.Toast;
import de.robv.android.xposed.XposedBridge;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

@SuppressWarnings("unused")
public class Utils {

    public static String PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/media/miui.statusbar.lyric/";
    public static boolean hasMiuiSetting = isPresent("android.provider.MiuiSettings");
    @SuppressLint("StaticFieldLeak")
    public static Context context = null;
    public static boolean hasXposed = false;

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


    public static Boolean getIsEuMiui() {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader
                    (Runtime.getRuntime().exec("getprop ro.product.mod_device").getInputStream()), 1024);
            String ver = bufferedReader.readLine();
            bufferedReader.close();
            return !ver.replaceAll("\n", "").equals("");
        } catch (Exception e) {
            return false;
        }
    }

    public static void setIAlarm(String s) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader
                    (Runtime.getRuntime().exec("settings get secure icon_blacklist").getInputStream()), 1024);
            String icons = bufferedReader.readLine();
            bufferedReader.close();
            icons.replaceAll("\n", "");
        } catch (Exception ignored) {

        }
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
                config.sethNetSpeed(false);
                config.sethCUK(false);
                config.setHAlarm(false);
                config.setDebug(false);
                config.setisUsedCount(true);
                config.setAnim("off");
                config.setHook("");
                config.setLyricPosition(2);
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


    // log
    @SuppressWarnings("unused")
    public static void log(String text) {
        if (new Config().getDebug()) {
            if (context == null) {
                if (hasXposed) {
                    XposedBridge.log("MIUI状态栏歌词： " + text);
                } else {
                    Log.d("MIUI状态栏歌词", text);
                }
            } else {
                Log.d("MIUI状态栏歌词", text);
                if (Utils.context != null) {
                    showToastOnLooper(Utils.context, "MIUI状态栏歌词： " + text);
                }
            }
        }
    }

    // 判断服务是否运行
    @SuppressWarnings("unused")
    public static boolean isServiceRunning(Context context, String str) {
        List<ActivityManager.RunningServiceInfo> runningServices = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getRunningServices(200);
        if (runningServices.size() <= 0) {
            return false;
        }
        for (ActivityManager.RunningServiceInfo runningServiceInfo : runningServices) {
            if (runningServiceInfo.service.getClassName().contains(str)) {
                Utils.log("服务运行: " + str);
                return true;
            }
        }
        return false;
    }

    // 判断程序是否运行
    @SuppressWarnings("unused")
    public static boolean isAppRunning(Context context, String str) {
        if (isServiceRunning(context, str)) {
            return true;
        }
        List<ActivityManager.RunningTaskInfo> runningServices = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getRunningTasks(200);
        if (runningServices.size() <= 0) {
            return false;
        }
        for (ActivityManager.RunningTaskInfo runningServiceInfo : runningServices) {
            if (runningServiceInfo.baseActivity.getClassName().contains(str)) {
                Utils.log("程序运行: " + str);
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unused")
    public static void setStatusBar(Context application, Boolean isOpen) {
        if (!hasMiuiSetting) {
            return;
        }
        Config config = new Config();
        int isCarrier = 1;
        int notCarrier = 0;
        if (isOpen) {
            isCarrier = 0;
            notCarrier = 1;
        }

//        if (config.getHAlarm() && !isOpen) {
//            setIAlarm("settings put secure icon_blacklist alarm_clock");
//        } else {
//            if (config.getHAlarm()) {
//                setIAlarm("settings delete secure icon_blacklist");
//            }
//        }
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
        if (new Config().getFileLyric()) {
            Utils.setLyricFile(icon, lyric);
        } else {
            context.sendBroadcast(new Intent().setAction("Lyric_Server").putExtra("Lyric_Data", lyric).putExtra("Lyric_Icon", icon).putExtra("Lyric_Type", "hook"));
        }
    }

    public static void addLyricCount() {
        if (new Config().getisUsedCount()) {
            new Config().setUsedCount(new Config().getUsedCount() + 1);
        }
    }

    // 判断服务是否运行 列表
    @SuppressWarnings("unused")
    public static boolean isServiceRunningList(Context context, String[] str) {
        for (String mStr : str) {
            if (mStr != null) {
                if (isAppRunning(context, mStr)) {
                    return true;
                }
            }
        }
        return false;
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

    public static String[] stringsListAdd(String[] strArr, String newStr) {
        String[] newStrArr = new String[strArr.length + 1];
        System.arraycopy(strArr, 0, newStrArr, 0, strArr.length);
        newStrArr[strArr.length] = newStr;
        return newStrArr;
    }

    public static Animation inAnim(String str) {
        AnimationSet animationSet = new AnimationSet(true);
        TranslateAnimation translateAnimation;
        switch (str) {
            case "top":
                translateAnimation = new TranslateAnimation(0, 0, 100, 0);
                break;
            case "lower":
                translateAnimation = new TranslateAnimation(0, 0, -100, 0);
                break;
            case "left":
                translateAnimation = new TranslateAnimation(100, 0, 0, 0);
                break;
            case "right":
                translateAnimation = new TranslateAnimation(-100, 0, 0, 0);
                break;
            case "random":
                return inAnim(new String[]{
                        "off", "top", "lower",
                        "left", "right", "random"
                }[
                        (int) (Math.random() * 4)
                        ]);
            default:
                return null;
        }
        // 设置动画300ms
        translateAnimation.setDuration(300);

        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        // 设置动画300ms
        alphaAnimation.setDuration(300);

        animationSet.addAnimation(translateAnimation);
        animationSet.addAnimation(alphaAnimation);
        return animationSet;
    }

    public static Animation outAnim(String str) {
        AnimationSet animationSet = new AnimationSet(true);
        TranslateAnimation translateAnimation = null;
        switch (str) {
            case "top":
                translateAnimation = new TranslateAnimation(0, 0, 0, -100);
                break;
            case "lower":
                translateAnimation = new TranslateAnimation(0, 0, 0, 100);
                break;
            case "left":
                translateAnimation = new TranslateAnimation(0, -100, 0, 0);
                break;
            case "right":
                translateAnimation = new TranslateAnimation(0, 100, 0, 0);
                break;
            case "random":
                return outAnim(new String[]{
                        "off", "top", "lower",
                        "left", "right", "random"
                }[
                        (int) (Math.random() * 4)
                        ]);
            default:
                return null;
        }
        // 设置动画300ms
        translateAnimation.setDuration(300);

        AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
        // 设置动画300ms
        alphaAnimation.setDuration(300);

        animationSet.addAnimation(translateAnimation);
        animationSet.addAnimation(alphaAnimation);
        return animationSet;
    }

    // 打印堆栈
    public static void dumpStack() {
        log("Dump Stack: ---------------start----------------");
        Throwable ex = new Throwable();
        StackTraceElement[] stackElements = ex.getStackTrace();
        if (stackElements != null) {
            for (int i = 0; i < stackElements.length; i++) {

                log("Dump Stack" + i + ": " + stackElements[i].getClassName()
                        + "----" + stackElements[i].getFileName()
                        + "----" + stackElements[i].getLineNumber()
                        + "----" + stackElements[i].getMethodName());
            }
        }
        log("Dump Stack: ---------------over----------------");
    }

    // 报错转内容
    public static String dumpException(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }

    // 报错转内容
    public static String dumpNoSuchFieldError(NoSuchFieldError e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }

    // 判断class是否存在
    public static boolean isPresent(String name) {
        try {
            Thread.currentThread().getContextClassLoader().loadClass(name);
            Log.d("MIUI状态栏歌词", name + " class存在");
            return true;
        } catch (ClassNotFoundException e) {
            Log.d("MIUI状态栏歌词", name + " class不存在");
            return false;
        }
    }

    public static String[] getLyricFile() {
        String[] res = {"", ""};
        try {
            StringBuilder stringBuffer = new StringBuilder();
            // 打开文件输入流
            FileInputStream fileInputStream = new FileInputStream(PATH + "lyric.txt");

            byte[] buffer = new byte[1024];
            int len = fileInputStream.read(buffer);
            // 读取文件内容
            while (len > 0) {
                stringBuffer.append(new String(buffer, 0, len));
                // 继续将数据放到buffer中
                len = fileInputStream.read(buffer);
            }
            String json = stringBuffer.toString();
            log("获取歌词 " + json);
            JSONArray jsonArray = new JSONArray(json);
            res = new String[]{(String) jsonArray.get(0), (String) jsonArray.get(1)};
        } catch (FileNotFoundException ignored) {
        } catch (Exception e) {
            log("歌词读取错误: " + e + "\n" + dumpException(e));
        }
        return res;
    }

    public static void setLyricFile(String app_name, String lyric) {
        try {
            FileOutputStream outputStream = new FileOutputStream(PATH + "lyric.txt");
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(app_name);
            jsonArray.put(lyric);
            String json = jsonArray.toString();
            log("设置歌词 " + json);
            outputStream.write(json.getBytes());
            outputStream.close();
        } catch (Exception e) {
            log("写歌词错误: " + e + "\n" + dumpException(e));
        }
    }

}

