package com.beaconinc.roarhousing.dashBoard

import android.app.NotificationManager
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.beaconinc.roarhousing.MainActivity
import com.beaconinc.roarhousing.R
import com.beaconinc.roarhousing.cloudModel.FirebaseNotifier
import com.beaconinc.roarhousing.cloudModel.FirebaseUser
import com.beaconinc.roarhousing.listAdapters.NotifyListAdapter
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import timber.log.Timber


class NotifyFragment : Fragment() {

    private lateinit var clientDocument: DocumentReference
    private lateinit var fireStore: FirebaseFirestore
    private lateinit var notifyRecycler: RecyclerView
    private lateinit var notifyListAdapter: NotifyListAdapter
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fireStore = FirebaseFirestore.getInstance()
        sharedPref = (activity as MainActivity).sharedPref
        val clientId = sharedPref.getString("user_id", "")

        Timber.i("clientId: $clientId")
        clientDocument = fireStore.collection("clients").document(clientId!!)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_notify, container, false)
        notifyRecycler = view.findViewById(R.id.notifyRecycler)
        val notifyBtn = view.findViewById<ImageView>(R.id.pagerBack)

        notifyListAdapter = NotifyListAdapter(
            NotifyListAdapter.DeleteClickListener(
            {
                deleteCard(it.notifierId)
            },
            {

            }
        ))

        notifyBtn.setOnClickListener { findNavController().navigateUp() }
        notifyRecycler.adapter = notifyListAdapter

        val notificationManager = requireActivity().getSystemService(NotificationManager::class.java)
        notificationManager.cancelAll()

        return view
    }

    override fun onStart() {
        super.onStart()
        val visitCollection = clientDocument.collection("visits")
        visitCollection.get().addOnSuccessListener { snapShots ->
            snapShots.documents.mapNotNull {
                it.toObject(FirebaseNotifier::class.java)
            }.also { visits ->
                clientDocument.update("visitCounter", 0)
                notifyListAdapter.submitList(visits)
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

}