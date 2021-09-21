package com.beaconinc.roarhousing.home

import android.content.Context
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
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.beaconinc.roarhousing.MainActivity
import com.beaconinc.roarhousing.R
import com.beaconinc.roarhousing.cloudModel.FirebaseLodge
import com.beaconinc.roarhousing.cloudModel.FirebaseProperty
import com.beaconinc.roarhousing.listAdapters.LodgeClickListener
import com.beaconinc.roarhousing.listAdapters.LodgesAdapter
import com.beaconinc.roarhousing.listAdapters.NewListAdapter
import com.beaconinc.roarhousing.listAdapters.storeAdapter.PropertyListAdapter
import com.beaconinc.roarhousing.util.ChipClickListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.launch
import timber.log.Timber

class HomeFragment : Fragment() {

    private lateinit var homePager: RecyclerView
    private lateinit var chipGroup: ChipGroup
    private lateinit var backDrop: LinearLayout
    private lateinit var menuIcon: ImageView
    private lateinit var roarItemsAdapter: NewListAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var fireStore: FirebaseFirestore
    private lateinit var propertiesQuery: Query
    private lateinit var chipsCategory: Array<String>
    private lateinit var lodgesRef: Query
    private var counter = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fireStore = FirebaseFirestore.getInstance()
        propertiesQuery = fireStore.collection("properties")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        smallAdvertNativeAd()
        mediumAdvertNativeAd()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        homePager = view.findViewById(R.id.homePager)
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

        menuIcon = view.findViewById<ImageView>(R.id.menuNav)
        chipGroup = view.findViewById<ChipGroup>(R.id.chipGroup)
        progressBar = view.findViewById(R.id.progressBar)

        roarItemsAdapter = NewListAdapter(LodgeClickListener({ lodge ->
            val bundle = bundleOf("Lodge" to lodge)
            findNavController().navigate(R.id.lodgeDetail, bundle)
        },{}), PropertyListAdapter.PropertyClickListener({}, {}, justClick = {
            findNavController().navigate(R.id.lodgeProperty)
        }), this)
        homePager.adapter = roarItemsAdapter

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
            findNavController().navigate(R.id.lodgeProperty)
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
        val settingP = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val campus = settingP.getString("campus", "UNN")
        Timber.i("campus: $campus")
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

    private fun fetchLodges(filter: String) {
        showProgress()

        val filterRef = fireStore.collection("lodges")
            .whereEqualTo("location", filter)

        propertiesQuery.get().addOnSuccessListener { snapShot ->
            snapShot.documents.mapNotNull { docShot ->
                docShot.toObject(FirebaseProperty::class.java)
            }.also { properties ->

                if (filter == "Lodges") {
                    lodgesRef.addSnapshotListener { value, error ->
                        if (error != null) {
                            return@addSnapshotListener
                        }
                        value?.documents?.mapNotNull { snapShot ->
                            snapShot.toObject(FirebaseLodge::class.java)
                        }.also { lodges ->

                            if (lodges.isNullOrEmpty()) {
                                //Show empty view
                                Toast.makeText(
                                    requireContext(),
                                    "Filter: $filter", Toast.LENGTH_SHORT
                                ).show()
                                return@addSnapshotListener
                            }
                            roarItemsAdapter.addLodgeAndProperty(lodges, properties)
                            hideProgress()
                        }
                    }
                } else {

                    filterRef.get().addOnSuccessListener { result ->
                        result.documents.mapNotNull { snapLodge ->
                            snapLodge.toObject(FirebaseLodge::class.java)
                        }.also { lodges ->
                            if (lodges.isNullOrEmpty()) {
                                //Show empty view
                                Toast.makeText(
                                    requireContext(),
                                    "Filter: $filter", Toast.LENGTH_SHORT
                                ).show()
                            }
                            roarItemsAdapter.addLodgeAndProperty(lodges, properties)
                            hideProgress()
                        }
                    }
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