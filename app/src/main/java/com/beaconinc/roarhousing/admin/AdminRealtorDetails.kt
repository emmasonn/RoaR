package com.beaconinc.roarhousing.admin

import android.annotation.SuppressLint
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import coil.load
import com.beaconinc.roarhousing.R
import com.beaconinc.roarhousing.cloudModel.FirebaseLodge
import com.beaconinc.roarhousing.cloudModel.FirebaseUser
import com.beaconinc.roarhousing.listAdapters.ManageAdapterListener
import com.beaconinc.roarhousing.listAdapters.ManageListAdapter
import com.beaconinc.roarhousing.notification.api.RetrofitInstance
import com.beaconinc.roarhousing.notification.data.NotificationData
import com.beaconinc.roarhousing.notification.data.PushNotification
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class AdminRealtorDetails : Fragment() {


    private val client: FirebaseUser by lazy {
        arguments?.get("client") as FirebaseUser
    }

    private lateinit var fireStore: FirebaseFirestore
    private lateinit var manageListAdapter: ManageListAdapter
    private lateinit var lodgeCollection: Query
    private lateinit var clientRef: DocumentReference
    private lateinit var swipeRefreshContainer: SwipeRefreshLayout
    private lateinit var emptyLayout: MaterialCardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fireStore = FirebaseFirestore.getInstance()
        clientRef = fireStore.collection("clients").document(client.clientId!!)
        lodgeCollection = fireStore.collection("lodges")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_realtors_detail, container, false)
        val brandName = view.findViewById<TextView>(R.id.brandName)
        val lodgeRecycler = view.findViewById<RecyclerView>(R.id.lodgeRecycler)
        val toolbar = view.findViewById<MaterialToolbar>(R.id.materialToolbar)
        val backBtn = view.findViewById<ImageView>(R.id.realtorBack)
        emptyLayout = view.findViewById(R.id.emptyListView)
        brandName.text = client.brandName
        swipeRefreshContainer = view.findViewById(R.id.swipeContainer)

        swipeRefreshContainer.isRefreshing = true

        backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        manageListAdapter = ManageListAdapter(ManageAdapterListener({
                 val bundle = bundleOf("Lodge" to it)
                findNavController().navigate(R.id.editLodgePager, bundle)
        },{
            editRoomDialog(it)
        },{ lodge ->
            activateAccount(lodge)
        }))

        lodgeRecycler.adapter = manageListAdapter

        swipeRefreshContainer.setOnRefreshListener {
            fetchLodges()
        }

        toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.addLodge -> {
                    findNavController().navigate(R.id.lodgeDetailUpload)
                    true
                }

                R.id.changePassword -> {
                    editPasswordDialog(client)
                    true
                }
                R.id.suspendAccount -> {
                    suspendAccount()
                    true
                }
                else -> {
                    true
                }
            }
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
            fetchLodges()
    }

    private fun fetchLodges () {
        lodgeCollection.get().addOnSuccessListener { value ->
            value?.documents?.mapNotNull {
                it.toObject(FirebaseLodge::class.java)
            }.also { lodges ->
                manageListAdapter.submitList(lodges)
                emptyLayout.visibility = View.GONE
                swipeRefreshContainer.isRefreshing = false
                if(lodges.isNullOrEmpty()) {
                    emptyLayout.visibility = View.VISIBLE
                }
            }
        }.addOnFailureListener {
            emptyLayout.visibility = View.VISIBLE
        }
    }

    private fun suspendAccount() {
        MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle("You're about to suspend account")
            setPositiveButton("Continue") { dialog, _ ->
                clientRef.update("certified", false).addOnSuccessListener {
                    lifecycleScope.launch {
                        dialog.dismiss()
                    }
                }
            }
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
    private fun editPasswordDialog(client: FirebaseUser) {
        val documentReference = fireStore.collection("clients").document(client.clientId!!)
        MaterialAlertDialogBuilder(requireContext()).apply {
            val inflater = LayoutInflater.from(requireContext())
            val view = inflater.inflate(R.layout.edit_room_dialog,null)
            val roomField = view.findViewById<TextInputEditText>(R.id.roomNumber)
            roomField.hint = "New password"
            roomField.setText(client.password.toString())

            setPositiveButton("Submit") { _, _ ->
                val number = roomField.text.toString()
                documentReference.update("password", number)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(),"Update is Successfully",
                            Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener {
                        Toast.makeText(requireContext(),"Failed to Update",
                            Toast.LENGTH_SHORT).show()
                    }
            }
            setNegativeButton("Cancel") { dialog,_ ->
                dialog.dismiss()
            }
            setView(view)
            show()
        }
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

            setPositiveButton("Submit") { _, _ ->
                val number = roomField.text.toString()
                documentReference.update("availableRoom", number.toLong())
                    .addOnSuccessListener {
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

    @SuppressLint("InflateParams")
    private fun activateAccount(lodge: FirebaseLodge) {
        val documentReference = fireStore.collection("lodges").document(lodge.lodgeId!!)
        MaterialAlertDialogBuilder(requireContext()).apply {
            val inflater = LayoutInflater.from(requireContext())
            val view = inflater.inflate(R.layout.activate_spinner_layout, null)
            val switchBtn = view.findViewById<SwitchMaterial>(R.id.switchBtn)
            val broadCast = view.findViewById<MaterialButton>(R.id.broadCastBtn)

            broadCast.setOnClickListener {
                notifySubscribers(lodge)
            }

            switchBtn.isChecked = lodge.certified ?: false
            switchBtn.setOnCheckedChangeListener { _, isChecked ->
                    documentReference.update("certified",isChecked)
            }
            setPositiveButton("Save") { dialog,_ ->
                dialog.dismiss()
                fetchLodges()
            }

            setNegativeButton("Close") { dialog, _ ->
                dialog.dismiss()
            }
            setView(view)
            show()
        }
    }



}