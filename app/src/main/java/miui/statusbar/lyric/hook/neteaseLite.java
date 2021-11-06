package miui.statusbar.lyric.hook;

import android.graphics.Bitmap;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class neteaseLite {
    public static class Hook {
        public Hook(XC_LoadPackage.LoadPackageParam lpparam) {
            XposedHelpers.findAndHookMethod("com.netease.cloudmusic.utils.u", lpparam.classLoader, "R", new XC_MethodHook() {
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
            XposedHelpers.findAndHookMethod("com.netease.cloudmusic.module.player.f.c", lpparam.classLoader, "a", String.class, String.class, String.class, long.class, Bitmap.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    XposedBridge.log("网易云Lite 1: " + param.args[0].toString());
                    XposedBridge.log("网易云Lite 2: " + param.args[1].toString());
                    XposedBridge.log("网易云Lite 3: " + param.args[2].toString());


                }
            });
            XposedHelpers.findAndHookMethod("android.support.v4.media.MediaMetadataCompat$Builder", lpparam.classLoader, "putString", String.class, String.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    XposedBridge.log("网易云Lite 4: " + param.args[0].toString());
                    XposedBridge.log("网易云Lite 5: " + param.args[1].toString());
                }
            });

        }
    }
}
