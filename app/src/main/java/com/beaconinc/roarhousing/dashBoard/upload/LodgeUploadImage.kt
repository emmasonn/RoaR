package com.beaconinc.roarhousing.dashBoard.upload

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.beaconinc.roarhousing.home.HomeFragment
import com.beaconinc.roarhousing.R
import com.beaconinc.roarhousing.cloudModel.FirebaseLodgePhoto
import com.beaconinc.roarhousing.listAdapters.ClickListener
import com.beaconinc.roarhousing.listAdapters.UploadPhotosAdapter
import com.beaconinc.roarhousing.util.MB
import com.beaconinc.roarhousing.util.MB_THRESHOLD
import com.beaconinc.roarhousing.util.Memory_Access_code
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.ByteArrayOutputStream

class LodgeUploadImage : Fragment() {

    private lateinit var selectedImage: ImageView
    private lateinit var fireStore: FirebaseFirestore
    private lateinit var lodgePhotos: CollectionReference
    private lateinit var storage: FirebaseStorage
    private lateinit var registration: ListenerRegistration
    private lateinit var homeFragment: HomeFragment //composition
    private lateinit var lodgeDocument: DocumentReference

    private val lodgesId: String by lazy {
        arguments?.get("uid") as String
    }

    private val lodgeName: String by lazy {
        arguments?.get("lodgeName") as String
    }

    private var lodgeImage: Bitmap? = null
    private var lodgeView: String? = null
    private lateinit var lodgeType: TextInputLayout
    private lateinit var uploadPhotosAdapter: UploadPhotosAdapter



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fireStore = Firebase.firestore
        storage = FirebaseStorage.getInstance()
        lodgeDocument = fireStore.collection("lodges").document(lodgesId)
        lodgePhotos = lodgeDocument.collection("lodgePhotos")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_upload_image, container, false)
        selectedImage = view.findViewById<ImageView>(R.id.selectedImage)
        val browseBtn = view.findViewById<MaterialButton>(R.id.browseBtn)
        val uploadBtn = view.findViewById<MaterialButton>(R.id.myAccountBtn)
        val lodgeTitle = view.findViewById<TextView>(R.id.lodgeTitle)
        val uploadRecycler = view.findViewById<RecyclerView>(R.id.uploaded)
        lodgeType = view.findViewById<TextInputLayout>(R.id.viewSpinner)
        val finishBtn = view.findViewById<MaterialButton>(R.id.finishBtn)
        lodgeTitle.text = lodgeName

        val viewAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.lodge_view,
            android.R.layout.simple_spinner_dropdown_item
        )

        (lodgeType.editText as AutoCompleteTextView).setAdapter(viewAdapter)

        finishBtn.setOnClickListener {
            findNavController().popBackStack(R.id.manageLodge,false)
        }

        browseBtn.setOnClickListener {
            openStorageIntent()
        }

        uploadBtn.setOnClickListener {
            lodgeView = lodgeType.editText?.text.toString()

            lifecycleScope.launch(Dispatchers.Main){
                if(lodgeView!!.isNotBlank()) {
                    processLodgeImage(lodgeImage!!)
                }else {
                    lodgeType.error = "select a view"
                }
            }
        }

        uploadPhotosAdapter = UploadPhotosAdapter(ClickListener ({
            lodgePhotos.document(it.photoId!!).delete().addOnCompleteListener {
                Toast.makeText(requireContext(),
                    "Photo has Deleted successfully",Toast.LENGTH_SHORT).show()
            }
           }))

        uploadRecycler.adapter = uploadPhotosAdapter
        return view
    }

    override fun onStart() {
        super.onStart()
        registration = lodgePhotos.addSnapshotListener { snapshot, _ ->
           snapshot?.documents?.mapNotNull {
               it.toObject(FirebaseLodgePhoto::class.java)
           }.also {
               uploadPhotosAdapter.submitList(it)
           }
        }
    }

    override fun onStop() {
        super.onStop()
        registration.remove()
    }

    //Note no request  for Memory permission is defined here
    @SuppressLint("QueryPermissionsNeeded")
    private fun openStorageIntent() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivityForResult(intent, Memory_Access_code)
        }
    }

    //this function  returns the result from the camera
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Memory_Access_code && resultCode == Activity.RESULT_OK) {
            val fullPhotoUri: Uri? = data!!.data
            val bitmap: Bitmap =
                MediaStore.Images.Media.getBitmap(requireContext().contentResolver, fullPhotoUri)
            lodgeImage = bitmap
            selectedImage.setImageBitmap(bitmap)
        }
    }

    private suspend fun processLodgeImage(imageBitmap: Bitmap) {
        Timber.i("Processing Image")
        withContext(Dispatchers.IO) {
            val compressedImage = startCompressing(imageBitmap)
            startUploadingProfileImage(compressedImage)
        }
    }

    private suspend fun startCompressing(bitmap: Bitmap): ByteArray? {
        return withContext(Dispatchers.Default) byte@{
            var bytes: ByteArray? = null

            for (i in 1 until 11) {
                if (i == 10) {
                    Timber.i("Image is too large")
                    Toast.makeText(activity, "Image is Too Large", Toast.LENGTH_LONG)
                        .show()
                    break
                }
                bytes = getBytesFromBitmap(bitmap, 100 / i)
                if (bytes.size / MB < MB_THRESHOLD) {
                    return@byte bytes
                }
            }
            bytes
        }
    }

    //the function reduces the size of the image
    private suspend fun getBytesFromBitmap(bitmap: Bitmap, quality: Int): ByteArray {
        return coroutineScope {
            val stream: ByteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
            stream.toByteArray()
        }
    }

    //this function upload the image on both cloud storage and fireStore
    private fun startUploadingProfileImage(
        imageByte: ByteArray?,
    ) {
        val uid = lodgePhotos.document().id
        Timber.i("Storing image on Storage")
        val storageRef: StorageReference =
            storage.reference.child(
                "images/${lodgeName}/${uid}/"
            )
        //var imageUri: String? = null
        imageByte?.let { imageByteArray ->
            val uploadTask = storageRef.putBytes(imageByteArray)
            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let { e ->
                        Timber.e(e, "Error uploading Image")
                    }
                }
                storageRef.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val imageUri = task.result.toString()
                    val lodgeView = lodgeType.editText?.text.toString()

                    if(lodgeView == "Cover Image") {
                        lodgeDocument.update("coverImage", imageUri).addOnSuccessListener {
                            Toast.makeText(requireContext(),"Cover Image Uploaded",Toast.LENGTH_SHORT).show()
                        }
                    }

                    val lodgePhoto = FirebaseLodgePhoto(
                        photoId = uid,
                        photoUrl = imageUri,
                        photoTitle = lodgeView
                    )
                    lodgePhotos.document(uid).set(lodgePhoto).addOnSuccessListener {
                        Toast.makeText(requireContext(),
                            "Upload Success",Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener {
                        Toast.makeText(requireContext(),
                        "Upload Failed",Toast.LENGTH_SHORT).show()
                    }
                }
            }//end complete listener
        }
    }

    companion object {

        fun newInstance(_homeFragment: HomeFragment) =
            LodgeUploadImage().apply {
                homeFragment = _homeFragment
            }
    }

}