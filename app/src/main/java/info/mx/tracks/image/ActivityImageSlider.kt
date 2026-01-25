package info.mx.tracks.image

import com.robotoworks.mechanoid.db.SQuery
import info.mx.tracks.common.QueryHelper

class ActivityImageSlider : ActivityBaseImageSlider() {

    override val picturesQuery: SQuery
        get() = QueryHelper.getPictureFilter(trackRestId)

}
