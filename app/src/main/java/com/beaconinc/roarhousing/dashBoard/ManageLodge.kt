package com.beaconinc.roarhousing.dashBoard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.beaconinc.roarhousing.R
import com.beaconinc.roarhousing.cloudModel.FirebaseLodge
import com.beaconinc.roarhousing.listAdapters.LodgeClickListener
import com.beaconinc.roarhousing.listAdapters.ManageListAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ManageLodge : Fragment() {

    private lateinit var manageListAdapter: ManageListAdapter
    private lateinit var manageRecycler: RecyclerView
    private lateinit var fireStore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var lodgeCollection: Query

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser?.uid
        fireStore = FirebaseFirestore.getInstance()
        lodgeCollection = fireStore.collection("property").whereEqualTo("agentId", uid)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_manage_lodge, container, false)
        manageRecycler = view.findViewById<RecyclerView>(R.id.manageRecycler)
        val floatingBtn = view.findViewById<ExtendedFloatingActionButton>(R.id.myAccountBtn)

        manageListAdapter = ManageListAdapter(LodgeClickListener { lodgeData ->
            val action = R.id.action_manageLodge_to_manageLodgeDetail
            val bundle = bundleOf("Lodge" to lodgeData ) //modify lodge details
            findNavController().navigate(action,bundle)
        })

        manageRecycler.adapter = manageListAdapter

        floatingBtn.setOnClickListener {
            val action = R.id.action_manageLodge_to_lodgeDetailUpload
            findNavController().navigate(action)
        }
        showMessageDialog()
        return view
    }

    private fun showMessageDialog() {
        MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle("Agent Career")
            setCancelable(false)
            setMessage("This section is still under development we will notify you when it is available." +
                    "\n Thank you for using Roar Housing Agency")
            setPositiveButton("Okay") { _, _ ->
                findNavController().navigateUp()
            }
            show()
        }
    }

    override fun onStart() {
        super.onStart()
        lodgeCollection.get().addOnSuccessListener { snapShots ->
            snapShots.documents.mapNotNull {
                it.toObject(FirebaseLodge::class.java)
            }.also {
                manageListAdapter.submitList(it)
            }
        }
    }
}