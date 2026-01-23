package info.mx.comlib.data

class MemoryItem<T>(val item: T, private val validationDuration: Long) {
    private val timestamp: Long = System.currentTimeMillis()
    val isValid: Boolean
        get() = validationDuration == DURATION_INFINITE.toLong() || timestamp + validationDuration >= System.currentTimeMillis()

    companion object {
        const val DURATION_INFINITE = -1
    }

}
