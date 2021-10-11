package com.column.roar.payment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.column.roar.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class PaySuccessful : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        showSuccessDialog()
        return inflater.inflate(R.layout.fragment_pay_successful, container, false)
    }

    @SuppressLint("InflateParams")
    fun showSuccessDialog() {
        MaterialAlertDialogBuilder(requireContext()).apply {
            val inflater = LayoutInflater.from(requireContext())
            val view = inflater.inflate(R.layout.successful_dialog,null)
            setView(view)
            setCancelable(false)
            setNeutralButton("Close") { dialog, _ ->
                dialog.dismiss()
                findNavController().popBackStack(R.id.lodgeDetail,false)
            }
            show()
        }
    }
}