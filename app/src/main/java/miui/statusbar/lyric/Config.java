package miui.statusbar.lyric;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import static miui.statusbar.lyric.Utils.PATH;


public class Config {
    JSONObject config;

    public Config() {
        try {
            if (getConfig().equals("")) {
                this.config = new JSONObject();
                return;
            }
            this.config = new JSONObject(getConfig());
        } catch (JSONException ignored) {
        }
    }

    public static String getConfig() {
        String str = "";
        try {
            FileInputStream fileInputStream = new FileInputStream(Utils.PATH + "Config.json");
            byte[] bArr = new byte[fileInputStream.available()];
            fileInputStream.read(bArr);
            str = new String(bArr);
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static void setConfig(String str) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(Utils.PATH + "Config.json");
            fileOutputStream.write(str.getBytes());
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public Boolean getLyricService() {
        return this.config.optBoolean("LyricService", false);
    }

    public void setLyricService(Boolean bool) {
        try {
            this.config.put("LyricService", bool);
            setConfig(this.config.toString());
        } catch (JSONException ignored) {
        }
    }

    public int getLyricWidth() {
        return this.config.optInt("LyricWidth", -1);
    }

    public void setLyricWidth(int i) {
        try {
            this.config.put("LyricWidth", i);
            setConfig(this.config.toString());
        } catch (JSONException ignored) {
        }
    }

    public int getLyricMaxWidth() {
        return this.config.optInt("LyricMaxWidth", -1);
    }

    public void setLyricPosition(int i) {
        try {
            this.config.put("LyricPosition", i);
            setConfig(this.config.toString());
        } catch (JSONException ignored) {
        }
    }

    public int getLyricPosition() {
        return this.config.optInt("LyricPosition", 2);
    }

    public void setLyricMaxWidth(int i) {
        try {
            this.config.put("LyricMaxWidth", i);
            setConfig(this.config.toString());
        } catch (JSONException ignored) {
        }
    }

    public Boolean getLyricAutoOff() {
        return this.config.optBoolean("LyricAutoOff", true);
    }

    public void setLyricAutoOff(Boolean bool) {
        try {
            this.config.put("LyricAutoOff", bool);
            setConfig(this.config.toString());
        } catch (JSONException ignored) {
        }
    }
    public Boolean getLockScreenOff() {
        return this.config.optBoolean("lockScreenOff", false);
    }

    public void setLockScreenOff(Boolean bool) {
        try {
            this.config.put("lockScreenOff", bool);
            setConfig(this.config.toString());
        } catch (JSONException ignored) {
        }
    }

    public void sethNoticeIcon(Boolean bool) {
        try {
            this.config.put("hNoticeIcon", bool);
            setConfig(this.config.toString());
        } catch (JSONException ignored) {
        }
    }

    public String getLyricColor() {
        return this.config.optString("LyricColor", "off");
    }


    public Boolean getLyricSwitch() {
        return this.config.optBoolean("LyricSwitch", false);
    }

    public void setLyricSwitch(Boolean bool) {
        try {
            this.config.put("LyricSwitch", bool);
            setConfig(this.config.toString());
        } catch (JSONException ignored) {
        }
    }

    public Boolean getHNoticeIco() {
        return this.config.optBoolean("hNoticeIcon", false);
    }

    public void setHAlarm(Boolean bool) {
        try {
            this.config.put("hAlarm", bool);
            setConfig(this.config.toString());
        } catch (JSONException ignored) {
        }
    }

    public Boolean getHAlarm() {
        return this.config.optBoolean("hAlarm", false);
    }


    public void setLyricColor(String str) {
        try {
            this.config.put("LyricColor", str);
            setConfig(this.config.toString());
        } catch (JSONException ignored) {
        }
    }

    public Boolean getHNetSpeed() {
        return this.config.optBoolean("hNetSpeed", false);
    }

    public void sethNetSpeed(Boolean bool) {
        try {
            this.config.put("hNetSpeed", bool);
            setConfig(this.config.toString());
        } catch (JSONException ignored) {
        }
    }

    public Boolean getHCUK() {
        return this.config.optBoolean("hCUK", false);
    }

    public void sethCUK(Boolean bool) {
        try {
            this.config.put("hCUK", bool);
            setConfig(this.config.toString());
        } catch (JSONException ignored) {
        }
    }

    public Boolean getDebug() {
        return this.config.optBoolean("debug", false);
    }

    public void setDebug(Boolean bool) {
        try {
            this.config.put("debug", bool);
            setConfig(this.config.toString());
        } catch (JSONException ignored) {
        }
    }

    public Boolean getisUsedCount() {
        return this.config.optBoolean("isusedcount", true);
    }

    public void setisUsedCount(Boolean bool) {
        try {
            this.config.put("isusedcount", bool);
            setConfig(this.config.toString());
        } catch (JSONException ignored) {
        }
    }

    public boolean getIcon() {
        return this.config.optBoolean("Icon", true);
    }

    public void setIcon(Boolean bool) {
        try {
            this.config.put("Icon", bool);
            setConfig(this.config.toString());
        } catch (JSONException ignored) {
        }
    }

    public boolean getLShowOnce() {
        return this.config.optBoolean("LShowOnce", false);
    }

    public void setLShowOnce(Boolean bool) {
        try {
            this.config.put("LShowOnce", bool);
            setConfig(this.config.toString());
        } catch (JSONException ignored) {
        }
    }

    public Boolean getIconAutoColor() {
        return this.config.optBoolean("IconAutoColor", true);
    }

    public void setIconAutoColor(Boolean bool) {
        try {
            this.config.put("IconAutoColor", bool);
            setConfig(this.config.toString());
        } catch (JSONException ignored) {
        }
    }

    public String getIconPath() {
        return this.config.optString("IconPath", PATH);
    }

    public void setIconPath(String str) {
        try {
            this.config.put("IconPath", str);
            setConfig(this.config.toString());
        } catch (JSONException ignored) {
        }
    }

    public boolean getAntiBurn() {
        return this.config.optBoolean("antiburn", false);
    }

    public void setAntiBurn(Boolean bool) {
        try {
            this.config.put("antiburn", bool);
            setConfig(this.config.toString());
        } catch (JSONException ignored) {
        }
    }

    public int getUsedCount() {
        return this.config.optInt("usedcount", 0);
    }

    public void setUsedCount(int i) {
        try {
            this.config.put("usedcount", i);
            setConfig(this.config.toString());
        } catch (JSONException ignored) {
        }
    }

    public String getAnim() {
        return this.config.optString("Anim", "off");
    }

    public void setAnim(String str) {
        try {
            this.config.put("Anim", str);
            setConfig(this.config.toString());
        } catch (JSONException ignored) {
        }
    }

    public String getHook() {
        return this.config.optString("Hook", "");
    }

    public void setHook(String str) {
        try {
            this.config.put("Hook", str);
            setConfig(this.config.toString());
        } catch (JSONException ignored) {
        }
    }

    public boolean getFileLyric() {
        return this.config.optBoolean("FileLyric", false);
    }

    public void setFileLyric(boolean bool) {
        try {
            this.config.put("FileLyric", bool);
            setConfig(this.config.toString());
        } catch (JSONException ignored) {
        }
    }

}