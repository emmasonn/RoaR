package com.beaconinc.roarhousing


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.net.ConnectivityManager
import android.provider.ContactsContract
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.getSystemService
import androidx.core.os.bundleOf
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceDataStore
import androidx.preference.PreferenceManager
import com.beaconinc.roarhousing.cloudModel.FirebaseLodge
import com.beaconinc.roarhousing.database.AppDatabase
import com.beaconinc.roarhousing.util.ConnectivityChecker
import com.google.android.gms.ads.MobileAds
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber

//const val TOPIC = "/topics/myTopic"

class MainActivity : AppCompatActivity() {

    private lateinit var fireStore: FirebaseFirestore
    var connectivityChecker: ConnectivityChecker? = null
    private lateinit var navController: NavController

    lateinit var db: AppDatabase
    var clientId: String? = null
    val sharedPref: SharedPreferences by lazy {
        this.getPreferences(Context.MODE_PRIVATE)
    }

    var chipState = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        MobileAds.initialize(this)

        fireStore = FirebaseFirestore.getInstance()
        clientId = sharedPref.getString("user_id", null)
        db = AppDatabase.getInstance(applicationContext)
        navController = findNavController(R.id.mainNavHost)

        val settingsPref = PreferenceManager.getDefaultSharedPreferences(this)
        val theme = settingsPref.getBoolean("dark_theme", false)

        if (theme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        subScribeTopics(settingsPref)
        connectivityChecker = connectivityChecker(this)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val string = intent?.getStringExtra("notification")
        val lodgeId = intent?.getStringExtra("lodgeId")
        if (string == "lodge_notifier") {
            fireStore.collection("lodges").document(lodgeId!!)
                .get().addOnSuccessListener { snapShot ->
                    snapShot.toObject(FirebaseLodge::class.java).also { data ->
                        lifecycleScope.launch {
                            val bundle = bundleOf("Lodge" to data!!)
                            navController.navigate(R.id.lodgeDetail, bundle)
                        }
                    }
                }
        } else {
            Timber.i("DeepLink Called")
            navController.handleDeepLink(intent)
        }
    }

    private fun connectivityChecker(activity: Activity): ConnectivityChecker? {
        val connectivityManager = activity.getSystemService<ConnectivityManager>()
        return if (connectivityManager != null) {
            ConnectivityChecker(connectivityManager)
        } else {
            null
        }
    }

    private fun subScribeTopics(sharedPreferences: SharedPreferences) {
        val topics = sharedPreferences.getStringSet("notify_topics", null)
        val propertyTopic = sharedPreferences.getString("propertyTopic",null)
        Timber.i("topics: $topics")
        topics?.forEach { myTopic ->
            subScribeLodgeTopic("/topics/${myTopic}")
        }
        subScribeLodgeTopic("/topics/${propertyTopic}")
    }

    private fun subScribeLodgeTopic(topic: String) {
        FirebaseMessaging.getInstance().subscribeToTopic(topic)
            .addOnSuccessListener {
                Toast.makeText(applicationContext, "successful", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener { e ->
                Timber.e(e, "Error Failed to Subscribe")
            }
    }

}