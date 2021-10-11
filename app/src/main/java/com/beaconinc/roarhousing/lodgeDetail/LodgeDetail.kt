package com.beaconinc.roarhousing.lodgeDetail

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import coil.load
import com.beaconinc.roarhousing.MainActivity
import com.beaconinc.roarhousing.R
import com.beaconinc.roarhousing.cloudModel.FirebaseLodge
import com.beaconinc.roarhousing.cloudModel.FirebaseLodgePhoto
import com.beaconinc.roarhousing.database.FavModel
import com.beaconinc.roarhousing.database.FavModelDao
import com.beaconinc.roarhousing.databinding.FragmentLodgeDetailBinding
import com.beaconinc.roarhousing.listAdapters.ClickListener
import com.beaconinc.roarhousing.listAdapters.LodgeClickListener
import com.beaconinc.roarhousing.listAdapters.LodgesAdapter
import com.beaconinc.roarhousing.listAdapters.PhotosAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LodgeDetail : Fragment() {

    private val lodgeData: FirebaseLodge by lazy {
        arguments?.get("Lodge") as FirebaseLodge
    }

    private lateinit var favModelDao: FavModelDao
    private lateinit var fireStore: FirebaseFirestore
    private lateinit var photosReference: CollectionReference
    private lateinit var photosAdapter: PhotosAdapter
    private lateinit var clientDocumentRef: DocumentReference
    private lateinit var lodgeCollection: Query
    private lateinit var lodgesAdapter: LodgesAdapter
    private lateinit var photoListRecycler: RecyclerView
    private lateinit var binding: FragmentLodgeDetailBinding
    private lateinit var sharedPref: SharedPreferences
    private lateinit var swipeRefreshContainer: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPref = (activity as MainActivity).sharedPref
        favModelDao = (activity as MainActivity).db.favModelDao()

        fireStore = FirebaseFirestore.getInstance()
        photosReference = fireStore.collection("lodges")
            .document(lodgeData.lodgeId!!).collection("lodgePhotos")

        lodgeCollection = fireStore.collection("lodges")
            .whereNotEqualTo("lodgeId", lodgeData.lodgeId)
        //replace lodgeId with agentId when it is not null
        clientDocumentRef = fireStore.collection("clients").document(lodgeData.agentId!!)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentLodgeDetailBinding.inflate(inflater, container, false)
        initializeAd()
        binding.lifecycleOwner = this
        binding.lodgeDetail = lodgeData
        photoListRecycler = binding.listPhotos
        val favIcon = binding.favoriteBtn
        swipeRefreshContainer = binding.swipeContainer
        swipeRefreshContainer.isRefreshing = true

        lodgesAdapter = LodgesAdapter(LodgeClickListener({
            val bundle = bundleOf("Lodge" to it)
            findNavController().navigate(R.id.lodgeDetail, bundle)
        }, {}), this, false)

        favIcon.setOnClickListener {
            lifecycleScope.launch {
                favIcon.visibility = View.GONE
                storeFavId(favModelDao.getFavOnce())
            }
        }

        binding.coverImage.setOnClickListener {
            val photo = FirebaseLodgePhoto(
                photoId = clientDocumentRef.id,
                photoUrl = lodgeData.coverImage,
                photoTitle = "CoverImage"
            )
            val bundle = bundleOf("picture" to photo)
            findNavController().navigate(R.id.viewLodge, bundle)
        }

        binding.playBtn.setOnClickListener {
            val bundle = bundleOf("Lodge" to lodgeData)
            findNavController().navigate(R.id.watchTour,bundle)
        }

        favModelDao.getFavString().observe(viewLifecycleOwner, { favIds ->
            val currentId = lodgeData.lodgeId
            val oldIds: List<String> = favIds.map { it.id }

            if (oldIds.contains(currentId)) {
                favIcon.setImageResource(R.drawable.ic_fav_red)
            } else {
                favIcon.setImageResource(R.drawable.ic_fav_outline)
            }
            favIcon.visibility = View.VISIBLE
        })

        binding.othersRecycler.adapter = lodgesAdapter
        binding.bookBtn.setOnClickListener {
            showBottomSheet(lodgeData)
        }

        Glide.with(binding.coverImage.context)
            .load(lodgeData.coverImage)
            .apply(
                 RequestOptions().placeholder(R.drawable.animated_gradient)
                    .error(R.drawable.animated_gradient)
            )
            .into(binding.coverImage)

        val status = sharedPref.getString("accountType", "")

        binding.titleText.setOnClickListener {
            if (status == "admin") {
                showLodgeName(lodgeData.lodgeName)
            }
        }

        Glide.with(binding.agentImageCover.context)
            .load(lodgeData.agentUrl)
            .apply(
                RequestOptions().placeholder(R.drawable.ic_round_person)
                    .error(R.drawable.ic_round_person)
            )
            .into(binding.agentImageCover)

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        photosAdapter = PhotosAdapter(ClickListener(listener = { photo ->
            val bundle = bundleOf("picture" to photo)
            findNavController().navigate(R.id.viewLodge, bundle)
        }))
        photoListRecycler.adapter = photosAdapter
        fetchLodgeAndPhotos()

        swipeRefreshContainer.setOnRefreshListener {
            fetchLodgeAndPhotos()
        }

        return binding.root
    }

    private fun fetchLodgeAndPhotos() {
        photosReference.get().addOnSuccessListener { photosSnap ->
            photosSnap.documents.mapNotNull {
                it.toObject(FirebaseLodgePhoto::class.java)
            }.also { photos ->
                lifecycleScope.launch {
                    if (photos.size <= 3) {
                        photoListRecycler.layoutManager = LinearLayoutManager(
                            requireContext(), LinearLayoutManager.HORIZONTAL, false
                        )
                    } else {

                            photoListRecycler.layoutManager = GridLayoutManager(
                                requireContext(),
                                2, GridLayoutManager.HORIZONTAL, false
                            )
                            photosAdapter.submitList(photos)
                            swipeRefreshContainer.isRefreshing = false
                    }
                }
            }
        }

        lodgeCollection.get().addOnSuccessListener { values ->
            values.documents.mapNotNull {
                it.toObject(FirebaseLodge::class.java)
            }.also { lodges ->
                lifecycleScope.launchWhenCreated {
                if(lodges.isNullOrEmpty()) {

                        lodgesAdapter.addLodgeAndProperty(lodges, true)
                        binding.othersRecycler.visibility = View.VISIBLE
                        binding.similarLodges.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private suspend fun storeFavId(favModels: List<FavModel>) {
        val currentID = lodgeData.lodgeId
        val oldIDS: List<String> = favModels.map { it.id }
        val favModel = FavModel(currentID!!)

        if (oldIDS.contains(currentID)) {
            lifecycleScope.launch(Dispatchers.IO) {
                favModelDao.delete(favModel)
            }
        } else {
            lifecycleScope.launch(Dispatchers.IO) {
                favModelDao.insert(favModel)
            }
        }
    }

    private fun showBottomSheet(lodge: FirebaseLodge) {
        val bottomSheetLayout = BottomSheetDialog(requireContext()).apply {
            setContentView(R.layout.realtor_bottom_dialog)
            val coverImage = this.findViewById<ImageView>(R.id.lodgeImage)
            val agentImage = this.findViewById<ImageView>(R.id.agentImage)

            Glide.with(coverImage!!.context)
                .load(lodge.coverImage)
                .apply(
                    RequestOptions().placeholder(R.drawable.animated_gradient)
                        .error(R.drawable.animated_gradient)
                )
                .into(coverImage)

            Glide.with(agentImage!!.context)
                .load(lodge.agentUrl)
                .apply(
                    RequestOptions().placeholder(R.drawable.ic_round_person)
                        .error(R.drawable.ic_round_person)
                )
                .into(agentImage)
        }

        val whatsAppBtn = bottomSheetLayout.findViewById<MaterialCardView>(R.id.whatsAppBtn)
        val callBtn = bottomSheetLayout.findViewById<MaterialButton>(R.id.callBtn)

        callBtn?.setOnClickListener {
            dialPhoneNumber(lodge.agentPhone)
        }

        whatsAppBtn?.setOnClickListener {
            chatWhatsApp(lodge.agentPhone)
        }
        bottomSheetLayout.show()
    }

    private fun chatWhatsApp(pNumber: String?) {

        val message = """
            Hi, Am interested in visiting ${lodgeData.randomId}
             https://roar.com.ng/lodge/${lodgeData.lodgeId}
        """.trimIndent()

        val uri =
            "https://api.whatsapp.com/send?phone=+234$pNumber&text=$message"

         val intent = Intent().apply {
            action = Intent.ACTION_VIEW
            data = Uri.parse(uri)
        }

        try {
            startActivity(intent)
        } catch (ex: android.content.ActivityNotFoundException) {
            Toast.makeText(
                requireContext(), "WhatsApp is not Found",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

//    private fun getBitmapUri(bitmapImage: Bitmap): String {
//        return MediaStore.Images.Media.insertImage(
//            requireContext().contentResolver,
//            bitmapImage,
//            null, null
//        )
//    }

    private fun dialPhoneNumber(phoneNumber: String?) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phoneNumber") //or use Uri.fromParts()
        }
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(intent)
        }
    }

    private fun showLodgeName(lodgeName: String?) {
        MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle(lodgeName)
            setCancelable(false)
            show()
        }
    }

    private fun initializeAd() {
        (activity as MainActivity).detailScreenSmallAd.observe(viewLifecycleOwner,{ ad ->
            binding.firstSmallNativeAd.setNativeAd(ad)
        })

        (activity as MainActivity).detailScreenMediumAd.observe(viewLifecycleOwner, { ad ->
            binding.adNativeMedium.setNativeAd(ad)
        })
    }

}
//class PhotosPager(private val photos: List<FirebaseLodgePhoto>, fragment: Fragment):
//        FragmentStateAdapter(fragment) {
//        override fun getItemCount(): Int  = photos.size
//
//        override fun createFragment(position: Int): Fragment =
//            PagerPhotoSlide.newInstance(photos[position])
//    }

