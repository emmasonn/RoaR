package com.beaconinc.roarhousing


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.net.ConnectivityManager
import android.net.Uri
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.getSystemService
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.preference.PreferenceManager
import com.beaconinc.roarhousing.cloudModel.FirebaseLodge
import com.beaconinc.roarhousing.database.AppDatabase
import com.beaconinc.roarhousing.util.ConnectivityChecker
import com.google.android.gms.ads.MobileAds
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

        lifecycleScope.launch(Dispatchers.Default) {
            subscribeProductLodge(settingsPref)
            subscribeUnecLodge(settingsPref)
            subscribeUnnLodge(settingsPref)
        }

        connectivityChecker = connectivityChecker(this)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val string = intent?.getStringExtra("notification")
        val lodgeId = intent?.getStringExtra("lodgeId")
        val productId = intent?.getStringExtra("productId")
        when (string) {
            "lodge_notifier" -> {
                fireStore.collection("lodges").document(lodgeId!!)
                    .get().addOnSuccessListener { snapShot ->
                        snapShot.toObject(FirebaseLodge::class.java).also { data ->
                            lifecycleScope.launch {
                                val bundle = bundleOf("Lodge" to data!!)
                                navController.navigate(R.id.lodgeDetail, bundle)
                            }
                        }
                    }
            }
            "product_notifier" -> {
                val link = Uri.parse("https://roar.com.ng/property/${productId}")
                navController.navigate(link)
            }
            else -> {
                navController.handleDeepLink(intent)
            }
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

    private suspend fun subscribeUnnLodge(sharedPreferences: SharedPreferences) {
        withContext(Dispatchers.Default) {
            val unn = sharedPreferences.getStringSet("unn_topics", null)

            unn?.forEach { myTopic ->
                subscribeTopics("/topics/${myTopic}")
            }
        }
    }

    private suspend fun subscribeUnecLodge(sharedPreferences: SharedPreferences) {
        withContext(Dispatchers.Default) {
            val unec = sharedPreferences.getStringSet("unec_topics", null)

            unec?.forEach { myTopic ->
                subscribeTopics("/topics/${myTopic}")
            }
        }
    }

    private suspend fun subscribeProductLodge(sharedPreferences: SharedPreferences) {
        withContext(Dispatchers.Default) {
            val products = sharedPreferences.getStringSet("product_topics", null)
             Timber.i("products: $products")
            products?.forEach { myTopic ->
                subscribeTopics("/topics/${myTopic}")
            }
        }
    }

    private fun subscribeTopics(topic: String) {
        FirebaseMessaging.getInstance().subscribeToTopic(topic)
    }

}