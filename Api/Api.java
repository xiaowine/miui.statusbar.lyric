import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;

public class Api {
    // 初始化状态栏歌词
    public void initLyric(Context context, String icon, String packName) {
        context.sendBroadcast(new Intent().setAction("Lyric_Server").putExtra("Lyric_Icon", icon).putExtra("Lyric_PackName", packName).putExtra("Lyric_Type", "app_init"));
    }

    // 开始播放
    public void startLyric(Context context) {
        context.sendBroadcast(new Intent().setAction("Lyric_Server").putExtra("Lyric_Type", "app_start"));
    }

    // 发送歌词
    public void sendLyric(Context context, String lyric) {
        context.sendBroadcast(new Intent().setAction("Lyric_Server").putExtra("Lyric_Data", lyric).putExtra("Lyric_Type", "app"));
    }

    // 停止播放
    public void stopLyric(Context context) {
        context.sendBroadcast(new Intent().setAction("Lyric_Server").putExtra("Lyric_Type", "app_stop"));
    }

    // systemUI重启
    public void initSystemReStart(Context context, String icon, String packName) {
        public static class LyricReceiver extends BroadcastReceiver {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("Lyric_Server_ReStart")) {
                    context.sendBroadcast(new Intent().setAction("Lyric_Server").putExtra("Lyric_Icon", icon).putExtra("Lyric_PackName", packName).putExtra("Lyric_Type", "app_init"));
                }
            }
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction("Lyric_Server_ReStart");
        context.registerReceiver(new LyricReceiver(), filter);
    }
}