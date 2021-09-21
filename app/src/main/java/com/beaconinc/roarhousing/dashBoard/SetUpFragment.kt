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
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.beaconinc.roarhousing.MainActivity
import com.beaconinc.roarhousing.R
import com.beaconinc.roarhousing.cloudModel.FirebaseUser
import com.beaconinc.roarhousing.listAdapters.AdminListAdapter
import com.beaconinc.roarhousing.listAdapters.AdminListAdapter.AdminClickListener
import com.beaconinc.roarhousing.util.MB
import com.beaconinc.roarhousing.util.MB_THRESHOLD
import com.beaconinc.roarhousing.util.Memory_Access_code
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
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
    private lateinit var clientCollection: CollectionReference
    private lateinit var fireStore: FirebaseFirestore
    private lateinit var clientImageView: ImageView
    private lateinit var fullNameView: TextInputEditText
    private lateinit var storage: FirebaseStorage
    private var profileBitmap: Bitmap? = null
    private lateinit var sharedPref: SharedPreferences
    private lateinit var phoneTextView: TextInputEditText
    private lateinit var password: TextInputEditText
    private lateinit var brandName: TextInputEditText
    private lateinit var accountSpinner: TextInputLayout
    private lateinit var campusSpinner: TextInputLayout
    private lateinit var clientId: String
    private lateinit var saveBtn: MaterialButton
    private lateinit var progressBar: ProgressBar
    private lateinit var adminsRef: Query
    private lateinit var bottomSheetLayout: BottomSheetDialog
    private lateinit var admin: FirebaseUser


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        sharedPref = (activity as MainActivity).sharedPref
        fireStore = FirebaseFirestore.getInstance()
        clientCollection = fireStore.collection("clients")
        clientId = clientCollection.document().id
        Timber.i("Client New id: $clientId")
        clientDocument = clientCollection.document(clientId)
        adminsRef = clientCollection.whereEqualTo("accountType","Admin")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_set_up, container, false)
        clientImageView = view.findViewById(R.id.clientImage)
        fullNameView = view.findViewById(R.id.fullName)
        saveBtn = view.findViewById<MaterialButton>(R.id.saveBtn)
        val brownBtn = view.findViewById<ConstraintLayout>(R.id.browseImageBtn)
        campusSpinner = view.findViewById<TextInputLayout>(R.id.campusView)
        accountSpinner = view.findViewById<TextInputLayout>(R.id.account)
        phoneTextView = view.findViewById<TextInputEditText>(R.id.phoneNumber)
        password = view.findViewById<TextInputEditText>(R.id.password)
        brandName =view.findViewById(R.id.brandName)
        progressBar = view.findViewById(R.id.progressBar)
        val backBtn = view.findViewById<ImageView>(R.id.setUpBack)

        backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        saveBtn.setOnClickListener {
            if (profileBitmap !=null  ) {
                selectAdminBottomSheet()
            }else {
                Toast.makeText(requireContext(),"Please Select Profile Image First", Toast.LENGTH_SHORT).show()
            }
        }



        brownBtn.setOnClickListener {
             openStorageIntent()
        }

        val campusAdapter = ArrayAdapter.createFromResource (
            requireContext(),
            R.array.campus_array,
            android.R.layout.simple_spinner_dropdown_item
        )

        val accountType = sharedPref.getString("accountType","")

        var accountAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.admin_account_array,
            android.R.layout.simple_spinner_dropdown_item
        )

        if(accountType == "Super Admin") {
            accountAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.super_account_array,
                android.R.layout.simple_spinner_dropdown_item
            )
        }

        (campusSpinner.editText as AutoCompleteTextView).setAdapter(campusAdapter)
        (accountSpinner.editText as AutoCompleteTextView).setAdapter(accountAdapter)

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
        showProgress()
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
                    saveDetails(imageUri)
                }
            }//end complete listener
        }
    }

    private fun finishSetUp(firebaseUser: FirebaseUser?) {
            firebaseUser?.let {
                lifecycleScope.launch {
                    processProfileImage(profileBitmap!!)
                }
            }
    }

    private fun saveDetails(image: String) {
        val fullName = fullNameView.text.toString()
        val phoneNumber = phoneTextView.text.toString()
        val password = password.text.toString()
        val campus = campusSpinner.editText?.text.toString()
        val accountType = accountSpinner.editText?.text.toString()
        val brandName = brandName.text.toString()

        val client = FirebaseUser (
            clientId = clientId,
            clientPhone = phoneNumber,
            clientName = fullName,
            clientUrl = image,
            accountType = accountType,
            password = password,
            campus = campus,
            brandName = brandName,
            adminId = admin.clientId
        )

        clientDocument.set(client).addOnSuccessListener {
            lifecycleScope.launch {
                Toast.makeText(requireContext(),
                    "User Created", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }
        }.addOnFailureListener {
            hideProgress()
            Toast.makeText(requireContext(),
                "Check your Internet Connection: $it", Toast.LENGTH_SHORT).show()
        }
    }


   private fun selectAdminBottomSheet() {
        bottomSheetLayout = BottomSheetDialog(requireContext()).apply {
            setContentView(R.layout.admin_bottom_sheet)
            val progressBar = this.findViewById<ProgressBar>(R.id.progressBar)
            val adminRecycler = this.findViewById<RecyclerView>(R.id.adminRecyclerView)
            progressBar?.visibility = View.VISIBLE

            val adminListAdapter = AdminListAdapter(AdminClickListener { adminUser ->
                  bottomSheetLayout.dismiss()
                  admin = adminUser
                  finishSetUp(adminUser)
            })
            adminRecycler?.adapter = adminListAdapter

            adminsRef.get().addOnSuccessListener { snapShots ->
                snapShots.documents.mapNotNull {
                    it.toObject(FirebaseUser::class.java)
                }.also {
                    adminListAdapter.submitList(it)
                    lifecycleScope.launch {
                        progressBar?.visibility = View.GONE
                    }
                }
            }
        }
        bottomSheetLayout.show()
    }

    private fun showProgress() {
        progressBar.visibility = View.VISIBLE
        saveBtn.alpha = 0.1f
    }

    private fun hideProgress() {
        lifecycleScope.launch {
            progressBar.visibility = View.GONE
            saveBtn.alpha = 1f
        }
    }

}