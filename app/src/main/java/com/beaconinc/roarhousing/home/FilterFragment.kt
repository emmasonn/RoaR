package com.beaconinc.roarhousing.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.beaconinc.roarhousing.MainActivity
import com.beaconinc.roarhousing.R
import com.beaconinc.roarhousing.cloudModel.FirebaseLodge
import com.beaconinc.roarhousing.listAdapters.LodgeClickListener
import com.beaconinc.roarhousing.listAdapters.LodgesAdapter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.launch


class FilterFragment : Fragment() {

    lateinit var fireStore: FirebaseFirestore
    private lateinit var lodgesQuery: Query
    private lateinit var progressBar: ProgressBar
    private lateinit var lodgesAdapter: LodgesAdapter
    private lateinit var connectionView: ConstraintLayout
    private lateinit var swipeRefreshContainer: SwipeRefreshLayout


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
        connectionView = view.findViewById(R.id.connectionView)
        progressBar = view.findViewById(R.id.progressBar)
        title.text = getString(R.string.choice_title, filter)

        swipeRefreshContainer = view.findViewById(R.id.swipeContainer)
        swipeRefreshContainer.isRefreshing = true


        lodgesAdapter = LodgesAdapter(LodgeClickListener({
            val bundle = bundleOf("Lodge" to it)
            findNavController().navigate(R.id.lodgeDetail, bundle)
        }, {}), this, false)

        showProgress()
        filterRecycler.adapter = lodgesAdapter
        initializeAd()
        fetchLiveData()

        filterBackBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        swipeRefreshContainer.setOnRefreshListener {
            fetchLiveData()
        }
        return view
    }

    private fun initializeAd() {
        (activity as MainActivity).detailScreenMediumAd.observe(viewLifecycleOwner,{ ad ->
            lodgesAdapter.postAd2(ad)
        })

        (activity as MainActivity).detailScreenSmallAd.observe(viewLifecycleOwner,{ ad ->
            lodgesAdapter.postAd1(ad)
        })
    }

    private fun fetchLiveData() {
        lodgesAdapter.clear()
        lodgesQuery.get().addOnSuccessListener { result ->
            result.documents.mapNotNull { snapLodge ->
                snapLodge.toObject(FirebaseLodge::class.java)
            }.also { lodges ->
                lifecycleScope.launch {
                    if(lodges.isNullOrEmpty()){
                        lodgesAdapter.addLodgeAndProperty(emptyList(),false)
                        hideProgress()
                        swipeRefreshContainer.isRefreshing = false
                    }else {
                        lodgesAdapter.addLodgeAndProperty(lodges,false)
                        hideProgress()
                        swipeRefreshContainer.isRefreshing = false
                    }
                }
            }
        }.addOnFailureListener {
            showInternetError()
            hideProgress()
            swipeRefreshContainer.isRefreshing = false
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

    private fun showInternetError() {
        (activity as MainActivity).connectivityChecker?.apply {
            lifecycle.addObserver(this)
            connectedStatus.observe(viewLifecycleOwner, {
                if (!it) {
                    connectionView.visibility = View.VISIBLE
                }else {
                    lodgesAdapter.addLodgeAndProperty(emptyList(),false)
                }
            })
        }
    }
}