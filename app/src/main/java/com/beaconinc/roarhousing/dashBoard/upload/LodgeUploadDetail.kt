package com.beaconinc.roarhousing.dashBoard.upload

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
import com.beaconinc.roarhousing.MainActivity
import com.beaconinc.roarhousing.home.HomeFragment
import com.beaconinc.roarhousing.R
import com.beaconinc.roarhousing.cloudModel.FirebaseLodge
import com.beaconinc.roarhousing.cloudModel.FirebaseUser
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
    private lateinit var lodgeIdentifier: TextInputEditText

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
        lodgeName = view.findViewById<TextInputEditText>(R.id.lodgeTitle)
        initialPay = view.findViewById<TextInputEditText>(R.id.initialPay)
        subPay = view.findViewById<TextInputEditText>(R.id.subPay)
        lodgeDesc = view.findViewById<TextInputEditText>(R.id.description)
        water = view.findViewById<TextInputLayout>(R.id.wSpinner)
        light = view.findViewById<TextInputLayout>(R.id.lightSpinner)
        lodgeType = view.findViewById(R.id.type)
        lodgeSize = view.findViewById(R.id.size)
        availableRoom = view.findViewById(R.id.availableRm)
        network = view.findViewById<TextInputLayout>(R.id.netWorkSpinner)
        surrounding = view.findViewById<TextInputLayout>(R.id.surroundingSpinner)
        distanceAway = view.findViewById<TextInputLayout>(R.id.distanceSpinner)
        address = view.findViewById<TextInputLayout>(R.id.addressSpinner)
        val nextBtn = view.findViewById<MaterialButton>(R.id.nextBtn)
        campus = view.findViewById(R.id.campusSpinner)
        parentView = view.findViewById(R.id.parentView)
        val uploadBack = view.findViewById<ImageView>(R.id.uploadBack)
        landLordName = view.findViewById(R.id.houseOwner)
        landLordPhone = view.findViewById(R.id.housePhone)
        lodgeIdentifier = view.findViewById(R.id.lodgeId)

        nextBtn.setOnClickListener {
            //submitDetails()
            val lodgeName = lodgeName.text.toString()
            if (lodgeName.isNotBlank()) {
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
        lodgeIdentifier.setText(lodge?.randomId)
        address.editText?.setText(lodge?.location)
        distanceAway.editText?.setText(lodge?.distance)
        surrounding.editText?.setText(lodge?.surrounding)
        light.editText?.setText(lodge?.light)
        network.editText?.setText(lodge?.network)
        water.editText?.setText(lodge?.water)
        lodgeName.setText(lodge?.lodgeName)
        initialPay.setText(lodge?.subPayment)
        subPay.setText(lodge?.subPayment)
        campus.editText?.setText(lodge?.campus)
        lodgeType.editText?.setText(lodge?.type)
        landLordName.setText(lodge?.ownerName)
        landLordPhone.setText(lodge?.ownerPhone)
        lodgeDesc.setText(lodge?.description)
        landLordName.setText(lodge?.ownerName)
        landLordPhone.setText(lodge?.ownerPhone)
    }

    private fun submitDetails() {
        val lodgeIdentifier = lodgeIdentifier.text.toString()
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
        //val randomUserId = generateLodgeId()

        nameOfLodge = lodgeName

        val lodge = FirebaseLodge(
            randomId = lodgeIdentifier,
            lodgeId = documentId,
            lodgeName = lodgeName,
            location = address,
            description = description,
            light = light,
            campus = campus,
            type = type,
            size = size,
            availableRoom = availableRooms.toLong(),
            surrounding = surrounding,
            water = water,
            network = network,
            subPayment = subPay,
            initialPayment = initialPay,
            distance = distance,
            ownerName = ownerName,
            ownerPhone = ownerPhone
        )

        clientDocument.get().addOnSuccessListener {
            it.toObject(FirebaseUser::class.java).also {
                lodge.apply {
                    agentUrl = it?.clientUrl
                    agentId = it?.clientId
                    agentName = it?.clientName
                    accountType = it?.accountType
                }
            }

            lodgeCollection.document(documentId!!).set(lodge)
                .addOnSuccessListener {
                    showDescTemplate("Lodge uploaded successfully")
                    lifecycleScope.launch(Dispatchers.Main) {
                        val action = R.id.action_lodgeDetailUpload_to_editLodgePager
                        val bundle = bundleOf("Lodge" to lodge)
                        findNavController().navigate(action, bundle)
                    }
                }.addOnFailureListener {
                    showDescTemplate("Unable to upload lodge")
                }
        }.addOnFailureListener {
            showDescTemplate("Unable to upload Lodge")
        }
    }

    private fun generateLodgeId(): String =
        (0..9).shuffled().take(4).joinToString("").let { "Lodge$it" }

    private fun showDescTemplate(message: String?) {
        val snackBar = Snackbar.make(parentView, "$message", Snackbar.LENGTH_SHORT)
        val view = snackBar.view
        val params = view.layoutParams as FrameLayout.LayoutParams
        params.gravity = Gravity.CENTER
        view.layoutParams = params
        snackBar.animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
        snackBar.show()
    }

    companion object {
        fun newInstance(_homeFragment: HomeFragment) =
            LodgeUploadDetail().apply {
                homeFragment = _homeFragment
            }
    }
}