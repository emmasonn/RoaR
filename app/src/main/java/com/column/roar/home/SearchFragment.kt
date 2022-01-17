package com.column.roar.home

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.column.roar.MainActivity
import com.column.roar.R
import com.column.roar.cloudModel.FirebaseLodge
import com.column.roar.database.LodgeDao
import com.column.roar.listAdapters.LodgeClickListener
import com.column.roar.listAdapters.LodgesAdapter
import com.google.android.material.card.MaterialCardView
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.launch
import timber.log.Timber


class SearchFragment : Fragment() {

    private lateinit var searchView: SearchView
    private lateinit var queryTextListener: SearchView.OnQueryTextListener
    private lateinit var fireStore: FirebaseFirestore
    private lateinit var lodgesCollection: Query
    private lateinit var lodgesAdapter: LodgesAdapter
    private lateinit var connectionError: MaterialCardView
    private lateinit var noItemFound: MaterialCardView
    private lateinit var lodgesList: List<FirebaseLodge>
    private lateinit var progressBar: ProgressBar
    private lateinit var lodgeDao: LodgeDao


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fireStore = FirebaseFirestore.getInstance()
        lodgesCollection = fireStore.collection("lodges").orderBy("payment", Query.Direction.ASCENDING)
        lodgeDao = (activity as MainActivity).db.lodgeDao()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.lodgeRecyclerView)
        searchView = view.findViewById<SearchView>(R.id.searchBar)
        connectionError = view.findViewById<MaterialCardView>(R.id.connectionView)
        val backBtn = view.findViewById<ImageView>(R.id.searchBack)

        noItemFound = view.findViewById<MaterialCardView>(R.id.emptyListView)
        progressBar = view.findViewById(R.id.progressBar)

        initConnection()
        setUpQueryListener()
        setUpSearchView()

        backBtn.setOnClickListener {
            hideKeyBoard(requireView())
            findNavController().popBackStack()
        }

        lodgeDao.getAllLodges().observe(viewLifecycleOwner, { lodges ->
            val lodgesId: List<String?> = lodges.map { it.id }

            lodgesAdapter = LodgesAdapter(LodgeClickListener({
                val bundle = bundleOf("Lodge" to it)
                val action = R.id.action_searchFragment_to_lodgeDetail
                findNavController().navigate(action, bundle)
            }, {}), this, false, lodgesId)

            recyclerView.adapter = lodgesAdapter
            fetchLodges()
            initializeAd()

        })

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

   private fun fetchLodges() {
         showProgressBar()
        lodgesCollection.get().addOnSuccessListener { task ->
                task.documents.mapNotNull {
                    it.toObject(FirebaseLodge::class.java)
                }.also { lodges ->
                    lifecycleScope.launchWhenCreated {
                        hideConnectionError()
                        searchView.alpha = 1F
                        hideProgressBar()
                        lodgesList = lodges
                    }
                }
            }.addOnFailureListener {
               lifecycleScope.launchWhenCreated {
                   hideProgressBar()
                   showConnectionError()
               }
           }
    }

    private fun setUpSearchView() {
        searchView.apply {
            //assumes that the current activity is the searchable activity
            val searchManager =
                requireActivity().getSystemService(Context.SEARCH_SERVICE) as SearchManager
            setSearchableInfo(searchManager.getSearchableInfo(requireActivity().componentName))
            setIconifiedByDefault(false)
            isSubmitButtonEnabled = true
            setOnQueryTextListener(queryTextListener)
        }
    }

    private fun setUpQueryListener() {
        queryTextListener = object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                hideKeyBoard(requireView())
                if(lodgesAdapter.currentList.isEmpty()) {
                    showEmptyList()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                hideEmptyList()
                if(newText.isNullOrBlank()) {
                    lodgesAdapter.submitList(emptyList())
                }else {
                    showProgressBar()
                    lodgesList.filter { newText.toInt() <= it.payment!! }.also { result ->
                        lodgesAdapter.addLodgeAndProperty(result, false).also {
                            hideProgressBar()
                        }
                    }
                }
                return true
            }
        }
    }

    private fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        lifecycleScope.launch {
            progressBar.visibility = View.GONE
        }
    }


    private fun initConnection() {
        (activity as MainActivity).connectivityChecker?.apply {
            lifecycle.addObserver(this)
            connectedStatus.observe(viewLifecycleOwner, {
                if ( it && ::lodgesList.isInitialized && lodgesList.isEmpty()) {
                    fetchLodges()
                }
            })
        }
    }

    private fun hideConnectionError() {
        lifecycleScope.launch {
            connectionError.visibility = View.GONE
            searchView.alpha = 1F
        }
    }

    private fun showConnectionError() {
        lifecycleScope.launch {
            connectionError.visibility = View.VISIBLE
            searchView.alpha = 0.3F
        }
    }

    fun hideKeyBoard(view: View) {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }


    private fun showEmptyList() {
        noItemFound.visibility = View.VISIBLE
    }

    private fun hideEmptyList() {
        lifecycleScope.launch {
            noItemFound.visibility = View.GONE
        }
    }
}