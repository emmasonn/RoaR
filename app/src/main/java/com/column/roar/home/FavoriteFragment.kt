package com.column.roar.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.column.roar.MainActivity
import com.column.roar.R
import com.column.roar.cloudModel.FirebaseLodge
import com.column.roar.database.FavModel
import com.column.roar.database.FavModelDao
import com.column.roar.listAdapters.LodgeClickListener
import com.column.roar.listAdapters.LodgesAdapter
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class FavoriteFragment : Fragment() {

    lateinit var fireStore: FirebaseFirestore
    private lateinit var lodgesQuery: CollectionReference
    private lateinit var lodgesAdapter: LodgesAdapter
    private lateinit var favModelDao: FavModelDao
    private lateinit var swipeRefreshContainer: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fireStore = FirebaseFirestore.getInstance()
        favModelDao = (activity as MainActivity).db.favModelDao()
        lodgesQuery = fireStore.collection(getString(R.string.firestore_lodges))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_favorite, container, false)
        val favBack = view.findViewById<ImageView>(R.id.favBackBtn)
        val lodgeRecycler = view.findViewById<RecyclerView>(R.id.lodgeList)
        swipeRefreshContainer = view.findViewById(R.id.swipeContainer)
        swipeRefreshContainer.isRefreshing = true

        lodgesAdapter = LodgesAdapter(LodgeClickListener({
            val bundle = bundleOf("Lodge" to it)
            val action = R.id.action_favoriteFragment_to_lodgeDetail
            findNavController().navigate(action, bundle)
        }, { id ->
            lifecycleScope.launch {
                favModelDao.delete(FavModel(id))
                fetchFavId(favModelDao.getFavOnce())
            }
        }), this, true)

        favBack.setOnClickListener {
            findNavController().popBackStack()
        }
        lodgeRecycler.adapter = lodgesAdapter
        initialize()
        swipeRefreshContainer.setOnRefreshListener {
            lifecycleScope.launch {
                fetchFavId(favModelDao.getFavOnce())
            }
        }

        lifecycleScope.launch {
            fetchFavId(favModelDao.getFavOnce())
        }
        return view
    }

    private fun fetchFavId(favorites: List<FavModel>) {
        lodgesAdapter.clear()
        favorites.map { it.id }.also { ids ->
            if (ids.isNotEmpty()) {
                lodgesQuery.whereIn("lodgeId", ids).get().addOnSuccessListener { snapShot ->
                    snapShot.documents.mapNotNull { shot ->
                        shot.toObject(FirebaseLodge::class.java)
                    }.also { lodges ->
                        lifecycleScope.launchWhenCreated {
                            lodgesAdapter.addLodgeAndProperty(lodges, false)
                            swipeRefreshContainer.isRefreshing = false
                        }
                    }
                }
            } else {
                lifecycleScope.launchWhenCreated {
                    lodgesAdapter.clear()
                    lodgesAdapter.addLodgeAndProperty(emptyList(), false)
                    swipeRefreshContainer.isRefreshing = false

                    Toast.makeText(
                        requireContext(),
                        "No Favorite Lodge", Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun initialize() {
        (activity as MainActivity).storeScreenAd.observe(viewLifecycleOwner,{ ad ->
            lodgesAdapter.postAd1(ad)
        })

        (activity as MainActivity).detailScreenMediumAd.observe(viewLifecycleOwner,{ ad ->
            lodgesAdapter.postAd2(ad)
        })
    }
}