package info.hannes.mxadmin.service

import info.mx.comlib.data.MemoryCache
import info.mx.tracks.MxAccessApplication.Companion.aadhresUBase
import info.mx.tracks.data.DataManagerBase
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.Single
import io.reactivex.SingleTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * It keeps a reference to every helper class and uses them to satisfy the requests coming from the UI.
 * Its methods make extensive use of Rx operators to combine, transform or filter the output coming from the helpers in order to generate the
 * desired output ready for the UI.
 * It returns observables that emit data models.
 */
open class DataManagerAdminBase : DataManagerBase() {

    protected object MemoryKeys {
        const val KEY_NETWORK = "key_network"
    }

    // ================================================
    // schedulers transformer
    // ================================================
    protected fun <T> applyObservableSchedulers(): ObservableTransformer<T, T> {
        return ObservableTransformer { observable: Observable<T> ->
            observable.subscribeOn(
                Schedulers.io()
            )
                .observeOn(AndroidSchedulers.mainThread())
        }
    }

    protected fun <T> applySingleSchedulers(): SingleTransformer<T, T> {
        return SingleTransformer { single: Single<T> ->
            single.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        }
    }

    companion object {
        @kotlin.jvm.JvmField
        var memoryCache: MemoryCache = MemoryCache()
        val basic = aadhresUBase
        const val MEMORY_VALIDATION_PERIOD = (60 * 1000).toLong()
    }
}
