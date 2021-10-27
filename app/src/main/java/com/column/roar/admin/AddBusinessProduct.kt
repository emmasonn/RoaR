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
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import androidx.media.MediaBrowserServiceCompat.*
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.column.roar.R
import com.column.roar.cloudModel.FirebaseLodgePhoto
import com.column.roar.cloudModel.FirebasePhotoAd
import com.column.roar.listAdapters.ClickListener
import com.column.roar.listAdapters.UploadPhotosAdapter
import com.column.roar.notification.Constant
import com.column.roar.util.MB
import com.column.roar.util.MB_THRESHOLD
import com.column.roar.util.Memory_Access_code
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.util.*

class AddBusinessProduct : Fragment() {
    private lateinit var dialog: AlertDialog
    private lateinit var storage: FirebaseStorage
    private lateinit var fireStore: FirebaseFirestore
    private lateinit var productCollection: CollectionReference
    private lateinit var otherProductCollection: CollectionReference
    private lateinit var swipeRefreshContainer: SwipeRefreshLayout
    private lateinit var othersPhotoAdapter: UploadPhotosAdapter
    private lateinit var noItemList : MaterialCardView

    private val brand: FirebaseLodgePhoto by lazy {
        arguments?.get("property") as FirebaseLodgePhoto
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        storage = FirebaseStorage.getInstance()
        fireStore = FirebaseFirestore.getInstance()
        productCollection = fireStore.collection("properties")

        otherProductCollection = fireStore.collection("properties")
            .document(brand.id!!).collection("others")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_business_product, container, false)
        val uploadVideo = view.findViewById<TextView>(R.id.uploadBtn)
        val uploadImage = view.findViewById<TextView>(R.id.uploadImage)
        swipeRefreshContainer = view.findViewById(R.id.swipeContainer)
        val otherRecyclerView = view.findViewById<RecyclerView>(R.id.othersRecycler)
        val businessBack = view.findViewById<ImageView>(R.id.businessBack)
        val titleText = view.findViewById<TextView>(R.id.titleTxt)
        val videoCover = view.findViewById<ImageView>(R.id.videoCoverImage)
        val videoHeader = view.findViewById<ConstraintLayout>(R.id.videoHeader)
        val imageBtn = view.findViewById<ImageView>(R.id.playVideoBtn)
        noItemList = view.findViewById(R.id.emptyListView)

        titleText.text = brand.title
        brand.video?.let {
            Glide.with(videoCover.context)
                .load(brand.image).apply(
                    RequestOptions().placeholder(R.drawable.animated_gradient)
                        .error(R.drawable.animated_gradient)
                ).into(videoCover)
        }

        if(brand.video == "none") {
            videoHeader.visibility = View.GONE
        }else if(brand.video == null) {
            imageBtn.visibility = View.GONE
        }

        imageBtn.setOnClickListener {

        }

        othersPhotoAdapter = UploadPhotosAdapter(ClickListener ({
            showDeleteDialog(it.id!!)
        }))

        businessBack.setOnClickListener {
            findNavController().popBackStack()
        }

        otherRecyclerView.adapter = othersPhotoAdapter
        fetchProducts()
        swipeRefreshContainer.isRefreshing = true

        uploadVideo.setOnClickListener {
            chooseVideo()
        }

        uploadImage.setOnClickListener {
            openStorageIntent()
        }

        swipeRefreshContainer.setOnRefreshListener {
            fetchProducts()
        }
        return view
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

    private fun chooseVideo() {
        val intent = Intent().apply {
            type = "video/*"
            action = Intent.ACTION_GET_CONTENT
        }
        startActivityForResult(intent, Constant.PICK_VIDEO)
    }

    @SuppressLint("RestrictedApi")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Memory_Access_code && resultCode == Activity.RESULT_OK) {
            val fullPhotoUri: Uri? = data!!.data
            val bitmap: Bitmap =
                MediaStore.Images.Media.getBitmap(requireContext().contentResolver, fullPhotoUri)
            lifecycleScope.launchWhenCreated {
                swipeRefreshContainer.isRefreshing = true
                processProductImage(bitmap)
            }
        } else if (requestCode == Constant.PICK_VIDEO || resultCode == RESULT_OK ||
            data != null || data?.data != null) {
            val videoUri = data?.data
            lifecycleScope.launchWhenCreated {
                videoUri?.let {
                    swipeRefreshContainer.isRefreshing = true
                    initializeVideoUpload(videoUri)
                }
            }
        }
    }

    private fun getExt(uri: Uri): String? {
        val contentResolver = requireContext().contentResolver
        val mimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri))
    }

    private fun initializeVideoUpload(videoUri: Uri) {
        loadVideoToFirebase()
        val brandDocument = productCollection.document(brand.id!!)

        val storageRef: StorageReference =
            storage.reference.child(
                "videos/${brand.id!!}.${getExt(videoUri)}"
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
                val videoUrl:String = task.result.toString()

                brandDocument.update("videoUrl", videoUrl)
                    .addOnSuccessListener {
                        lifecycleScope.launch {
                            Toast.makeText(requireContext(),
                                "Done uploading Video", Toast.LENGTH_SHORT).show()
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

    private suspend fun processProductImage(imageBitmap: Bitmap) {
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
        val itemId = productCollection.document().id

        val storageRef: StorageReference =
            storage.reference.child("images/${itemId}.jpg")

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
                    val photoAd = FirebasePhotoAd (id = itemId, image = imageUri)
                    otherProductCollection.document().set(photoAd)
                        .addOnSuccessListener {
                            swipeRefreshContainer.isRefreshing = true
                            Toast.makeText(requireContext(),"Upload was Successfully",
                                Toast.LENGTH_SHORT).show()
                            lifecycleScope.launch {
                                swipeRefreshContainer.isRefreshing = false
                            }
                        }.addOnFailureListener {
                            swipeRefreshContainer.isRefreshing = false
                            Toast.makeText(requireContext(),"Failed to Upload",Toast.LENGTH_SHORT)
                                .show()
                        }
                }
            }//end complete listener
        }
    }

    private fun fetchProducts() {
        otherProductCollection.get().addOnSuccessListener { snapShot ->
            snapShot.documents.mapNotNull {
                it.toObject(FirebasePhotoAd::class.java)
            }.also { otherItems ->
                otherItems.map {
                    FirebaseLodgePhoto(
                        id = it.id,
                        image = it.image,
                        video = it.video
                    )
                }.let { result ->
                    lifecycleScope.launchWhenCreated {
                        if(result.isEmpty()) {
                            noItemList.visibility = View.VISIBLE
                            swipeRefreshContainer.isRefreshing = false
                        }else {
                            othersPhotoAdapter.submitList(result)
                            swipeRefreshContainer.isRefreshing = false
                            noItemList.visibility = View.GONE
                        }
                    }
                }
            }
        }.addOnFailureListener { e ->
            swipeRefreshContainer.isRefreshing = false
            noItemList.visibility = View.VISIBLE
            Timber.e(e,"Unable to fetch data")
            Toast.makeText(requireContext(),"Cannot fetch data",Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteCard(id: String) {
        val document = otherProductCollection.document(id)
        document.delete().addOnSuccessListener {
            Toast.makeText(requireContext(),"Deleted Successfully", Toast.LENGTH_SHORT).show()
            lifecycleScope.launch {
                swipeRefreshContainer.isRefreshing = true
                fetchProducts()
            }
        }.addOnFailureListener {
            Toast.makeText(requireContext(),"Failed Successfully",Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("InflateParams")
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

    private fun showDeleteDialog(stringId: String) {
        MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle("Do you want to delete photo")
            setPositiveButton("Yes") { dialog, _ ->
                dialog.dismiss()
                deleteCard(stringId)
            }
            setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            show()
        }
    }

}