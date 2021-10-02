package com.beaconinc.roarhousing.home

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.beaconinc.roarhousing.MainActivity
import com.beaconinc.roarhousing.R
import com.beaconinc.roarhousing.cloudModel.FirebaseLodge
import com.beaconinc.roarhousing.cloudModel.FirebaseProperty
import com.beaconinc.roarhousing.listAdapters.LodgeClickListener
import com.beaconinc.roarhousing.listAdapters.NewListAdapter
import com.beaconinc.roarhousing.listAdapters.storeAdapter.PropertyListAdapter
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
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
    private lateinit var progressBar: ProgressBar
    private lateinit var fireStore: FirebaseFirestore
    private lateinit var propertiesQuery: Query
    private lateinit var chipsCategory: Array<String>
    private lateinit var lodgesRef: Query
    private lateinit var connectionView: ConstraintLayout
    private lateinit var swipeContainer: SwipeRefreshLayout
    private var counter = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        smallAdvertNativeAd()
//        mediumAdvertNativeAd()
        fireStore = FirebaseFirestore.getInstance()
        propertiesQuery = fireStore.collection("properties")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        homeRecycler = view.findViewById(R.id.homePager)
        backDrop = view.findViewById<LinearLayout>(R.id.backDrop)
        val luxBtn = view.findViewById<MaterialButton>(R.id.luxBtn)
        val cheapBtn = view.findViewById<MaterialButton>(R.id.cheapBtn)
        val propertyBtn = view.findViewById<MaterialButton>(R.id.propertyBtn)
        val settingsBtn = view.findViewById<MaterialButton>(R.id.settingsBtn)
        val aboutBtn = view.findViewById<MaterialButton>(R.id.aboutUs)
        val accommodationBtn = view.findViewById<MaterialButton>(R.id.accommodationBtn)
        val favBtn = view.findViewById<MaterialButton>(R.id.favBtn)
        val accountBtn = view.findViewById<MaterialButton>(R.id.accountBtn)
        val parentLayout = view.findViewById<ConstraintLayout>(R.id.childLayout)
        val appIcon = view.findViewById<ImageView>(R.id.appIcon)
        connectionView = view.findViewById(R.id.connectionView)
        swipeContainer = view.findViewById(R.id.swipeContainer)

        menuIcon = view.findViewById<ImageView>(R.id.menuNav)
        chipGroup = view.findViewById<ChipGroup>(R.id.chipGroup)
        progressBar = view.findViewById(R.id.progressBar)

        val argsNav: HomeFragmentArgs by navArgs()
        argsNav.lodgeId.let {
            if (it != "roar") {
                arguments = bundleOf("lodgeId" to "roar")
                Timber.i("data: ${argsNav.lodgeId}")
                resolveUrl(argsNav.lodgeId)
            }
        }

        swipeContainer.isRefreshing = true
        showProgress()

        roarItemsAdapter = NewListAdapter(LodgeClickListener({ lodge ->
            val bundle = bundleOf("Lodge" to lodge)
            findNavController().navigate(R.id.lodgeDetail, bundle)
        }, {}), PropertyListAdapter.PropertyClickListener(
            { data ->
                val link = Uri.parse("https://roar.com.ng/property/${data.id}")
                findNavController().navigate(link)
            },
            {}, justClick = {
                findNavController().navigate(R.id.productStore)
            }), this
        )
        homeRecycler.adapter = roarItemsAdapter

        swipeContainer.setOnRefreshListener {
            if (::chipsCategory.isInitialized) {
                val position = (activity as MainActivity).chipState
                roarItemsAdapter.clear()
                fetchLodges(chipsCategory[position])
            }
        }

        menuIcon.setOnClickListener(
            NavigationIconClickListener(
                requireActivity(),
                parentLayout,
                AccelerateDecelerateInterpolator(),
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_menu),
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_nav_close)
            )
        )

        accountBtn.setOnClickListener {
            val action = R.id.action_homeFragment_to_becomeAgent
            findNavController().navigate(action)
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
            val action = R.id.action_homeFragment_to_filterFragment
            findNavController().navigate(action, bundle)
        }

        cheapBtn.setOnClickListener {
            val bundle = bundleOf("choice" to "simple")
            val action = R.id.action_homeFragment_to_filterFragment
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
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            initializeHome()
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
                    roarItemsAdapter.addLodgeAndProperty(emptyList(), emptyList())
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
            val campus = settingP.getString("campus", "UNN")
            when (campus) {
                "UNN" -> {
                    chipsCategory = resources.getStringArray(R.array.lodges_location)
                    lodgesRef = fireStore.collection("lodges")
                        .whereEqualTo("campus", campus)
                }
                "UNEC" -> {
                    chipsCategory = resources.getStringArray(R.array.unec_campus)
                    lodgesRef = fireStore.collection("lodges")
                        .whereEqualTo("campus", campus)
                }

                else -> {
                    chipsCategory = resources.getStringArray(R.array.lodges_location)
                    lodgesRef = fireStore.collection("lodges")
                        .whereEqualTo("campus", campus)
                }
            }
            setUpChips()
            val position = (activity as MainActivity).chipState
            fetchLodges(chipsCategory[position])
        }
    }


    private fun fetchLodges(filter: String) {
        showProgress()
        propertiesQuery.get().addOnSuccessListener { snapShot ->
            snapShot.documents.mapNotNull { docShot ->
                docShot.toObject(FirebaseProperty::class.java)
            }.also { properties ->
                if (filter == "Lodges") {
                    lodgesRef.addSnapshotListener { value, error ->
                        if (error != null) {
                            connectionFailure() //error view
                            return@addSnapshotListener
                        }
                        value?.documents?.mapNotNull { snapShot ->
                            snapShot.toObject(FirebaseLodge::class.java)
                        }.also { lodges ->

                            if (lodges.isNullOrEmpty()) {
                                //Show empty view
                                roarItemsAdapter.showEmpty = true
                                roarItemsAdapter.addLodgeAndProperty(emptyList(), properties)
                                roarItemsAdapter.clear()
                                swipeContainer.isRefreshing = false
                                return@addSnapshotListener

                            } else {
                                roarItemsAdapter.addLodgeAndProperty(lodges, properties)
                                roarItemsAdapter.clear()
                                swipeContainer.isRefreshing = false
                                hideProgress()
                            }
                        }
                    }
                } else {
                    val filterRef = fireStore.collection("lodges")
                        .whereEqualTo("location", filter)

                    filterRef.get().addOnSuccessListener { result ->
                        result.documents.mapNotNull { snapLodge ->
                            snapLodge.toObject(FirebaseLodge::class.java)
                        }.also { lodges ->
                            if (lodges.isNotEmpty()) {
                                roarItemsAdapter.addLodgeAndProperty(lodges, properties)
                                roarItemsAdapter.clear()
                                swipeContainer.isRefreshing = false
                                hideProgress()
                            } else {
                                roarItemsAdapter.showEmpty = true
                                roarItemsAdapter.addLodgeAndProperty(emptyList(), properties)
                                roarItemsAdapter.clear()
                                swipeContainer.isRefreshing = false
                                hideProgress()
                            }
                        }
                    }.addOnFailureListener {
                        Toast.makeText(
                            requireContext(),
                            "${it.message} no internet",
                            Toast.LENGTH_LONG
                        ).show()
                        roarItemsAdapter.clear()
                        swipeContainer.isRefreshing = false
                        connectionFailure()
                    }
                }
            }
        }
    }

    private fun resolveUrl(id: String?) {
        id?.let {
            fireStore.collection("lodges").document(id)
                .get().addOnSuccessListener {
                    it.toObject(FirebaseLodge::class.java).also { lodge ->
                        val bundle = bundleOf("Lodge" to lodge)
                        findNavController().navigate(R.id.lodgeDetail, bundle)
                    }
                }
        }
    }

    private fun showProgress() {
        progressBar.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        lifecycleScope.launch {
            progressBar.visibility = View.GONE
        }
    }

    private fun smallAdvertNativeAd() {
        val adLoader = AdLoader.Builder(requireContext(), "ca-app-pub-3940256099942544/2247696110")
            .forNativeAd { ad: NativeAd ->
                run {
                    lifecycleScope.launchWhenCreated {
                        roarItemsAdapter.postAd1(ad)
                    }
                    if (this.isDetached) {
                        ad.destroy()
                        return@forNativeAd
                    }
                }
            }.build()
        adLoader.loadAds(AdRequest.Builder().build(), 5)
    }

    private fun mediumAdvertNativeAd() {
        val adLoader = AdLoader.Builder(requireContext(), "ca-app-pub-3940256099942544/2247696110")
            .forNativeAd { ad: NativeAd ->
                run {
                    lifecycleScope.launchWhenCreated {
                        roarItemsAdapter.postAd2(ad)
                    }
                    if (this.isDetached) {
                        ad.destroy()
                        return@forNativeAd
                    }
                }
            }.build()
        adLoader.loadAds(AdRequest.Builder().build(), 5)
    }

    private fun connectionFailure() {
        val connection = (activity as MainActivity).connectivityChecker
        connection?.apply {
            lifecycle.addObserver(this)
            connectedStatus.observe(viewLifecycleOwner, Observer {
                if (!it) {
                    connectionView.visibility = View.VISIBLE
                }
            })
        }
    }

    @SuppressLint("InflateParams")
    fun showWelcomeDialog() {
        MaterialAlertDialogBuilder(requireContext()).apply {
            val inflater = LayoutInflater.from(requireContext())
            val view = inflater.inflate(R.layout.item_edit_product_dialog, null)
            setView(view)
            show()
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