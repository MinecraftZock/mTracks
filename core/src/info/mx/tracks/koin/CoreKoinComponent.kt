package info.mx.tracks.koin

import org.koin.core.Koin
import org.koin.core.component.KoinComponent

interface CoreKoinComponent : KoinComponent {
    override fun getKoin(): Koin {
        return CoreKoinContext.koinApplication.koin
    }
}
