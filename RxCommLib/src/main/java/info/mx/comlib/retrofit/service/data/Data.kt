package info.mx.comlib.retrofit.service.data

/**
 * Wrapper for every sort of backend data objects that includes information where
 * this data is coming from
 */
class Data<T> private constructor(val data: T, val source: Source) {
    enum class Source {
        NETWORK, DB, MEMORY
    }

    companion object {
        @JvmStatic
        fun <T> network(data: T): Data<T> {
            return Data(data, Source.NETWORK)
        }

        fun <T> db(data: T): Data<T> {
            return Data(data, Source.DB)
        }

        @JvmStatic
        fun <T> memory(data: T): Data<T> {
            return Data(data, Source.MEMORY)
        }
    }
}
