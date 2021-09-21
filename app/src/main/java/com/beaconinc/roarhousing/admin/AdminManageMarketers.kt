package com.beaconinc.roarhousing.admin

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.beaconinc.roarhousing.R
import com.beaconinc.roarhousing.cloudModel.FirebaseProperty
import com.beaconinc.roarhousing.cloudModel.FirebaseUser
import com.beaconinc.roarhousing.listAdapters.ManagePropertyListAdapter
import com.beaconinc.roarhousing.listAdapters.storeAdapter.PropertyListAdapter.PropertyClickListener
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


class AdminManageMarketers : Fragment() {

    private lateinit var toolBar: MaterialToolbar
    private lateinit var managePropertyAdapter: ManagePropertyListAdapter
    private lateinit var fireStore: FirebaseFirestore
    private lateinit var productsRef: Query

    private val client: FirebaseUser by lazy {
        arguments?.get("client") as FirebaseUser
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fireStore = FirebaseFirestore.getInstance()
        productsRef = fireStore.collection("properties")
            .whereEqualTo("sellerId",client.clientId)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.admin_manage_marketers, container, false)
        val manageProductsRecycler = view.findViewById<RecyclerView>(R.id.manageProductsRecycler)
        toolBar = view.findViewById(R.id.toolBar)
        val backBtn = view.findViewById<ImageView>(R.id.businessBack)

        backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        managePropertyAdapter = ManagePropertyListAdapter(PropertyClickListener(
            {
              //single click
             val bundle = bundleOf("property" to it)
                findNavController().navigate(R.id.uploadProperty,bundle)
            },
            {
              //long click
              activateAccount(it)
            },
            {
                //just Click
            }
        ))
        manageProductsRecycler.adapter = managePropertyAdapter

        toolBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.changePassword -> {
                    true
                }
                R.id.suspendAccount -> {
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
        fetchProducts()
    }

    private fun fetchProducts() {
        productsRef.get().addOnSuccessListener { snapShot ->
            snapShot.documents.mapNotNull {
                it.toObject(FirebaseProperty::class.java)
            }.also { properties ->
                managePropertyAdapter.submitList(properties)
            }
        }
    }

    @SuppressLint("InflateParams")
    private fun activateAccount(property: FirebaseProperty) {
        val documentReference = fireStore.collection("properties").document(property.id!!)
        MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle("Activate Product ")
            val inflater = LayoutInflater.from(requireContext())
            val view = inflater.inflate(R.layout.activate_spinner_layout, null)
            val switchBtn = view.findViewById<SwitchMaterial>(R.id.switchBtn)
            switchBtn.isChecked = property.certified ?: false
            switchBtn.setOnCheckedChangeListener { _, isChecked ->
                documentReference.update("certified",isChecked)
            }
            setView(view)
            show()
        }
    }

}