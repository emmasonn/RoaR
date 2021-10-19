package com.column.roar.home

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.*
import androidx.activity.OnBackPressedCallback
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
import com.column.roar.cloudModel.FirebaseLodge
import com.column.roar.cloudModel.FirebaseProperty
import com.column.roar.listAdapters.LodgeClickListener
import com.column.roar.listAdapters.NewListAdapter
import com.column.roar.listAdapters.storeAdapter.PropertyListAdapter.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

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
    private var counter = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fireStore = FirebaseFirestore.getInstance()
        propertiesQuery = fireStore.collection("properties").whereEqualTo("certified",true)
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

        menuIcon = view.findViewById<ImageView>(R.id.menuNav)
        chipGroup = view.findViewById<ChipGroup>(R.id.chipGroup)
        swipeContainer.isRefreshing = true

        searchIcon.setOnClickListener {
            findNavController().navigate(R.id.searchFragment)
        }

        setUpOnBackPressedCallback()

        val argsNav: HomeFragmentArgs by navArgs()
        argsNav.lodgeId.let {
            if (it != "roar") {
                arguments = bundleOf("lodgeId" to "roar")
                Timber.i("lodgeId : $argsNav.lodgeId")
                resolveUrl(argsNav.lodgeId)
            }
        }

     lifecycleScope.launch (Dispatchers.Main) {
         roarItemsAdapter = NewListAdapter(LodgeClickListener({ lodge ->
             val bundle = bundleOf("Lodge" to lodge)
             findNavController().navigate(R.id.lodgeDetail, bundle)
         }, {}), PropertyClickListener(
             { data ->
                 swipeContainer.isRefreshing = true
                 if (data.propertyType == "Ads") {
                     showAdDialog(data)
                 } else {
                     val link = Uri.parse("https://unnapp.page.link/ads/${data.id}")
                     findNavController().navigate(link)
                 }
             },
             {}, justClick = {
                 findNavController().navigate(R.id.productStore)
             }), this@HomeFragment,resources)
         homeRecycler.adapter = roarItemsAdapter
     }
        connectionFailure(false) //this function calls the ads when internet is available

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
            initializeHome()
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
                    roarItemsAdapter.submitList(emptyList())
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
                        .whereEqualTo("certified",true)
                        .whereEqualTo("campus", campus)
                }
                "UNEC" -> {
                    chipsCategory = resources.getStringArray(R.array.unec_campus)
                    lodgesRef = fireStore.collection("lodges")
                        .whereEqualTo("certified",true)
                        .whereEqualTo("campus", campus)
                }

                else -> {
                    chipsCategory = resources.getStringArray(R.array.lodges_location)
                    lodgesRef = fireStore.collection("lodges")
                        .whereEqualTo("certified",true)
                        .whereEqualTo("campus", campus)
                }
            }
            setUpChips()
            val position = (activity as MainActivity).chipState
            fetchLodges(chipsCategory[position])
        }
    }

    private fun fetchLodges(filter: String) {
        swipeContainer.isRefreshing =  true
        val source = Source.DEFAULT
        propertiesQuery.get(source).addOnSuccessListener { snapShot ->
            snapShot.documents.mapNotNull { docShot ->
                docShot.toObject(FirebaseProperty::class.java)
            }.also { properties ->
                if (filter == "Lodges") {
                    lodgesRef.addSnapshotListener { value, error ->
                        if (error != null) {
                            connectionFailure(true) //error view
                            return@addSnapshotListener
                        }
                        value?.documents?.mapNotNull { snapShot ->
                            snapShot.toObject(FirebaseLodge::class.java)
                        }.also { lodges ->

                            if (lodges.isNullOrEmpty()) {
                                //Show empty view
                                    lifecycleScope.launchWhenCreated {
                                        roarItemsAdapter.showEmpty = true
                                        roarItemsAdapter.addLodgeAndProperty(emptyList(), properties)
                                        roarItemsAdapter.clear()
                                        swipeContainer.isRefreshing = false
                                    }
                                return@addSnapshotListener

                            } else {
                                lifecycleScope.launchWhenCreated {
                                    roarItemsAdapter.addLodgeAndProperty(lodges, properties)
                                    roarItemsAdapter.clear()
                                    swipeContainer.isRefreshing = false
                                }
                            }
                        }
                    }
                } else {
                    val filterRef = fireStore.collection("lodges")
                        .whereEqualTo("certified",true)
                        .whereEqualTo("location", filter)

                    filterRef.get(source).addOnSuccessListener { result ->
                        result.documents.mapNotNull { snapLodge ->
                            snapLodge.toObject(FirebaseLodge::class.java)
                        }.also { lodges ->
                            if (lodges.isNotEmpty()) {
                                lifecycleScope.launchWhenCreated {
                                    roarItemsAdapter.addLodgeAndProperty(lodges, properties)
                                    roarItemsAdapter.clear()
                                    swipeContainer.isRefreshing = false
                                }
                            } else {
                                lifecycleScope.launchWhenCreated {
                                    roarItemsAdapter.showEmpty = true
                                    roarItemsAdapter.addLodgeAndProperty(emptyList(), properties)
                                    roarItemsAdapter.clear()
                                    swipeContainer.isRefreshing = false
                                }
                            }
                        }
                    }.addOnFailureListener {

                        lifecycleScope.launchWhenCreated {
                            Toast.makeText(
                                requireContext(),
                                "${it.message} no internet",
                                Toast.LENGTH_LONG
                            ).show()

                            roarItemsAdapter.clear()
                            swipeContainer.isRefreshing = false
                            connectionFailure(true)
                        }
                    }
                }
            }
        }.addOnFailureListener {
            lifecycleScope.launchWhenCreated {
                roarItemsAdapter.submitList(emptyList())
                roarItemsAdapter.clear()
                swipeContainer.isRefreshing = false
                connectionFailure(true)
            }
        }
    }

    private fun resolveUrl(id: String?) {
        swipeContainer.isRefreshing = true
        id?.let {
            fireStore.collection("lodges").document(id)
                .get().addOnSuccessListener {
                    lifecycleScope.launchWhenCreated {
                        swipeContainer.isRefreshing = true

                        it.toObject(FirebaseLodge::class.java).also { lodge ->
                            val bundle = bundleOf("Lodge" to lodge)
                            findNavController().navigate(R.id.lodgeDetail, bundle)
                        }
                    }
                }.addOnFailureListener {
                    lifecycleScope.launch {
                        swipeContainer.isRefreshing = false
                        Toast.makeText(requireContext(),"Failed to Load item, Network failure",
                        Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }


    private fun connectionFailure(error: Boolean) {
        val connection = (activity as MainActivity).connectivityChecker
        connection?.apply {
            lifecycle.addObserver(this)
            connectedStatus.observe(viewLifecycleOwner, { network ->

                if (!network && error) {
                    connectionView.visibility = View.VISIBLE
                } else {
                    connectionView.visibility = View.GONE
                }
            })
        }
    }

    @SuppressLint("InflateParams")
    fun showAdDialog(property: FirebaseProperty) {
        val bottomSheetLayout = BottomSheetDialog(requireContext()).apply {
            setContentView(R.layout.dialog_show_ad)
            val adImage = this.findViewById<ImageView>(R.id.adImage)

            adImage?.let {
                Glide.with(adImage.context)
                    .load(property.firstImage)
                    .apply(RequestOptions()
                        .placeholder(R.drawable.animated_gradient)
                        .error(R.drawable.animated_gradient)).into(adImage)
            }
        }
        val callBtn = bottomSheetLayout.findViewById<MaterialButton>(R.id.callNow)

        lifecycleScope.launchWhenCreated {
            animateWarning(callBtn!!)
        }

        callBtn?.setOnClickListener {}
        bottomSheetLayout.show()
        swipeContainer.isRefreshing = false
    }

    //this function animates the location icon
    private fun animateWarning(icon: MaterialButton) {
        val prevColor = Color.parseColor("#046d86")
        val newColor = Color.parseColor("#d32f2f")
        ValueAnimator.ofObject(ArgbEvaluator(), newColor, prevColor).apply {
            repeatCount = 10000000
            duration = 200
            addUpdateListener { valueAnimator ->
                val background = valueAnimator.animatedValue as Int
                icon.setBackgroundColor(background)
                icon.setTextColor(Color.WHITE)
            }
            start()
        }
    }

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