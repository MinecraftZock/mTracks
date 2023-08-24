package info.mx.tracks.map

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout

import info.hannes.commonlib.utils.ViewUtils
import info.mx.tracks.MxCoreApplication
import info.mx.tracks.R
import info.mx.tracks.prefs.MxPreferences

class MapButtonsOverlay(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    private val tracksBtn: ImageView
    private val stageBtn: ImageView
    private var listener: MapOverlayButtonsListener? = null
    private var tracksOn = true
    private var stageOn = false
    private val trafficBtn: ImageView
    private val typeBtn: ImageView
    private val clusterBtn: ImageView

    interface MapOverlayButtonsListener {
        fun onMapButtonStageClicked()

        fun onMapButtonTracksClicked()

        fun onMapButtonTrafficClicked()

        fun onMapButtonTypeClicked()

        fun onMapButtonClusterClicked()
    }

    init {

        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.layout_map_buttons_overlay, this, true)

        tracksBtn = findViewById(R.id.map_tracks_btn)
        stageBtn = findViewById(R.id.map_stage_btn)
        trafficBtn = findViewById(R.id.map_traffic_btn)
        typeBtn = findViewById(R.id.map_type_btn)
        clusterBtn = findViewById(R.id.map_cluster_btn)

        if (!isInEditMode) {
            trafficBtn.setBackgroundResource(
                    if (MxPreferences.getInstance().mapTraffic) R.drawable.map_traffic_on_btn else R.drawable.map_traffic_off_btn)
            clusterBtn.setBackgroundResource(
                    if (MxPreferences.getInstance().mapCluster) R.drawable.map_cluster_on_btn else R.drawable.map_cluster_off_btn)
            setAdmindView()
        }

        setParentLayoutParams()
        setButtonActions()
    }

    private fun setAdmindView() {
        tracksBtn.visibility = if (MxCoreApplication.isAdmin) View.VISIBLE else View.GONE
        stageBtn.visibility = if (MxCoreApplication.isAdmin) View.VISIBLE else View.GONE
    }

    fun toggleTracksShow(): Boolean {
        tracksOn = !tracksOn
        return tracksOn
    }

    fun toggleStageShow(): Boolean {
        stageOn = !stageOn
        return stageOn
    }

    fun toggleTrafficShow(): Boolean {
        val trafficOn = !MxPreferences.getInstance().mapTraffic
        trafficBtn.setBackgroundResource(if (trafficOn) R.drawable.map_traffic_on_btn else R.drawable.map_traffic_off_btn)
        return trafficOn
    }

    fun toggleClusterShow(): Boolean {
        val clusterOn = !MxPreferences.getInstance().mapCluster
        clusterBtn.setBackgroundResource(if (clusterOn) R.drawable.map_cluster_on_btn else R.drawable.map_cluster_off_btn)
        return clusterOn
    }

    fun setTracksEnabled(enabled: Boolean) {
        if (enabled) {
            ViewUtils.enableButtons(tracksBtn)
        } else {
            ViewUtils.disableButtons(tracksBtn)
        }
    }

    fun setStageEnabled(enabled: Boolean) {
        if (enabled) {
            ViewUtils.enableButtons(stageBtn)
        } else {
            ViewUtils.disableButtons(stageBtn)
        }
    }

    private fun setParentLayoutParams() {
        this.orientation = HORIZONTAL
    }

    private fun setButtonActions() {
        tracksBtn.setOnClickListener { listener!!.onMapButtonTracksClicked() }

        stageBtn.setOnClickListener { listener!!.onMapButtonStageClicked() }

        trafficBtn.setOnClickListener { listener!!.onMapButtonTrafficClicked() }

        clusterBtn.setOnClickListener { listener!!.onMapButtonClusterClicked() }

        typeBtn.setOnClickListener { listener!!.onMapButtonTypeClicked() }

    }

    fun setListener(listener: MapOverlayButtonsListener) {
        this.listener = listener
    }
}
