package com.beaconinc.roarhousing

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Layout
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.beaconinc.roarhousing.cloudModel.FirebaseLodge
import com.beaconinc.roarhousing.cloudModel.FirebaseProperty
import com.beaconinc.roarhousing.listAdapters.ManagePropertyListAdapter
import com.beaconinc.roarhousing.listAdapters.storeAdapter.PropertyListAdapter
import com.beaconinc.roarhousing.listAdapters.storeAdapter.PropertyListAdapter.*
import com.beaconinc.roarhousing.notification.api.RetrofitInstance
import com.beaconinc.roarhousing.notification.data.NotificationData
import com.beaconinc.roarhousing.notification.data.PushNotification
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber


class BroadCastProduct : Fragment() {

    private lateinit var managePropertyAdapter: ManagePropertyListAdapter
    private lateinit var fireStore: FirebaseFirestore
    private lateinit var productsRef: Query
    private lateinit var swipeContainer: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fireStore = FirebaseFirestore.getInstance()
        productsRef = fireStore.collection("properties")
            .orderBy("postDate",Query.Direction.DESCENDING)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_broadcast_products, container, false)
        val manageProductsRecycler = view.findViewById<RecyclerView>(R.id.manageProductsRecycler)
        val backBtn = view.findViewById<ImageView>(R.id.businessBack)
        swipeContainer = view.findViewById(R.id.swipeContainer)
        swipeContainer.isRefreshing = true

        backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        managePropertyAdapter = ManagePropertyListAdapter(
            PropertyClickListener(
            { product ->
                //single click
             editDialog(product)
            },{ product->
                //long click
                notifySubscribers(product)
            }, {})
        )
        manageProductsRecycler.adapter = managePropertyAdapter

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
                managePropertyAdapter.submitList(properties)
                swipeContainer.isRefreshing = false
            }
        }
    }

    //use this function to notify user on any update on lodges
    private fun notifySubscribers(firebaseProperty: FirebaseProperty) {
        val title = getString(R.string.product_notification_channel_name)
        val message = "Checkout this Product: ${firebaseProperty.propertyTitle}"
        Timber.i("Message: /topics/${firebaseProperty.propertyType}")

        val pushNotification = PushNotification(
            NotificationData(
                firebaseProperty.id!!,
                title,
                message,
                "Product",
                firebaseProperty.firstImage
            ),
            "/topics/${firebaseProperty.propertyType}"
        )
        sendNotification(pushNotification)
    }

    private fun sendNotification(notification: PushNotification)
    = CoroutineScope(Dispatchers.IO).launch {
        try {
            RetrofitInstance.api.postNotification(notification).also {
                lifecycleScope.launch {
                    Timber.i("Successfully Sent")
                }
            }
        }catch (e: Exception) {
            Timber.e(e,"failed to send request")
        }
    }

    @SuppressLint("InflateParams")
    private fun editDialog(firebaseProperty: FirebaseProperty) {
        MaterialAlertDialogBuilder(requireContext()).apply {
            val inflater = LayoutInflater.from(requireContext())
            val view = inflater.inflate(R.layout.item_edit_product_dialog,null)

            val editBtn = view.findViewById<TextView>(R.id.dialogEditItem)
            val deleteBtn = view.findViewById<TextView>(R.id.dialogDeleteItem)

            editBtn.setOnClickListener {
                val bundle = bundleOf("product" to firebaseProperty )
                findNavController().navigate(R.id.uploadProperty, bundle)
            }

            deleteBtn.setOnClickListener {
            }

            setView(view)
            show()
        }
    }

}