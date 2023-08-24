package info.mx.tracks.react

import io.reactivex.functions.Consumer

/**
 * Implements chain-of-responsibility to concatenate RX actions in a meaningful manner.
 */
abstract class Command<T> : Consumer<T> {

    private var mNext: Command<T>? = null

    enum class Result {
        Continue,
        Return
    }

    fun setNext(next: Command<T>): Command<T> {
        mNext = next
        return this
    }

    override fun accept(t: T) {
        if (process(t) == Result.Return) {
            return
        }
        processNext(t)
    }

    /**
     * Override this method to process the payload
     *
     * @param payload The payload
     * @return [Result.Continue] in case you want to execute the next action in the chain,
     * [Result.Return] otherwise
     */
    protected abstract fun process(payload: T): Result

    private fun processNext(payload: T) {
        if (mNext != null) {
            mNext!!.accept(payload)
        }
    }

}
