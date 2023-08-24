package info.hannes.commonlib.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import info.hannes.commonlib.utils.CompressHelper.zip
import info.hannes.commonlib.utils.FileHelper.copyFile
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

object BackupHelper {
    private const val DATABASE_EXTENSION = ".db"
    private const val SHARE_FILE_PREFIX = "share"
    private var dbName = ""
    private var dbVersion = 0

    @Throws(IOException::class)
    fun createShareIntent(context: Context, appName: String, dbDir: String, dbVersion: Int): Intent {
        val file = copyPrivateToPublicWithLog(context, appName, dbDir, dbVersion)
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "application/zip"
        val sendFile = File(file)
        val uri = Uri.fromFile(sendFile)
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
        return shareIntent
    }

    @Throws(IOException::class)
    private fun copyPrivateToPublicWithLog(context: Context, appName: String, dbDir: String, dbVersion: Int): String {
        val sdf = SimpleDateFormat("yyyy.MM.dd_HHmmss", Locale.getDefault())
        val shareTempDir = context.externalCacheDir.toString() + File.separator +
                SHARE_FILE_PREFIX + appName.replace(" ".toRegex(), "") + "_" +
                sdf.format(System.currentTimeMillis()) + File.separator
        val shareTempPrefsDir = shareTempDir + File.separator + "shared_prefs" + File.separator
        val shareTempFilesDir = shareTempDir + File.separator + "files" + File.separator
        val shareTempFiles = File(shareTempFilesDir)
        shareTempFiles.mkdirs()
        val shareTempPrefs = File(shareTempPrefsDir)
        shareTempPrefs.mkdirs()
        val fromDB = context.getDatabasePath(dbDir)
        val zipFileName: String?
        val toDB = File(
            shareTempDir +
                    dbDir.replace(
                        ".db", "v" +
                                dbVersion + "." + sdf.format(System.currentTimeMillis()) + ".db"
                    )
        )
        copyFile(fromDB, toDB)
        val files: MutableList<String?> = ArrayList()
        files.add(toDB.absolutePath)

        // json files
        context.filesDir.listFiles()?.forEach { fileJson ->
            if (!fileJson.name.startsWith("DATA_") &&
                !fileJson.name.contains("_") &&
                !fileJson.name.endsWith("history") &&
                !fileJson.isDirectory &&
                !fileJson.name.contains(".")
            ) {
                val jsonTo = File(shareTempFilesDir + fileJson.name + ".json")
                copyFile(fileJson, jsonTo)
                files.add(jsonTo.absolutePath)
            }
        }

        // add shared prefs files
        val filesPrefs = File(context.filesDir, "../shared_prefs")
        filesPrefs.listFiles()?.forEach { fileXML ->
            val xmlTo = File(shareTempPrefsDir + fileXML.name)
            copyFile(fileXML, xmlTo)
            files.add(xmlTo.absolutePath)
        }
        zipFileName = toDB.absolutePath.replace(".db", ".zip")
        zip(files, zipFileName)
        return zipFileName
    }

    @Throws(IOException::class)
    fun createDBShareIntent(context: Context, dbname: String, dbversion: Int): Intent {
        dbName = dbname
        dbVersion = dbversion
        val sourceDB = context.getDatabasePath(dbName)
        val file = copyPrivateDbToPublicAccessibleFile(context, sourceDB)
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "application/zip"
        val sendFile = File(file)
        val uri = Uri.fromFile(sendFile)
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
        return shareIntent
    }

    @Throws(IOException::class)
    private fun copyPrivateDbToPublicAccessibleFile(context: Context, fromDB: File): String {
        val zipFileName: String
        val sdf = SimpleDateFormat("yyyy.MM.dd_HHmmss", Locale.getDefault())
        val internalFileName = (dbName + ".v" + dbVersion + "." + sdf.format(System.currentTimeMillis()) + DATABASE_EXTENSION)
        val toDB = File(context.externalCacheDir, internalFileName)
        toDB.deleteOnExit()
        copyFile(fromDB, toDB)
        val files: MutableList<String?> = ArrayList()
        files.add(toDB.absolutePath)
        zipFileName = toDB.absolutePath + ".zip"
        zip(files, zipFileName)
        return zipFileName
    }

    fun sendIntent(context: Context, intentMail: Intent?, caption: String, mailAddress: String, subject: String?) {
        if (intentMail != null) {
            // intentMail.setType("message/rfc822");
            intentMail.putExtra(Intent.EXTRA_EMAIL, arrayOf(mailAddress))
            intentMail.putExtra(Intent.EXTRA_SUBJECT, subject)
            intentMail.putExtra(Intent.EXTRA_TEXT, caption)
            // intentMail.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // from service
            // intentMail.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);

            // prevent from a " exposed beyond app through ClipData.Item.getUri()"
            val builder = VmPolicy.Builder()
            StrictMode.setVmPolicy(builder.build())
            context.startActivity(Intent.createChooser(intentMail, "send $caption ..."))
        }
    }
}
