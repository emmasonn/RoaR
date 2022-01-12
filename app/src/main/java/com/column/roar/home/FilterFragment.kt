package com.column.roar.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.column.roar.MainActivity
import com.column.roar.R
import com.column.roar.cloudModel.FirebaseLodge
import com.column.roar.database.LodgeDao
import com.column.roar.listAdapters.LodgeClickListener
import com.column.roar.listAdapters.LodgesAdapter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.Source
import kotlinx.coroutines.launch
import timber.log.Timber


class FilterFragment : Fragment() {

    lateinit var fireStore: FirebaseFirestore
    private lateinit var lodgesQuery: Query
    private lateinit var lodgesAdapter: LodgesAdapter
    private lateinit var connectionView: ConstraintLayout
    private lateinit var swipeRefreshContainer: SwipeRefreshLayout
    private  lateinit var lodgeDao: LodgeDao
    private var isAdapterReady = false

    private val filter: String by lazy {
        arguments?.get("choice") as String
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fireStore = FirebaseFirestore.getInstance()
        val settingsPref = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val campus = settingsPref.getString("campus", "UNN")
        lodgeDao = (activity as MainActivity).db.lodgeDao()


        lodgesQuery = if(filter == "simple") {
            fireStore.collection("lodges")
                .whereEqualTo("certified",true)
                .whereEqualTo("campus", campus)
                .whereLessThan("payment",100000)
        } else {
            fireStore.collection("lodges")
                .whereEqualTo("certified",true)
                .whereEqualTo("campus",campus)
                .whereGreaterThan("payment",100000)
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
        title.text = getString(R.string.choice_title, filter)

        swipeRefreshContainer = view.findViewById(R.id.swipeContainer)
        swipeRefreshContainer.isRefreshing = true

        lodgeDao.getAllLodges().observe(viewLifecycleOwner, { lodges ->
            val lodgesId: List<String?> = lodges.map { it.id }

            lodgesAdapter = LodgesAdapter(LodgeClickListener({
                val bundle = bundleOf("Lodge" to it)
                val action = R.id.action_filterFragment_to_lodgeDetail
                findNavController().navigate(action, bundle)
            }, {}), this, false, lodgesId)

            filterRecycler.adapter = lodgesAdapter

            initializeAd()
            fetchLiveData()
            isAdapterReady = true

        })
        //showProgress()

        filterBackBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        swipeRefreshContainer.setOnRefreshListener {
            if(isAdapterReady) fetchLiveData()
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
        val  isDataFetched = (activity as MainActivity).isDataFetched
        val source = if (isDataFetched) Source.CACHE else Source.DEFAULT

        lodgesQuery.get(source).addOnSuccessListener { result->
            result.documents.mapNotNull { snapLodge ->
                snapLodge.toObject(FirebaseLodge::class.java)
            }.also { lodges ->
                lifecycleScope.launchWhenCreated {
                    if(!lodges.isNullOrEmpty()) {
                        lodgesAdapter.addLodgeAndProperty(lodges,false)
                        swipeRefreshContainer.isRefreshing = false
                    }else {
                        lodgesAdapter.addLodgeAndProperty(emptyList(),false)
                        swipeRefreshContainer.isRefreshing = false
                    }
                }
            }
        }.addOnFailureListener {
            lifecycleScope.launchWhenCreated {
                showInternetError()
                swipeRefreshContainer.isRefreshing = false
            }
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