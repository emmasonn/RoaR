package com.column.roar.dashBoard

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.column.roar.MainActivity
import com.column.roar.R
import com.column.roar.cloudModel.FirebaseUser
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class SubscriptionFragment : Fragment() {

    private lateinit var subscribeType: TextView
    private lateinit var subscribePrice: TextView
    private lateinit var subscribeSlot: TextView
    private lateinit var sharedPref: SharedPreferences
    private lateinit var remainingDay: String

    val client: FirebaseUser by lazy {
        arguments?.get("Client") as FirebaseUser
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPref = (activity as MainActivity).sharedPref
        remainingDay = sharedPref.getLong("remaining", 0).toString()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_subscription, container, false)
        val subBack = view.findViewById<ImageView>(R.id.subBack)
        subscribeType = view.findViewById(R.id.subscribeType)
        subscribePrice = view.findViewById(R.id.subscribePrice)
        subscribeSlot = view.findViewById(R.id.subscribeSlot)
        val remainingTime = view.findViewById<TextView>(R.id.remainingTime)
        val professionalBtn = view.findViewById<MaterialButton>(R.id.professionalBtn)
        val personalBtn = view.findViewById<MaterialButton>(R.id.personalBtn)
        remainingTime.text = getString(R.string.remaining_day,remainingDay)

        personalBtn.setOnClickListener {
            showSignDialog()
        }

        professionalBtn.setOnClickListener {
           showSignDialog()
        }

        subBack.setOnClickListener {
            findNavController().popBackStack()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(client.account != null && client.account == "Realtor") {
            setUpEstatePlan(client.slots)

        }else if(client.account == "Business") {
            setUpBusinessPlan(client.slots)
        }
    }

    private fun setUpEstatePlan(slots: String?) {
        when (slots) {
            "1" -> {
                subscribeSlot.text = getString(R.string.business_default_slot)
                subscribePrice.text = getString(R.string.business_default_price)
                subscribeType.text = getString(R.string.business_default)
            }
            "10" -> {
                subscribeSlot.text = getString(R.string.business_personal_slot)
                subscribePrice.text = getString(R.string.business_personal_price)
                subscribeType.text = getString(R.string.business_personal)
            }
            "20" -> {
                subscribeSlot.text = getString(R.string.business_pro_slot)
                subscribePrice.text = getString(R.string.business_pro_price)
                subscribeType.text = getString(R.string.business_pro)
            }
        }
    }

    private fun setUpBusinessPlan(slots: String?) {
        when (slots) {
            "1" -> {
                subscribeSlot.text = getString(R.string.estate_default_slot)
                subscribePrice.text = getString(R.string.estate_default_price)
                subscribeType.text = getString(R.string.estate_default)
            }

            "10"  -> {
                subscribeSlot.text = getString(R.string.estate_personal_slot)
                subscribePrice.text = getString(R.string.estate_personal_price)
                subscribeType.text = getString(R.string.estate_personal)
            }

            "20" -> {
                subscribeSlot.text = getString(R.string.estate_pro_slot)
                subscribePrice.text = getString(R.string.estate_pro_price)
                subscribeType.text = getString(R.string.estate_pro)
            }
        }
    }

    private fun showSignDialog() {
        MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle("Connect with an admin for more detail on Subscription")
            setPositiveButton("Continue") { dialog , _ ->
                dialog.dismiss()
                findNavController().navigateUp()
            }
            show()
        }
    }

}