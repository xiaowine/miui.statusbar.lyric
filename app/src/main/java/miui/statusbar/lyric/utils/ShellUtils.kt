package miui.statusbar.lyric.utils

import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader

class ShellUtils {
    companion object {
        @JvmStatic
        fun voidShell(command: String, isSu: Boolean) {
            try {
                if (isSu) {
                    val p = Runtime.getRuntime().exec("su")
                    val outputStream = p.outputStream
                    val dataOutputStream = DataOutputStream(outputStream)
                    dataOutputStream.writeBytes(command)
                    dataOutputStream.flush()
                    dataOutputStream.close()
                    outputStream.close()
                } else {
                    Runtime.getRuntime().exec(command)
                }

            } catch (t: Throwable) {
                t.printStackTrace()
            }
        }

        @JvmStatic
        fun returnShell(command: String): String {
            return try {
                val bufferedReader = BufferedReader(
                    InputStreamReader(
                        Runtime.getRuntime().exec(command).inputStream
                    ), 1024
                )
                val buffer = bufferedReader.readLine()
                bufferedReader.close()
                buffer.replace("\n", "")
            } catch (e: Exception) {
                ""
            }
        }
    }
}