package com.column.roar.dashBoard.upload

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.column.roar.R
import com.column.roar.cloudModel.FirebaseLodge

class EditLodgePager : Fragment() {

    private lateinit var pager: ViewPager2

    val lodgesData: FirebaseLodge by lazy {
        arguments?.get("Lodge") as FirebaseLodge
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_edit_lodge_pager, container, false)
        pager = view.findViewById<ViewPager2>(R.id.editLodgeViewPager)
        val adapter = EditPagerAdapter(this,this)
        pager.adapter = adapter
        return view
    }

    fun moveForward() {
        pager.currentItem = pager.currentItem + 1
    }

    fun moveBackward() {
        pager.currentItem = pager.currentItem - 1
    }

    class EditPagerAdapter(fragment: Fragment,
                           private val editLodgePager: EditLodgePager
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