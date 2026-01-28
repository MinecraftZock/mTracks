package info.mx.tracks.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.RadioButton
import androidx.fragment.app.DialogFragment
import info.mx.tracks.R
import info.mx.core_generated.prefs.MxPreferences

class MapLayerDialog : DialogFragment(), CompoundButton.OnCheckedChangeListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, 0)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.map_layer_contextmenu, container)
        val toggleStd = view.findViewById<RadioButton>(R.id.radioMapStd)
        val toggleSatellite = view.findViewById<RadioButton>(R.id.radioMapStellite)
        val toggleHybrid = view.findViewById<RadioButton>(R.id.radioMapHybrid)
        val toggleTerrain = view.findViewById<RadioButton>(R.id.radioMapTerrain)
        when (MxPreferences.getInstance().mapType) {
            0 -> toggleStd.isChecked = true
            1 -> toggleHybrid.isChecked = true
            2 -> toggleTerrain.isChecked = true
            3 -> toggleSatellite.isChecked = true
        }
        toggleStd.setOnCheckedChangeListener(this)
        toggleSatellite.setOnCheckedChangeListener(this)
        toggleHybrid.setOnCheckedChangeListener(this)
        toggleTerrain.setOnCheckedChangeListener(this)
        dialog!!.setCancelable(true)
        return view
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        val id = buttonView.id
        if (isChecked) {
            when (id) {
                R.id.radioMapStd -> MxPreferences.getInstance().edit().putMapType(0).commit()
                R.id.radioMapStellite -> MxPreferences.getInstance().edit().putMapType(3).commit()
                R.id.radioMapHybrid -> MxPreferences.getInstance().edit().putMapType(1).commit()
                R.id.radioMapTerrain -> MxPreferences.getInstance().edit().putMapType(2).commit()
            }
            dismiss()
        }
    }
}
