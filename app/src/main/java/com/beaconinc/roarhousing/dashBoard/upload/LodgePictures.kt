package com.beaconinc.roarhousing.dashBoard.upload

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.beaconinc.roarhousing.R
import com.beaconinc.roarhousing.cloudModel.FirebaseLodgePhoto
import com.beaconinc.roarhousing.listAdapters.ClickListener
import com.beaconinc.roarhousing.listAdapters.UploadPhotosAdapter
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import timber.log.Timber


class LodgePictures : Fragment() {

    private lateinit var registration: ListenerRegistration
    private lateinit var fireStore: FirebaseFirestore
    private lateinit var lodgePhotos: CollectionReference
    private lateinit var lodgeDocument: DocumentReference
    private lateinit var uploadPhotosAdapter: UploadPhotosAdapter
    private lateinit var pagerObject: EditLodgePager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fireStore = Firebase.firestore
        lodgeDocument = fireStore.collection("lodges").document(pagerObject.lodgesData.lodgeId!!)
        lodgePhotos = lodgeDocument.collection("lodgePhotos")
        Timber.i("OnCreate")
        registerListener()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view=  inflater.inflate(R.layout.fragment_lodge_pictures, container, false)
        val uploadRecycler = view.findViewById<RecyclerView>(R.id.uploaded)
        val uploadBtn = view.findViewById<MaterialButton>(R.id.uploadBtn)
        uploadPhotosAdapter = UploadPhotosAdapter(ClickListener ({
            lodgePhotos.document(it.photoId!!).delete().addOnCompleteListener {
                Toast.makeText(requireContext(),
                    "Photo has Deleted successfully", Toast.LENGTH_SHORT).show()
            }
        }))
        uploadBtn.setOnClickListener {
            pagerObject.moveForward()
        }
        uploadRecycler.adapter = uploadPhotosAdapter
        return view
    }


   private fun registerListener() {
        registration = lodgePhotos.addSnapshotListener { snapshot, _ ->
            snapshot?.documents?.mapNotNull {
                it.toObject(FirebaseLodgePhoto::class.java)
            }.also {
                uploadPhotosAdapter.submitList(it)
            }
        }
    }

    companion object {
        fun newInstance(_editLodgePager: EditLodgePager) =
            LodgePictures().apply {
                pagerObject = _editLodgePager
            }
    }

}