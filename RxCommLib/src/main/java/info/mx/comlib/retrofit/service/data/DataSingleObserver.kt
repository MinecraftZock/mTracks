package info.mx.comlib.retrofit.service.data

import android.content.Context

/**
 * Extended subscriber handling split of data and its source
 */
abstract class DataSingleObserver<T>(context: Context) : BaseSingleObserver<Data<T>>(context) {
    //    @Override
    //    public final void onNext(Data<T> tData) {
    //        onNext(tData.getData(), tData.getSource());
    //    }
    abstract fun onNext(data: T, source: Data.Source)
}
