package miui.statusbar.lyric;

import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import static miui.statusbar.lyric.Utils.PATH;

@SuppressWarnings("unused")
public class Config {
    JSONObject config;

    public Config() {
        try {
            if (getConfig().equals("")) {
                this.config = new JSONObject();
                return;
            }
            this.config = new JSONObject(getConfig());
        } catch (Exception ignored) {
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
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
        try {
            return (Boolean) this.config.get("LyricService");
        } catch (Exception e) {
            return false;
        }
    }

    public void setLyricService(Boolean bool) {
        try {
            this.config.put("LyricService", bool);
            setConfig(this.config.toString());
        } catch (Exception ignored) {
        }
    }

    public int getLyricWidth() {
        try {
            return (Integer) this.config.get("LyricWidth");
        } catch (Exception e) {
            return -1;
        }
    }

    public void setLyricWidth(int i) {
        try {
            this.config.put("LyricWidth", i);
            setConfig(this.config.toString());
        } catch (Exception ignored) {
        }
    }

    public int getLyricMaxWidth() {
        try {
            return (Integer) this.config.get("LyricMaxWidth");
        } catch (Exception e) {
            return -1;
        }
    }

    public void setLyricPosition(int i) {
        try {
            this.config.put("LyricPosition", i);
            setConfig(this.config.toString());
        } catch (Exception ignored) {
        }
    }

    public int getLyricPosition() {
        try {
            return (Integer) this.config.get("LyricPosition");
        } catch (Exception e) {
            return 2;
        }
    }

    public void setLyricMaxWidth(int i) {
        try {
            this.config.put("LyricMaxWidth", i);
            setConfig(this.config.toString());
        } catch (Exception ignored) {
        }
    }

    public Boolean getLyricAutoOff() {
        try {
            return (Boolean) this.config.get("LyricAutoOff");
        } catch (Exception e) {
            return true;
        }
    }

    public void setLyricAutoOff(Boolean bool) {
        try {
            this.config.put("LyricAutoOff", bool);
            setConfig(this.config.toString());
        } catch (Exception ignored) {
        }
    }

    public void sethNoticeIcon(Boolean bool) {
        try {
            this.config.put("hNoticeIcon", bool);
            setConfig(this.config.toString());
        } catch (Exception ignored) {
        }
    }

    public String getLyricColor() {
        try {
            return (String) this.config.get("LyricColor");
        } catch (Exception e) {
            return "off";
        }
    }


    public Boolean getLyricSwitch() {
        try {
            return (Boolean) this.config.get("LyricSwitch");
        } catch (Exception e) {
            return false;
        }
    }

    public void setLyricSwitch(Boolean bool) {
        try {
            this.config.put("LyricSwitch", bool);
            setConfig(this.config.toString());
        } catch (Exception ignored) {
        }
    }

    public Boolean getHNoticeIco() {
        try {
            return (Boolean) this.config.get("hNoticeIcon");
        } catch (Exception e) {
            return false;
        }
    }

    public void setHAlarm(Boolean bool) {
        try {
            this.config.put("hAlarm", bool);
            setConfig(this.config.toString());
        } catch (Exception ignored) {
        }
    }

    public Boolean getHAlarm() {
        try {
            return (Boolean) this.config.get("hAlarm");
        } catch (Exception e) {
            return true;
        }
    }


    public void setLyricColor(String str) {
        try {
            this.config.put("LyricColor", str);
            setConfig(this.config.toString());
        } catch (Exception ignored) {
        }
    }

    public Boolean getHNetSpeed() {
        try {
            return (Boolean) this.config.get("hNetSpeed");
        } catch (Exception e) {
            return true;
        }
    }

    public void sethNetSpeed(Boolean bool) {
        try {
            this.config.put("hNetSpeed", bool);
            setConfig(this.config.toString());
        } catch (Exception ignored) {
        }
    }

    public Boolean getHCUK() {
        try {
            return (Boolean) this.config.get("hCUK");
        } catch (Exception e) {
            return false;
        }
    }

    public void sethCUK(Boolean bool) {
        try {
            this.config.put("hCUK", bool);
            setConfig(this.config.toString());
        } catch (Exception ignored) {
        }
    }

    public Boolean getDebug() {
        try {
            return (Boolean) this.config.get("debug");
        } catch (Exception e) {
            return false;
        }
    }

    public void setDebug(Boolean bool) {
        try {
            this.config.put("debug", bool);
            setConfig(this.config.toString());
        } catch (Exception ignored) {
        }
    }

    public Boolean getisUsedCount() {
        try {
            return (Boolean) this.config.get("isusedcount");
        } catch (Exception e) {
            return true;
        }
    }

    public void setisUsedCount(Boolean bool) {
        try {
            this.config.put("isusedcount", bool);
            setConfig(this.config.toString());
        } catch (Exception ignored) {
        }
    }

    public boolean getIcon() {
        try {
            return (Boolean) this.config.get("Icon");
        } catch (Exception e) {
            return true;
        }
    }

    public void setIcon(Boolean bool) {
        try {
            this.config.put("Icon", bool);
            setConfig(this.config.toString());
        } catch (Exception ignored) {
        }
    }

    public boolean getLShowOnce() {
        try {
            return (Boolean) this.config.get("LShowOnce");
        } catch (Exception e) {
            return false;
        }
    }

    public void setLShowOnce(Boolean bool) {
        try {
            this.config.put("LShowOnce", bool);
            setConfig(this.config.toString());
        } catch (Exception ignored) {
        }
    }

    public Boolean getIconAutoColor() {
        try {
            return (Boolean) this.config.get("IconAutoColor");
        } catch (Exception e) {
            return true;
        }
    }

    public void setIconAutoColor(Boolean bool) {
        try {
            this.config.put("IconAutoColor", bool);
            setConfig(this.config.toString());
        } catch (Exception ignored) {
        }
    }

    public String getIconPath() {
        try {
            return (String) this.config.get("IconPath");
        } catch (Exception e) {
            return PATH;
        }
    }

    public void setIconPath(String str) {
        try {
            this.config.put("IconPath", str);
            setConfig(this.config.toString());
        } catch (Exception ignored) {
        }
    }

    public boolean getAntiBurn() {
        try {
            return (boolean) this.config.get("antiburn");
        } catch (Exception e) {
            return false;
        }
    }

    public void setAntiBurn(Boolean bool) {
        try {
            this.config.put("antiburn", bool);
            setConfig(this.config.toString());
        } catch (Exception ignored) {
        }
    }

    public int getUsedCount() {
        try {
            return (int) this.config.get("usedcount");
        } catch (Exception e) {
            return 0;
        }
    }

    public void setUsedCount(int i) {
        try {
            this.config.put("usedcount", i);
            setConfig(this.config.toString());
        } catch (Exception ignored) {
        }
    }

    public String getAnim() {
        try {
            return (String) this.config.get("Anim");
        } catch (Exception e) {
            return "off";
        }
    }

    public void setAnim(String str) {
        try {
            this.config.put("Anim", str);
            setConfig(this.config.toString());
        } catch (Exception ignored) {
        }
    }

    public String getHook() {
        try {
            return (String) this.config.get("Hook");
        } catch (Exception e) {
            return "";
        }
    }

    public void setHook(String str) {
        try {
            this.config.put("Hook", str);
            setConfig(this.config.toString());
        } catch (Exception ignored) {
        }
    }

    public boolean getFileLyric() {
        try {
            return (boolean) this.config.get("FileLyric");
        } catch (Exception e) {
            return false;
        }
    }

    public void setFileLyric(boolean bool) {
        try {
            this.config.put("FileLyric", bool);
            setConfig(this.config.toString());
        } catch (Exception ignored) {
        }
    }

}