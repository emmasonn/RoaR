package com.beaconinc.roarhousing.dashBoard

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
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import com.beaconinc.roarhousing.R
import com.beaconinc.roarhousing.cloudModel.FirebaseUser
import com.beaconinc.roarhousing.util.MB
import com.beaconinc.roarhousing.util.MB_THRESHOLD
import com.beaconinc.roarhousing.util.Memory_Access_code
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.ByteArrayOutputStream

class ManageAccount : Fragment() {

    private lateinit var clientImage: ImageView
    private lateinit var fullName: TextView
    private lateinit var phoneNumber: TextView
    private lateinit var clientDocument: DocumentReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var fireStore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var progressBar: ProgressBar
    private lateinit var clientId: String


    private val client: FirebaseUser? by lazy {
        arguments?.get("Client") as FirebaseUser?
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        clientId = firebaseAuth.currentUser?.uid!!
        fireStore = FirebaseFirestore.getInstance()
        clientDocument = fireStore.collection("clients").document(clientId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_manage_account, container, false)
        clientImage = view.findViewById(R.id.clientImage)
        fullName = view.findViewById(R.id.fullName)
        val changeImage = view.findViewById<ImageView>(R.id.changeIcon)
        val changeNumber = view.findViewById<ConstraintLayout>(R.id.changeNumber)
        val changeName = view.findViewById<ConstraintLayout>(R.id.changeName)
        phoneNumber = view.findViewById<TextView>(R.id.phoneNumber)
        progressBar = view.findViewById(R.id.progressBar)

        //load the current data from bundle
        fullName.text = client?.clientName
        clientImage.load(client?.clientUrl)
        phoneNumber.text = client?.clientPhone

        changeImage.setOnClickListener {
            openStorageIntent()
        }

        changeNumber.setOnClickListener {

        }

        changeName.setOnClickListener {

        }

        return view
    }

    override fun onStart() {
        super.onStart()
        fetchUser()
    }

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
           lifecycleScope.launch {
               processProfileImage(bitmap)
           }
        }
    }

    private suspend fun processProfileImage(imageBitmap: Bitmap) {
        Timber.i("Processing Image")
        progressBar.visibility = View.VISIBLE

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
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
            stream.toByteArray()
        }
    }

    //this function upload the image on both cloud storage and fireStore
    private fun startUploadingProfileImage(
        imageByte: ByteArray?,
    ) {

        val storageRef: StorageReference =
            storage.reference.child(
                "images/${client?.clientName}/profile/${clientId}/"
            )
        //var imageUri: String? = null
        imageByte?.let { imageByteArray ->
            val uploadTask = storageRef.putBytes(imageByteArray)
            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let { e ->
                        Timber.e(e, "Error uploading Image")
                        lifecycleScope.launch {
                            progressBar.visibility = View.GONE
                        }
                    }
                }
                storageRef.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val imageUri = task.result.toString()

                    clientDocument.update("clientUrl", imageUri).addOnSuccessListener {
                        lifecycleScope.launch {
                            clientImage.load(imageUri)
                            progressBar.visibility = View.GONE
                        }
                    }.addOnFailureListener {
                        progressBar.visibility = View.VISIBLE

                        Toast.makeText(requireContext(),
                            "Check your Internet Connection: $it",
                            Toast.LENGTH_SHORT).show()
                    }
                }
            }//end complete listener
        }
    }

    fun changeFullName() {

    }

    private fun fetchUser() {
        clientDocument.get().addOnSuccessListener {
            it.toObject(FirebaseUser::class.java).also { client ->
                fullName.text = client?.clientName
                clientImage.load(client?.clientUrl)
                phoneNumber.text = client?.clientPhone
            }
        }
    }

}