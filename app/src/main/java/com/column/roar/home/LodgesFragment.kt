package com.column.roar.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.column.roar.R
import com.column.roar.databinding.FragmentLodgesBinding
import com.column.roar.listAdapters.LodgeClickListener
import com.column.roar.listAdapters.LodgesAdapter
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class LodgesFragment : Fragment() {

    private lateinit var fireStore: FirebaseFirestore
    private lateinit var lodges: CollectionReference
    private lateinit var lodgesAdapter: LodgesAdapter
    lateinit var homeFragment: HomeFragment
    private lateinit var location: String
    private lateinit var binding: FragmentLodgesBinding

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
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentLodgesBinding.inflate(inflater, container, false)
        val lodgesRecycler = binding.lodgeList

//        lodgesAdapter = LodgesAdapter(LodgeClickListener({ data ->
//            val bundle = bundleOf("Lodge" to data)
//            findNavController().navigate(R.id.lodgeDetail, bundle)
//        },{}),this,true)
//
//        lodgesRecycler.adapter = lodgesAdapter
        return binding.root
    }

    private fun hideProgress() {
        lifecycleScope.launch {
            binding.progressBar.visibility = View.GONE
        }
    }
}