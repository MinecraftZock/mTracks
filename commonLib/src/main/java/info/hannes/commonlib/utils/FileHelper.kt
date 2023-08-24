package info.hannes.commonlib.utils

import java.io.*
import java.util.*

object FileHelper {
    private const val SIGNATURE_PNG = "89504E47" //"0D0A1A0A";
    private const val SIGNATURE_JPEG = "FFD8FF"
    private const val SIGNATURE_GIF = "474946"
    private val SIGNATURES = arrayOfNulls<String>(3)
    private const val MAX_SIGNATURE_LENGTH = 16

    init {
        SIGNATURES[0] = SIGNATURE_JPEG
        SIGNATURES[1] = SIGNATURE_PNG
        SIGNATURES[2] = SIGNATURE_GIF
    }

    @Throws(IOException::class)
    fun copyFile(fromFile: File?, toFile: File) {
        toFile.parentFile?.let {
            if (!it.exists()) {
                it.mkdirs()
            }
        }
        if (!toFile.exists()) {
            toFile.createNewFile()
        }
        val fis = FileInputStream(fromFile)
        val from = fis.channel
        val fos = FileOutputStream(toFile)
        val to = fos.channel
        to.transferFrom(from, 0, from.size())
        fis.close()
        fos.close()
        from.close()
        to.close()
    }

    /**
     * @param is InputStream on start of file. Otherwise signature can not be defined.
     * @return int id of signature or -1, if unknown signature was found. See SIGNATURE_ID_(type) constants to
     * identify signature by its id.
     * @throws IOException in cases of read errors.
     */
    @JvmStatic
    @Throws(IOException::class)
    fun isImageFromSignatureIdFromHeader(`is`: InputStream): Boolean {
        // read signature from head of source and compare with known signatures
        var found = false
        val byteArray = IntArray(MAX_SIGNATURE_LENGTH)
        val builder = StringBuilder()
        for (i in 0 until MAX_SIGNATURE_LENGTH) {
            byteArray[i] = `is`.read()
            builder.append(Integer.toHexString(byteArray[i]))
        }
        val firstChars = builder.toString()
        for (signature in SIGNATURES) {
            if (firstChars.startsWith(signature!!.lowercase(Locale.getDefault()))) {
                found = true
                break
            }
        }
        return found
    }

}
