package com.column.roar.dashBoard.lodge

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.column.roar.MainActivity
import com.column.roar.R
import com.column.roar.cloudModel.FirebaseLodge
import com.column.roar.listAdapters.ManageAdapterListener
import com.column.roar.listAdapters.ManageListAdapter
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.*

class CareerFragment : Fragment() {
    private lateinit var manageListAdapter: ManageListAdapter
    private lateinit var manageRecycler: RecyclerView
    private lateinit var fireStore: FirebaseFirestore
    private lateinit var lodgeCollection: Query
    private lateinit var visitDocument: CollectionReference
    private lateinit var registration: ListenerRegistration
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fireStore = FirebaseFirestore.getInstance()
        sharedPref = (activity as MainActivity).sharedPref

        val clientId = sharedPref.getString("user_id", "")
        lodgeCollection = fireStore.collection("lodges").whereEqualTo("agentId", clientId)
        visitDocument = fireStore.collection("clients").document(clientId!!)
            .collection("visits")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_career, container, false)
        val toolBar = view.findViewById<MaterialToolbar>(R.id.toolBar)
        manageRecycler = view.findViewById<RecyclerView>(R.id.manageRecycler)
        val backBtn = view.findViewById<ImageView>(R.id.pagerBack)

        manageListAdapter = ManageListAdapter(
            ManageAdapterListener ({ lodgeData ->
            val action = R.id.action_careerFragment_to_editLodgePager
            val bundle = bundleOf("Lodge" to lodgeData ) //modify lodge details
            findNavController().navigate(action,bundle)
        },{
            editRoomDialog(it)
        },{}))

        manageRecycler.adapter = manageListAdapter
        backBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        toolBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.uploadCareer -> {
                    val action = R.id.action_careerFragment_to_lodgeDetailUpload
                    findNavController().navigate(action)
                    true
                }

                R.id.notifyBtn -> {
                    findNavController().navigate(R.id.notifyFragment)
                    true
                }

                R.id.callAdmin -> {

                    true
                }
                else -> false
            }
        }
        return view
    }

    override fun onStart() {
        super.onStart()
        addListener()
    }

    private fun addListener() {
       registration = lodgeCollection.addSnapshotListener { value, _ ->
            value?.documents?.mapNotNull {
                it.toObject(FirebaseLodge::class.java)
            }.also {
                manageListAdapter.submitList(it)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        registration.remove()
    }

    @SuppressLint("InflateParams")
    private fun editRoomDialog(lodge: FirebaseLodge) {
        val documentReference = fireStore.collection("lodges").document(lodge.lodgeId!!)
        MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle("Update available Room")
            val inflater = LayoutInflater.from(requireContext())
            val view = inflater.inflate(R.layout.edit_room_dialog,null)
            val roomField = view.findViewById<TextInputEditText>(R.id.roomNumber)
            roomField.hint = "Available Room Number"
            roomField.setText(lodge.availableRoom.toString())

            setPositiveButton("Submit") { _, _ ->
                val number = roomField.text.toString()
                documentReference.update("availableRoom", number.toLong())
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
}