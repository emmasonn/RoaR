package com.beaconinc.roarhousing.admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
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
import kotlinx.coroutines.launch

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
        productsRef = productCollection.whereEqualTo("propertyType","Ads")
            .orderBy("postDate",Query.Direction.DESCENDING)

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
            deleteCard(it.photoId!!)
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

    private fun deleteCard(id: String) {
        val document = lodgeCollection.document(id)
        document.delete().addOnSuccessListener {
            Toast.makeText(requireContext(),"Deleted Successfully",Toast.LENGTH_SHORT).show()
            lifecycleScope.launch {
                swipeContainer.isRefreshing = true
                fetchProducts()
            }
        }.addOnFailureListener {
            Toast.makeText(requireContext(),"Failed Successfully",Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchProducts() {
        productsRef.get().addOnSuccessListener{ snapShot ->
            snapShot?.documents?.mapNotNull {
                it.toObject(FirebaseProperty::class.java)
            }.also { properties ->
                properties?.map {
                    FirebaseLodgePhoto(
                        photoId = it.id,
                        photoTitle = it.brandName,
                        photoUrl = it.firstImage
                    )
                }.let {
                    if(it!=null){
                        uploadPhotosAdapter.submitList(it)
                        swipeContainer.isRefreshing = false
                }}

            }
        }
    }

}