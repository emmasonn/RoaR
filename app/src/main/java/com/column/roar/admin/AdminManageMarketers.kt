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
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.column.roar.R
import com.column.roar.cloudModel.FirebaseProperty
import com.column.roar.cloudModel.FirebaseUser
import com.column.roar.listAdapters.ManagePropertyListAdapter
import com.column.roar.listAdapters.storeAdapter.PropertyListAdapter.PropertyClickListener
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.launch


class AdminManageMarketers : Fragment() {

    private lateinit var toolBar: MaterialToolbar
    private lateinit var managePropertyAdapter: ManagePropertyListAdapter
    private lateinit var fireStore: FirebaseFirestore
    private lateinit var productsRef: Query
    private lateinit var clientRef: DocumentReference
    private lateinit var swipeRefreshContainer: SwipeRefreshLayout
    private lateinit var emptyLayout: MaterialCardView

    private val client: FirebaseUser by lazy {
        arguments?.get("client") as FirebaseUser
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fireStore = FirebaseFirestore.getInstance()
        clientRef = fireStore.collection("clients").document(client.clientId!!)

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
        val productTitle = view.findViewById<TextView>(R.id.titleText)
        toolBar = view.findViewById(R.id.toolBar)
        val backBtn = view.findViewById<ImageView>(R.id.businessBack)
        swipeRefreshContainer = view.findViewById(R.id.swipeContainer)
        emptyLayout = view.findViewById(R.id.emptyListView)
        swipeRefreshContainer.isRefreshing = true

        productTitle.text = client.brand

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
            }, {}
        ))
        manageProductsRecycler.adapter = managePropertyAdapter

        swipeRefreshContainer.setOnRefreshListener {
            fetchProducts()
        }

        toolBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.addProduct -> {
                    val bundle = bundleOf("stringId" to client.clientId)
                    val action = R.id.uploadProperty
                    findNavController().navigate(action,bundle)
                    true
                }
                R.id.changePassword -> {
                    editPasswordDialog(client)
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


    fun suspendAccount() {
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
                emptyLayout.visibility = View.GONE
                swipeRefreshContainer.isRefreshing = false
                if(properties.isNullOrEmpty()) {
                    emptyLayout.visibility = View.VISIBLE
                }
            }
        }.addOnFailureListener {
            emptyLayout.visibility = View.VISIBLE
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