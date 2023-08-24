package info.mx.comlib.retrofit.service.data

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import info.mx.rxcommlibrary.BuildConfig
import info.mx.rxcommlibrary.R
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

/**
 * Convenience subscriber with error handling
 */
abstract class BaseObserver<T>(private val context: Context) : Observer<T> {
    override fun onSubscribe(d: Disposable) {}
    override fun onComplete() {
        //Not needed as onNext is only called once from retrofit
    }

    override fun onError(throwable: Throwable) {
        if (BuildConfig.DEBUG) {
            defaultErrorHandling(throwable)
        }
    }

    private fun defaultErrorHandling(throwable: Throwable) {
        showErrorPopup(throwable)
    }

    private fun showErrorPopup(throwable: Throwable) {
        if (context is AppCompatActivity) {
            val alertDialog = AlertDialog.Builder(
                context
            ).create()
            alertDialog.setTitle("Error")
            alertDialog.setMessage(throwable.message)
            alertDialog.setButton(
                DialogInterface.BUTTON_POSITIVE, context.getString(R.string.close)
            ) { dialog: DialogInterface, _: Int ->
                buttonClicked()
                dialog.dismiss()
            }
            alertDialog.show()
        } else {
            throw RuntimeException("Can't show error as dialog as missing AppCompatActivity")
        }
    }

    private fun buttonClicked() = Unit
}
