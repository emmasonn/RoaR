package com.beaconinc.roarhousing.admin

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.beaconinc.roarhousing.MainActivity
import com.beaconinc.roarhousing.R
import com.beaconinc.roarhousing.cloudModel.FirebaseUser
import com.beaconinc.roarhousing.listAdapters.ClientListAdapter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class RealtorFragment : Fragment() {

    private lateinit var fireStore: FirebaseFirestore
    private lateinit var realtorsRef: Query
    private lateinit var sharedPref: SharedPreferences
    private lateinit var clientListAdapter: ClientListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPref = (activity as MainActivity).sharedPref
        fireStore = FirebaseFirestore.getInstance()
        val accountType = sharedPref.getString("accountType", "")
        setUpQuery(accountType)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_realtor, container, false)
        val realtorRecycler = view.findViewById<RecyclerView>(R.id.clientsRecycler)
        val backBtn = view.findViewById<ImageView>(R.id.realtorBack)

        backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        clientListAdapter = ClientListAdapter(ClientListAdapter.UserClickListener {
            val bundle = bundleOf("client" to it)
            val action = R.id.action_realtorFragment_to_adminRealtors
            findNavController().navigate(action, bundle)
        })
        realtorRecycler.adapter = clientListAdapter

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchRealtors()
    }

    private fun fetchRealtors() {
        realtorsRef.get().addOnSuccessListener { value ->
            value?.documents?.mapNotNull {
                it.toObject(FirebaseUser::class.java)
            }.also {
                clientListAdapter.submitList(it)
            }
        }
    }

    private fun setUpQuery(accountType: String?) {
        val clientId = sharedPref.getString("user_id", "")

        when (accountType) {
            "Admin" -> {
                realtorsRef = fireStore.collection("clients")
                    .whereEqualTo("accountType", "Realtor")
                    .whereEqualTo("adminId", clientId)
            }
            "Super Admin" -> { //replace to super Admin
                realtorsRef = fireStore.collection("clients").whereEqualTo("accountType", "Realtor")
            }
        }
    }

}