package com.beaconinc.roarhousing.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.beaconinc.roarhousing.R
import com.beaconinc.roarhousing.cloudModel.FirebaseProperty
import com.beaconinc.roarhousing.listAdapters.storeAdapter.PropertyListAdapter
import com.beaconinc.roarhousing.listAdapters.storeAdapter.PropertyListAdapter.*
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.launch
import timber.log.Timber

class RoarStore : Fragment() {

    private lateinit var propertyListAdapter: PropertyListAdapter
    lateinit var fireStore: FirebaseFirestore
    private lateinit var propertyCollection: CollectionReference
    private lateinit var registration: ListenerRegistration
    private lateinit var progressBar: ProgressBar
    private lateinit var spinnerCallBack: AdapterView.OnItemSelectedListener
    private lateinit var titleText: TextView
    private lateinit var propertyRecycler: RecyclerView

    private val argsNav: RoarStoreArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fireStore = FirebaseFirestore.getInstance()
        propertyCollection = fireStore.collection("properties")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        setUpNativeAd()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_store, container, false)
        propertyRecycler = view.findViewById<RecyclerView>(R.id.propertyRecycler)
        val backBtn = view.findViewById<ImageView>(R.id.propertyBackBtn)
        val filterBtn = view.findViewById<ImageView>(R.id.filterBtn)
        titleText = view.findViewById(R.id.titleText)
        val filterSpinner = view.findViewById<Spinner>(R.id.filterSpinner)
        progressBar = view.findViewById(R.id.progressBar)

        argsNav.propertyId?.let {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }?: Timber.i("productId: ${argsNav.propertyId}")

        showProgress()
        backBtn.setOnClickListener {
            findNavController().navigateUp()
        }
        setUpSpinnerCallBack()

        ArrayAdapter.createFromResource(
            requireContext(), R.array.product_type_filter,
            android.R.layout.simple_spinner_dropdown_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            filterSpinner.adapter = adapter
        }
        filterSpinner.onItemSelectedListener = spinnerCallBack

        propertyRecycler.setHasFixedSize(true)
        val gridLayoutManager = GridLayoutManager(context, 2, RecyclerView.HORIZONTAL, false)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (position % 3 == 2) 2 else 1
            }
        }
        propertyRecycler.layoutManager = gridLayoutManager

        val largePadding =
            resources.getDimensionPixelSize(R.dimen.shr_staggered_product_grid_spacing_large)
        val smallPadding =
            resources.getDimensionPixelSize(R.dimen.shr_staggered_product_grid_spacing_small)
        propertyRecycler.addItemDecoration(ProductGridItemDecoration(largePadding, smallPadding))

        propertyListAdapter = PropertyListAdapter(PropertyClickListener(
            listener = {
            }, longClick = { showBottomSheet(it) }),
            lifecycleOwner = this@RoarStore
        )
        propertyRecycler.adapter = propertyListAdapter

        filterBtn.setOnClickListener {
            //val action = R.id.action_homeFragment_to_profileFragment
            filterSpinner.performClick()
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeFetch()
    }

    private fun setUpNativeAd() {
        val adLoader = AdLoader.Builder(requireContext(), "ca-app-pub-3940256099942544/2247696110")
            .forNativeAd { ad: NativeAd ->
                lifecycleScope.launchWhenCreated {
                    propertyListAdapter.setNativeAd(ad)
                }
                if (this@RoarStore.isDetached) {
                    ad.destroy()
                    return@forNativeAd
                }
            }.build()

        adLoader.loadAds(AdRequest.Builder().build(), 5)
    }

    override fun onStop() {
        super.onStop()
        if (::registration.isInitialized)
            registration.remove()
    }

    private fun initializeFetch() {
        registration = propertyCollection.addSnapshotListener { snapShots, _ ->
            snapShots?.documents?.mapNotNull {
                it.toObject(FirebaseProperty::class.java)
            }.also { properties ->
                propertyListAdapter.submitList(properties)
                hideProgress()
            }
        }
    }

    private fun showBottomSheet(property: FirebaseProperty) {
        val bottomSheetLayout = BottomSheetDialog(requireContext()).apply {
            setContentView(R.layout.layout_person_sheet)
            val sellerImage = this.findViewById<ImageView>(R.id.brandImage)
            val productImage = this.findViewById<ImageView>(R.id.productImage)
            val productName = this.findViewById<TextView>(R.id.productName)
            val productPrice = this.findViewById<TextView>(R.id.productPrice)
            val brandName = this.findViewById<TextView>(R.id.brandName)
            val aboutProduct = this.findViewById<TextView>(R.id.productDesc)


            sellerImage?.load(property.sellerUrl)
            productImage?.load(property.firstImage)
            productName?.text = property.propertyTitle
            productPrice?.text = getString(R.string.format_price,property.propertyPrice)
            brandName?.text = property.sellerName
            aboutProduct?.text = property.propertyDesc
        }
        val whatsAppBtn = bottomSheetLayout.findViewById<MaterialCardView>(R.id.whatsAppBtn)
        val share = bottomSheetLayout.findViewById<MaterialButton>(R.id.shareBtn)
        val callBtn = bottomSheetLayout.findViewById<MaterialButton>(R.id.callBtn)


        share?.setOnClickListener {
            share(property)
        }

        whatsAppBtn?.setOnClickListener {
            chatWhatsApp(property.sellerNumber)
        }

        callBtn?.setOnClickListener {
            dialPhoneNumber(property.sellerNumber)
        }

        bottomSheetLayout.show()
    }


    private fun share(product: FirebaseProperty) {
            val message =
            "Hi, check out this ${product.propertyTitle} on Roar App \n"+
                    "https://roar.com/property/${product.id}"

            val intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, message)
                type="text/plain"
            }

            try {
                startActivity(intent)
            }catch (ex: android.content.ActivityNotFoundException){
                Toast.makeText(requireContext(),"WhatsApp is not Found",
                    Toast.LENGTH_SHORT).show()
            }
        }

    private fun dialPhoneNumber(phoneNumber: String?) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phoneNumber") //or use Uri.fromParts()
        }
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(intent)
        }
    }


    private fun onCreateMenuLayout(view: View) {
        val popUpMenu = PopupMenu(requireContext(), view)
        val popInflater = popUpMenu.menuInflater
        popInflater.inflate(R.menu.view_product_menu, popUpMenu.menu)
        popUpMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.viewAllProduct -> {
                    Toast.makeText(requireContext(), "Not available", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> {
                    true
                }
            }
        }
        popUpMenu.show()
    }


    private fun setUpSpinnerCallBack() {
        spinnerCallBack = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selected = parent?.getItemAtPosition(position) as String
                titleText.text = selected
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
    }

    private fun chatWhatsApp(pNumber: String?) {

        val uri =
            "https://api.whatsapp.com/send?phone=+234$pNumber"

        val intent = Intent().apply {
            action = Intent.ACTION_VIEW
            data = Uri.parse(uri)
        }
        startActivity(intent)
    }

    private fun showProgress() {
        progressBar.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        lifecycleScope.launch {
            progressBar.visibility = View.GONE
        }
    }
}