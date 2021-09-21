package com.beaconinc.roarhousing.home

import android.app.ActionBar
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.core.view.marginTop
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beaconinc.roarhousing.R
import com.beaconinc.roarhousing.cloudModel.FirebaseLodge
import com.beaconinc.roarhousing.databinding.FragmentLodgesBinding
import com.beaconinc.roarhousing.listAdapters.LodgeClickListener
import com.beaconinc.roarhousing.listAdapters.LodgesAdapter
import com.beaconinc.roarhousing.util.ChipClickListener
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.internal.addHeaderLenient
import timber.log.Timber

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

        lodgesAdapter = LodgesAdapter(LodgeClickListener({ data ->
            val bundle = bundleOf("Lodge" to data)
            findNavController().navigate(R.id.lodgeDetail, bundle)
        },{}))

        lodgesRecycler.adapter = lodgesAdapter
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        showProgress()
        lodges.get().addOnSuccessListener { result ->
            result.documents.mapNotNull { snapLodge ->
                snapLodge.toObject(FirebaseLodge::class.java)
            }.also {
                lodgesAdapter.submitList(it)
            }
            hideProgress()
        }
    }

    private fun onChipFilterClicked() {
        Toast.makeText(
            requireContext(), "Cat $location",
            Toast.LENGTH_SHORT
        ).show()
        Timber.i("Area: $location")
    }


    private fun structureListData(data: List<FirebaseLodge>) {
        data.groupBy { it.location!! }.forEach { lodge ->
            populateLayout(lodge.key, lodge.value)
        }
    }

    private fun populateLayout(title: String, lodges: List<FirebaseLodge>) {

        val params1 = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(20, 10, 0, 0)
        }

        val params2 = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(0,10,0,0)
        }

        val listTitle = TextView(requireContext()).apply {
            text = title
            layoutParams
        }

        val recyclerView: RecyclerView = RecyclerView(requireContext()).apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            layoutParams = params2
        }
        recyclerView.adapter = lodgesAdapter
        lodgesAdapter.submitList(lodges)
        binding.roarParentView.addView(listTitle)
        binding.roarParentView.addView(recyclerView)
    }


    companion object {
        @JvmStatic
        fun newInstance(_homeFragment: HomeFragment, param: String) =
            LodgesFragment().apply {
                homeFragment = _homeFragment
                arguments = Bundle().apply {
                    putString("Lodge_Location", param)
                }
            }
    }

    private fun showProgress() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        lifecycleScope.launch {
            binding.progressBar.visibility = View.GONE
        }
    }
}