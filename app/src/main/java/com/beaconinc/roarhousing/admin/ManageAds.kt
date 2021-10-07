package com.beaconinc.roarhousing.admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.beaconinc.roarhousing.R
import com.beaconinc.roarhousing.cloudModel.FirebaseLodgePhoto
import com.beaconinc.roarhousing.cloudModel.FirebaseProperty
import com.beaconinc.roarhousing.listAdapters.ClickListener
import com.beaconinc.roarhousing.listAdapters.UploadPhotosAdapter
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ManageAds : Fragment() {

    private lateinit var uploadPhotosAdapter: UploadPhotosAdapter
    private lateinit var fireStore: FirebaseFirestore
    private lateinit var productsRef: Query
    private lateinit var productCollection: CollectionReference
    private lateinit var swipeContainer: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fireStore = FirebaseFirestore.getInstance()
        productCollection = fireStore.collection("properties")
        productsRef = productCollection.whereEqualTo("propertyType","Ads")
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
           productCollection.document(it.photoId!!).delete().addOnCompleteListener {
                Toast.makeText(requireContext(),
                    "Image Item has Deleted successfully", Toast.LENGTH_SHORT).show()
            }
        }))

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

    private fun fetchProducts() {
        productsRef.get().addOnSuccessListener { snapShot ->
            snapShot.documents.mapNotNull {
                it.toObject(FirebaseProperty::class.java)
            }.also { properties ->
                val adsItem = properties.map {
                    FirebaseLodgePhoto(
                        photoId = it.id,
                        photoTitle = it.brandName,
                        photoUrl = it.firstImage
                    )
                }
                uploadPhotosAdapter.submitList(adsItem)
                swipeContainer.isRefreshing = false
            }
        }
    }

}