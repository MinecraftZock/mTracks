package info.hannes.commonlib.utils

import android.os.Environment
import java.io.File
import java.io.FileNotFoundException
import java.util.*

object ExternalStorage {
    private const val SD_CARD = "sdCard"
    const val EXTERNAL_SD_CARD = "externalSdCard"
    val sdCardPath: String
        get() = Environment.getExternalStorageDirectory().path + "/"

    // don't add the default mount path
    // it's already in the list.
    @get:Throws(FileNotFoundException::class)
    val allStorageLocations: Map<String, File>
        get() {
            val map: MutableMap<String, File> = HashMap(10)
            val mounts: MutableList<String> = ArrayList(10)
            val vold: MutableList<String> = ArrayList(10)
            mounts.add("/mnt/sdcard")
            vold.add("/mnt/sdcard")
            val mountFile = File("/proc/mounts")
            if (mountFile.exists()) {
                val scanner = Scanner(mountFile)
                while (scanner.hasNext()) {
                    val line = scanner.nextLine()
                    if (line.startsWith("/dev/block/vold/")) {
                        val lineElements = line.split(" ").toTypedArray()
                        val element = lineElements[1]

                        // don't add the default mount path
                        // it's already in the list.
                        if (element != "/mnt/sdcard") {
                            mounts.add(element)
                        }
                    }
                }
            }
            val voldFile = File("/system/etc/vold.fstab")
            if (voldFile.exists()) {
                val scanner = Scanner(voldFile)
                while (scanner.hasNext()) {
                    val line = scanner.nextLine()
                    if (line.startsWith("dev_mount")) {
                        val lineElements = line.split(" ").toTypedArray()
                        var element = lineElements[2]
                        if (element.contains(":")) {
                            element = element.substring(0, element.indexOf(":"))
                        }
                        if (element != "/mnt/sdcard") {
                            vold.add(element)
                        }
                    }
                }
            }
            var i = 0
            while (i < mounts.size) {
                val mount = mounts[i]
                if (!vold.contains(mount)) {
                    mounts.removeAt(i--)
                }
                i++
            }
            vold.clear()
            val mountHash: MutableList<String> = ArrayList(10)
            for (mount in mounts) {
                val root = File(mount)
                if (root.exists() && root.isDirectory && root.canWrite()) {
                    val list = root.listFiles()
                    val hash = StringBuilder("[")
                    if (list != null) {
                        for (f in list) {
                            hash.append(f.name.hashCode()).append(":").append(f.length()).append(", ")
                        }
                    }
                    hash.append("]")
                    if (!mountHash.contains(hash.toString())) {
                        var key = SD_CARD + "_" + map.size
                        if (map.isEmpty()) {
                            key = SD_CARD
                        } else if (map.size == 1) {
                            key = EXTERNAL_SD_CARD
                        }
                        mountHash.add(hash.toString())
                        map[key] = root
                    }
                }
            }
            mounts.clear()
            if (map.isEmpty()) {
                map[SD_CARD] = Environment.getExternalStorageDirectory()
            }
            return map
        }
}
