package com.beaconinc.roarhousing.home

import android.os.Bundle
import android.util.DisplayMetrics
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import com.beaconinc.roarhousing.MainActivity
import com.beaconinc.roarhousing.R
import com.beaconinc.roarhousing.cloudModel.FirebaseLodge
import com.beaconinc.roarhousing.listAdapters.LodgeClickListener
import com.beaconinc.roarhousing.listAdapters.LodgesAdapter
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.launch


class FilterFragment : Fragment() {

    //private lateinit var lodgesAdapter: LodgesAdapter
    lateinit var fireStore: FirebaseFirestore
    private lateinit var lodgesQuery: Query
//    private lateinit var propertiesQuery: Query
    private lateinit var progressBar: ProgressBar
    private lateinit var lodgesAdapter: LodgesAdapter
    private lateinit var connectionView: ConstraintLayout
    private lateinit var adView: AdView
    private lateinit var adViewParent: FrameLayout
    private var initialLayoutComplete = false

    private val filter: String by lazy {
        arguments?.get("choice") as String
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fireStore = FirebaseFirestore.getInstance()
        val settingsPref = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val campus = settingsPref.getString("campus", "")

        lodgesQuery = if(filter == "Simple") {
            fireStore.collection("lodges")
                .whereEqualTo("location",campus)
                .whereLessThan("subPayment","100000")
        } else {
            fireStore.collection("lodges")
                .whereEqualTo("location",campus)
                .whereGreaterThan("subPayment","100000")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_filter, container, false)
        val filterRecycler = view.findViewById<RecyclerView>(R.id.lodgeList)
        val title = view.findViewById<TextView>(R.id.titleText)
        val filterBackBtn = view.findViewById<ImageView>(R.id.filterBackBtn)
        adViewParent = view.findViewById(R.id.ad_view_container)
        connectionView = view.findViewById(R.id.connectionView)
        progressBar = view.findViewById(R.id.progressBar)
        title.text = getString(R.string.choice_title, filter)

        adView = AdView(requireContext())
        adViewParent.addView(adView)

        adViewParent.viewTreeObserver.addOnGlobalLayoutListener {
            if(!initialLayoutComplete) {
                initialLayoutComplete = true
                loadBannerAd()
            }
        }

        lodgesAdapter = LodgesAdapter(LodgeClickListener({
            val bundle = bundleOf("Lodge" to it)
            findNavController().navigate(R.id.lodgeDetail, bundle)
        }, {}), this, false)

        showProgress()
        filterRecycler.adapter = lodgesAdapter
        fetchLiveData()

        filterBackBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        (activity as MainActivity).connectivityChecker?.apply {
            lifecycle.addObserver(this)
            connectedStatus.observe(viewLifecycleOwner, Observer {
                if(it) {
                    smallAdvertNativeAd()
                    mediumAdvertNativeAd()
                }
            })
        }
        return view
    }


    public override fun onPause() {
        adView.pause()
        super.onPause()
    }

    private fun smallAdvertNativeAd() {
        val adLoader = AdLoader.Builder(requireContext(), "ca-app-pub-3940256099942544/2247696110")
            .forNativeAd { ad: NativeAd ->
                run {
                    lifecycleScope.launchWhenStarted {
                        lodgesAdapter.postAd1(ad)
                    }
                    if (this.isDetached) {
                        ad.destroy()
                        return@forNativeAd
                    }
                }
            }.build()
        adLoader.loadAds(AdRequest.Builder().build(), 5)
    }

    private fun mediumAdvertNativeAd() {
        val adLoader = AdLoader.Builder(requireContext(), "ca-app-pub-3940256099942544/2247696110")
            .forNativeAd { ad: NativeAd ->
                run {
                    lifecycleScope.launchWhenStarted {
                        lodgesAdapter.postAd2(ad)
                    }
                    if (this.isDetached) {
                        ad.destroy()
                        return@forNativeAd
                    }
                }
            }.build()
        adLoader.loadAds(AdRequest.Builder().build(), 5)
    }

    private fun fetchLiveData() {
        lodgesQuery.get().addOnSuccessListener { result ->
            result.documents.mapNotNull { snapLodge ->
                snapLodge.toObject(FirebaseLodge::class.java)
            }.also { lodges ->
                lifecycleScope.launch {
                    if(lodges.isNullOrEmpty()){
                        lodgesAdapter.addLodgeAndProperty(emptyList(),false)
                        hideProgress()
                    }else {
                        lodgesAdapter.addLodgeAndProperty(lodges,false)
                        hideProgress()
                    }
                }
            }
        }.addOnFailureListener {
            showInternetError()
            hideProgress()
        }
    }

    override fun onResume() {
        super.onResume()
        adView.resume()
    }


    override fun onDestroy() {
        adView.destroy()
        super.onDestroy()
    }

    private fun showProgress() {
        progressBar.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        lifecycleScope.launch {
            progressBar.visibility = View.GONE
        }
    }

    private fun showInternetError() {
        (activity as MainActivity).connectivityChecker?.apply {
            lifecycle.addObserver(this)
            connectedStatus.observe(viewLifecycleOwner, Observer {
                if (!it) {
                    connectionView.visibility = View.VISIBLE
                }else {
                    lodgesAdapter.addLodgeAndProperty(emptyList(),false)
                }
            })
        }
    }

    private fun loadBannerAd() {
        adView.adUnitId = "ca-app-pub-3940256099942544/2247696110"
        adView.adSize = getScreenSize()

        val adRequest = AdRequest
            .Builder().build()
        adView.loadAd(adRequest)
    }

    private fun getScreenSize(): AdSize {
        val display = requireActivity().windowManager.defaultDisplay
        val outMetrics = DisplayMetrics()
        display.getRealMetrics(outMetrics)
        val density  = outMetrics.density

        var adWidthPixels = adViewParent.width.toFloat()
            if(adWidthPixels == 0f) {
                adWidthPixels = outMetrics.widthPixels.toFloat()
            }

        val adWidth = (adWidthPixels/density).toInt()
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(requireContext(),adWidth)
    }
}