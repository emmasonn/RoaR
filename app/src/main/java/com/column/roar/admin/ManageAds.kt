package com.column.roar.admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.column.roar.R
import com.column.roar.cloudModel.FirebaseLodgePhoto
import com.column.roar.cloudModel.FirebaseProperty
import com.column.roar.listAdapters.ClickListener
import com.column.roar.listAdapters.UploadPhotosAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.launch
import timber.log.Timber

class ManageAds : Fragment() {

    private lateinit var uploadPhotosAdapter: UploadPhotosAdapter
    private lateinit var fireStore: FirebaseFirestore
    private lateinit var productsRef: Query
    private lateinit var productCollection: CollectionReference
    private lateinit var swipeContainer: SwipeRefreshLayout
    private lateinit var lodgeCollection: CollectionReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fireStore = FirebaseFirestore.getInstance()
        productCollection = fireStore.collection("properties")
        productsRef = productCollection.whereEqualTo("type","Ads")
            .orderBy("postDate", Query.Direction.DESCENDING)

        lodgeCollection = fireStore.collection("properties")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_manage_ads, container, false)
        val manageProductsRecycler = view.findViewById<RecyclerView>(R.id.manageProductsRecycler)
        val backBtn = view.findViewById<ImageView>(R.id.businessBack)
        val uploadBtn = view.findViewById<ImageView>(R.id.uploadImage)
        swipeContainer = view.findViewById(R.id.swipeContainer)
        swipeContainer.isRefreshing = true

        backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        uploadBtn.setOnClickListener {
            findNavController().navigate(R.id.uploadAds)
        }

        uploadPhotosAdapter = UploadPhotosAdapter(ClickListener ({
            showDeleteDialog(it.id!!)
        },{
            val bundle = bundleOf("property" to it)
            findNavController().navigate(R.id.addBusinessProduct, bundle)
        }),true)

        manageProductsRecycler.adapter = uploadPhotosAdapter
        swipeContainer.setOnRefreshListener {
            fetchProducts()
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchProducts()
    }

    private fun deleteCard(id: String) {
        val document = lodgeCollection.document(id)
        document.delete().addOnSuccessListener {
            Toast.makeText(requireContext(),"Deleted Successfully",Toast.LENGTH_SHORT).show()
            lifecycleScope.launch {
                swipeContainer.isRefreshing = true
                fetchProducts()
            }
        }.addOnFailureListener {
            Toast.makeText(requireContext(),"Failed to delete",Toast.LENGTH_SHORT).show()
        }
    }

    private fun suspendCard(id: String) {
        val document = lodgeCollection.document(id)
        document.update("certified",false).addOnSuccessListener {
            Toast.makeText(requireContext(),"suspended Successfully",Toast.LENGTH_SHORT).show()
            lifecycleScope.launch {
                swipeContainer.isRefreshing = true
                fetchProducts()
            }
        }.addOnFailureListener {
            Toast.makeText(requireContext(),"Failed to suspend",Toast.LENGTH_SHORT).show()
        }
    }

    private fun approveCard(id: String) {
        val document = lodgeCollection.document(id)
        document.update("certified",true).addOnSuccessListener {
            Toast.makeText(requireContext(),"Approved Successfully",Toast.LENGTH_SHORT).show()
            lifecycleScope.launch {
                swipeContainer.isRefreshing = true
                fetchProducts()
            }
        }.addOnFailureListener {
            Toast.makeText(requireContext(),"Failed to Approve",Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchProducts() {
        productsRef.get().addOnSuccessListener { snapShot ->
            snapShot.documents.mapNotNull {
                it.toObject(FirebaseProperty::class.java)
            }.also { properties ->
                properties.map {
                    FirebaseLodgePhoto(
                        id = it.id,
                        title = it.brand,
                        image = it.cover,
                        video = it.video,
                        certified = it.certified
                    )
                }.let {
                    lifecycleScope.launchWhenCreated {
                        uploadPhotosAdapter.submitList(it)
                        swipeContainer.isRefreshing = false
                    }
                }
            }
        }.addOnFailureListener { e ->
            swipeContainer.isRefreshing = false
            Timber.e(e,"Unable to fetch data")
            Toast.makeText(requireContext(),"Cannot fetch data",Toast.LENGTH_SHORT).show()
        }
    }


    private fun showDeleteDialog(stringId: String) {
        MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle("Choose action to perform")
            setPositiveButton("Delete") { dialog, _ ->
                dialog.dismiss()
                deleteCard(stringId)
            }

            setNeutralButton("Approve") { dialog,_ ->
                dialog.dismiss()
                approveCard(stringId)
            }

            setNegativeButton("Suspend") { dialog, _ ->
                dialog.dismiss()
                suspendCard(stringId)            }
            show()
        }
    }
}