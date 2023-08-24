package info.mx.tracks.map

import android.graphics.Bitmap
import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.PhotoMetadata
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import com.google.android.libraries.places.api.net.PlacesClient
import info.mx.tracks.util.suspended
import timber.log.Timber
import java.util.*

class MxPlace : Parcelable {
    private var photoMeta: List<PhotoMetadata>? = null
    var id: String? = null
        private set
    var address: CharSequence? = null
        private set
    var name: CharSequence? = null
        private set
    var latLng: LatLng? = null
        private set
    var websiteUri: Uri? = null
        private set
    var phoneNumber: CharSequence? = null
        private set
    var rating: Float = 0.toFloat()
        private set
    private var photoReadyCallBack: PhotoReadyCallBack? = null
    lateinit var photoList: MutableList<Bitmap>

    interface PhotoReadyCallBack {
        fun onPhotoReceived(photoList: List<Bitmap>)
    }

    constructor(place: Place) {
        id = place.id
        address = place.address
        name = place.name
        latLng = place.latLng
        websiteUri = place.websiteUri
        phoneNumber = place.phoneNumber
        photoMeta = place.photoMetadatas
        if (place.rating != null) {
            rating = (place.rating!! * 10).toFloat()
        }
        if (rating > 5) {
            Timber.w("Rating > 5")
        }
        photoList = ArrayList()
        Timber.d("Place=$place")
    }

    suspend fun getPhotos(placesClient: PlacesClient, width: Int, height: Int) {
        val photoMetadata = photoMeta?.get(0)

        photoMetadata?.let {

            // Create a FetchPhotoRequest.
            val photoRequest = FetchPhotoRequest.builder(it)
                    .setMaxWidth(width)
                    .setMaxHeight(height)
                    .build()
            val bitmap = placesClient.fetchPhoto(photoRequest).suspended().bitmap

            photoList.add(bitmap)
            photoReadyCallBack!!.onPhotoReceived(photoList)
        }
    }

    fun setOnPhotoReadyCallBack(photoReadyCallBack: PhotoReadyCallBack) {
        this.photoReadyCallBack = photoReadyCallBack
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(if (address == null) null else address!!.toString())
        parcel.writeString(if (name == null) null else name!!.toString())
        latLng!!.writeToParcel(parcel, flags)
        parcel.writeString(if (websiteUri == null) null else websiteUri!!.toString())
        parcel.writeString(if (phoneNumber == null) null else phoneNumber!!.toString())
        parcel.writeFloat(rating)
    }

    constructor(source: Parcel) {
        id = source.readString()
        address = source.readString()
        name = source.readString()
        latLng = source.readParcelable(LatLng::class.java.classLoader)
        try {
            websiteUri = Uri.parse(source.readString())
        } catch (ignored: Exception) {
        }

        try {
            phoneNumber = source.readString()
        } catch (ignored: Exception) {
        }

        try {
            rating = source.readFloat()
        } catch (ignored: Exception) {
        }

    }

    companion object {

        @JvmField
        val CREATOR: Parcelable.Creator<MxPlace> = object : Parcelable.Creator<MxPlace> {
            override fun createFromParcel(`in`: Parcel): MxPlace {
                return MxPlace(`in`)
            }

            override fun newArray(size: Int): Array<MxPlace?> {
                return arrayOfNulls(size)
            }
        }
    }
}
