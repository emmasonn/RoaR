package com.column.roar.dashBoard.upload

import android.content.SharedPreferences
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.column.roar.MainActivity
import com.column.roar.home.HomeFragment
import com.column.roar.R
import com.column.roar.cloudModel.FirebaseLodge
import com.column.roar.cloudModel.FirebaseUser
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LodgeUploadDetail : Fragment() {

    private lateinit var homeFragment: HomeFragment
    private lateinit var firebase: FirebaseFirestore
    private lateinit var lodgeCollection: CollectionReference
    private lateinit var lodgeName: TextInputEditText
    private lateinit var initialPay: TextInputEditText
    private lateinit var subPay: TextInputEditText
    private lateinit var lodgeDesc: TextInputEditText
    private lateinit var address: TextInputLayout
    private lateinit var distanceAway: TextInputLayout
    private lateinit var lodgeType: TextInputLayout
    private lateinit var lodgeSize: TextInputLayout
    private lateinit var availableRoom: TextInputEditText
    private lateinit var campus: TextInputLayout
    private lateinit var light: TextInputLayout
    private lateinit var network: TextInputLayout
    private lateinit var surrounding: TextInputLayout
    private lateinit var water: TextInputLayout
    private lateinit var clientDocument: DocumentReference
    private lateinit var sharedPref: SharedPreferences
    private var documentId: String? = null
    private var nameOfLodge: String? = null
    private lateinit var parentView: ConstraintLayout
    private lateinit var landLordPhone: TextInputEditText
    private lateinit var landLordName: TextInputEditText
//    private lateinit var lodgeIdentifier: TextInputEditText //commented out manuel inputting  lodgeIdentifier
    private lateinit var progressBar: ProgressBar
    private lateinit var nextBtn: MaterialButton

    private val lodge: FirebaseLodge? by lazy {
        arguments?.get("Lodge") as FirebaseLodge?
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebase = Firebase.firestore
        sharedPref = (activity as MainActivity).sharedPref
        val clientId = sharedPref.getString("user_id", "")

        lodgeCollection = firebase.collection("lodges")
        clientDocument = firebase.collection("clients").document(clientId!!)

        documentId = if(lodge == null) {
            lodgeCollection.document().id
        }else {
            lodge?.lodgeId
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_upload_lodge, container, false)
        lodgeName = view.findViewById(R.id.lodgeTitle)
        initialPay = view.findViewById(R.id.initialPay)
        subPay = view.findViewById(R.id.subPay)
        lodgeDesc = view.findViewById(R.id.description)
        water = view.findViewById(R.id.wSpinner)
        light = view.findViewById(R.id.lightSpinner)
        lodgeType = view.findViewById(R.id.type)
        lodgeSize = view.findViewById(R.id.size)
        availableRoom = view.findViewById(R.id.availableRm)
        network = view.findViewById(R.id.netWorkSpinner)
        surrounding = view.findViewById(R.id.surroundingSpinner)
        distanceAway = view.findViewById(R.id.distanceSpinner)
        address = view.findViewById(R.id.addressSpinner)
        campus = view.findViewById(R.id.campusSpinner)
        parentView = view.findViewById(R.id.parentView)
        val uploadBack = view.findViewById<ImageView>(R.id.uploadBack)
        landLordName = view.findViewById(R.id.houseOwner)
        landLordPhone = view.findViewById(R.id.housePhone)
//        lodgeIdentifier = view.findViewById(R.id.lodgeId)
        progressBar = view.findViewById(R.id.progressBar)
        nextBtn = view.findViewById(R.id.nextBtn)

        nextBtn.setOnClickListener {
            val lodgeName = lodgeName.text.toString()
            if (lodgeName.isNotBlank()) {
                showProgress()
                submitDetails()
            }
        }

        uploadBack.setOnClickListener {
            findNavController().popBackStack()
        }

        val campusAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.campus_array,
            android.R.layout.simple_spinner_dropdown_item
        )

        val addressAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.lodges_all_location,
            android.R.layout.simple_spinner_dropdown_item
        )

        val surroundingAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.surrounding,
            android.R.layout.simple_spinner_dropdown_item
        )

        val distanceAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.distance_level,
            android.R.layout.simple_spinner_dropdown_item
        )

        val typeAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.lodge_type,
            android.R.layout.simple_spinner_dropdown_item
        )

        val sizeAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.lodge_size,
            android.R.layout.simple_spinner_dropdown_item
        )

        val qualityAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.quality_level,
            android.R.layout.simple_spinner_dropdown_item
        )

        (campus.editText as AutoCompleteTextView).setAdapter(campusAdapter)
        (lodgeSize.editText as AutoCompleteTextView).setAdapter(sizeAdapter)
        (lodgeType.editText as AutoCompleteTextView).setAdapter(typeAdapter)
        (address.editText as AutoCompleteTextView).setAdapter(addressAdapter)
        (water.editText as AutoCompleteTextView).setAdapter(qualityAdapter)
        (light.editText as AutoCompleteTextView).setAdapter(qualityAdapter)
        (network.editText as AutoCompleteTextView).setAdapter(qualityAdapter)
        (surrounding.editText as AutoCompleteTextView).setAdapter(surroundingAdapter)
        (distanceAway.editText as AutoCompleteTextView).setAdapter(distanceAdapter)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateFields()
    }

    private fun updateFields() {
//        lodgeIdentifier.setText(lodge?.hiddenName)
        address.editText?.setText(lodge?.location)
        distanceAway.editText?.setText(lodge?.distance)
        surrounding.editText?.setText(lodge?.surrounding)
        light.editText?.setText(lodge?.light)
        network.editText?.setText(lodge?.network)
        water.editText?.setText(lodge?.water)
        lodgeName.setText(lodge?.lodgeName)
        initialPay.setText(lodge?.rent)
        campus.editText?.setText(lodge?.campus)
        lodgeType.editText?.setText(lodge?.type)
        landLordName.setText(lodge?.owner)
        landLordPhone.setText(lodge?.number)
        lodgeDesc.setText(lodge?.description)
        landLordName.setText(lodge?.owner)
        landLordPhone.setText(lodge?.number)
        lodgeSize.editText?.setText(lodge?.size)
        availableRoom.setText(lodge?.rooms?.toString())
        lodge?.let {
            if(it.payment !=null) {
                subPay.setText(lodge?.payment.toString())
            }
        }
    }

    private fun submitDetails() {
//        val lodgeIdentifier = lodgeIdentifier.text.toString()
        val address = address.editText?.text.toString()
        val distance = distanceAway.editText?.text.toString()
        val surrounding = surrounding.editText?.text.toString()
        val light = light.editText?.text.toString()
        val network = network.editText?.text.toString()
        val water = water.editText?.text.toString()
        val lodgeName = lodgeName.text.toString()
        val initialPay = initialPay.text.toString()
        val subPay = subPay.text.toString()
        val description = lodgeDesc.text.toString()
        val type = lodgeType.editText?.text.toString()
        val size = lodgeSize.editText?.text.toString()
        val availableRooms = availableRoom.text.toString()
        val campus = campus.editText?.text.toString()
        val ownerName = landLordName.text.toString()
        val ownerPhone = landLordPhone.text.toString()
        val randomUserId = generateLodgeId()

        nameOfLodge = lodgeName
        val subPayment = subPay.substringAfter("₦").toInt()

        val lodge = FirebaseLodge(
            hiddenName = randomUserId,
            lodgeId = documentId,
            lodgeName = lodgeName,
            location = address,
            description = description,
            light = light,
            campus = campus,
            type = type,
            size = size,
            rooms = availableRooms.toLong(),
            surrounding = surrounding,
            water = water,
            coverImage = lodge?.coverImage,
            tour = lodge?.tour,
            network = network,
            payment = subPayment,
            rent = initialPay,
            distance = distance,
            owner = ownerName,
            number = ownerPhone
        )

        clientDocument.get().addOnSuccessListener {
            it.toObject(FirebaseUser::class.java).also {
                lodge.apply {
                    agentImage = it?.clientImage
                    agentId = it?.clientId
                    agentName = it?.clientName
                    account = it?.account
                }
            }
            lodgeCollection.document(documentId!!).set(lodge)
                .addOnSuccessListener {
                    showDescTemplate("Lodge uploaded successfully")
                    hideProgress()
                    lifecycleScope.launchWhenCreated {
                        val action = R.id.action_lodgeDetailUpload_to_editLodgePager
                        val bundle = bundleOf("Lodge" to lodge)
                        findNavController().navigate(action, bundle)
                    }
                }.addOnFailureListener {
                    showDescTemplate("Unable to upload lodge")
                    hideProgress()
                }
        }.addOnFailureListener {
            showDescTemplate("Unable to upload Lodge")
            hideProgress()
        }
    }

    private fun generateLodgeId(): String =
        (0..9).shuffled().take(5).joinToString("").let { "Lodge$it" }

    private fun showDescTemplate(message: String?) {
        val snackBar = Snackbar.make(parentView, "$message", Snackbar.LENGTH_SHORT)
        val view = snackBar.view
        val params = view.layoutParams as FrameLayout.LayoutParams
        params.gravity = Gravity.CENTER
        view.layoutParams = params
        snackBar.animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
        snackBar.show()
    }

    private fun showProgress() {
        nextBtn.alpha = 0.5F
        progressBar.visibility = View.VISIBLE
    }

    private fun hideProgress(){
        lifecycleScope.launchWhenCreated {
            progressBar.visibility = View.GONE
            nextBtn.alpha = 1F
        }
    }

    companion object {
        fun newInstance(_homeFragment: HomeFragment) =
            LodgeUploadDetail().apply {
                homeFragment = _homeFragment
            }
    }
}