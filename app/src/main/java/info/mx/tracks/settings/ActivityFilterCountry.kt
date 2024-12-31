/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package info.mx.tracks.settings

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import info.mx.tracks.R
import info.mx.tracks.base.ActivityRx
import info.mx.tracks.databinding.ActivityFilterCountryBinding
import info.mx.tracks.prefs.MxPreferences

class ActivityFilterCountry : ActivityRx() {

    private lateinit var binding: ActivityFilterCountryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFilterCountryBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { v: View? ->
            //onBackPressed()
            finish()
        }
    }

    override fun onPause() {
        super.onPause()
        // force a refresh of markers in map
        MxPreferences.getInstance().edit().putSearchOpenDi(MxPreferences.getInstance().searchOpenDi)
            .commit()
    }
}