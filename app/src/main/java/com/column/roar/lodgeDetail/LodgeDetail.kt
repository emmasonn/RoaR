package com.column.roar.lodgeDetail

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.column.roar.MainActivity
import com.column.roar.R
import com.column.roar.cloudModel.FirebaseLodge
import com.column.roar.cloudModel.FirebaseLodgePhoto
import com.column.roar.database.FavModel
import com.column.roar.database.FavModelDao
import com.column.roar.databinding.FragmentLodgeDetailBinding
import com.column.roar.listAdapters.ClickListener
import com.column.roar.listAdapters.LodgeClickListener
import com.column.roar.listAdapters.LodgesAdapter
import com.column.roar.listAdapters.PhotosAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.dynamiclinks.ktx.shortLinkAsync
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class LodgeDetail : Fragment() {

    private val lodgeData: FirebaseLodge by lazy {
        arguments?.get("Lodge") as FirebaseLodge
    }

    private lateinit var bottomSheetLayout: BottomSheetDialog

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

        binding.shareBtn.setOnClickListener {
            shareLodgeData()
        }

        lockLodge() //call this function to lock lodge
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
            findNavController().navigate(R.id.watchTour, bundle)
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
                lifecycleScope.launchWhenStarted {
                    if (photos.size <= 3) {
                        photoListRecycler.layoutManager = LinearLayoutManager(
                            requireContext(), LinearLayoutManager.HORIZONTAL, false
                        )
                    } else {
                        photoListRecycler.layoutManager = GridLayoutManager(
                            requireContext(),
                            2, GridLayoutManager.HORIZONTAL, false
                        )

                    }
                    photosAdapter.submitList(photos)
                    swipeRefreshContainer.isRefreshing = false
                }
            }
        }

        lodgeCollection.get().addOnSuccessListener { values ->
            values.documents.mapNotNull {
                it.toObject(FirebaseLodge::class.java)
            }.also { lodges ->
                lifecycleScope.launchWhenCreated {
                    if (lodges.isNullOrEmpty()) {

                        lodgesAdapter.addLodgeAndProperty(lodges, true)
                        binding.othersRecycler.visibility = View.VISIBLE
                        binding.similarLodges.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun lockLodge() {
        if (lodgeData.availableRoom == null) {
            binding.lockLodge.alpha = 1F
        } else {
            if (lodgeData.availableRoom == 0L) {
                binding.lockLodge.alpha = 1F
                binding.availableRoom.alpha = 0F
            } else {
                binding.lockLodge.alpha = 0F
                binding.availableRoom.alpha = 1F
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
        bottomSheetLayout = BottomSheetDialog(requireContext()).apply {
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
        val proceedBtn = bottomSheetLayout.findViewById<MaterialButton>(R.id.proceedPayment)


        proceedBtn?.setOnClickListener {
            bottomSheetLayout.dismiss()
            findNavController().navigate(R.id.paymentPager)
        }

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
            Hi, Am interested in visiting ${lodgeData.randomId} \n\n
             https://unnapp.page.link/lodges/${lodgeData.lodgeId}
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

    private fun shareLodgeData() {
         swipeRefreshContainer.isRefreshing = true

        val futureTarget = Glide.with(requireContext())
            .asBitmap()
            .load(lodgeData.coverImage)
            .submit()

        lifecycleScope.launch(Dispatchers.Default) {
            val bitmapImage: Bitmap = futureTarget.get()

            withContext(Dispatchers.Main) {
                val bitmapUri = getBitmapUri(bitmapImage)
                val uriInUri = Uri.parse(bitmapUri)

                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    flags= Intent.FLAG_GRANT_READ_URI_PERMISSION
                }

                Firebase.dynamicLinks.shortLinkAsync {
                    longLink = Uri.parse("https://unnapp.page.link/?link=https://unnapp.page.link/lodges?lodgeId%3D${lodgeData.lodgeId}" +
                            "&apn=com.column.roar&st=Campus+Lodge&sd=Get+lodges+at+a+better+price+from+trusted+community" +
                            "&si=${lodgeData.coverImage}")
                }.addOnSuccessListener { shortLink ->

                    lifecycleScope.launchWhenCreated {
                        val message = "Hey, check this lodge at ${lodgeData.location} \n" +
                                "Price: ${getString(R.string.format_price_integer, lodgeData.subPayment)} \n\n " +
                                "${shortLink.shortLink}"
                        shareIntent.putExtra(Intent.EXTRA_TEXT, message)
                        shareIntent.putExtra(Intent.EXTRA_STREAM, uriInUri)
                        shareIntent.type = "image/*"
                        shareDynamicLink(shareIntent)
                    }
                }.addOnFailureListener { e ->
                    Timber.e(e,"cannot resolve link")
                    swipeRefreshContainer.isRefreshing = false
                    Toast.makeText(requireContext(),"Sharing failed, Network error",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun shareDynamicLink(shareIntent: Intent) {
        try {
            swipeRefreshContainer.isRefreshing = false
            startActivity(Intent.createChooser(shareIntent, null))
        } catch (ex: android.content.ActivityNotFoundException) {
            swipeRefreshContainer.isRefreshing = false
            Toast.makeText(
                requireContext(), "Cannot Share item",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun getBitmapUri(bitmapImage: Bitmap): String {
        return MediaStore.Images.Media.insertImage(
            requireContext().contentResolver,
            bitmapImage,
            null, null
        )
    }

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
        (activity as MainActivity).detailScreenSmallAd.observe(viewLifecycleOwner, { ad ->
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

