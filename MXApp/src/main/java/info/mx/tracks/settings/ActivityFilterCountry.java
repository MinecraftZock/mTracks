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
package info.mx.tracks.settings;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;

import info.mx.tracks.base.ActivityRx;
import info.mx.tracks.R;
import info.mx.tracks.prefs.MxPreferences;

public class ActivityFilterCountry extends ActivityRx {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_country);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> {
            //onBackPressed()
            finish();
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        // force a refresh of markers in map
        MxPreferences.getInstance().edit().putSearchOpenDi(MxPreferences.getInstance().getSearchOpenDi()).commit();
    }

}
