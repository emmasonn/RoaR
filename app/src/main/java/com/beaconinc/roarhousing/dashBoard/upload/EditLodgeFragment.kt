package com.beaconinc.roarhousing.dashBoard.upload

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.beaconinc.roarhousing.R
import com.beaconinc.roarhousing.databinding.FragmentEditLodgeBinding


class EditLodgeFragment : Fragment() {

    private lateinit var editLodgePager: EditLodgePager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val binding = FragmentEditLodgeBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.lodgeDetail = editLodgePager.lodgesData

            binding.editLodge.setOnClickListener {
                val bundle = bundleOf("Lodge" to editLodgePager.lodgesData)
                findNavController().navigate(R.id.lodgeDetailUpload,bundle)
            }

        binding.pagerBack.setOnClickListener {
           findNavController().navigateUp()
        }

        return binding.root
    }

    companion object {
        fun newInstance(_editLodgePager: EditLodgePager) =
            EditLodgeFragment().apply {
                editLodgePager = _editLodgePager
            }
    }
}