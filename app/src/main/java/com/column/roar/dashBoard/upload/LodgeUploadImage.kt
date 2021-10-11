package com.column.roar.dashBoard.upload

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
import coil.clear
import com.column.roar.R
import com.column.roar.cloudModel.FirebaseLodgePhoto
import com.column.roar.util.MB
import com.column.roar.util.MB_THRESHOLD
import com.column.roar.util.Memory_Access_code
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
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
    private lateinit var pagerObject: EditLodgePager//composition
    private lateinit var lodgeDocument: DocumentReference
    private lateinit var browseImageBtn: MaterialButton
    private lateinit var saveImageBtn: MaterialButton
    private lateinit var progressBar: ProgressBar

    private var lodgeImage: Bitmap? = null
    private var lodgeView: String? = null
    private lateinit var lodgeType: TextInputLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fireStore = Firebase.firestore
        storage = FirebaseStorage.getInstance()
        lodgeDocument = fireStore.collection("lodges").document(pagerObject.lodgesData.lodgeId!!)
        lodgePhotos = lodgeDocument.collection("lodgePhotos")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_upload_image, container, false)
        selectedImage = view.findViewById<ImageView>(R.id.selectedImage)
        browseImageBtn = view.findViewById<MaterialButton>(R.id.browseBtn)
        saveImageBtn = view.findViewById<MaterialButton>(R.id.saveImage)
        lodgeType = view.findViewById<TextInputLayout>(R.id.viewSpinner)
         progressBar = view.findViewById(R.id.progressBar)
        val viewAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.lodge_view,
            android.R.layout.simple_spinner_dropdown_item
        )
        (lodgeType.editText as AutoCompleteTextView).setAdapter(viewAdapter)


        browseImageBtn.setOnClickListener {
            openStorageIntent()
        }

        saveImageBtn.setOnClickListener {
            lodgeView = lodgeType.editText?.text.toString()

            lifecycleScope.launch(Dispatchers.Main){
                if(lodgeView!!.isNotBlank()) {
                    Timber.i(("view: $lodgeView"))
                    showLoadingBar()
                    processLodgeImage(lodgeImage!!)
                }else {
                    lodgeType.error = "select a view"
                }
            }
        }
        return view
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
                "images/Realtor/${pagerObject.lodgesData.lodgeName!!}/${uid}/"
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
                    }else {

                        val lodgePhoto = FirebaseLodgePhoto(
                            photoId = uid,
                            photoUrl = imageUri,
                            photoTitle = lodgeView
                        )
                        lodgePhotos.document(uid).set(lodgePhoto).addOnSuccessListener {
                            Toast.makeText(requireContext(),
                                "Picture has uploaded",Toast.LENGTH_SHORT).show()
                            hideLoadingBar()
                            pagerObject.moveBackward()
                        }.addOnFailureListener {
                            Toast.makeText(requireContext(),
                                "Upload Failed",Toast.LENGTH_SHORT).show()
                        }
                        selectedImage.clear()

                    }
                }
            }//end complete listener
        }
    }

    private fun showLoadingBar() {
        browseImageBtn.alpha = 0.1f
        saveImageBtn.alpha = 0.1f
        progressBar.visibility = View.VISIBLE
    }

    private fun hideLoadingBar() {
          lifecycleScope.launch {
              browseImageBtn.alpha = 1f
              saveImageBtn.alpha = 1f
              progressBar.visibility = View.GONE
          }
    }

    companion object {
        fun newInstance(_editLodgePager: EditLodgePager) =
            LodgeUploadImage().apply {
                pagerObject = _editLodgePager
            }
    }

}