# MIUI状态栏歌词

### 自动获取音乐软件当前播放的歌曲，并实时将歌词显示在状态栏

__支持MIUI12以后的MIUI__

#### 支持的音乐软件版本

- 酷狗音乐:v10.8.4 (需打开蓝牙歌词)
- 酷我音乐:v9.4.6.2 (需打开蓝牙歌词)
- 网易云音乐:v8.6.0 (完美适配)
- Aplayer:v1.5.7.9 (完美适配)
- QQ音乐:v10.17.0.11 (需打开蓝牙歌词和戴耳机)

__最后感谢 @潇风残月 大大的帮助，然后再求个图标__

### API

#### 初始化状态栏歌词

```
public void initLyric(Context context, String icon, String packName) {
    context.sendBroadcast(new Intent().setAction("Lyric_Server").putExtra("Lyric_Icon", icon).putExtra("Lyric_PackName", packName).putExtra("Lyric_Type", "app_init"));
}
```

| 参数      | 解释                      |
| -------- | ------------------------ |
| context  | Context                  |
| icon     | 无前缀的base64图片(推荐32x32)|
| packName | 软件包名                   |

#### 开始播放

```
public void startLyric(Context context) {
    context.sendBroadcast(new Intent().setAction("Lyric_Server").putExtra("Lyric_Type", "app_start"));
}
```

| 参数      | 解释                      |
| -------- | ------------------------ |
| context  | Context                  |

#### 发送歌词

```
public void sendLyric(Context context, String lyric) {
    context.sendBroadcast(new Intent().setAction("Lyric_Server").putExtra("Lyric_Data", lyric).putExtra("Lyric_Type", "app"));
}
```

| 参数      | 解释                      |
| -------- | ------------------------ |
| context  | Context                  |
| lyric    | 歌词文本                   |

#### 停止播放

```
public void stopLyric(Context context) {
    context.sendBroadcast(new Intent().setAction("Lyric_Server").putExtra("Lyric_Type", "app_stop"));
}
```

| 参数      | 解释                      |
| -------- | ------------------------ |
| context  | Context                  |

#### systemUI重启

SystemUI重启后包名和图标会丢失，需要重新初始化。

```
public void initSystemReStart(Context context, String icon, String packName) {
    public static class LyricReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("Lyric_Server_ReStart")) {
                initLyric(context, icon, packName);
            }
        }
    }
    IntentFilter filter = new IntentFilter();
    filter.addAction("Lyric_Server_ReStart");
    context.registerReceiver(new LyricReceiver(), filter);
}
```

| 参数      | 解释                      |
| -------- | ------------------------ |
| context  | Context                  |
| icon     | 无前缀的base64图片(推荐32x32)|
| packName | 软件包名                   |

### 下载

[Releases](https://github.com/577fkj/MIUIStatusBarLyric_new/releases)

### 联系方式

[电报群](https://t.me/MIUIStatusBatLyric)
[爱发电](https://afdian.net/@xiao_wine)
