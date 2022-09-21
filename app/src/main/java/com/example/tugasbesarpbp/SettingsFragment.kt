package com.example.tugasbesarpbp

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)


        // add back arrow to toolbar
        if (activity != null) {
            (activity as HomeActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
            (activity as HomeActivity).supportActionBar?.setDisplayShowHomeEnabled(true)

            // on click back button
            (activity as HomeActivity).supportActionBar?.setHomeButtonEnabled(true)
        }
    }
}