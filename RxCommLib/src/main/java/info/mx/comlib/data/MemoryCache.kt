package info.mx.comlib.data

import io.reactivex.Observable
import java.util.*

@Suppress("UNCHECKED_CAST")
class MemoryCache {
    private val memory = HashMap<String, MemoryItem<*>>()
    fun put(key: String, value: Any?, validationPeriod: Long) {
        memory[key] = MemoryItem(value, validationPeriod)
    }

    fun <T> getObservable(key: String): Observable<T> {
        val memoryItem: MemoryItem<T>? = memory[key] as MemoryItem<T>?
        return if (memoryItem != null && memoryItem.isValid)
            Observable.just(memoryItem.item)
        else
            (Observable.empty<Any>() as Observable<T>)
    }

    operator fun <T> get(key: String): T? {
        val memoryItem: MemoryItem<T>? = memory[key] as MemoryItem<T>?
        return memoryItem?.item
    }

    /**
     * Clear the cache
     */
    fun clear() {
        memory.clear()
    }
}
