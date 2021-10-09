package com.beaconinc.roarhousing.admin

import android.annotation.SuppressLint
import android.content.SharedPreferences
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
import com.beaconinc.roarhousing.MainActivity
import com.beaconinc.roarhousing.R
import com.beaconinc.roarhousing.cloudModel.FirebaseLodge
import com.beaconinc.roarhousing.listAdapters.ManageAdapterListener
import com.beaconinc.roarhousing.listAdapters.ManageListAdapter
import com.beaconinc.roarhousing.notification.api.RetrofitInstance
import com.beaconinc.roarhousing.notification.data.NotificationData
import com.beaconinc.roarhousing.notification.data.PushNotification
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
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
    private lateinit var editDialog: AlertDialog
    private lateinit var lodgeCollection: CollectionReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fireStore = FirebaseFirestore.getInstance()
        sharedPref = (activity as MainActivity).sharedPref
        lodgeCollection = fireStore.collection("lodges")

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
            { editDialog(it) },
            { editRoomDialog(it) }, {}))

        notifyBtn.setOnClickListener { findNavController().popBackStack() }
        notifyRecycler.adapter = notifyListAdapter
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchLatest()
    }

    private fun fetchLatest() {
        lodgeQuery.addSnapshotListener { value, _ ->
            value?.documents?.mapNotNull { snapShot ->
                snapShot.toObject(FirebaseLodge::class.java)
            }.also { data ->
                notifyListAdapter.submitList(data)
            }
        }
    }

    private fun deleteCard(id: String) {
        val document = lodgeCollection.document(id)
        document.delete().addOnSuccessListener {
           Toast.makeText(requireContext(),"Deleted Successfully",Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
           Toast.makeText(requireContext(),"Failed Successfully",Toast.LENGTH_SHORT).show()
        }
    }

    //use this function to notify user on any update on lodges
    private fun notifySubscribers(firebaseLodge: FirebaseLodge) {
        val title = getString(R.string.lodges_notification_channel_name)
        val message = "Check this vacant room at ${firebaseLodge.location}"
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
                    Toast.makeText(requireContext(),"Notification Sent", Toast.LENGTH_SHORT).show()
                }
            }
        }catch (e: Exception) {
            Toast.makeText(requireContext(),"Cannot Send Notification ",Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("InflateParams")
    private fun editDialog(firebaseLodge: FirebaseLodge): AlertDialog {
        editDialog =  MaterialAlertDialogBuilder(requireContext()).apply {
            val inflater = LayoutInflater.from(requireContext())
            val view = inflater.inflate(R.layout.item_edit_product_dialog,null)
            val editBtn = view.findViewById<TextView>(R.id.dialogEditItem)
            val deleteBtn = view.findViewById<TextView>(R.id.dialogDeleteItem)
            val notifyBtn = view.findViewById<TextView>(R.id.notifyBtn)

            editBtn.setOnClickListener {
                editDialog.dismiss()
                val bundle = bundleOf("Lodge" to firebaseLodge )
                findNavController().navigate(R.id.lodgeDetailUpload, bundle)
            }

            notifyBtn.setOnClickListener {
                editDialog.dismiss()
                notifySubscribers(firebaseLodge)
            }

            deleteBtn.setOnClickListener {
                editDialog.dismiss()
                deleteCard(firebaseLodge.lodgeId!!)
            }

            setView(view)
        }.show()

        return editDialog
    }

    @SuppressLint("InflateParams")
    private fun editRoomDialog(lodge: FirebaseLodge) {
        val documentReference = fireStore.collection("lodges").document(lodge.lodgeId!!)
        MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle("Update available Room")
            val inflater = LayoutInflater.from(requireContext())
            val view = inflater.inflate(R.layout.edit_room_dialog, null)
            val roomField = view.findViewById<TextInputEditText>(R.id.roomNumber)
            roomField.hint = "Available Room Number"
            roomField.setText(lodge.availableRoom.toString())
            val updates = hashMapOf<String,Any>("timeStamp" to FieldValue.serverTimestamp())

            setPositiveButton("Submit") { _, _ ->
                val number = roomField.text.toString()
                documentReference.update("availableRoom", number.toLong())
                    .addOnSuccessListener {

                        documentReference.update(updates)
                        Toast.makeText(
                            requireContext(), "Update is Successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    }.addOnFailureListener {
                        Toast.makeText(
                            requireContext(), "Failed to Update",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
            setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            setView(view)
            show()
        }
    }

}