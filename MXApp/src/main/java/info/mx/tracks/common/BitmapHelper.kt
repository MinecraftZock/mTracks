package info.mx.tracks.common

import android.content.Context
import com.google.android.gms.maps.model.BitmapDescriptor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import info.mx.tracks.R
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import java.util.HashMap

object BitmapHelper {
    private val bitmapDescriptorMap = HashMap<String, BitmapDescriptor>()
    private val bitmapMap = HashMap<String, Bitmap?>()
    fun getBitmapDescriptor(
        context: Context,
        iconRes: Int,
        trackAccess: String?
    ): BitmapDescriptor? {
        val bitmapDescriptor: BitmapDescriptor?
        if (bitmapDescriptorMap.containsKey(iconRes.toString() + trackAccess)) {
            bitmapDescriptor = bitmapDescriptorMap[iconRes.toString() + trackAccess]
        } else {
            when (trackAccess) {
                "C" -> {
                    var bmpBasis = BitmapFactory.decodeResource(context.resources, iconRes)
                    val bmpClosed =
                        BitmapFactory.decodeResource(context.resources, R.drawable.pin_closed)
                    bmpBasis = mergeBitmaps(bmpBasis, bmpClosed)
                    bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bmpBasis)
                }
                "CS", "CT" -> {
                    var bmpBasis = BitmapFactory.decodeResource(context.resources, iconRes)
                    val bmpClosed =
                        BitmapFactory.decodeResource(context.resources, R.drawable.pin_attention)
                    bmpBasis = mergeBitmaps(bmpBasis, bmpClosed)
                    bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bmpBasis)
                }
                else -> bitmapDescriptor = BitmapDescriptorFactory.fromResource(iconRes)
            }
            bitmapDescriptorMap[iconRes.toString() + trackAccess] = bitmapDescriptor
        }
        return bitmapDescriptor
    }

    fun getBitmap(context: Context, iconRes: Int, trackAccess: String?): Bitmap? {
        val bitmap: Bitmap?
        if (trackAccess == null || bitmapMap.containsKey(iconRes.toString() + trackAccess)) {
            bitmap = bitmapMap[iconRes.toString() + trackAccess]
        } else {
            bitmap = when (trackAccess) {
                "C" -> {
                    val bmpBasis = BitmapFactory.decodeResource(context.resources, iconRes)
                    val bmpClosed =
                        BitmapFactory.decodeResource(context.resources, R.drawable.pin_closed)
                    mergeBitmaps(bmpBasis, bmpClosed)
                }
                "CS", "CT" -> {
                    val bmpBasis = BitmapFactory.decodeResource(context.resources, iconRes)
                    val bmpClosed =
                        BitmapFactory.decodeResource(context.resources, R.drawable.pin_attention)
                    mergeBitmaps(bmpBasis, bmpClosed)
                }
                else -> BitmapFactory.decodeResource(context.resources, iconRes)
            }
            bitmapMap[iconRes.toString() + trackAccess] = bitmap
        }
        return bitmap
    }

    private fun mergeBitmaps(bmp1: Bitmap, bmp2: Bitmap?): Bitmap {
        val bmOverlay = Bitmap.createBitmap(bmp1.width, bmp1.height, bmp1.config)
        val canvas = Canvas(bmOverlay)
        canvas.drawBitmap(bmp1, Matrix(), null)
        canvas.drawBitmap(bmp2!!, 0f, 0f, null)
        return bmOverlay
    }
}