# MIUI 状态栏歌词

### 自动获取音乐软件当前播放的歌曲，并实时将歌词显示在状态栏

支持 MIUI 12 以后的 MIUI

#### 支持的音乐软件版本

只记录最高版本，其余版本自测
- 网易云音乐:v8.6.02 （完美适配）
- 酷狗音乐:v10.9.0 （需打开蓝牙歌词）
- 酷我音乐:v10.0.1.0 （需打开蓝牙歌词）
- QQ音乐:v10.17.0.11 （需打开蓝牙歌词并连接）
- Aplayer:v1.5.7.9 （完美适配）
- 椒盐音乐：暂未正式发布

#### 感谢名单
- @潇风残月 (感谢提供的库以及对代码的极限精简)
- @柒猫Sebun_Neko (感谢提供的图标)


### 下载

[Releases](https://github.com/577fkj/MIUIStatusBarLyric_new/releases)

### 联系方式

[电报群](https://t.me/MIUIStatusBatLyric)  
[爱发电](https://afdian.net/@xiao_wine)



## 音乐主动适配
### API 发送歌词示例

Kotlin

```kotlin
fun sendLyric(context: Context, lyric: String, icon: String, packName: String) {
    val intent = Intent().apply {
        action = "Lyric_Server"
        putExtra("Lyric_Data", lyric)
        putExtra("Lyric_Type", "app")
        putExtra("Lyric_PackName", packName)
        putExtra("Lyric_Icon", icon)
    }
    context.sendBroadcast(intent)
}
```

Java

```java
public void sendLyric(Context context, String lyric, String icon, String packName) {
    context.sendBroadcast(new Intent()
            .setAction("Lyric_Server")
            .putExtra("Lyric_Data", lyric)
            .putExtra("Lyric_Type", "app")
            .putExtra("Lyric_PackName", packName)
            .putExtra("Lyric_Icon", icon)
    );
}
```

| 参数 | 解释 |
| :-- | :-- |
| context  | Context |
| lyric    | 歌词文本 |
| icon     | 无前缀的 base64 编码（推荐32x32） |
| packName | 你的软件包名 |

#### 暂停/停止播放 (不提供会导致无法正常取消显示歌词)

Kotlin

```kotlin
fun stopLyric(context: Context) {
    val intent = Intent().apply {
        action = "Lyric_Server"
        putExtra("Lyric_Type", "app_stop")
    }
    context.sendBroadcast(intent)
}
```

Java

```java
public void stopLyric(Context context) {
    context.sendBroadcast(new Intent()
            .setAction("Lyric_Server")
            .putExtra("Lyric_Type", "app_stop")
    );
}
```


