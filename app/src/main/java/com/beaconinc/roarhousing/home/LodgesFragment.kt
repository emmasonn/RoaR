package com.beaconinc.roarhousing.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.beaconinc.roarhousing.R
import com.beaconinc.roarhousing.cloudModel.FirebaseLodge
import com.beaconinc.roarhousing.listAdapters.LodgeClickListener
import com.beaconinc.roarhousing.listAdapters.LodgesAdapter
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class LodgesFragment : Fragment() {

    private lateinit var fireStore: FirebaseFirestore
    private lateinit var lodges: CollectionReference
    private lateinit var lodgesAdapter: LodgesAdapter
    lateinit var homeFragment: HomeFragment
    private lateinit var location: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            location = it.getString("Lodge_Location") as String
        }
        fireStore = FirebaseFirestore.getInstance()
        lodges = fireStore.collection("lodges")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_lodges, container, false)
        val lodgesRecycler = view.findViewById<RecyclerView>(R.id.lodgeList)

        lodgesAdapter = LodgesAdapter(LodgeClickListener { data ->
            val bundle = bundleOf("Lodge" to data )
            findNavController().navigate(R.id.lodgeDetail,bundle)
        })
        lodgesRecycler.adapter = lodgesAdapter
        return view
    }

    override fun onStart() {
        super.onStart()

        lodges.get().addOnSuccessListener { result ->
            result.documents.mapNotNull { snapLodge ->
                snapLodge.toObject(FirebaseLodge::class.java)
            }.also {
                lodgesAdapter.submitList(it)
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(_homeFragment: HomeFragment, param: String) =
            LodgesFragment().apply {
                homeFragment = _homeFragment
                arguments = Bundle().apply {
                    putString("Lodge_Location",param)
                }
            }
    }
}