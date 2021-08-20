package com.beaconinc.roarhousing.dashBoard

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore.Images.Media.getBitmap
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import com.beaconinc.roarhousing.MainActivity
import com.beaconinc.roarhousing.R
import com.beaconinc.roarhousing.cloudModel.FirebaseUser
import com.beaconinc.roarhousing.util.MB
import com.beaconinc.roarhousing.util.MB_THRESHOLD
import com.beaconinc.roarhousing.util.Memory_Access_code
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
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


class SetUpFragment : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var clientDocument: DocumentReference
    private lateinit var fireStore: FirebaseFirestore
    private lateinit var clientImageView: ImageView
    private lateinit var fullNameView: TextInputEditText
    private lateinit var storage: FirebaseStorage
    private var profileBitmap: Bitmap? = null
    private lateinit var sharedPref: SharedPreferences


    private val phoneNumber: String? by lazy {
        arguments?.get("phone") as String
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        sharedPref = (activity as MainActivity).sharedPref
        val clientId = firebaseAuth.currentUser?.uid!!
        fireStore = FirebaseFirestore.getInstance()
        clientDocument = fireStore.collection("clients").document(clientId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_set_up, container, false)
        clientImageView = view.findViewById(R.id.clientImage)
        fullNameView = view.findViewById(R.id.fullName)
        val saveBtn = view.findViewById<MaterialButton>(R.id.saveBtn)
        val brownBtn = view.findViewById<ConstraintLayout>(R.id.browseImageBtn)

        saveBtn.setOnClickListener {

            val name = fullNameView.text.toString()

            if (profileBitmap !=null  ) {
                lifecycleScope.launch {
                  processProfileImage(profileBitmap!!)
                }
            }else if(name.isNotBlank()) {
                verifyStateToNull()
                val action = R.id.action_setUpFragment_to_profileFragment
                findNavController().navigate(action)
            }
        }

        brownBtn.setOnClickListener {
             openStorageIntent()
        }

        return view
    }


    override fun onStart() {
        super.onStart()
        fetchUser()
    }

    private fun fetchUser() {
        clientDocument.get().addOnSuccessListener {
            it.toObject(FirebaseUser::class.java).also { client ->
                fullNameView.setText(client?.clientName)
                clientImageView.load(client?.clientUrl)
            }
        }
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
                getBitmap(requireContext().contentResolver, fullPhotoUri)
            profileBitmap = bitmap
            clientImageView.setImageBitmap(bitmap)
        }
    }

    private suspend fun processProfileImage(imageBitmap: Bitmap) {
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

    private fun verifyStateToNull() {
        with(sharedPref.edit()) {
            putString("VERIFY_ID", null)
            apply()
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
        val uid = clientDocument.id
        Timber.i("Storing image on Storage")
        val storageRef: StorageReference =
            storage.reference.child(
                "images/clients/${uid}/"
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
                    val fullName = fullNameView.text.toString()
                    val userId = firebaseAuth.currentUser?.uid

                    val client = FirebaseUser (
                        clientId = userId,
                        clientUrl = imageUri,
                        clientPhone = phoneNumber,
                        clientName = fullName,
                        accountType = "client"
                    )

                    clientDocument.set(client).addOnSuccessListener {
                       lifecycleScope.launch {
                           val action = R.id.action_setUpFragment_to_profileFragment
                           findNavController().navigate(action)
                       }
                    }.addOnFailureListener {
                        Toast.makeText(requireContext(),
                            "Check your Internet Connection: $it", Toast.LENGTH_SHORT).show()
                    }
                }
            }//end complete listener
        }
    }

}