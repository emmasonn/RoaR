package com.column.roar.home

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.column.roar.MainActivity
import com.column.roar.R
import com.column.roar.listAdapters.LodgeClickListener
import com.column.roar.listAdapters.NewListAdapter
import com.column.roar.listAdapters.storeAdapter.PropertyListAdapter.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.column.roar.cloudModel.*
import com.column.roar.database.LodgeDao
import com.column.roar.listAdapters.ClickListener
import com.column.roar.listAdapters.UploadPhotosAdapter
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.Source
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class HomeFragment : Fragment() {
    private lateinit var homeRecycler: RecyclerView
    private lateinit var chipGroup: ChipGroup
    private lateinit var backDrop: LinearLayout
    private lateinit var menuIcon: ImageView
    private lateinit var roarItemsAdapter: NewListAdapter
    private lateinit var fireStore: FirebaseFirestore
    private lateinit var propertiesQuery: Query
    private lateinit var chipsCategory: Array<String>
    private lateinit var lodgesRef: Query
    private lateinit var connectionView: MaterialCardView
    private lateinit var swipeContainer: SwipeRefreshLayout
    private lateinit var callback: OnBackPressedCallback
    private var emptyList: MaterialCardView? = null
    private var networkError: MaterialCardView? = null
    private var player: SimpleExoPlayer? = null
    private lateinit var playerView: PlayerView
    private var counter = 0
    private lateinit var retryBtn: MaterialButton
    private lateinit var sharedPref: SharedPreferences
    private  lateinit var lodgeDao: LodgeDao
    private var isAdapterReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fireStore = FirebaseFirestore.getInstance()
        propertiesQuery = fireStore.collection(getString(R.string.firestore_products))
            .whereEqualTo("certified", true)
        sharedPref = (activity as MainActivity).sharedPref
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        homeRecycler = view.findViewById(R.id.homePager)
        backDrop = view.findViewById(R.id.backDrop)
        val luxBtn = view.findViewById<MaterialButton>(R.id.luxBtn)
        val cheapBtn = view.findViewById<MaterialButton>(R.id.cheapBtn)
        val propertyBtn = view.findViewById<MaterialButton>(R.id.propertyBtn)
        val settingsBtn = view.findViewById<MaterialButton>(R.id.settingsBtn)
        val aboutBtn = view.findViewById<MaterialButton>(R.id.aboutUs)
        val accommodationBtn = view.findViewById<MaterialButton>(R.id.accommodationBtn)
        val favBtn = view.findViewById<MaterialButton>(R.id.favBtn)
        val accountBtn = view.findViewById<MaterialButton>(R.id.accountBtn)
        val parentLayout = view.findViewById<ConstraintLayout>(R.id.childLayout)
        connectionView = view.findViewById(R.id.connectionView)
        swipeContainer = view.findViewById(R.id.swipeContainer)
        val searchIcon = view.findViewById<ImageView>(R.id.searchIcon)
        retryBtn = view.findViewById(R.id.retryBtn)
        val marqueeText = view.findViewById<TextView>(R.id.marqueeText)
        lodgeDao = (activity as MainActivity).db.lodgeDao()

        menuIcon = view.findViewById(R.id.menuNav)
        chipGroup = view.findViewById(R.id.chipGroup)
        swipeContainer.isRefreshing = true
        marqueeText.isSelected = true

        lifecycleScope.launch(Dispatchers.Main) {
            val marqueeInfo = sharedPref.getString("marquee_text", null)
            if (marqueeInfo.isNullOrEmpty()) {
                marqueeText.visibility = View.GONE
            }else {
                marqueeText.visibility = View.VISIBLE
                marqueeText.text = marqueeInfo
            }
        }

        searchIcon.setOnClickListener {
            findNavController().navigate(R.id.searchFragment)
        }

        retryBtn.setOnClickListener {
            fetchLodges(chipsCategory[counter])
        }
        setUpOnBackPressedCallback()

   //      resolve the implicit link
        val argsNav: HomeFragmentArgs by navArgs()
        argsNav.lodgeId.let {
            if (it != "roar") {
                arguments = bundleOf("lodgeId" to "roar")
                resolveUrl(argsNav.lodgeId)
            }
        }

        lodgeDao.getAllLodges().observe(viewLifecycleOwner,{ lodges ->
             val lodgesId: List<String?> = lodges.map { it.id }

            roarItemsAdapter = NewListAdapter(LodgeClickListener({ lodge ->
                val bundle = bundleOf("Lodge" to lodge)
                findNavController().navigate(R.id.lodgeDetail, bundle)
            }, {}), PropertyClickListener ({ data ->
                    swipeContainer.isRefreshing = true
                    if (data.type == "Ads") {
                        showAdDialog(data)
                    } else {
                        val link = Uri.parse("https://unnapp.page.link/ads/${data.id}")
                        findNavController().navigate(link)
                    }
                },
                { data ->
                    showAdDialog(data)
                }, justClick = { _ , _ ->
                        swipeContainer.isRefreshing = true
                        findNavController().navigate(R.id.productStore)

                }), this@HomeFragment, resources,
                lodgesId )
            homeRecycler.adapter = roarItemsAdapter
            isAdapterReady = true
            lifecycleScope.launch (Dispatchers.Main){
                initializeHome()
            }
        })

        swipeContainer.setOnRefreshListener {
            if (::chipsCategory.isInitialized) {
                val position = (activity as MainActivity).chipState
                roarItemsAdapter.clear()
                if(isAdapterReady) fetchLodges(chipsCategory[position])
            }
        }

        menuIcon.setOnClickListener(
            NavigationIconClickListener(
                requireActivity(),
                parentLayout,
                AccelerateDecelerateInterpolator(),
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_menu_icon),
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_nav_close)
            )
        )

        accountBtn.setOnClickListener {
            findNavController().navigate(R.id.becomeAgent)
        }

        favBtn.setOnClickListener {
            findNavController().navigate(R.id.favoriteFragment)
        }

        accommodationBtn.setOnClickListener {
            findNavController().navigate(R.id.accommodationFragment)
        }

        luxBtn.setOnClickListener {
            //isBackDropVisible = false
            val bundle = bundleOf("choice" to "classic")
            val action = R.id.filterFragment
            findNavController().navigate(action, bundle)
        }

        cheapBtn.setOnClickListener {
            val bundle = bundleOf("choice" to "simple")
            val action = R.id.filterFragment
            findNavController().navigate(action, bundle)
        }

        propertyBtn.setOnClickListener {
            findNavController().navigate(R.id.productStore)
        }

        settingsBtn.setOnClickListener {
            findNavController().navigate(R.id.settingsPager)
        }

        aboutBtn.setOnClickListener {
            findNavController().navigate(R.id.aboutFragment)
        }
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, callback)
        return view
    }


    private fun setUpOnBackPressedCallback() {
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showExitDialog()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch(Dispatchers.Main) {
//            initializeHome()
            (activity as MainActivity).homeScreenAd.observe(viewLifecycleOwner, { ad ->
                roarItemsAdapter.postAd1(ad)
                roarItemsAdapter.postAd2(ad)
            })
        }
    }

    private fun setUpChips() {
        chipsCategory.let { cat ->
            val chipInflater = LayoutInflater.from(chipGroup.context)
            val children: List<Chip> = cat.map { item ->
                val chip = chipInflater.inflate(R.layout.region, chipGroup, false) as Chip
                chip.text = item
                chip.tag = counter++
                val chipState = (activity as MainActivity).chipState

                if ((chip.tag) as Int == chipState) {
                    chip.isChecked = true
                }

                chip.setOnClickListener { view ->
                    menuIcon.performClick()
                    val query = (view as Chip).tag as Int
                    view.isChecked = true
                    (activity as MainActivity).chipState = query

                    roarItemsAdapter.showEmpty = false
//                    roarItemsAdapter.submitList(emptyList())
                    roarItemsAdapter.clear()
                    fetchLodges(chipsCategory[query])
                }
                chip
            }

            chipGroup.removeAllViews()
            children.forEach { chip ->
                chipGroup.addView(chip)
            }
            counter = 0
        }
    }

    private suspend fun initializeHome() {
        withContext(Dispatchers.Main) {
            val settingP = PreferenceManager.getDefaultSharedPreferences(requireContext())
            when (val campus = settingP.getString("campus", "UNN")) {
                "UNN" -> {
                    chipsCategory = resources.getStringArray(R.array.lodges_location)
                    lodgesRef = fireStore.collection(getString(R.string.firestore_lodges))
                        .whereEqualTo("certified", true)
                        .whereEqualTo("campus", campus)
                }
                "UNEC" -> {
                    chipsCategory = resources.getStringArray(R.array.unec_campus)
                    lodgesRef = fireStore.collection(getString(R.string.firestore_lodges))
                        .whereEqualTo("certified", true)
                        .whereEqualTo("campus", campus)
                }
                else -> {
                    chipsCategory = resources.getStringArray(R.array.lodges_location)
                    lodgesRef = fireStore.collection(getString(R.string.firestore_lodges))
                        .whereEqualTo("certified", true)
                        .whereEqualTo("campus", campus)
                }
            }
            setUpChips()
            val position = (activity as MainActivity).chipState
            fetchLodges(chipsCategory[position])
        }
    }

    private fun fetchLodges(filter: String) {
        swipeContainer.isRefreshing = true
       val  isDataFetched = (activity as MainActivity).isDataFetched
        val source = if (isDataFetched) Source.CACHE else Source.DEFAULT
        propertiesQuery.get(source).addOnSuccessListener { snapShot ->
            snapShot.documents.mapNotNull { docShot ->
                docShot.toObject(FirebaseProperty::class.java)
            }.also { properties ->
                if (filter == "Lodges") {
                    lodgesRef.get(source).addOnSuccessListener { value ->
                        value.documents.mapNotNull { snapShot ->
                            snapShot.toObject(FirebaseLodge::class.java)
                        }.also { dataResult ->
                            if (dataResult.isNullOrEmpty()) {
                                //Show empty view
                                lifecycleScope.launchWhenCreated {
                                    showNetworkError(false)
                                    roarItemsAdapter.showEmpty = true
                                    roarItemsAdapter.addLodgeAndProperty(emptyList(), properties)
                                    swipeContainer.isRefreshing = false
                                }
                            } else {
                                lifecycleScope.launchWhenCreated {
                                    (activity as MainActivity).isDataFetched = true
                                    showNetworkError(false)
                                    roarItemsAdapter.addLodgeAndProperty(dataResult, properties)
                                    swipeContainer.isRefreshing = false
                                }
                            }
                        }
                    }.addOnFailureListener {
                        showNetworkError(true) //error view
                        swipeContainer.isRefreshing = false
                    }
                } else {
                    val filterRef = fireStore.collection(getString(R.string.firestore_lodges))
                        .whereEqualTo("certified", true)
                        .whereEqualTo("location", filter)

                    filterRef.get(source).addOnSuccessListener { result ->
                        result.documents.mapNotNull { snapLodge ->
                            snapLodge.toObject(FirebaseLodge::class.java)
                        }.also { lodges ->
                            if (lodges.isNotEmpty()) {
                                lifecycleScope.launchWhenCreated {
                                    showNetworkError(false)
                                    roarItemsAdapter.addLodgeAndProperty(lodges, properties)
//                                    roarItemsAdapter.clear()
                                    swipeContainer.isRefreshing = false
                                }
                            } else {
                                lifecycleScope.launchWhenCreated {
                                    roarItemsAdapter.showEmpty = true
                                    showNetworkError(false)
                                    roarItemsAdapter.addLodgeAndProperty(emptyList(), properties)
//                                    roarItemsAdapter.clear()
                                    swipeContainer.isRefreshing = false
                                }
                            }
                        }
                    }.addOnFailureListener {
                        lifecycleScope.launchWhenCreated {
//                            roarItemsAdapter.clear()
                            swipeContainer.isRefreshing = false
                            showNetworkError(true)
                        }
                    }
                }
            }
        }.addOnFailureListener {
            lifecycleScope.launchWhenCreated {
                swipeContainer.isRefreshing = false
                showNetworkError(true)
            }
        }
    }

    private fun showNetworkError(error: Boolean) {
        if (error) {
            connectionView.visibility = View.VISIBLE
            retryBtn.visibility = View.VISIBLE
        } else {
            connectionView.visibility = View.GONE
        }
    }

    private fun resolveUrl(id: String?) {
        swipeContainer.isRefreshing = true
        id?.let {
            fireStore.collection(getString(R.string.firestore_lodges)).document(id)
                .get().addOnSuccessListener { snapShot ->
                    lifecycleScope.launch {   //this was launch when created
                        swipeContainer.isRefreshing = true
                        snapShot.toObject(FirebaseLodge::class.java).also { lodge ->
                            val bundle = bundleOf("Lodge" to lodge)
                            findNavController().navigate(R.id.lodgeDetail, bundle)
                        }
                    }
                }.addOnFailureListener {
                    lifecycleScope.launch {
                        swipeContainer.isRefreshing = false
                        Toast.makeText(
                            requireContext(), "Failed to Load item, Network failure",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

    //dialog for business advert
    @SuppressLint("InflateParams")
    fun showAdDialog(product: FirebaseProperty) {
        val bottomSheetLayout = BottomSheetDialog(requireContext()).apply {
            setContentView(R.layout.home_dialog_full)
            val adImage = this.findViewById<ImageView>(R.id.adImage)
//            val productsRecycler = this.findViewById<RecyclerView>(R.id.productPager)
//            playerView = this.findViewById(R.id.videoCover)!!
//            val noVideo = this.findViewById<TextView>(R.id.noVideo)

//            otherProductAdapter = UploadPhotosAdapter(ClickListener(
//                {}, {
//                    showImageDialog(it.image!!)
//                }
//            ))
//            productsRecycler?.adapter = otherProductAdapter
//            fetchProduct(product.id!!)

//            if (product.video != null) {
//                setUpExoPlayer(product.video)
//            } else {
//                noVideo?.visibility = View.VISIBLE
//            }

            adImage?.let {
                Glide.with(adImage.context)
                    .load(product.cover)
                    .apply(
                        RequestOptions()
                            .placeholder(R.drawable.loading_background)
                            .error(R.drawable.loading_background)
                    ).into(adImage)
            }
        }
        val callBtn = bottomSheetLayout.findViewById<MaterialButton>(R.id.callNow)
        val whatsAppBtn = bottomSheetLayout.findViewById<MaterialCardView>(R.id.whatsAppBtn)
        networkError = bottomSheetLayout.findViewById(R.id.connectionView)
        emptyList = bottomSheetLayout.findViewById(R.id.emptyListView)

        callBtn?.setOnClickListener { product.number?.let { it1 -> callDialog(it1,product.brand) } }
        whatsAppBtn?.setOnClickListener { product.number?.let { it1 -> whatsAppDialog(it1,product.brand) } }
        bottomSheetLayout.show()
        swipeContainer.isRefreshing = false
    }

    //this function fetches the products of every business person
//    private fun fetchProduct(productId: String) {
//        swipeContainer.isRefreshing = true
//
//        val otherProductCollection = fireStore.collection(getString(R.string.firestore_products))
//            .document(productId).collection(getString(R.string.firestore_others))
//
//        otherProductCollection.get().addOnSuccessListener { snapShot ->
//            snapShot.documents.mapNotNull {
//                it.toObject(FirebasePhotoAd::class.java)
//            }.also { otherItems ->
//                otherItems.map {
//                    FirebaseLodgePhoto(
//                        id = it.id,
//                        image = it.image
//                    )
//                }.let { result ->
//                    lifecycleScope.launchWhenCreated {
//                        if (result.isNotEmpty()) {
//                            otherProductAdapter.submitList(result)
//                            swipeContainer.isRefreshing = false
//                            networkError?.visibility = View.GONE
//                            emptyList?.visibility = View.GONE
//                        } else {
//                            emptyList?.visibility = View.VISIBLE
//                        }
//                    }
//                }
//            }
//        }.addOnFailureListener {
//            swipeContainer.isRefreshing = false
//            networkError?.visibility = View.VISIBLE
//            emptyList?.visibility = View.GONE
//        }
//    }

    //this function animates the location icon
//    private fun animateWarning(icon: MaterialButton) {
//        val prevColor = Color.parseColor("#046d86")
//        val newColor = Color.parseColor("#d32f2f")
//        ValueAnimator.ofObject(ArgbEvaluator(), newColor, prevColor).apply {
//            repeatCount = 10000000
//            duration = 200
//            addUpdateListener { valueAnimator ->
//                val background = valueAnimator.animatedValue as Int
//                icon.setBackgroundColor(background)
//                icon.setTextColor(Color.WHITE)
//            }
//            start()
//        }
//    }

    //app exit dialog
    private fun showExitDialog() {
        MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle("You're about to exit app")
            setPositiveButton("Okay") { dialog, _ ->
                dialog.dismiss()
                (activity as MainActivity).finish()
            }

            setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            show()
        }
    }

    //dial phone number
    private fun dialPhoneNumber(phoneNumber: String?) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phoneNumber") //or use Uri.fromParts()
        }
        startActivity(intent)
    }

    //what chat
    private fun chatWhatsApp(pNumber: String?) {
        val message = """
             Hi, from *Roar*
            """
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

    private fun callDialog(number: String, brand: String?) {
        AlertDialog.Builder(requireContext()).apply {
            setTitle("You are about to leave app to make call $brand")
            setPositiveButton("Okay") { dialog, _ ->
                dialog.dismiss()
                dialPhoneNumber(number)
            }
            setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            show()
        }
    }

    //whats-App dialog
    private fun whatsAppDialog(data: String, brand: String?) {
        AlertDialog.Builder(requireContext()).apply {
            setTitle("You are about to leave app to chat with $brand ")
            setPositiveButton("Okay") { dialog, _ ->
                dialog.dismiss()
                chatWhatsApp(data)
            }

            setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            show()
        }
    }

//    @SuppressLint("InflateParams")
//    private fun showImageDialog(imageUrl: String) {
//        AlertDialog.Builder(requireContext()).apply {
//            val inflater = LayoutInflater.from(requireContext())
//            val view = inflater.inflate(R.layout.dialog_view_product, null)
//            val imageView = view.findViewById<ImageView>(R.id.fullImage)
//
//            Glide.with(imageView.context)
//                .load(imageUrl)
//                .apply(
//                    RequestOptions()
//                        .placeholder(R.drawable.animated_gradient)
//                        .error(R.drawable.animated_gradient)
//                ).into(imageView)
//
//            setView(view)
//        }.show()
//    }

    //created exo player
    private fun setUpExoPlayer(videoUrl: String?) {
        player = SimpleExoPlayer.Builder(requireContext())
            .build()
            .also { exoPlayer ->
                playerView.player = exoPlayer
                videoUrl?.let {
                    val mediaItem = MediaItem.fromUri(Uri.parse(it))
                    exoPlayer.setMediaItem(mediaItem)
                }
                exoPlayer.playWhenReady = true
                exoPlayer.prepare()
            }
    }

//    class HomePager(
//        val fragment: Fragment,
//        private val homeFragment: HomeFragment,
//        private val cats: Array<String>,
//    ) : FragmentStateAdapter(fragment) {
//        override fun getItemCount() = cats.size
//
//        override fun createFragment(position: Int): Fragment =
//            LodgesFragment.newInstance(homeFragment, cats[position])
//    }

}