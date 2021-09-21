package com.beaconinc.roarhousing.dashBoard.lodge

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
import android.widget.ProgressBar
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.beaconinc.roarhousing.MainActivity
import com.beaconinc.roarhousing.R
import com.beaconinc.roarhousing.cloudModel.FirebaseLodge
import com.beaconinc.roarhousing.cloudModel.FirebaseLodgePhoto
import com.beaconinc.roarhousing.cloudModel.FirebaseUser
import com.beaconinc.roarhousing.databinding.ItemLodgeDetailBinding
import com.beaconinc.roarhousing.listAdapters.ClickListener
import com.beaconinc.roarhousing.listAdapters.SelectPhotoAdapter
import com.beaconinc.roarhousing.util.MB
import com.beaconinc.roarhousing.util.MB_THRESHOLD
import com.beaconinc.roarhousing.util.Memory_Access_code
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


class ManageLodgeDetail : Fragment() {

    private var nameOfLodge: String? = null
    private val lodgeData: FirebaseLodge by lazy {
        arguments?.get("Lodge") as FirebaseLodge
    }
    private lateinit var binding: ItemLodgeDetailBinding
    private lateinit var clientDocument: DocumentReference
    private lateinit var firebase: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var lodgeCollection: CollectionReference
    private lateinit var selectPhotoAdapter: SelectPhotoAdapter
    private lateinit var lodgePhotos: CollectionReference
    private lateinit var lodgeDocument: DocumentReference
    private lateinit var storage: FirebaseStorage
    private lateinit var registration: ListenerRegistration
    private lateinit var sharedPref: SharedPreferences
    private var imageId:String? = null
    private var imageTitle: String? = null
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebase = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        sharedPref = (activity as MainActivity).sharedPref

        val clientId = sharedPref.getString("user_id","")
        lodgeCollection = firebase.collection("lodges")
        clientDocument = firebase.collection("clients").document(clientId!!)
        lodgeDocument = lodgeCollection.document(lodgeData.lodgeId!!)
        lodgePhotos = lodgeDocument.collection("lodgePhotos")

    }

    //Invalidate cache to make data binding available
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = ItemLodgeDetailBinding.inflate(inflater,container,false)
        binding.lifecycleOwner = this
        progressBar = binding.progressBar

        selectPhotoAdapter = SelectPhotoAdapter(ClickListener(listener = { photoData ->
            imageTitle = photoData.photoTitle
            imageId = photoData.photoId
            openStorageIntent()
        }))

        binding.updateBtn.setOnClickListener {
            showProgressBar()
            submitDetails()
        }

        binding.managePhotoRecycler.adapter = selectPhotoAdapter

        val campusAdapter = ArrayAdapter.createFromResource (
            requireContext(),
            R.array.campus_array,
            android.R.layout.simple_spinner_dropdown_item
        )

        val addressAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.address_array,
            android.R.layout.simple_spinner_dropdown_item
        )

        val surroundingAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.surrounding,
            android.R.layout.simple_spinner_dropdown_item
        )

        val distanceAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.distance_level,
            android.R.layout.simple_spinner_dropdown_item
        )

        val typeAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.lodge_type,
            android.R.layout.simple_spinner_dropdown_item
        )

        val sizeAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.lodge_size,
            android.R.layout.simple_spinner_dropdown_item
        )

        val qualityAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.quality_level,
            android.R.layout.simple_spinner_dropdown_item
        )

        (binding.campusSpinner.editText as AutoCompleteTextView).setAdapter(campusAdapter)
        (binding.size.editText as AutoCompleteTextView).setAdapter(sizeAdapter)
        (binding.type.editText as AutoCompleteTextView).setAdapter(typeAdapter)
        (binding.addressSpinner.editText as AutoCompleteTextView).setAdapter(addressAdapter)
        (binding.wSpinner.editText as AutoCompleteTextView).setAdapter(qualityAdapter)
        (binding.lightSpinner.editText as AutoCompleteTextView).setAdapter(qualityAdapter)
        (binding.netWorkSpinner.editText as AutoCompleteTextView).setAdapter(qualityAdapter)
        (binding.surroundingSpinner.editText as AutoCompleteTextView).setAdapter(surroundingAdapter)
        (binding.distanceSpinner.editText as AutoCompleteTextView).setAdapter(distanceAdapter)

        return binding.root
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
                processLodgeImage(bitmap)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lodge = lodgeData
    }

    private fun submitDetails() {
        val address = binding.addressSpinner.editText?.text.toString()
        val distance = binding.distanceSpinner.editText?.text.toString()
        val surrounding = binding.surroundingSpinner.editText?.text.toString()
        val light = binding.lightSpinner.editText?.text.toString()
        val network = binding.netWorkSpinner.editText?.text.toString()
        val water = binding.wSpinner.editText?.text.toString()
        val lodgeName = binding.lodgeTitle.text.toString()
        val initialPay = binding.initialPay.text.toString()
        val subPay = binding.subPay.text.toString()
        val description = binding.description.text.toString()
        val type = binding.type.editText?.text.toString()
        val size = binding.size.editText?.text.toString()
        val availableRooms = binding.availableRm.text.toString()
        val campus = binding.campusSpinner.editText?.text.toString()

        nameOfLodge = lodgeName
        val lodge = FirebaseLodge (
            lodgeId = lodgeData.lodgeId!!,
            lodgeName = lodgeName,
            location = address,
            description = description,
            light = light,
            campus = campus,
            type = type,
            size = size,
            availableRoom = availableRooms.toLong(),
            surrounding = surrounding,
            water = water,
            network = network,
            subPayment = subPay,
            initialPayment = initialPay,
            distance = distance
        )

        clientDocument.get().addOnSuccessListener {
            it.toObject(FirebaseUser::class.java).also {
                lodge.apply {
                    agentUrl = it?.clientUrl
                    agentId = it?.clientId
                    agentName = it?.clientName
                    accountType = it?.accountType
                }
            }
            lodgeCollection.document(lodgeData.lodgeId!!).set(lodge)
                .addOnSuccessListener {
                    Toast.makeText(
                        requireContext(),
                        "Upload was successful",
                        Toast.LENGTH_SHORT
                    ).show()
                    lifecycleScope.launch(Dispatchers.Main) {
                        hideProgressBar()
                        binding.invalidateAll()
                        binding.lodge = lodge
                    }
                }.addOnFailureListener { ex ->
                    hideProgressBar()
                    Toast.makeText(
                        requireContext(),
                        "upload Failed: $ex",
                        Toast.LENGTH_SHORT
                    ).show()
                }
         }.addOnFailureListener {
               Toast.makeText(requireContext(),"Failed to Update Details",
                   Toast.LENGTH_SHORT).show()
        }
    }

    override fun onStart() {
        super.onStart()
        fetchLodgePhotos()
    }

    private fun fetchLodgePhotos(){
        registration = lodgePhotos.addSnapshotListener { snapShots, _ ->
            snapShots?.documents?.mapNotNull {
                it.toObject(FirebaseLodgePhoto::class.java)
            }.also { photos ->
                selectPhotoAdapter.submitList(photos)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        registration.remove()
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
        showProgressBar()

        Timber.i("Storing image on Storage")
        val storageRef: StorageReference =
            storage.reference.child(
                "images/${lodgeData.lodgeName}/${imageId}/"
            )

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

                    if(imageTitle == "Cover Image") {
                        lodgeDocument.update("coverImage", imageUri).addOnSuccessListener {
                            Toast.makeText(requireContext(),"Cover Image Uploaded",
                                Toast.LENGTH_SHORT).show()
                        }
                    }

                    lodgePhotos.document(imageId!!).update("photoUrl",imageUri).addOnSuccessListener {
                        hideProgressBar()
                        Toast.makeText(requireContext(),
                            "Upload Success",Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener {
                        hideProgressBar()
                        Toast.makeText(requireContext(),
                            "Upload Failed",Toast.LENGTH_SHORT).show()
                    }
                }
            }//end complete listener
        }
    }

    private fun showProgressBar() {
        lifecycleScope.launch {
            progressBar.visibility = View.VISIBLE
        }
    }

    private fun hideProgressBar() {
        lifecycleScope.launch {
            progressBar.visibility = View.GONE
        }
    }
}