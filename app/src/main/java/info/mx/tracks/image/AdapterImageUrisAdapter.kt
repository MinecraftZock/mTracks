package info.mx.tracks.image

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.BaseAdapter
import android.widget.ImageView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import info.mx.tracks.R
import info.mx.tracks.common.PictureHelper.getRealPathFromUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class AdapterImageUrisAdapter(
    private val context: Context,
    private val uris: ArrayList<Uri>,
    private val lifecycleOwner: LifecycleOwner
) : BaseAdapter() {

    init {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        DisplayMetrics().let { displayMetrics ->
            wm.defaultDisplay.getMetrics(displayMetrics)
            desiredScreenWidth = displayMetrics.widthPixels * 80 / 100
            desiredScreenHeight = displayMetrics.heightPixels * PERCENTAGE / 100
        }
    }

    override fun getCount(): Int {
        return uris.size
    }

    override fun getItem(position: Int): Any {
        return uris[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView

        if (view == null) {
            val inflater = LayoutInflater.from(context)
            view = inflater.inflate(R.layout.item_image, parent, false)
        }

        val layout = view.findViewById<View>(R.id.layoutPreview)
        val imageView = view.findViewById<ImageView>(R.id.imageShare)
        if (getItem(position).toString().startsWith("http")) {
            if (imageView.tag == null) {
                lifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                    AsyncSetImage.loadImageIntoView(
                        imageView,
                        getItem(position).toString()
                    )
                }
            }
        } else if (getItem(position).toString().startsWith("content")) {
            val realPath = getRealPathFromUri(context, Uri.parse(getItem(position).toString()))
            val imgFile = File(realPath)
            if (imgFile.exists()) {
                val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                imageView.setImageBitmap(myBitmap)
            }
        } else if (getItem(position).toString().startsWith("file:///")) {
            val realPath = (getItem(position) as Uri).path
            if (realPath != null) {
                val imgFile = File(realPath)
                if (imgFile.exists()) {
                    val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                    imageView.setImageBitmap(myBitmap)
                }
            }
        }

        // ListView uses all place, grid only halve of it
        val params = layout.layoutParams
        params.width = desiredScreenWidth / (if (count == 1) 1 else 2)
        params.height = params.width * 3 / 4
        layout.layoutParams = params
        return view
    }

    companion object {
        private const val PERCENTAGE = 30
        var desiredScreenWidth: Int = 0
        var desiredScreenHeight: Int = 0
    }
}
