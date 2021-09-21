package com.beaconinc.roarhousing.dashBoard.property

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.beaconinc.roarhousing.MainActivity
import com.beaconinc.roarhousing.R
import com.beaconinc.roarhousing.cloudModel.FirebaseProperty
import com.beaconinc.roarhousing.cloudModel.FirebaseUser
import com.beaconinc.roarhousing.listAdapters.ManagePropertyListAdapter
import com.beaconinc.roarhousing.listAdapters.storeAdapter.PropertyListAdapter.*
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.launch

class ManageProperty : Fragment() {

    private lateinit var managePropertyAdapter: ManagePropertyListAdapter
    private lateinit var manageRecycler: RecyclerView
    private lateinit var fireStore: FirebaseFirestore
    private lateinit var propertyCollection: Query
    private lateinit var sharePref: SharedPreferences
    private lateinit var registration: ListenerRegistration
    lateinit var progressBar: ProgressBar
    lateinit var toolBar: MaterialToolbar

    val client: FirebaseUser by lazy {
        arguments?.get("Client") as FirebaseUser
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fireStore = FirebaseFirestore.getInstance()
        sharePref = (activity as MainActivity).sharedPref
        val clientId = sharePref.getString("user_id", "")
        propertyCollection = fireStore.collection("properties")
            .whereEqualTo("sellerId", clientId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_manage_property, container, false)
        manageRecycler = view.findViewById<RecyclerView>(R.id.manageRecycler)
        toolBar = view.findViewById<MaterialToolbar>(R.id.materialToolbar)
        progressBar = view.findViewById(R.id.progressBar)
        val backKey = view.findViewById<ImageView>(R.id.pagerBack)

        managePropertyAdapter = ManagePropertyListAdapter(PropertyClickListener(
            {
                val action = R.id.action_manageProperty_to_uploadProperty
                val bundle = bundleOf("property" to it) //modify lodge details
                findNavController().navigate(action, bundle)
            }, {
                showDeleteDialog(it)
            })
        )

        backKey.setOnClickListener {
            findNavController().popBackStack()
        }

        manageRecycler.adapter = managePropertyAdapter
        toolBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.uploadProperty -> {
                    val action = R.id.uploadProperty
                    findNavController().navigate(action)
                    true
                }
                else -> false
            }
        }
        return view
    }

    private fun deleteItem(item: FirebaseProperty) {
        showProgressBar()
        fireStore.collection("properties").document(item.id!!)
            .delete().addOnSuccessListener {
                Toast.makeText(
                    requireContext(),
                    "Deleted", Toast.LENGTH_SHORT
                ).show()
                hideProgressBar()
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showProgressBar()
        registerListener()
    }

    override fun onStop() {
        super.onStop()
        registration.remove()
    }

    private fun registerListener() {
        registration = propertyCollection.addSnapshotListener { value, _ ->
            value?.documents?.mapNotNull {
                it.toObject(FirebaseProperty::class.java)
            }.also { products ->
                hideProgressBar()
                managePropertyAdapter.submitList(products)

                products?.let {
                    if (client.slots != null && client.slots!!.toInt() <= products.size) {
                        //use this line of code  to hide the upload button
                        val item = toolBar.menu.findItem(R.id.uploadProperty)
                        item.isVisible = false
                    }
                }
            }
        }
    }

    private fun showDeleteDialog(item: FirebaseProperty) {
        MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle("Are you sure to delete item?")
            setPositiveButton("yes") { dialog, _ ->
                deleteItem(item)
                dialog.dismiss()
            }
            setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }

            show()
        }
    }

    private fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        lifecycleScope.launch {
            progressBar.visibility = View.GONE
        }
    }
}