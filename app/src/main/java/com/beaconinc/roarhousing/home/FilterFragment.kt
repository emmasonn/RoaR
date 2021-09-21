package com.beaconinc.roarhousing.home

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.beaconinc.roarhousing.R
import com.beaconinc.roarhousing.cloudModel.FirebaseLodge
import com.beaconinc.roarhousing.cloudModel.FirebaseProperty
import com.beaconinc.roarhousing.listAdapters.LodgeClickListener
import com.beaconinc.roarhousing.listAdapters.NewListAdapter
import com.beaconinc.roarhousing.listAdapters.storeAdapter.PropertyListAdapter.PropertyClickListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.nativead.NativeAd
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.launch


class FilterFragment : Fragment() {

    //private lateinit var lodgesAdapter: LodgesAdapter
    lateinit var fireStore: FirebaseFirestore
    private lateinit var lodgesQuery: Query
    private lateinit var propertiesQuery: Query
    private lateinit var roarItemsAdapter: NewListAdapter
    private lateinit var progressBar: ProgressBar

    private val filter: String by lazy {
        arguments?.get("choice") as String
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        smallAdvertNativeAd()
        mediumAdvertNativeAd()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fireStore = FirebaseFirestore.getInstance()
        lodgesQuery = fireStore.collection("lodges")
        propertiesQuery = fireStore.collection("properties")

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
        progressBar = view.findViewById(R.id.progressBar)
        title.text = getString(R.string.choice_title, filter)

//        lodgesAdapter = LodgesAdapter(LodgeClickListener { data ->
//            val bundle = bundleOf("Lodge" to data )
//           findNavController().navigate(R.id.lodgeDetail,bundle)
//        })
        showProgress()
        roarItemsAdapter = NewListAdapter(LodgeClickListener ({ lodge ->
            val bundle = bundleOf("Lodge" to lodge)
            findNavController().navigate(R.id.lodgeDetail, bundle)
        },{}), PropertyClickListener({}, {}, justClick = {
            findNavController().navigate(R.id.lodgeProperty)
        }), this)
        filterRecycler.adapter = roarItemsAdapter
        fetchLiveData()

//        roarItemsAdapter = NewListAdapter(LodgeClickListener { lodge ->
//                 val bundle = bundleOf("Lodge" to lodge )
//                 findNavController().navigate(R.id.lodgeDetail, bundle)
//        },
//            PropertyClickListener(
//            {},{}, justClick = {
//              findNavController().navigate(R.id.lodgeProperty)
//            })
//        )

        filterBackBtn.setOnClickListener {
            findNavController().navigateUp()
        }
        return view
    }

    private fun smallAdvertNativeAd() {
        val adLoader = AdLoader.Builder(requireContext(), "ca-app-pub-3940256099942544/2247696110")
            .forNativeAd { ad: NativeAd ->
                run {
                    lifecycleScope.launchWhenCreated {
                        roarItemsAdapter.postAd1(ad)
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
                    lifecycleScope.launchWhenCreated {
                        roarItemsAdapter.postAd2(ad)
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
                propertiesQuery.get().addOnSuccessListener { snapShot ->
                    snapShot.documents.mapNotNull { docShot ->
                        docShot.toObject(FirebaseProperty::class.java)
                    }.also { properties ->
                        lifecycleScope.launch {
                            roarItemsAdapter.addLodgeAndProperty(lodges, properties)
                            hideProgress()
                        }
                    }
                }
            }
        }
    }

    private fun showProgress() {
        progressBar.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        lifecycleScope.launch {
            progressBar.visibility = View.GONE
        }
    }

}