package miui.statusbar.lyric.hook;


import android.app.AndroidAppHelper;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import miui.statusbar.lyric.AutoMarqueeTextView;
import miui.statusbar.lyric.Config;
import miui.statusbar.lyric.Utils;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;


public class MainHook implements IXposedHookLoadPackage {
    private static final String KEY_LYRIC = "lyric";
    private static String lyric = "";
    private static String iconPath = "";
    private Context context = null;
    private boolean showLyric = true;


    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        // 获取Context
        XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                context = (Context) param.args[0];
                // 注册广播
                if (lpparam.packageName.equals("com.android.systemui")) {
                    IntentFilter filter = new IntentFilter();
                    filter.addAction("Lyric_Server");
                    context.registerReceiver(new LyricReceiver(), filter);
                }
            }
        });

        switch (lpparam.packageName) {
            case "com.android.systemui":
                Utils.log("正在hook系统界面");
                // 状态栏歌词
                XposedHelpers.findAndHookMethod("com.android.systemui.statusbar.phone.CollapsedStatusBarFragment", lpparam.classLoader, "onViewCreated", View.class, Bundle.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        Field clockField;

                        // 获取当前进程的Application
                        Application application = AndroidAppHelper.currentApplication();

                        // 获取音频管理器
                        AudioManager audioManager = (AudioManager) application.getSystemService(Context.AUDIO_SERVICE);

                        // 获取窗口管理器
                        DisplayMetrics displayMetrics = new DisplayMetrics();
                        ((WindowManager) application.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(displayMetrics);

                        // 获取屏幕宽度
                        int dw = displayMetrics.widthPixels;

                        // 获取系统版本
                        String miuiVer = Utils.getMIUIVer();
                        Utils.log("MIUI Ver: " + miuiVer);

                        // 反射获取时钟
                        if (miuiVer.equals("V12")) {
                            clockField = XposedHelpers.findField(param.thisObject.getClass(), "mStatusClock");
                        } else if (miuiVer.equals("V125")) {
                            clockField = XposedHelpers.findField(param.thisObject.getClass(), "mClockView");
                        } else {
                            Utils.log("Unknown version");
                            clockField = XposedHelpers.findField(param.thisObject.getClass(), "mClockView");
                        }
                        TextView clock = (TextView) clockField.get(param.thisObject);

                        // 创建TextView
                        AutoMarqueeTextView lyricTextView = new AutoMarqueeTextView(application);
                        lyricTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        lyricTextView.setWidth((dw * 35) / 100);
                        lyricTextView.setHeight(clock.getHeight());
                        lyricTextView.setTypeface(clock.getTypeface());
                        lyricTextView.setTextSize(0, clock.getTextSize());
                        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) lyricTextView.getLayoutParams();
                        layoutParams.setMargins(10, 0, 0, 0);
                        lyricTextView.setLayoutParams(layoutParams);

                        // 设置跑马灯效果
                        lyricTextView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                        // 设置跑马灯重复次数，-1为无限重复
                        lyricTextView.setMarqueeRepeatLimit(-1);
                        // 单行显示
                        lyricTextView.setSingleLine(true);
                        lyricTextView.setMaxLines(1);

                        // 将歌词文字加入时钟布局
                        LinearLayout clockLayout = (LinearLayout) clock.getParent();
                        clockLayout.setGravity(Gravity.CENTER);
                        clockLayout.setOrientation(LinearLayout.HORIZONTAL);
                        clockLayout.addView(lyricTextView, 1);

                        // 创建图标
                        TextView iconView = new TextView(application);
                        iconView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        layoutParams = (LinearLayout.LayoutParams) iconView.getLayoutParams();
                        layoutParams.setMargins(0, 2, 0, 0);
                        iconView.setLayoutParams(layoutParams);
                        clockLayout.addView(iconView, 1);

                        // 歌词点击事件
                        if (new Config().getLyricSwitch()) {
                            lyricTextView.setOnClickListener((view) -> {
                                // 显示时钟
                                clock.setLayoutParams(new LinearLayout.LayoutParams(-2, -2));
                                // 歌词隐藏
                                lyricTextView.setVisibility(View.GONE);
                                // 隐藏图标
                                iconView.setVisibility(View.GONE);
                                showLyric = false;
                                clock.setOnClickListener((mView) -> {
                                    // 隐藏时钟
                                    clock.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
                                    // 显示图标
                                    iconView.setVisibility(View.VISIBLE);
                                    // 设置歌词文本
                                    lyricTextView.setText(lyricTextView.getText());
                                    // 歌词显示
                                    lyricTextView.setVisibility(View.VISIBLE);
                                    showLyric = true;
                                });
                            });
                        }

                        final Handler iconUpdate = new Handler(Looper.getMainLooper(), message -> {
                            iconView.setCompoundDrawables((Drawable) message.obj, null, null, null);
                            return true;
                        });

                        // 歌词更新 Handler
                        Handler LyricUpdate = new Handler(Looper.getMainLooper(), message -> {
                            Config config = new Config();
                            String string = message.getData().getString(KEY_LYRIC);
                            if (!string.equals("")) {
                                if (!string.equals(lyricTextView.getText().toString())) {
                                    // 设置状态栏
                                    Utils.setStatusBar(application, false);
                                    // 设置歌词文本
                                    lyricTextView.setText(string);
                                    // 歌词显示
                                    if (showLyric) {
                                        lyricTextView.setVisibility(View.VISIBLE);
                                    }

                                    // 自适应/歌词宽度
                                    if (config.getLyricWidth() == -1) {
                                        TextPaint paint1 = lyricTextView.getPaint(); // 获取字体
                                        if (config.getLyricMaxWidth() == -1 || ((int) paint1.measureText(string)) + 6 <= (dw * config.getLyricMaxWidth()) / 100) {
                                            lyricTextView.setWidth(((int) paint1.measureText(string)) + 6);

                                        } else {
                                            lyricTextView.setWidth((dw * config.getLyricMaxWidth()) / 100);
                                        }
                                    } else {
                                        lyricTextView.setWidth((dw * config.getLyricWidth()) / 100);
                                    }
                                }
                                // 隐藏时钟
                                if (showLyric) {
                                    clock.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
                                }
                                return false;
                            }
                            // 清除图标
                            iconView.setCompoundDrawables(null, null, null, null);
                            // 显示时钟
                            clock.setLayoutParams(new LinearLayout.LayoutParams(-2, -2));
                            // 歌词隐藏
                            lyricTextView.setVisibility(View.GONE);
                            // 清除时钟点击事件
                            if (config.getLyricSwitch()) {
                                clock.setOnClickListener(null);
                            }

                            return true;
                        });
                        new Timer().schedule(
                                new TimerTask() {
                                    boolean enable = false;
                                    Config config = new Config();
                                    ColorStateList color = null;
                                    int count = 0;
                                    int lyricSpeed = 0;
                                    String oldLyric = "";
                                    boolean lyricServer = false;
                                    boolean lyricOff = false;
                                    Boolean iconReverseColor = false;
                                    boolean iconReverseColorStatus = false;

                                    @Override
                                    public void run() {
                                        if (count == 100) {
                                            if (Utils.isServiceRunning(application, "com.kugou") | Utils.isServiceRunning(application, "com.netease.cloudmusic") | Utils.isServiceRunning(application, "com.tencent.qqmusic.service") | Utils.isServiceRunning(application, "cn.kuwo") | Utils.isServiceRunning(application, "com.maxmpz.audioplayer") | Utils.isServiceRunning(application, "remix.myplayer")) {
                                                enable = true;
                                                config = new Config();
                                                lyricServer = config.getLyricService();
                                                if (config.getLyricAutoOff())
                                                    lyricOff = audioManager.isMusicActive();
                                                iconReverseColor = config.getIconAutoColor();
                                                iconReverseColorStatus = true;
                                            } else {
                                                if (enable || (lyricTextView.getVisibility() != View.GONE)) {
                                                    Utils.log("播放器关闭 清除歌词");
                                                    lyric = "";
                                                    enable = false;

                                                    // 关闭歌词
                                                    Message obtainMessage = LyricUpdate.obtainMessage();
                                                    Bundle bundle = new Bundle();
                                                    bundle.putString(KEY_LYRIC, "");
                                                    obtainMessage.setData(bundle);
                                                    LyricUpdate.sendMessage(obtainMessage);

                                                    // 恢复状态栏
                                                    Utils.setStatusBar(application, true);
                                                }
                                            }

                                            if (enable && !lyric.equals("")) {
                                                // 设置颜色
                                                if (!config.getLyricColor().equals("off")) {
                                                    if (color != ColorStateList.valueOf(Color.parseColor(config.getLyricColor()))) {
                                                        color = ColorStateList.valueOf(Color.parseColor(config.getLyricColor()));
                                                        lyricTextView.setTextColor(color);
                                                    }
                                                } else if (!(clock.getTextColors() == null || color == clock.getTextColors())) {
                                                    color = clock.getTextColors();
                                                    lyricTextView.setTextColor(color);
                                                    iconReverseColorStatus = true;
                                                }
                                                if (!iconPath.equals("")) {
                                                    if (iconReverseColorStatus) {
                                                        if (new File(iconPath).exists()) {
                                                            Drawable createFromPath = Drawable.createFromPath(iconPath);
                                                            createFromPath.setBounds(0, 0, (int) clock.getTextSize(), (int) clock.getTextSize());
                                                            if (iconReverseColor) {
                                                                createFromPath = Utils.reverseColor(createFromPath, Utils.isDark(clock.getTextColors().getDefaultColor()));
                                                            }
                                                            Message obtainMessage2 = iconUpdate.obtainMessage();
                                                            obtainMessage2.obj = createFromPath;
                                                            iconUpdate.sendMessage(obtainMessage2);
                                                        }
                                                    }
                                                } else {
                                                    Drawable createFromPath = Drawable.createFromPath(null);
                                                    Message obtainMessage2 = iconUpdate.obtainMessage();
                                                    obtainMessage2.obj = createFromPath;
                                                    iconUpdate.sendMessage(obtainMessage2);
                                                }
                                            }
                                            count = 0;
                                        }
                                        count++;

                                        if (enable && lyricSpeed == 10) {
                                            lyricSpeed = 0;
                                            if (!lyric.equals("") && lyricServer && lyricOff) {
                                                if (!oldLyric.equals(lyric)) {
                                                    Message message = LyricUpdate.obtainMessage();
                                                    Bundle bundle = new Bundle();
                                                    bundle.putString(KEY_LYRIC, lyric);
                                                    message.setData(bundle);
                                                    LyricUpdate.sendMessage(message);
                                                    oldLyric = lyric;
                                                }
                                            } else if (enable) {
                                                if (lyricTextView.getVisibility() != View.GONE) {
                                                    Utils.log("开关关闭或播放器暂停 清除歌词");
                                                    Message message = LyricUpdate.obtainMessage();
                                                    Bundle bundle = new Bundle();
                                                    bundle.putString(KEY_LYRIC, "");
                                                    message.setData(bundle);
                                                    LyricUpdate.sendMessage(message);
                                                    lyric = "";
                                                    oldLyric = lyric;
                                                    enable = false;


                                                    // 恢复状态栏
                                                    Utils.setStatusBar(context, true);
                                                }
                                            }
                                        }

                                        if (lyricSpeed < 10) lyricSpeed++;
                                    }


                                }, 0, 10);

                        Handler handler = new Handler(message -> {
                            return false;
                        });
                        new Thread(() -> {
                            Message message = new Message();
                            message.what = 101;
                            handler.sendMessage(message);
                        }).start();
                    }
                });
                break;
            case "com.netease.cloudmusic":
                Utils.log("正在hook网易云音乐");
                new netease.Hook(lpparam);
                break;
            case "com.kugou.android":
                Utils.log("正在hook酷狗音乐");
                XposedHelpers.findAndHookMethod("android.media.AudioManager", lpparam.classLoader, "isBluetoothA2dpOn", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        param.setResult(true);
                    }
                });
                XposedHelpers.findAndHookMethod("com.kugou.framework.player.c", lpparam.classLoader, "a", HashMap.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        Utils.log("酷狗音乐:" + ((HashMap) param.args[0]).values().toArray()[0]);
                        Utils.sendLyric(context, "" + ((HashMap) param.args[0]).values().toArray()[0], "kugou");
                    }
                });
                break;
            case "cn.kuwo.player":
                Utils.log("正在hook酷我音乐");
                XposedHelpers.findAndHookMethod("android.bluetooth.BluetoothAdapter", lpparam.classLoader, "isEnabled", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        param.setResult(true);
                    }
                });
                XposedHelpers.findAndHookMethod("cn.kuwo.mod.playcontrol.RemoteControlLyricMgr", lpparam.classLoader, "updateLyricText", Class.forName("java.lang.String"), new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        String str = (String) param.args[0];
                        Utils.log("酷我音乐:" + str);
                        if (param.args[0] != null && !str.equals("") && !str.equals("好音质 用酷我") && !str.equals("正在搜索歌词...") && !str.contains(" - ")) {
                            Utils.sendLyric(context, "" + str, "kuwo");
                        }
                        param.setResult(replaceHookedMethod());
                    }

                    private Object replaceHookedMethod() {
                        return null;
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                    }
                });
                break;
            case "com.tencent.qqmusic":
                Utils.log("正在hookQQ音乐");
                XposedHelpers.findAndHookMethod(lpparam.classLoader.loadClass("com.tencent.qqmusicplayerprocess.servicenew.mediasession.d$d"), "run", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);

                        Class<?> findClass = XposedHelpers.findClass("com.lyricengine.base.h", lpparam.classLoader);
                        Field declaredField = findClass.getDeclaredField("a");
                        declaredField.setAccessible(true);

                        Object obj = XposedHelpers.findField(param.thisObject.getClass(), "b").get(param.thisObject);
                        String str = (String) declaredField.get(obj);

                        Utils.log("qq音乐: " + str);

                        Utils.sendLyric(context, str, "qqmusic");
                    }
                });
                break;
            case "remix.myplayer":
                Utils.log("正在Hook myplayer");
                // 开启状态栏歌词
                XposedHelpers.findAndHookMethod("remix.myplayer.util.p", lpparam.classLoader, "o", Context.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        param.setResult(true);
                    }
                });
                XposedHelpers.findAndHookMethod("remix.myplayer.service.MusicService", lpparam.classLoader, "n1", String.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        Utils.log("myplayer: " + param.args[0].toString());
                        Utils.sendLyric(context, param.args[0].toString(), "myplayer");
                    }
                });
        }
    }


    public static class LyricReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("Lyric_Server")) {
                lyric = intent.getStringExtra("Lyric_Data");
                if (new Config().getIcon()) {
                    iconPath = new Config().getIconPath() + intent.getStringExtra("Lyric_Icon") + ".webp";
                } else {
                    iconPath = "";
                }
            }
        }
    }


}
