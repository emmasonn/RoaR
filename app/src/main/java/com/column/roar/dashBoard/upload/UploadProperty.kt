package com.column.roar.dashBoard.upload

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.column.roar.MainActivity
import com.column.roar.R
import com.column.roar.cloudModel.FirebaseProperty
import com.column.roar.cloudModel.FirebaseUser
import com.column.roar.databinding.FragmentUploadPropertyBinding
import com.column.roar.util.MB
import com.column.roar.util.MB_THRESHOLD
import com.column.roar.util.Memory_Access_code
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.util.*

class UploadProperty : Fragment() {
    private lateinit var binding: FragmentUploadPropertyBinding
    private lateinit var clientDocument: DocumentReference
    private lateinit var propertyDocument: DocumentReference
    private lateinit var fireStore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var registration: ListenerRegistration
    private lateinit var propertyCollection: CollectionReference
    private lateinit var firstImage: Bitmap
    private lateinit var propertyId: String
    private lateinit var sharedPref: SharedPreferences

    private val property: FirebaseProperty? by lazy {
        arguments?.get("property") as FirebaseProperty?
    }

    /*this local function allows us to fetch data passed from business accounts during add product*/
    private val stringId: String? by lazy { //used in the admin dashboard
        arguments?.get("stringId") as String?
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fireStore = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        sharedPref = (activity as MainActivity).sharedPref

        val clientId = sharedPref.getString("user_id", null)

        if(stringId != null) {
            clientDocument = fireStore.collection("clients").document(stringId!!)
        }else if(clientId != null) {
            clientDocument = fireStore.collection("clients").document(clientId)
        }

        propertyCollection = fireStore.collection("properties")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentUploadPropertyBinding.inflate(inflater,container,false)
        binding.lifecycleOwner = this

        val campusAdapter = ArrayAdapter.createFromResource (
            requireContext(),
            R.array.campus_array,
            android.R.layout.simple_spinner_dropdown_item
        )

        val productAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.product_type_form,
            android.R.layout.simple_spinner_dropdown_item
        )

        Glide.with(binding.firstImage.context)
            .load(property?.cover)
            .into(binding.firstImage)


        binding.manageBckBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        (binding.campus.editText as AutoCompleteTextView).setAdapter(campusAdapter)
        (binding.productType.editText as AutoCompleteTextView).setAdapter(productAdapter)


        binding.firstImage.setOnClickListener {
            openStorageIntent()
        }

        binding.submitProperty.setOnClickListener {

            showProgressBar()
                    submitDetails()
        }

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (property != null) {
            propertyId = property?.id!!
            propertyDocument = propertyCollection.document(property?.id!!)
        }else {
            propertyId = propertyCollection.document().id
            propertyDocument = propertyCollection.document(propertyId)
        }
        registration = propertyDocument.addSnapshotListener { value, _ ->
            value?.toObject(FirebaseProperty::class.java).also { data ->
                lifecycleScope.launch {
                    binding.lodgeProperty = data
                    binding.invalidateAll()
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        registration.remove()
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
                firstImage = bitmap
                binding.firstImage.setImageBitmap(bitmap)
            }
        }


    private suspend fun processLodgeImage(imageBitmap: Bitmap) {
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
        showProgressBar()

        val storageRef: StorageReference =
            storage.reference.child(
                "images/products/${binding.propertyTitle}/${propertyId}.jpg"
            )

        imageByte?.let { imageByteArray ->
            val uploadTask = storageRef.putBytes(imageByteArray)
            uploadTask.continueWithTask {
                storageRef.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val imageUri = task.result.toString()
                        propertyDocument.update("cover", imageUri)
                            .addOnSuccessListener {
                                hideProgressBar()
                            Toast.makeText(requireContext(),"Upload was Successfully",
                                Toast.LENGTH_SHORT).show()
                                lifecycleScope.launch {
                                    doneUploading()
                                }
                        }.addOnFailureListener {
                             Toast.makeText(requireContext(),"Failed to Upload",Toast.LENGTH_SHORT)
                                 .show()
                            }
                }
            }//end complete listener
        }
    }


    private fun submitDetails() {
        val propertyName = binding.propertyTitle.text.toString()
        val propertyPrice = binding.productPrice.text.toString()
        val propertyDesc = binding.propertyDesc.text.toString()
        val campus = binding.campus.editText?.text.toString()
        val propertyType = binding.productType.editText?.text.toString()
        val ownerContact = binding.ownerContact.text.toString()

        val property = FirebaseProperty (
            id = propertyId,
            cover = property?.cover,
            product = propertyName,
            price = propertyPrice,
            description = propertyDesc,
            campus = campus,
            owner = ownerContact,
            type = propertyType
        )

        clientDocument.get().addOnSuccessListener {
            it.toObject(FirebaseUser::class.java).also {
                property.apply {
                    ownerId = it?.clientId
                    image = it?.clientImage
                    brand = it?.clientName
                    number = it?.clientPhone
                }
            }

            propertyDocument.set(property).addOnSuccessListener {
                if(::firstImage.isInitialized) {
                    startUploadingImage()
                }else if(property.cover!=null){
                    Toast.makeText(requireContext(),
                        "Update was successful",
                    Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }else {
                        Toast.makeText(requireContext(),"Upload product picture",Toast.LENGTH_LONG).show()
                }
            }.addOnFailureListener {
                hideProgressBar()
                Toast.makeText(requireContext(),
                    "Upload Failed",Toast.LENGTH_SHORT).show()
            }

        }.addOnFailureListener {
            hideProgressBar()
            Toast.makeText(requireContext(),
                "Upload Failed \n Check your Internet",Toast.LENGTH_SHORT).show()
        }
    }

    private fun startUploadingImage() {
            lifecycleScope.launchWhenStarted {
                    processLodgeImage(firstImage)
            }
        }

    private fun doneUploading() {
        MaterialAlertDialogBuilder(requireContext()).apply {
              setTitle("Product will be reviewed by the admin")
              setCancelable(false)
              setPositiveButton("Continue") { dialog, _ ->
                  dialog.dismiss()
                  findNavController().navigateUp()
              }
             show()
        }
    }

    private fun showProgressBar() {
        lifecycleScope.launch {
            binding.progressBar.visibility = View.VISIBLE
            binding.submitProperty.alpha = 0.1f
        }
    }

    private fun hideProgressBar() {
        lifecycleScope.launch {
            binding.progressBar.visibility = View.GONE
            binding.submitProperty.alpha = 1f
        }
    }
}