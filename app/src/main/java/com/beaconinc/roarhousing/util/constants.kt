package com.beaconinc.roarhousing.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

const val Memory_Access_code = 120834
const val MB = 1000000.0
const val MB_THRESHOLD = 0.47

object PreferenceKeys{
    val VERIFY_PHONE = stringPreferencesKey("VERIFICATION_PHONE")
    val VERIFY_ID = stringPreferencesKey("VERIFICATION_ID")
}