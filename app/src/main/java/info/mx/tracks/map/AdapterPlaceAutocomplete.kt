/*
 * Copyright (C) 2015 Google Inc. All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License")
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package info.mx.tracks.map

import android.graphics.Typeface
import android.text.style.StyleSpan
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.coroutineScope
import com.androidmapsextensions.GoogleMap
import com.androidmapsextensions.Marker
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.PlaceTypes
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import info.mx.tracks.R
import info.mx.tracks.util.suspended
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

class AdapterPlaceAutocomplete(
    private val fragment: Fragment,
    private val map: GoogleMap,
    private var bounds: LatLngBounds?,
    searchString: String
) : ArrayAdapter<PredictionPlace>(fragment.requireContext(), R.layout.item_place, R.id.textPlace1), Filterable {
    /**
     * Current results returned by this adapter.
     */
    private val resultPredictionList: ArrayList<PredictionPlace> = ArrayList()
    private var resultMarkerList: Array<Marker?>? = null
    private var detailRequest: Array<Boolean> = arrayOf(false)

    init {
        getAutocomplete(searchString)
    }

    /**
     * Sets the bounds for all subsequent queries.
     */
    @Suppress("unused")
    fun setBounds(bounds: LatLngBounds) {
        this.bounds = bounds
    }

    /**
     * Returns the number of results received in the last autocomplete query.
     */
    override fun getCount(): Int {
        return resultPredictionList.size
    }

    /**
     * Returns an item from the last autocomplete query.
     */
    override fun getItem(position: Int): PredictionPlace {
        return resultPredictionList[position]
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val row = super.getView(position, convertView, parent)

        val item = getItem(position)

        val textView1 = row.findViewById<TextView>(R.id.textPlace1)
        val textView2 = row.findViewById<TextView>(R.id.textPlace2)
        val imageProgress = row.findViewById<ImageView>(R.id.imagePlaceProgress)
        textView1.text = item.primaryText
        textView2.text = item.secondaryText

        if (!detailRequest[position]) {
            imageProgress.startAnimation(AnimationUtils.loadAnimation(context, R.anim.progress_animation_turn_left))
            imageProgress.visibility = View.VISIBLE

            val placesClient = Places.createClient(context)

            resultPredictionList[position].let {
                detailRequest[position] = true
                fragment.lifecycle.coroutineScope.launch {
                    val place = getPlaceMeta(it.placeId, placesClient)
                    val mxPlace = MxPlace(place)
                    val markerOption = mxPlace.getMarkerOption()
                    val marker = map.addMarker(markerOption)
                    resultMarkerList!![position] = marker
                    notifyDataSetChanged()
                }

            }
        } else {
            imageProgress.clearAnimation()
            imageProgress.visibility = View.GONE
        }
        return row
    }

    /**
     * Submits an autocomplete query to the Places Geo Data Autocomplete API.
     * Results are returned as frozen AutocompletePrediction objects, ready to be cached.
     * objects to store the Place ID and description that the API returns.
     * Returns an empty list if no results were found.
     * Returns null if the API client is not available or the query did not complete
     * successfully.
     * This method MUST be called off the main UI thread, as it will block until data is returned
     * from the API, which may include a network request.
     *
     * @return Results from the autocomplete API or null if the query was not successful.
     */
    private fun getAutocomplete(search: String) {
        Timber.i("Starting autocomplete query for: %s", search)

        // Create a new token for the autocomplete session. Pass this to FindAutocompletePredictionsRequest,
        // and once again when the user makes a selection (for example when calling fetchPlace()).
        val token = AutocompleteSessionToken.newInstance()
        val bounds = RectangularBounds.newInstance(bounds!!)
        val request = FindAutocompletePredictionsRequest.builder()
            // Call either setLocationBias() OR setLocationRestriction().
            .setLocationBias(bounds)
            //.setLocationRestriction(bounds)
            //.setCountry("au")
            .setTypesFilter(listOf(PlaceTypes.ADDRESS))
            .setSessionToken(token)
            .setQuery(search)
            .build()

        val placesClient = Places.createClient(context)
        placesClient.findAutocompletePredictions(request).addOnSuccessListener { response ->
            for (prediction in response.autocompletePredictions) {
                val predictionPlace = PredictionPlace(
                    prediction.getPrimaryText(STYLE_BOLD).toString(),
                    prediction.placeId,
                    prediction.getPrimaryText(STYLE_BOLD), prediction.getSecondaryText(STYLE_BOLD)
                )
                resultPredictionList.add(predictionPlace)
            }
            resultMarkerList = arrayOfNulls(resultPredictionList.size)
            detailRequest = BooleanArray(resultPredictionList.size).toTypedArray()
            if (resultPredictionList.isNotEmpty()) {
                notifyDataSetChanged()
            } else {
                // The API did not return any results, invalidate the data set.
                notifyDataSetInvalidated()
            }
        }.addOnFailureListener { Timber.e(it) }
    }

    private suspend fun getPlaceMeta(placeId: String, placesClient: PlacesClient): Place {
        val placeFields = mutableListOf(
            Place.Field.ID,
            Place.Field.FORMATTED_ADDRESS,
            Place.Field.INTERNATIONAL_PHONE_NUMBER,
            Place.Field.WEBSITE_URI,
            Place.Field.LOCATION,
            Place.Field.PHOTO_METADATAS,
            Place.Field.RATING
        )

        val request = FetchPlaceRequest.builder(placeId, placeFields)
            .build()
        return placesClient.fetchPlace(request).suspended().place
    }

    fun getMarker(position: Int): Marker {
        return resultMarkerList!![position]!!
    }

    override fun isEnabled(position: Int): Boolean {
        return resultMarkerList!![position] != null
    }

    companion object {

        private val STYLE_BOLD = StyleSpan(Typeface.BOLD)
    }
}
