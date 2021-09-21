package com.beaconinc.roarhousing.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.beaconinc.roarhousing.MainActivity
import com.beaconinc.roarhousing.R
import com.beaconinc.roarhousing.cloudModel.FirebaseLodge
import com.beaconinc.roarhousing.database.FavModel
import com.beaconinc.roarhousing.database.FavModelDao
import com.beaconinc.roarhousing.listAdapters.LodgeClickListener
import com.beaconinc.roarhousing.listAdapters.LodgesAdapter
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import timber.log.Timber

class FavoriteFragment : Fragment() {

    lateinit var fireStore: FirebaseFirestore
    private lateinit var lodgesQuery: CollectionReference
    private lateinit var lodgesAdapter: LodgesAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var favModelDao: FavModelDao


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fireStore = FirebaseFirestore.getInstance()
        favModelDao = (activity as MainActivity).db.favModelDao()
        lodgesQuery = fireStore.collection("lodges")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_favorite, container, false)
        progressBar = view.findViewById(R.id.progressBar)
        val favBack = view.findViewById<ImageView>(R.id.favBackBtn)

        val lodgeRecycler = view.findViewById<RecyclerView>(R.id.lodgeList)

        lodgesAdapter = LodgesAdapter(LodgeClickListener( {
            val bundle = bundleOf("Lodge" to it)
            findNavController().navigate(R.id.lodgeDetail, bundle)
        },{ id ->
            lifecycleScope.launch {
                favModelDao.delete(FavModel(id))
                fetchFavId(favModelDao.getFavOnce())
            }
        }), true)

        favBack.setOnClickListener {
            findNavController().popBackStack()
        }

        lodgeRecycler.adapter = lodgesAdapter
        showProgress()

        lifecycleScope.launch {
            fetchFavId(favModelDao.getFavOnce())
        }
        return view
    }

    private fun fetchFavId(favorites: List<FavModel>) {
        favorites.map{it.id}.also { ids ->
            if (ids.isNotEmpty()) {
                lodgesQuery.whereIn("lodgeId", ids).get().addOnSuccessListener { snapShot ->
                    snapShot.documents.mapNotNull { shot ->
                        shot.toObject(FirebaseLodge::class.java)
                    }.also { lodges ->
                        lodgesAdapter.submitList(lodges)
                        hideProgress()
                    }
                }
                    }else {
                            hideProgress()
                         Toast.makeText(requireContext(),
                             "No Favorite Lodge Empty",Toast.LENGTH_SHORT).show()
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