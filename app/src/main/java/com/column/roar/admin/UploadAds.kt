package com.column.roar.admin

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
import coil.clear
import com.column.roar.R
import com.column.roar.cloudModel.FirebaseProperty
import com.column.roar.util.MB
import com.column.roar.util.MB_THRESHOLD
import com.column.roar.util.Memory_Access_code
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.CollectionReference
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

class UploadAds : Fragment() {

    private lateinit var selectedImage: ImageView
    private lateinit var fireStore: FirebaseFirestore
    private lateinit var businessPhotos: CollectionReference
    private lateinit var storage: FirebaseStorage
    private lateinit var browseImageBtn: MaterialButton
    private lateinit var saveImageBtn: MaterialButton
    private lateinit var progressBar: ProgressBar
    private lateinit var brandName: TextInputEditText
    private lateinit var phoneNumber: TextInputEditText
    private lateinit var specialOffer: TextInputEditText
    private lateinit var campus: TextInputLayout

    private var lodgeImage: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fireStore = Firebase.firestore
        storage = FirebaseStorage.getInstance()
        businessPhotos = fireStore.collection("properties")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_upload_ads, container, false)
        selectedImage = view.findViewById(R.id.selectedImage)
        browseImageBtn = view.findViewById(R.id.browseBtn)
        saveImageBtn = view.findViewById(R.id.saveImage)
        progressBar = view.findViewById(R.id.progressBar)
        brandName = view.findViewById(R.id.brandTitle)
        phoneNumber = view.findViewById(R.id.phoneNumber)
        campus = view.findViewById(R.id.viewSpinner)
        specialOffer = view.findViewById(R.id.specialTitle)
        val backBtn = view.findViewById<ImageView>(R.id.businessBack)

        val campusAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.campus_array,
            android.R.layout.simple_spinner_dropdown_item
        )
        (campus.editText as AutoCompleteTextView).setAdapter(campusAdapter)

        backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        browseImageBtn.setOnClickListener {
            openStorageIntent()
        }

        saveImageBtn.setOnClickListener {
            val brandNameItem = brandName.text.toString()
            val campusItem = campus.editText?.text.toString()

            lifecycleScope.launch(Dispatchers.Main) {
                showLoadingBar()
                if(brandNameItem.isNotBlank() && campusItem.isNotBlank()) {
                    processLodgeImage(lodgeImage!!)
                }else {
                    brandName.error = "Field must be filled"
                    campus.error = "Please campus"
                    hideLoadingBar()
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
        val uid = businessPhotos.document().id
        val time = System.currentTimeMillis()

        Timber.i("Storing image on Storage")
        val storageRef: StorageReference =
            storage.reference.child(
                "images/BusinessAd/${time}/${uid}/"
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
                    val title = brandName.text.toString()
                    val campus = campus.editText?.text.toString()
                    val offer = specialOffer.text.toString()
                    val phone = phoneNumber.text.toString()

                    val lodgePhoto = FirebaseProperty(
                        id = uid,
                        cover = imageUri,
                        brand = title,
                        campus = campus,
                        number = phone,
                        promo = offer,
                        certified = true,
                        type = "Ads"
                    )
                    businessPhotos.document(uid).set(lodgePhoto).addOnSuccessListener {
                        if(!isDetached){
                            Toast.makeText(
                                requireContext(),
                                "Picture is uploaded successfully", Toast.LENGTH_SHORT
                            ).show()
                            hideLoadingBar()
                            findNavController().popBackStack()
                        }
                    }.addOnFailureListener {
                        if(!isDetached) {
                            Toast.makeText(
                                requireContext(),
                                "Upload Failed", Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    selectedImage.clear()
                }
            }
        }//end complete listener
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
}