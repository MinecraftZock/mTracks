package info.mx.tracks.react

import android.content.Context
import android.widget.Toast

import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

import info.mx.rxcommlibrary.BuildConfig
import info.mx.tracks.R
import timber.log.Timber

import android.content.DialogInterface.BUTTON_POSITIVE

/**
 * Kicks off common error handling when receiving a throwable from a network request
 */
open class ErrorCommand(private val context: Context) : Command<Throwable>() {

    override fun process(payload: Throwable): Result {
        Timber.e(payload)
        if (BuildConfig.DEBUG) {
            defaultErrorHandling(payload)
        }

        return Result.Continue
    }

    protected fun defaultErrorHandling(throwable: Throwable) {
        showErrorPopup(throwable)
    }

    protected fun showErrorPopup(throwable: Throwable) {
        if (context is AppCompatActivity) {
            val alertDialog = AlertDialog.Builder(context).create()
            alertDialog.setTitle("Error")
            alertDialog.setMessage(throwable.message)
            alertDialog.setButton(BUTTON_POSITIVE, context.getString(R.string.close)
            ) { dialog, _ ->
                this@ErrorCommand.buttonClicked()
                dialog.dismiss()
            }
            alertDialog.show()

        } else {
            Toast.makeText(context, throwable.message, Toast.LENGTH_SHORT).show()
            Timber.e(throwable, "Can't show error as dialog as missing AppCompatActivity")
        }
    }

    protected fun buttonClicked() = Unit
}
