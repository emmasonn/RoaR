package com.column.roar


import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.util.DisplayMetrics
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.getSystemService
import androidx.core.os.bundleOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.preference.PreferenceManager
import com.column.roar.cloudModel.FirebaseLodge
import com.column.roar.database.AppDatabase
import com.column.roar.util.ConnectivityChecker
import com.google.android.gms.ads.*
import com.google.android.gms.ads.nativead.NativeAd
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var fireStore: FirebaseFirestore
    var connectivityChecker: ConnectivityChecker? = null
    private lateinit var navController: NavController
    private lateinit var adViewParent: FrameLayout
    private lateinit var adView: AdView
    private var initialLayoutComplete = false
    private lateinit var isAddInitialized: String //we want to load ads once

    val homeScreenAd = MutableLiveData<NativeAd>()
    val storeScreenAd = MutableLiveData<NativeAd>()
    val detailScreenSmallAd = MutableLiveData<NativeAd>()
    val detailScreenMediumAd = MutableLiveData<NativeAd>()

    lateinit var db: AppDatabase
    var clientId: String? = null
    val sharedPref: SharedPreferences by lazy {
        this.getPreferences(Context.MODE_PRIVATE)
    }

    var chipState = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseAnalytics.getInstance(this)

        setContentView(R.layout.activity_main)
        adViewParent = findViewById(R.id.ad_view_container)
        val cancelBtn = findViewById<ImageView>(R.id.cancelBtn)

        MobileAds.initialize(this)
        MobileAds.setRequestConfiguration(
            RequestConfiguration.Builder()
                .setTestDeviceIds(listOf("1FDC32B9CBE3CDABBE6B40D81394FA10"))
                .build())

        cancelBtn.setOnClickListener {
            adViewParent.visibility = View.GONE
            adView.pause()
        }

        fireStore = FirebaseFirestore.getInstance()
        clientId = sharedPref.getString("user_id", null)
        db = AppDatabase.getInstance(applicationContext)
        navController = findNavController(R.id.mainNavHost)

        adView = AdView(this)
        adView.adUnitId = "ca-app-pub-3940256099942544/6300978111"
        adViewParent.addView(adView)
        adView.adSize = getScreenSize()

        adViewParent.viewTreeObserver.addOnGlobalLayoutListener {
            if(!initialLayoutComplete) {
                initialLayoutComplete = true
                performNetworkAction()
            }
        }

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

    override fun onPause() {
        adView.pause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        adView.resume()
    }

    override fun onDestroy() {
        adView.destroy()
        super.onDestroy()
    }

    private fun performNetworkAction() {
        connectivityChecker?.apply {
            lifecycle.addObserver(this)
            connectedStatus.observe(this@MainActivity, {
                if (it && !::isAddInitialized.isInitialized) {
                    loadBannerAd()
                    lifecycleScope.launch(Dispatchers.Default) {
                        smallAdvertNativeAd()
                        otherSmallNativeAds()
                        mediumAdvertNativeAd()
                    }
                    isAddInitialized = "initialized"
                }
            })
        }
    }

    private fun handleFirebaseDynamicLink (intent: Intent?) {
        intent?.let {
            FirebaseDynamicLinks.getInstance()
                .getDynamicLink(intent)
                .addOnSuccessListener { pendingLink ->
                    pendingLink?.let {
                        val deepLink: Uri? = pendingLink.link ?: Uri.parse("")
                        processDynamicLink(deepLink)
                    }
                }.addOnFailureListener(this) { e ->
                    Timber.e(e, "Error cannot parse dynamic link")
                }
        }
    }

    private fun processDynamicLink(uri: Uri?) {
        val lodgeId = uri?.getQueryParameter("lodgeId")
        val productId = uri?.getQueryParameter("productId")

        lodgeId?.let {
            val deepLink = Uri.parse("https://unnapp.page.link/lodges/$lodgeId")
            navController.navigate(deepLink)
        }

        productId?.let {
            val deepLink = Uri.parse("https://unnapp.page.link/ads/${productId}")
            navController.navigate(deepLink)
        }
    }

    //for now notification clicks are handle in the onNewIntent
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        intent?.let {
            handleFirebaseDynamicLink(intent)
        }

        intent?.let {
            handleNotificationIntent(intent)
        }
    }

    private fun handleNotificationIntent(intent: Intent?) {
        val string = intent?.getStringExtra("notification")
        val lodgeId = intent?.getStringExtra("lodgeId")
        val productId = intent?.getStringExtra("productId")
        when (string) {
            "lodge_notifier" -> {
                val deepLink = Uri.parse("https://unnapp.page.link/lodges/$lodgeId")
                navController.navigate(deepLink)
            }

            "product_notifier" -> {
                val link = Uri.parse("https://unnapp.page.link/ads/${productId}")
                navController.navigate(link)
            }
            else -> {
                navController.handleDeepLink(intent)
            }
        }
    }

    private fun loadBannerAd() {
        lifecycleScope.launch(Dispatchers.Default) {
        val adRequest = AdRequest
            .Builder().build()
            delay(1000)
            withContext(Dispatchers.Main) {
                adView.loadAd(adRequest)
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

    private fun getScreenSize(): AdSize {
        val display = windowManager.defaultDisplay
        val outMetrics = DisplayMetrics()
        display.getRealMetrics(outMetrics)
        val density  = outMetrics.density

        var adWidthPixels = adViewParent.width.toFloat()
        if(adWidthPixels == 0f) {
            adWidthPixels = outMetrics.widthPixels.toFloat()
        }

        val adWidth = (adWidthPixels/density).toInt()
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this,adWidth)
    }

    private fun subscribeTopics(topic: String) {
        FirebaseMessaging.getInstance().subscribeToTopic(topic)
    }

    private fun smallAdvertNativeAd() {
        val adLoader = AdLoader.Builder(this, "ca-app-pub-3940256099942544/2247696110")
            .forNativeAd { ad: NativeAd ->
                run {
                    lifecycleScope.launch(Dispatchers.Main){
                        homeScreenAd.postValue(ad)
                        storeScreenAd.postValue(ad)
                    }
                    if (this.isDestroyed) {
                        ad.destroy()
                        return@forNativeAd
                    }
                }
            }.build()
        adLoader.loadAds(AdRequest.Builder().build(), 5)
    }

    private fun otherSmallNativeAds() {
        val adLoader = AdLoader.Builder(this, "ca-app-pub-3940256099942544/2247696110")
            .forNativeAd { ad: NativeAd ->
                run {
                    lifecycleScope.launch(Dispatchers.Main){
                        storeScreenAd.postValue(ad)
                        detailScreenSmallAd.postValue(ad)
                    }
                    if (this.isDestroyed) {
                        ad.destroy()
                        return@forNativeAd
                    }
                }
            }.build()
        adLoader.loadAds(AdRequest.Builder().build(), 5)
    }

    private fun mediumAdvertNativeAd() {
        val adLoader = AdLoader.Builder(this, "ca-app-pub-3940256099942544/2247696110")
            .forNativeAd { ad: NativeAd ->
                run {
                    lifecycleScope.launch(Dispatchers.Main){
                        detailScreenMediumAd.postValue(ad)
                    }
                    if (this.isDestroyed) {
                        ad.destroy()
                        return@forNativeAd
                    }
                }
            }.build()
        adLoader.loadAds(AdRequest.Builder().build(), 5)
    }

}