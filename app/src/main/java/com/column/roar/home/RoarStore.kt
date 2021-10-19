package com.column.roar.home

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.column.roar.MainActivity
import com.column.roar.R
import com.column.roar.cloudModel.FirebaseProperty
import com.column.roar.listAdapters.storeAdapter.PropertyListAdapter
import com.column.roar.listAdapters.storeAdapter.PropertyListAdapter.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.dynamiclinks.ktx.shortLinkAsync
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.Source
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class RoarStore : Fragment() {

    private lateinit var propertyListAdapter: PropertyListAdapter
    private lateinit var productRef: CollectionReference
    lateinit var fireStore: FirebaseFirestore
    private lateinit var propertyCollection: Query
    private lateinit var progressBar: ProgressBar
    private lateinit var spinnerCallBack: AdapterView.OnItemSelectedListener
    private lateinit var titleText: TextView
    private lateinit var propertyRecycler: RecyclerView
    private lateinit var swipeRefreshContainer: SwipeRefreshLayout
    private lateinit var connectionView: MaterialCardView
    private lateinit var emptyItem: MaterialCardView
    private val argsNav: RoarStoreArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fireStore = FirebaseFirestore.getInstance()
        productRef = fireStore.collection("properties")
        propertyCollection = fireStore.collection("properties")
            .whereEqualTo("certified",true)
            .whereNotEqualTo("propertyType","Ads")
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
        swipeRefreshContainer = view.findViewById(R.id.swipeContainer)
        connectionView = view.findViewById(R.id.connectionView)
        emptyItem = view.findViewById(R.id.emptyListView)
        setUpSpinnerCallBack()

        showProgress()
        argsNav.propertyId.let {
            if(it!="roar"){
                showProgress()
                showProduct(it)
            }
        }

        backBtn.setOnClickListener {
            findNavController().navigateUp()
        }

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
            resources.getDimensionPixelSize(R.dimen.staggered_product_grid_spacing_large)
        val smallPadding =
            resources.getDimensionPixelSize(R.dimen.staggered_product_grid_spacing_small)
        propertyRecycler.addItemDecoration(ProductGridItemDecoration(largePadding, smallPadding))
        propertyListAdapter = PropertyListAdapter(PropertyClickListener(
            listener = {
            }, longClick = { showBottomSheet(it) }),
            lifecycleOwner = this@RoarStore
        )
        propertyRecycler.adapter = propertyListAdapter
        initializeAd() //initialize ad for this screen

        swipeRefreshContainer.setOnRefreshListener {
            initializeFetch(titleText.text.toString())
        }

        filterBtn.setOnClickListener {
            //val action = R.id.action_homeFragment_to_profileFragment
            filterSpinner.performClick()
        }
        return view
    }

    private fun showProduct(it: String) {
        productRef.document(it).get().addOnSuccessListener {
            it.toObject(FirebaseProperty::class.java).also { item ->
                    item?.let {
                        showBottomSheet(item)
                    }
            }
        }
    }

    private fun initializeAd() {
        (activity as MainActivity).storeScreenAd.observe(viewLifecycleOwner,{ ad ->
            propertyListAdapter.setNativeAd(ad)
        })
    }

    private fun initializeFetch(filter: String) {
        Timber.i("filter: $filter")

        showProgress()
        val source = Source.CACHE
        if(filter == "Store") {
            propertyCollection.get(source).addOnCompleteListener { task ->
                if(task.isSuccessful){
                    val document = task.result
                    document?.documents?.mapNotNull {
                        it.toObject(FirebaseProperty::class.java)
                    }.also { items ->
                        lifecycleScope.launchWhenCreated {

                            if (items.isNullOrEmpty()) {
                                connectionView(true)
                                swipeRefreshContainer.isRefreshing = false
                                hideProgress()
                            } else {
                                connectionView(false)
                                propertyListAdapter.submitList(items)
                                swipeRefreshContainer.isRefreshing = false
                                hideProgress()
                            }
                        }
                    }
                }
            }
        } else {
            propertyCollection.whereEqualTo("propertyType", filter)
                .get(source).addOnCompleteListener { task ->
                if(task.isSuccessful){
                    val document = task.result
                    document?.documents?.mapNotNull {
                        it.toObject(FirebaseProperty::class.java)
                    }.also { items ->
                        lifecycleScope.launchWhenCreated {
                            if (items.isNullOrEmpty()) {
                                connectionView(true)
                                swipeRefreshContainer.isRefreshing = false
                                hideProgress()
                            } else {
                                connectionView(false)
                                propertyListAdapter.submitList(items)
                                swipeRefreshContainer.isRefreshing = false
                                hideProgress()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showBottomSheet(product: FirebaseProperty) {
        val bottomSheetLayout = BottomSheetDialog(requireContext()).apply {
            setContentView(R.layout.layout_person_sheet)
            val productImage = this.findViewById<ImageView>(R.id.productImage)
            val productName = this.findViewById<TextView>(R.id.productName)
            val productPrice = this.findViewById<TextView>(R.id.productPrice)
            val aboutProduct = this.findViewById<TextView>(R.id.productInfo)

            Glide.with(productImage!!.context)
                .load(product.firstImage).apply(
                    RequestOptions().placeholder(R.drawable.animated_gradient)
                        .error(R.drawable.animated_gradient)
                ).into(productImage)

            productName?.text = product.propertyTitle
            productPrice?.text = getString(R.string.format_price,product.propertyPrice)
            aboutProduct?.text = product.propertyDesc
            hideProgress()
        }

        val whatsAppBtn = bottomSheetLayout.findViewById<MaterialCardView>(R.id.whatsAppBtn)
        val share = bottomSheetLayout.findViewById<MaterialButton>(R.id.shareBtn)
        val callBtn = bottomSheetLayout.findViewById<MaterialButton>(R.id.callBtn)

        share?.setOnClickListener {
            shareProduct(product)
        }

        whatsAppBtn?.setOnClickListener {
            chatWhatsApp(product.sellerNumber)
        }

        callBtn?.setOnClickListener {
            dialPhoneNumber(product.sellerNumber)
        }
        bottomSheetLayout.show()
    }

    private fun shareProduct(product: FirebaseProperty) {

        val futureTarget = Glide.with(requireContext())
            .asBitmap()
            .load(product.firstImage)
            .submit()

        lifecycleScope.launch(Dispatchers.Default) {
            val bitmapImage = futureTarget.get()

            withContext(Dispatchers.Main) {
                val bitmapUri = getBitmapUri(bitmapImage)
                val uriInUri = Uri.parse(bitmapUri)

                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TITLE, "Lodge at ${product.campus} Campus")
                    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                }

                Firebase.dynamicLinks.shortLinkAsync {
                    longLink = Uri.parse("https://unnapp.page.link/?link=https://unnapp.page.link/ads?productId%3D${product.id}&apn=com.column.roar&st=RoaR+Store" +
                                "&sd=Nice+product+for+sale,+review+and+trusted" +
                                "&si=${product.firstImage}")
                }.addOnSuccessListener { shortLink ->

                    lifecycleScope.launchWhenCreated {
                        val message = "Hey, check this ${product.propertyTitle} at RoaR \n" +
                                "Price: ${getString(R.string.format_price, product.propertyPrice)} \n\n " +
                                "${shortLink.shortLink}"

                        shareIntent.putExtra(Intent.EXTRA_TEXT, message)
                        shareIntent.putExtra(Intent.EXTRA_STREAM, uriInUri)
                        shareIntent.type = "image/*"
                        shareDynamicLink(shareIntent)
                    }
                }.addOnFailureListener { e ->
                    Timber.e(e, "cannot resolve link")
                    Toast.makeText(
                        requireContext(),
                        "Sharing failed, Network error",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }//end of withContext main
    }

    private fun shareDynamicLink(shareIntent: Intent) {
        try {
            startActivity(Intent.createChooser(shareIntent, null))
        } catch (ex: android.content.ActivityNotFoundException) {
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

    private fun showEmptyList() {
        emptyItem.visibility = View.VISIBLE
    }

    private fun hideEmptyList() {
        emptyItem.visibility = View.GONE
    }

    private fun dialPhoneNumber(phoneNumber: String?) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phoneNumber") //or use Uri.fromParts()
        }
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(intent)
        }
    }

    private fun connectionView(isEmpty: Boolean) {
        val connection = (activity as MainActivity).connectivityChecker
        connection?.apply {
            lifecycle.addObserver(this)
            connectedStatus.observe(viewLifecycleOwner, {
                if (!it && isEmpty) {
                    connectionView.visibility = View.VISIBLE
                    hideEmptyList()
                }else if(isEmpty){
                    connectionView.visibility = View.GONE
                    showEmptyList()
                }else if(!isEmpty) {
                    connectionView.visibility = View.GONE
                    hideEmptyList()
                }
            })
        }
    }

//    private fun onCreateMenuLayout(view: View) {
//        val popUpMenu = PopupMenu(requireContext(), view)
//        val popInflater = popUpMenu.menuInflater
//        popInflater.inflate(R.menu.view_product_menu, popUpMenu.menu)
//        popUpMenu.setOnMenuItemClickListener { menuItem ->
//            when (menuItem.itemId) {
//                R.id.viewAllProduct -> {
//                    Toast.makeText(requireContext(), "Not available", Toast.LENGTH_SHORT).show()
//                    true
//                }
//                else -> {
//                    true
//                }
//            }
//        }
//        popUpMenu.show()
//    }

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
                propertyListAdapter.submitList(emptyList())
                initializeFetch(selected)
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