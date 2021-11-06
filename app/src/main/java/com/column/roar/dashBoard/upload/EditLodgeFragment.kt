package com.column.roar.dashBoard.upload

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.media.MediaBrowserServiceCompat.RESULT_OK
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.column.roar.R
import com.column.roar.cloudModel.FirebaseLodge
import com.column.roar.databinding.FragmentEditLodgeBinding
import com.column.roar.notification.Constant.Companion.PICK_VIDEO
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.launch
import timber.log.Timber


class EditLodgeFragment : Fragment() {

    private lateinit var editLodgePager: EditLodgePager
    private lateinit var binding: FragmentEditLodgeBinding
    private lateinit var dialog: AlertDialog
    private lateinit var storage: FirebaseStorage
    private lateinit var fireStore: FirebaseFirestore
    private lateinit var lodgeCollection: CollectionReference
    private lateinit var lodgeFirebaseListener: ListenerRegistration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        storage = FirebaseStorage.getInstance()
        fireStore = FirebaseFirestore.getInstance()
        lodgeCollection = fireStore.collection("lodges")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentEditLodgeBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.lodgeDetail = editLodgePager.lodgesData

        binding.editLodge.setOnClickListener {
            val bundle = bundleOf("Lodge" to editLodgePager.lodgesData)
            findNavController().navigate(R.id.lodgeDetailUpload, bundle)
        }

        binding.playBtn.setOnClickListener {
            val bundle = bundleOf("Lodge" to editLodgePager.lodgesData)
            findNavController().navigate(R.id.watchTour,bundle)
        }

        binding.uploadVideo.setOnClickListener {
            chooseVideo()
        }

        binding.pagerBack.setOnClickListener {
            findNavController().popBackStack(R.id.lodgeDetailUpload,true)
        }
        setupListener()
        return binding.root
    }

    private fun chooseVideo() {
        val intent = Intent().apply {
            type = "video/*"
            action = Intent.ACTION_GET_CONTENT
        }
        startActivityForResult(intent,PICK_VIDEO)
    }

    @SuppressLint("RestrictedApi")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == PICK_VIDEO || resultCode == RESULT_OK ||
                data != null || data?.data != null) {
            val videoUri = data?.data
            videoUri?.let {
                initializeVideoUpload(videoUri)
            }
        }
    }

    private fun setupListener() {
        lodgeFirebaseListener = lodgeCollection.document(editLodgePager.lodgesData.lodgeId!!)
            .addSnapshotListener { value, _ ->
                value?.toObject(FirebaseLodge::class.java).also {
                    Glide.with(binding.coverImage.context)
                        .load(it?.coverImage).apply(
                            RequestOptions().placeholder(R.drawable.animated_gradient)
                                .error(R.drawable.animated_gradient)
                        ).into(binding.coverImage)
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        lodgeFirebaseListener.remove()  //check if onDestroy best for removing listener
    }



    private fun getExt(uri: Uri): String? {
        val contentResolver = requireContext().contentResolver
        val mimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri))
    }

    private fun initializeVideoUpload(videoUri: Uri) {
        loadVideoToFirebase()
       // val uid = lodgeCollection.document().id
        val lodgeDocument = lodgeCollection.document(editLodgePager.lodgesData.lodgeId!!)
        val itemId = editLodgePager.lodgesData.lodgeId!!

        val storageRef: StorageReference =
            storage.reference.child(
                "videos/${itemId}.${getExt(videoUri)}"
            )

        val uploadTask = storageRef.putFile(videoUri)
        uploadTask.continueWithTask { task ->
            if(!task.isSuccessful) {
                task.exception?.let { e ->
                    Timber.e(e,"Error uploading Video file") }
            }
            storageRef.downloadUrl
        }.addOnCompleteListener { task  ->
              if(task.isSuccessful) {
                 val imageUri:String = task.result.toString()

                lodgeDocument.update("tour",imageUri)
                    .addOnSuccessListener {
                        lifecycleScope.launch {
                            Toast.makeText(requireContext(),
                            "Done uploading Video",Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                        }
                    }.addOnFailureListener {
                        lifecycleScope.launch {
                            Toast.makeText(requireContext(),"Failed uploading Video",
                                Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                        }
                    }
            }else {
                Toast.makeText(requireContext(),"Cannot upload video \n " +
                        "check your internet connection",
                    Toast.LENGTH_SHORT).show()
              }
        }
    }

    private fun loadVideoToFirebase() {
        dialog = requireActivity().let {
            AlertDialog.Builder(it).apply {
                setCancelable(false)
                val inflater = LayoutInflater.from(requireContext())
                val view = inflater.inflate(R.layout.item_load_image,null)
                setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                setView(view)
            }.create()
        }
        dialog.show()

    }


    private fun showProgress() {
        lifecycleScope.launch {
            binding.playBtn.visibility = View.VISIBLE
        }
    }

    private fun hideProgress() {
        lifecycleScope.launch {
            binding.playBtn.visibility = View.VISIBLE
        }
    }

    companion object {
        fun newInstance(_editLodgePager: EditLodgePager) =
            EditLodgeFragment().apply {
                editLodgePager = _editLodgePager
            }
    }
}