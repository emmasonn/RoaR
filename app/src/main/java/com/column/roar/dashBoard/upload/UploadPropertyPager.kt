package com.column.roar.dashBoard.upload

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.column.roar.R


class UploadPropertyPager : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_upload_pager, container, false)
    }

    class UploadPagerAdapter(fragment: Fragment, private val editLodgePager: EditLodgePager
    )
        : FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int = 3
        override fun createFragment(position: Int): Fragment =
            when(position) {
                0 -> EditLodgeFragment.newInstance(editLodgePager)
                1 -> LodgePictures.newInstance(editLodgePager)
                else -> LodgeUploadImage.newInstance(editLodgePager)
            }
    }
}