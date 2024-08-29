package info.mx.tracks.uiautomator

import android.app.UiAutomation
import timber.log.Timber
import java.io.FileInputStream

fun UiAutomation.executeShellCommandBlocking(command: String): ByteArray {
    Timber.d("run $command")
    val output = executeShellCommand(command)
    return FileInputStream(output.fileDescriptor).use { it.readBytes() }
}
