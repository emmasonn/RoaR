package com.beaconinc.roarhousing.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.beaconinc.roarhousing.R
import com.beaconinc.roarhousing.cloudModel.FirebaseLodge
import com.beaconinc.roarhousing.listAdapters.LodgeClickListener
import com.beaconinc.roarhousing.listAdapters.LodgesAdapter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


class FilterFragment : Fragment() {

    private lateinit var lodgesAdapter: LodgesAdapter
    lateinit var fireStore: FirebaseFirestore
    private lateinit var lodges: Query

    private val filter: String by lazy {
        arguments?.get("choice") as String
    }

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        fireStore = FirebaseFirestore.getInstance()

        if(filter == "classic") {
            lodges = fireStore.collection("lodges")
                .whereGreaterThan("subPayment",100000)
        }else {
            lodges = fireStore.collection("lodges")
                .whereLessThan("subPayment",100000)
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
        title.text = getString(R.string.choice_title, filter)

        lodgesAdapter = LodgesAdapter(LodgeClickListener { data ->
            //val bundle = bundleOf("Lodge" to data )
           // findNavController().navigate(R.id.lodgeDetail,bundle)
        })

        filterRecycler.adapter = lodgesAdapter
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

}