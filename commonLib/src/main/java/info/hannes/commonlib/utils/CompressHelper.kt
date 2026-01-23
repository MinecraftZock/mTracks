package info.hannes.commonlib.utils

import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object CompressHelper {
    private const val BUFFER = 2048

    @JvmStatic
    @Throws(IOException::class)
    fun zip(files: List<String?>, zipFile: String?) {
        var origin: BufferedInputStream
        val dest = FileOutputStream(zipFile)
        val out = ZipOutputStream(BufferedOutputStream(dest))
        val data = ByteArray(BUFFER)
        for (i in files.indices) {
            val fi = FileInputStream(files[i])
            origin = BufferedInputStream(fi, BUFFER)
            val entry = ZipEntry(files[i])
            out.putNextEntry(entry)
            var count: Int
            while (origin.read(data, 0, BUFFER).also { count = it } != -1) {
                out.write(data, 0, count)
            }
            origin.close()
        }
        out.close()
    }
}
