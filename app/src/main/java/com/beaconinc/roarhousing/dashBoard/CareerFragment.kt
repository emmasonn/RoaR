package com.beaconinc.roarhousing.dashBoard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.beaconinc.roarhousing.R
import com.beaconinc.roarhousing.cloudModel.FirebaseLodge
import com.beaconinc.roarhousing.cloudModel.FirebaseNotifier
import com.beaconinc.roarhousing.listAdapters.LodgeClickListener
import com.beaconinc.roarhousing.listAdapters.ManageListAdapter
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class CareerFragment : Fragment() {

    private lateinit var manageListAdapter: ManageListAdapter
    private lateinit var manageRecycler: RecyclerView
    private lateinit var fireStore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var lodgeCollection: Query
    private lateinit var visitCounter: TextView
    private lateinit var lodgesCounter: TextView
    private lateinit var visitDocument: CollectionReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser?.uid
        fireStore = FirebaseFirestore.getInstance()
        lodgeCollection = fireStore.collection("lodges").whereEqualTo("agentId", uid)
        visitDocument = fireStore.collection("clients").document(uid!!)
            .collection("visits")

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_career, container, false)
        val toolBar = view.findViewById<MaterialToolbar>(R.id.toolBar)
        visitCounter = view.findViewById<TextView>(R.id.visitCount)
        lodgesCounter = view.findViewById<TextView>(R.id.lodgesCount)
        manageRecycler = view.findViewById<RecyclerView>(R.id.manageRecycler)
        val backBtn = view.findViewById<ImageView>(R.id.pagerBack)

        manageListAdapter = ManageListAdapter(LodgeClickListener { lodgeData ->
            val action = R.id.action_careerFragment_to_manageLodgeDetail
            val bundle = bundleOf("Lodge" to lodgeData ) //modify lodge details
            findNavController().navigate(action,bundle)
        })
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
                else -> false
            }
        }
        return view
    }

    override fun onStart() {
        super.onStart()
        lodgeCollection.get().addOnSuccessListener { snapShots ->
            snapShots.documents.mapNotNull {
                it.toObject(FirebaseLodge::class.java)
            }.also {
                lodgesCounter.text = it.size.toString()
                manageListAdapter.submitList(it)
            }
        }

        visitDocument.get().addOnSuccessListener { snapShots ->
            snapShots.documents.mapNotNull {
                it.toObject(FirebaseNotifier::class.java)
            }.also {
                visitCounter.text = it.size.toString()
            }
        }
    }
}