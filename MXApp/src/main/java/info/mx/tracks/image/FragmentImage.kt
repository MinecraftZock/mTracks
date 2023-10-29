package info.mx.tracks.image

import android.content.Context
import android.database.Cursor
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import com.robotoworks.mechanoid.db.SQuery
import info.mx.tracks.common.PictureHelper
import info.mx.tracks.databinding.FragmentImageSliderBinding
import info.mx.tracks.sqlite.MxInfoDBContract
import info.mx.tracks.sqlite.PicturesRecord

class FragmentImage : Fragment(), LoaderManager.LoaderCallbacks<Cursor> {
    private var imageRestId: Long = 0
    private var _binding: FragmentImageSliderBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentImageSliderBinding.inflate(inflater, container, false)
        val view = binding.root
        imageRestId = requireArguments().getLong(ActivityBaseImageSlider.IMAGE_ID, 0)
        loaderManager.initLoader(LOADER_PICTURE_FULL_SIZE, null, this)
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateLoader(loader: Int, bundle: Bundle?): Loader<Cursor> {
        return SQuery.newQuery().expr(MxInfoDBContract.Pictures.REST_ID, SQuery.Op.EQ, imageRestId)
            .createSupportLoader(MxInfoDBContract.Pictures.CONTENT_URI, null, MxInfoDBContract.Pictures._ID)
    }

    override fun onLoadFinished(loader: Loader<Cursor>, cursor: Cursor) {
        when (loader.id) {
            LOADER_PICTURE_FULL_SIZE -> if (cursor.count >= 1) {
                if (cursor.isBeforeFirst) {
                    cursor.moveToNext()
                }
                val rec = PicturesRecord.fromCursor(cursor)
                setImage(rec)
            }
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        when (loader.id) {
            LOADER_PICTURE_FULL_SIZE -> {
            }
        }
    }

    private fun setImage(record: PicturesRecord) {
        val point = Point()

        val wm = requireContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val width: Int
        val height: Int
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = wm.currentWindowMetrics
            val windowInsets: WindowInsets = windowMetrics.windowInsets

            val insets = windowInsets.getInsetsIgnoringVisibility(
                WindowInsets.Type.navigationBars() or WindowInsets.Type.displayCutout()
            )
            val insetsWidth = insets.right + insets.left
            val insetsHeight = insets.top + insets.bottom

            val b = windowMetrics.bounds
            width = b.width() - insetsWidth
            height = b.height() - insetsHeight
            point.x = width
            point.y = height
        } else {
            val size = Point()
            val display = wm.defaultDisplay // deprecated in API 30
            display?.getSize(size) // deprecated in API 30
            width = size.x
            height = size.y
            point.x = width
            point.y = height
        }

        val size = if (point.y > point.x)
            point.y
        else
            point.x
        val pictureShown = PictureHelper.checkAndSetImage(requireContext(), record, binding.imageFullSize, record.localfile, size)
        // stop loading file changes
        if (pictureShown) {
            loaderManager.destroyLoader(LOADER_PICTURE_FULL_SIZE)
        }
    }

    companion object {
        private const val LOADER_PICTURE_FULL_SIZE = 123

        /**
         * Create a new instance of FragmentImage, providing "imageRestId" as an argument.
         */
        fun newInstance(imageRestId: Long): FragmentImage {
            val f = FragmentImage()
            // Supply num input as an argument.
            val args = Bundle()
            args.putLong(ActivityBaseImageSlider.IMAGE_ID, imageRestId)
            f.arguments = args
            return f
        }

    }
}
