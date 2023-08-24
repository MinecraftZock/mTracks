package info.mx.tracks.base

import androidx.appcompat.app.AppCompatActivity
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

open class ActivityRx : AppCompatActivity() {
    //subscription to commonly unsubscribe on activity destroy
    private var compositeDisposable: CompositeDisposable? = null

    override fun onResume() {
        super.onResume()
        //we have to take care of all subscriptions to un-subscribe them when the activity gets destroyed, otherwise we gonna create memory leaks
        //as subscriptions hold references to views
        //composite subscription can't be reused after unsubscribe, so we instantiate it again
        compositeDisposable = CompositeDisposable()
    }

    override fun onPause() {
        removeSubscription()
        super.onPause()
    }

    private fun removeSubscription() {
        //we un-subscribe all subscriptions to release all references to views etc.
        if (compositeDisposable != null) {
            compositeDisposable!!.dispose()
        }
        compositeDisposable = null
    }

    fun addDisposable(disposable: Disposable?) {
        checkNotNull(compositeDisposable) { "Subscriptions can only be added within onResume / onPause lifecycle" }
        compositeDisposable!!.add(disposable!!)
    }
}