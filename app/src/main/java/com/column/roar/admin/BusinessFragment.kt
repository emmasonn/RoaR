package com.column.roar.admin

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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.column.roar.MainActivity
import com.column.roar.R
import com.column.roar.cloudModel.FirebaseUser
import com.column.roar.listAdapters.ClientListAdapter
import com.google.android.material.card.MaterialCardView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import timber.log.Timber

class BusinessFragment : Fragment() {

    private lateinit var fireStore: FirebaseFirestore
    private lateinit var businessRef: Query
    private lateinit var sharedPref: SharedPreferences
    private lateinit var clientListAdapter: ClientListAdapter
    private lateinit var swipeRefreshContainer: SwipeRefreshLayout
    private lateinit var emptyLayout: MaterialCardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPref = (activity as MainActivity).sharedPref
        fireStore = FirebaseFirestore.getInstance()
        val accountType = sharedPref.getString("accountType","")
        setUpQuery(accountType)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_business, container, false)
        val clientsRecycler = view.findViewById<RecyclerView>(R.id.clientsRecycler)
        val backBtn = view.findViewById<ImageView>(R.id.businessBack)
        swipeRefreshContainer = view.findViewById(R.id.swipeContainer)
        swipeRefreshContainer.isRefreshing = true
        emptyLayout = view.findViewById(R.id.emptyListView)

        backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        clientListAdapter = ClientListAdapter(ClientListAdapter.UserClickListener {
            val bundle = bundleOf("client" to it)
            val action = R.id.action_businessFragment_to_adminManageMarketers
            findNavController().navigate(action, bundle)
        })
        clientsRecycler.adapter  = clientListAdapter

        swipeRefreshContainer.setOnRefreshListener {
            fetchBusiness()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchBusiness()
    }

    private fun fetchBusiness () {
        businessRef.get().addOnSuccessListener { value ->
            value.documents.mapNotNull {
                it.toObject(FirebaseUser::class.java)
            }.also {
                clientListAdapter.submitList(it)
                emptyLayout.visibility = View.GONE
                swipeRefreshContainer.isRefreshing = false
                if(it.isNullOrEmpty()) {
                    emptyLayout.visibility = View.VISIBLE
                }
            }
        }.addOnFailureListener {
            emptyLayout.visibility = View.VISIBLE
        }
    }

    private fun setUpQuery(accountType: String?){
        when(accountType) {
            "Admin" -> {
                businessRef = fireStore.collection("clients")
                    .whereNotEqualTo("account","Realtor")
            }
        }
    }
}