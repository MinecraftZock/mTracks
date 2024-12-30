package info.mx.tracks.map.cluster

import android.content.res.Resources
import android.graphics.*
import android.graphics.Paint.Align
import androidx.collection.LruCache
import com.androidmapsextensions.ClusterOptions
import com.androidmapsextensions.ClusterOptionsProvider
import com.androidmapsextensions.Marker
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import info.mx.tracks.R

class MapClusterOptionsProvider(resources: Resources) : ClusterOptionsProvider {

    private val baseBitmaps: Array<Bitmap?> = arrayOfNulls(res.size)
    private val cache = LruCache<Int, BitmapDescriptor>(128)
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val bounds = Rect()
    private val clusterOptions = ClusterOptions().anchor(0.5f, 0.5f)

    init {
        for (i in res.indices) {
            baseBitmaps[i] = BitmapFactory.decodeResource(resources, res[i])
        }
        paint.color = Color.WHITE
        paint.textAlign = Align.CENTER
        paint.textSize = resources.getDimension(R.dimen.mapcluster_text_size)
    }

    override fun getClusterOptions(markers: List<Marker?>?): ClusterOptions? {
        markers?.let {
            val markersCount = markers.size
            val cachedIcon = cache[markersCount]
            if (cachedIcon != null) {
                return clusterOptions.icon(cachedIcon)
            }
            var base: Bitmap?
            var i = 0
            do {
                base = baseBitmaps[i]
            } while (markersCount >= forCounts[i++])
            val bitmap = base!!.copy(Bitmap.Config.ARGB_8888, true)
            val text = markersCount.toString()
            paint.getTextBounds(text, 0, text.length, bounds)
            val x = bitmap.width / 2.0f
            val y = (bitmap.height - bounds.height()) / 2.0f - bounds.top
            val canvas = Canvas(bitmap)
            canvas.drawText(text, x, y, paint)
            val icon = BitmapDescriptorFactory.fromBitmap(bitmap)
            cache.put(markersCount, icon)
            return clusterOptions.icon(icon)
        } ?: return null
    }

    companion object {
        private val res = intArrayOf(R.drawable.m1, R.drawable.m2, R.drawable.m3, R.drawable.m4, R.drawable.m5)
        private val forCounts = intArrayOf(10, 100, 1000, 10000, Int.MAX_VALUE)
    }
}
