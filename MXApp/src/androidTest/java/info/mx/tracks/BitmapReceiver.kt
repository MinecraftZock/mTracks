package info.mx.tracks

import android.graphics.Bitmap
import androidx.test.core.graphics.writeToTestStorage
import androidx.test.espresso.action.ViewActions

class BitmapReceiver(val name: String) : ViewActions.BitmapReceiver {
    override fun onBitmapCaptured(bitmap: Bitmap?) {
        bitmap?.writeToTestStorage(name)
    }
}
