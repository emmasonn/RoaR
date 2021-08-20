package com.beaconinc.roarhousing.lodgeDetail

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import com.beaconinc.roarhousing.R
import com.beaconinc.roarhousing.cloudModel.FirebaseLodge
import com.beaconinc.roarhousing.databinding.FragmentLodgeDetailBinding
import com.beaconinc.roarhousing.cloudModel.FirebaseLodgePhoto
import com.beaconinc.roarhousing.cloudModel.FirebaseNotifier
import com.beaconinc.roarhousing.listAdapters.ClickListener
import com.beaconinc.roarhousing.listAdapters.PhotosAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class LodgeDetail : Fragment() {

    private val lodgeData: FirebaseLodge by lazy {
        arguments?.get("Lodge") as FirebaseLodge
    }
    private lateinit var fireStore: FirebaseFirestore
    private lateinit var photosReference: CollectionReference
    private lateinit var photoImageView: ImageView
    private lateinit var photosAdapter: PhotosAdapter
    private lateinit var clientDocumentRef: DocumentReference
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fireStore = FirebaseFirestore.getInstance()
        photosReference = fireStore.collection("lodges")
            .document(lodgeData.lodgeId!!).collection("lodgePhotos")

        //replace lodgeId with agentId when it is not null
        clientDocumentRef = fireStore.collection("clients").document(lodgeData.agentId!!)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val binding = FragmentLodgeDetailBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.lodgeDetail = lodgeData
        photoImageView = binding.imageSlide
        progressBar = binding.progressBar
        val photoListRecycler = binding.listPhotos

        binding.bookBtn.setOnClickListener {
            showDateDialog()
        }

        binding.imageSlide.load(lodgeData.coverImage)
        binding.agentImageCover.load(lodgeData.agentUrl)

        binding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        photosAdapter = PhotosAdapter(ClickListener( listener = { photo ->
            photosAdapter.notifyDataSetChanged()

            photoImageView.load(photo.photoUrl){
                crossfade(true)
            }
        }))

        photoListRecycler.adapter = photosAdapter
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        photosReference.get().addOnSuccessListener { photosSnap ->
            photosSnap.documents.mapNotNull {
                it.toObject(FirebaseLodgePhoto::class.java)
            }.also { photos ->
                photosAdapter.submitList(photos)
            }
        }
    }

  @SuppressLint("InflateParams")
  private fun showDateDialog() {
      val visitId = photosReference.document().id
      val visitRef = clientDocumentRef.collection("visits").document(visitId)

       MaterialAlertDialogBuilder(requireContext(),
           R.style.ShapeAppearanceOverlay_App_dateDialog ).apply {
           val inflater = LayoutInflater.from(requireContext())
           val view = inflater.inflate(R.layout.date_picker_layout,null)
           val datePicker = view.findViewById<DatePicker>(R.id.datePicker)
           val name = view.findViewById<TextInputEditText>(R.id.clientName)
           val phone = view.findViewById<TextInputEditText>(R.id.clientPhone)

           setPositiveButton("Submit") { _ , _ ->

               val day = datePicker.dayOfMonth
               val month = datePicker.month
               val year = datePicker.year
               val clientName = name.text.toString()
               val clientPhone = phone.text.toString()
               val date = "$day/$month/$year"

               if (clientName.isBlank() || clientPhone.isBlank()) {
                   Toast.makeText(requireContext(),
                       "Name or PhoneNumber should be blank",Toast.LENGTH_LONG).show()
               }
               else {
                   progressBar.visibility = View.VISIBLE

                   val visitData = FirebaseNotifier(
                       notifierId = visitId,
                       lodgeCover = lodgeData.coverImage,
                       lodgeName = lodgeData.lodgeName,
                       campus = lodgeData.campus,
                       lodgeLocation = lodgeData.location,
                       clientName = clientName,
                       clientPhone = clientPhone,
                       visitDate = date
                   )

                   visitRef.set(visitData).addOnSuccessListener {
                       Toast.makeText(requireContext(),
                           "Booking send Successfully",Toast.LENGTH_SHORT).show()
                       lifecycleScope.launch {
                           clientDocumentRef.update("visitCounter",FieldValue.increment(1))
                           progressBar.visibility=View.GONE
                       }
                   }.addOnFailureListener {
                       Toast.makeText(requireContext(),
                           "Booking Failed, No Internet",Toast.LENGTH_SHORT).show()
                       lifecycleScope.launch {
                           progressBar.visibility=View.GONE
                       }
                   }
               }
           }
           setNegativeButton("Close") { dialog, _ ->
               findNavController().navigate(R.id.paymentFragment)
           }
           setView(view)
           show()
       }
   }
}

//class PhotosPager(private val photos: List<FirebaseLodgePhoto>, fragment: Fragment):
//        FragmentStateAdapter(fragment) {
//        override fun getItemCount(): Int  = photos.size
//
//        override fun createFragment(position: Int): Fragment =
//            PagerPhotoSlide.newInstance(photos[position])
//    }


