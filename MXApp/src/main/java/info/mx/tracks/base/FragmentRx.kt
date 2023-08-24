package info.mx.tracks.base

import androidx.fragment.app.Fragment
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class FragmentRx : Fragment() {

    //subscription to commonly unsubscribe on activity destroy
    private var compositeDisposable: CompositeDisposable? = null

    override fun onStart() {
        super.onStart()
        //we have to take care of all subscriptions to un-subscribe them when the activity gets destroyed, otherwise we gonna create memory leaks
        //as subscriptions hold references to views
        //composite subscription can't be reused after unsubscribe, so we instantiate it again
        compositeDisposable = CompositeDisposable()
    }

    override fun onStop() {
        super.onStop()
        removeDisposeable()
    }

    private fun removeDisposeable() {
        compositeDisposable?.dispose()
    }

    fun addDisposable(disposable: Disposable) {
        if (compositeDisposable == null) {
            throw RuntimeException("Disposable can only be added within onResume / onPause lifecycle")
        }

        compositeDisposable?.add(disposable)
    }
}
