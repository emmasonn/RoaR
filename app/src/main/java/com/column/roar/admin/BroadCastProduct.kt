package com.column.roar.admin

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.column.roar.R
import com.column.roar.cloudModel.FirebaseLodgePhoto
import com.column.roar.cloudModel.FirebaseProperty
import com.column.roar.listAdapters.ManagePropertyListAdapter
import com.column.roar.listAdapters.storeAdapter.PropertyListAdapter.*
import com.column.roar.notification.api.RetrofitInstance
import com.column.roar.notification.data.NotificationData
import com.column.roar.notification.data.PushNotification
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.CollectionReference
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
    private lateinit var editDialog: AlertDialog
    private lateinit var productCollection: CollectionReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fireStore = FirebaseFirestore.getInstance()
        productsRef = fireStore.collection("properties")
            .orderBy("postDate", Query.Direction.DESCENDING)
        productCollection = fireStore.collection("properties")
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
                {}, { product ->
                    editDialog(product)
                },{ products, _  ->
                    navigateToAddOthers(products!!)
                })
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
                managePropertyAdapter.submitList(properties.filter { it.type != "Ads" })
                swipeContainer.isRefreshing = false
            }
        }
    }

    private fun navigateToAddOthers(others: FirebaseProperty) {
        val firebaseLodgePhoto = FirebaseLodgePhoto(
            id = others.id,
            image = others.cover,
            title = others.product,
            video = "none",
        )
        val bundle = bundleOf("property" to firebaseLodgePhoto)
        findNavController().navigate(R.id.addBusinessProduct, bundle)
    }

    //use this function to notify user on any update on lodges
    private fun notifySubscribers(firebaseProperty: FirebaseProperty) {
        val title = getString(R.string.product_notification_channel_name)
        val message = "Check this Product: ${firebaseProperty.product}"

        firebaseProperty.cover?.let {
            val pushNotification = PushNotification(
                NotificationData (
                    firebaseProperty.id!!,
                    title,
                    message,
                    "Product",
                    firebaseProperty.cover
                ),
                "/topics/product"
            )
            sendNotification(pushNotification)
        }
    }

    //function that sends notification
    private fun sendNotification(notification: PushNotification) =
        CoroutineScope(Dispatchers.IO).launch {
            try {
                RetrofitInstance.api.postNotification(notification).also {
                    lifecycleScope.launch {
                        Toast.makeText(requireContext(), "Notification Sent", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "failed to send request")
                lifecycleScope.launch {
                    Toast.makeText(requireContext(), "Cannot send Notification", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

    @SuppressLint("InflateParams")
    private fun editDialog(firebaseProperty: FirebaseProperty): AlertDialog {
        editDialog = MaterialAlertDialogBuilder(requireContext()).apply {
            val inflater = LayoutInflater.from(requireContext())
            val view = inflater.inflate(R.layout.item_edit_product_dialog, null)

            val editBtn = view.findViewById<TextView>(R.id.dialogEditItem)
            val deleteBtn = view.findViewById<TextView>(R.id.dialogDeleteItem)
            val notifyBtn = view.findViewById<TextView>(R.id.notifyBtn)
            val approveBtn = view.findViewById<TextView>(R.id.approveItem)
            val suspendItem = view.findViewById<TextView>(R.id.suspendItem)

            editBtn.setOnClickListener {
                editDialog.dismiss()
                val bundle = bundleOf("property" to firebaseProperty)
                findNavController().navigate(R.id.uploadProperty, bundle)
            }

            notifyBtn.setOnClickListener {
                editDialog.dismiss()
                notifySubscribers(firebaseProperty)
            }

            approveBtn.setOnClickListener {
                editDialog.dismiss()
                approveItem(firebaseProperty.id!!)
            }

            suspendItem.setOnClickListener {
                editDialog.dismiss()
                suspendAccount(firebaseProperty.id!!)
            }

            deleteBtn.setOnClickListener {
                firebaseProperty.id?.let {
                    editDialog.dismiss()
                    deleteCard(it)
                }
            }

            setView(view)
        }.show()
        return editDialog
    }


    private fun approveItem(id: String) {
        productCollection.document(id).update("certified", true)
            .addOnSuccessListener {
                Toast.makeText(
                    requireContext(),
                    "Item Approved",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun suspendAccount(id: String) {
        MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle("You're about to suspend Product")
            setPositiveButton("Continue") { dialog, _ ->
                productCollection.document(id).update("certified", false)
                    .addOnSuccessListener {
                    lifecycleScope.launch {
                        dialog.dismiss()
                    }
                }
            }
        }
    }

    private fun deleteCard(id: String) {
        val document = productCollection.document(id)
        document.delete().addOnSuccessListener {
            Toast.makeText(requireContext(), "Deleted Successfully", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Failed Successfully", Toast.LENGTH_SHORT).show()
        }
    }
}