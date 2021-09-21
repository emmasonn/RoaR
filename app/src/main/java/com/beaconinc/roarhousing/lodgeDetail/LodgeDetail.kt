package com.beaconinc.roarhousing.lodgeDetail

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.beaconinc.roarhousing.MainActivity
import com.beaconinc.roarhousing.R
import com.beaconinc.roarhousing.cloudModel.FirebaseLodge
import com.beaconinc.roarhousing.databinding.FragmentLodgeDetailBinding
import com.beaconinc.roarhousing.cloudModel.FirebaseLodgePhoto
import com.beaconinc.roarhousing.database.FavModel
import com.beaconinc.roarhousing.database.FavModelDao
import com.beaconinc.roarhousing.listAdapters.ClickListener
import com.beaconinc.roarhousing.listAdapters.LodgeClickListener
import com.beaconinc.roarhousing.listAdapters.LodgesAdapter
import com.beaconinc.roarhousing.listAdapters.PhotosAdapter
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LodgeDetail : Fragment() {

    private val lodgeData: FirebaseLodge by lazy {
        arguments?.get("Lodge") as FirebaseLodge
    }

    private lateinit var favModelDao: FavModelDao

    private lateinit var fireStore: FirebaseFirestore
    private lateinit var photosReference: CollectionReference
    private lateinit var photosAdapter: PhotosAdapter
    private lateinit var clientDocumentRef: DocumentReference
    private lateinit var progressBar: ProgressBar
    private lateinit var lodgeCollection: CollectionReference
    private lateinit var lodgesAdapter: LodgesAdapter
    private lateinit var photoListRecycler: RecyclerView
    private lateinit var binding: FragmentLodgeDetailBinding
    private lateinit var lodgeRef: DocumentReference


    override fun onAttach(context: Context) {
        super.onAttach(context)
        setUpNativeAd()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        favModelDao = (activity as MainActivity).db.favModelDao()
        fireStore = FirebaseFirestore.getInstance()
        photosReference = fireStore.collection("lodges")
            .document(lodgeData.lodgeId!!).collection("lodgePhotos")
        lodgeCollection = fireStore.collection("lodges")
        //replace lodgeId with agentId when it is not null
        lodgeRef = lodgeCollection.document(lodgeData.lodgeId!!)
        clientDocumentRef = fireStore.collection("clients").document(lodgeData.agentId!!)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentLodgeDetailBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.lodgeDetail = lodgeData
        progressBar = binding.progressBar
        photoListRecycler = binding.listPhotos
        val favIcon = binding.favoriteBtn

        lodgesAdapter = LodgesAdapter(LodgeClickListener ({
            val bundle = bundleOf("Lodge" to it)
            findNavController().navigate(R.id.lodgeDetail, bundle)
        },{}),false)

        favIcon.setOnClickListener {
            lifecycleScope.launch {
                favIcon.visibility = View.GONE
                storeFavId(favModelDao.getFavOnce())
            }
        }

        favModelDao.getFavString().observe(viewLifecycleOwner, Observer { favIds ->
            val currentId = lodgeData.lodgeId
            val oldIds: List<String> = favIds.map { it.id }

            if (oldIds.contains(currentId)) {
                favIcon.setImageResource(R.drawable.ic_fav_red)
            }else {
                favIcon.setImageResource(R.drawable.ic_fav_outline)
            }
            favIcon.visibility = View.VISIBLE
        })

        binding.othersRecycler.adapter = lodgesAdapter
        binding.bookBtn.setOnClickListener {
            showBottomSheet(lodgeData)
        }

        binding.imageSlide.load(lodgeData.coverImage) {
            crossfade(true)
        }

        binding.agentImageCover.load(lodgeData.agentUrl)

        binding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        photosAdapter = PhotosAdapter(ClickListener(listener = { photo ->
            val bundle = bundleOf("picture" to photo)
            findNavController().navigate(R.id.viewLodge, bundle)
        }))
        photoListRecycler.adapter = photosAdapter
        fetchLodgeAndPhotos()

        return binding.root
    }

    private fun fetchLodgeAndPhotos() {
        showProgress()
        photosReference.get().addOnSuccessListener { photosSnap ->
            photosSnap.documents.mapNotNull {
                it.toObject(FirebaseLodgePhoto::class.java)
            }.also { photos ->
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
                hideProgress()
            }
        }


        lodgeCollection.get().addOnSuccessListener { values ->
            values.documents.mapNotNull {
                it.toObject(FirebaseLodge::class.java)
            }.also { lodges ->
                lodgesAdapter.submitList(lodges)
            }
        }
    }

    private suspend fun storeFavId( favModels: List<FavModel>) {
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

    private fun setUpNativeAd() {
        val adLoader = AdLoader.Builder(requireContext(),"ca-app-pub-3940256099942544/2247696110")
            .forNativeAd { ad: NativeAd ->
                lifecycleScope.launchWhenCreated {
                    val firstAd = binding.firstSmallNativeAd
                    val smallNativeView = binding.adNativeSmall
                    val mediumNativeView = binding.adNativeMedium
                    firstAd.setNativeAd(ad)
                    smallNativeView.setNativeAd(ad)
                    mediumNativeView.setNativeAd(ad)
                    showNativeAds()
                    if(this@LodgeDetail.isDetached) {
                        ad.destroy()
                        return@launchWhenCreated
                    }
                }
            }.build()

         adLoader.loadAds(AdRequest.Builder().build(),5)
    }


    private fun showNativeAds() {
        binding.firstSmallNativeAd.visibility = View.VISIBLE
        binding.adNativeSmall.visibility = View.VISIBLE
        binding.adNativeMedium.visibility = View.VISIBLE
    }

    private fun showProgress() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        lifecycleScope.launch {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun showBottomSheet(lodge: FirebaseLodge) {
        val bottomSheetLayout = BottomSheetDialog(requireContext()).apply {
            setContentView(R.layout.realtor_bottom_dialog)
            val coverImage = this.findViewById<ImageView>(R.id.lodgeImage)
            val aboutMe = this.findViewById<TextView>(R.id.aboutMe)
            val agentImage = this.findViewById<ImageView>(R.id.agentImage)
            val lodgeName = this.findViewById<TextView>(R.id.lodgeName)

            coverImage?.load(lodge.coverImage)
            agentImage?.load(lodge.agentUrl)
            lodgeName?.text = lodge.lodgeName
            aboutMe?.text = lodge.aboutRealtor
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
        val uri = "https://api.whatsapp.com/send?$pNumber"
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(uri)
        startActivity(intent)
    }

    private fun dialPhoneNumber(phoneNumber: String?) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phoneNumber") //or use Uri.fromParts()
        }
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(intent)
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

