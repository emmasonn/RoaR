package com.beaconinc.roarhousing.dashBoard

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
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
import com.beaconinc.roarhousing.MainActivity
import com.beaconinc.roarhousing.R
import com.beaconinc.roarhousing.cloudModel.FirebaseLodge
import com.beaconinc.roarhousing.cloudModel.FirebaseProperty
import com.beaconinc.roarhousing.listAdapters.ManageAdapterListener
import com.beaconinc.roarhousing.listAdapters.ManageListAdapter
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


class BroadCastLodge : Fragment() {

    private lateinit var lodgeQuery: Query
    private lateinit var fireStore: FirebaseFirestore
    private lateinit var notifyRecycler: RecyclerView
    private lateinit var notifyListAdapter: ManageListAdapter
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fireStore = FirebaseFirestore.getInstance()
        sharedPref = (activity as MainActivity).sharedPref

        val timeStamp = System.currentTimeMillis()

        lodgeQuery = fireStore.collection("lodges")
            .orderBy("timeStamp", Query.Direction.DESCENDING)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_notify, container, false)
        notifyRecycler = view.findViewById(R.id.notifyRecycler)
        val notifyBtn = view.findViewById<ImageView>(R.id.pagerBack)

        notifyListAdapter = ManageListAdapter(ManageAdapterListener(
            {editDialog(it)},{},{notifySubscribers(it)}))

        notifyBtn.setOnClickListener { findNavController().popBackStack() }
        notifyRecycler.adapter = notifyListAdapter
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchLatest()
    }

    private fun fetchLatest() {
        lodgeQuery.get().addOnSuccessListener {
            it.documents.mapNotNull { snapShot ->
                snapShot.toObject(FirebaseLodge::class.java)
            }.also { data ->
                notifyListAdapter.submitList(data)
            }
        }
    }

    private fun deleteCard(id: String?) {
        MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle("Do you want to delete Notice")
            setPositiveButton("Yes") { dialog, _ ->
                dialog.dismiss()
            }
            setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            show()
        }
    }

    //use this function to notify user on any update on lodges
    private fun notifySubscribers(firebaseLodge: FirebaseLodge) {
        val title = getString(R.string.lodges_notification_channel_name)
        val message = "Checkout this vacant room at ${firebaseLodge.location}"
        Timber.i("Message: /topics/${firebaseLodge.location}")

        val pushNotification = PushNotification(
            NotificationData(
                firebaseLodge.lodgeId!!,
                title,
                message,
                "Lodge",
                firebaseLodge.coverImage
            ),
            "/topics/${firebaseLodge.location}"
        )
        sendNotification(pushNotification)
    }

    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
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
    private fun editDialog(firebaseLodge: FirebaseLodge) {
        MaterialAlertDialogBuilder(requireContext()).apply {
            val inflater = LayoutInflater.from(requireContext())
            val view = inflater.inflate(R.layout.item_edit_product_dialog,null)

            val editBtn = view.findViewById<TextView>(R.id.dialogEditItem)
            val deleteBtn = view.findViewById<TextView>(R.id.dialogDeleteItem)

            editBtn.setOnClickListener {
                val bundle = bundleOf("Lodge" to firebaseLodge )
                findNavController().navigate(R.id.lodgeDetailUpload, bundle)
            }

            deleteBtn.setOnClickListener {
            }

            setView(view)
            show()
        }
    }

}