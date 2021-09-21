package com.beaconinc.roarhousing.dashBoard

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.fragment.findNavController
import androidx.preference.MultiSelectListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreferenceCompat
import com.beaconinc.roarhousing.MainActivity
import com.beaconinc.roarhousing.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import timber.log.Timber

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        val darkMode = findPreference<SwitchPreferenceCompat>("dark_theme")
        darkMode?.setOnPreferenceChangeListener { _, theme ->
            if(theme == true) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            true
        }
    }
}