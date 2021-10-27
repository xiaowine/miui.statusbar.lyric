package miui.statusbar.lyric.hook;

import android.annotation.SuppressLint;
import android.content.Context;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import miui.statusbar.lyric.Utils;

public class netease {
    private static String musicName = "";
    private final static String PACKAGE_NAME = "com.netease.cloudmusic";
    @SuppressLint("StaticFieldLeak")
    private static Context context = null;

    public static class Hook {
        public Hook(XC_LoadPackage.LoadPackageParam lpparam) {
            XposedHelpers.findAndHookMethod(XposedHelpers.findClass("com.netease.cloudmusic.NeteaseMusicApplication", lpparam.classLoader), "attachBaseContext", Context.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) {
                    context = (Context) param.thisObject;
                    String enableBTLyric_Class;
                    String enableBTLyric_Method;
                    String getMusicName_Class;
                    String getMusicName_Method;
                    String getMusicLyric_Class;
                    String getMusicLyric_Method;
                    try {
                        int cloudmusicVer = context.getPackageManager().getPackageInfo(PACKAGE_NAME, 0).versionCode;
                        if (cloudmusicVer >= 8006000) {
                            enableBTLyric_Class = "com.netease.cloudmusic.module.player.w.h";
                            enableBTLyric_Method = "o";

                            getMusicName_Class = "com.netease.cloudmusic.module.player.w.h";
                            getMusicName_Method = "B";

                            getMusicLyric_Class = "com.netease.cloudmusic.module.player.w.h";
                        } else {
                            enableBTLyric_Class = "com.netease.cloudmusic.module.player.t.e";
                            enableBTLyric_Method = "o";

                            getMusicName_Class = "com.netease.cloudmusic.module.player.t.e";
                            getMusicName_Method = "B";

                            getMusicLyric_Class = "com.netease.cloudmusic.module.player.t.e";
                        }
                        getMusicLyric_Method = "F";
                        try {
                            XposedHelpers.findAndHookMethod(enableBTLyric_Class, lpparam.classLoader, enableBTLyric_Method, new XC_MethodHook() {
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
                            XposedHelpers.findAndHookMethod(getMusicName_Class, lpparam.classLoader, getMusicName_Method, java.lang.String.class, java.lang.String.class, java.lang.String.class, Long.TYPE, java.lang.Boolean.class, new XC_MethodHook() {
                                @Override
                                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                    super.beforeHookedMethod(param);
                                }

                                @Override
                                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                    super.afterHookedMethod(param);
                                    Utils.sendLyric(context, param.args[0].toString(), "netease");
                                    musicName = param.args[0].toString();
                                    Utils.log("网易云： " + param.args[0].toString());
                                }
                            });
                            XposedHelpers.findAndHookMethod(getMusicLyric_Class, lpparam.classLoader, getMusicLyric_Method, java.lang.String.class, java.lang.String.class, new XC_MethodHook() {
                                @Override
                                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                    super.beforeHookedMethod(param);
                                    Utils.sendLyric(context, param.args[0].toString(), "netease");
                                    Utils.log("网易云： " + param.args[0].toString());
                                    param.args[0] = musicName;
                                    param.setResult(param.args);
                                }

                                @Override
                                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                    super.afterHookedMethod(param);
                                }
                            });
                        } catch (Exception e) {
                            Utils.log(e.toString());
                            Utils.log("未知版本: " + cloudmusicVer);
                            Utils.showToastOnLooper(context, "MIUI状态栏歌词 未知版本: " + cloudmusicVer);
                        }
                    } catch (Exception e) {
                        XposedBridge.log(e.toString());
                        Utils.showToastOnLooper(context, "hook网易云失败: " + e);
                    }
                }
            });
        }
    }
}
