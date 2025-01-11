package info.hannes.commonlib

import android.app.AlertDialog
import android.content.Context

object DialogHelper {

    interface Callable {
        fun execute(param: Long)
    }

    fun doAskYesNo(context: Context, recordId: Long, title: Int, msg: String, positiveMethod: Callable, negativeMethod: Callable?) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(context.getString(title))
        builder.setMessage(msg)
        if (negativeMethod != null) {
            builder.setNegativeButton(android.R.string.cancel) { _, _ -> negativeMethod.execute(recordId) }
        } else {
            builder.setNegativeButton(android.R.string.cancel, null)
        }
        builder.setPositiveButton(android.R.string.ok) { _, _ -> positiveMethod.execute(recordId) }
        builder.create().show()
    }
}
