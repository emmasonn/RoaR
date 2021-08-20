package com.beaconinc.roarhousing.home

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.beaconinc.roarhousing.MainActivity
import com.beaconinc.roarhousing.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class HomeFragment : Fragment() {

    private lateinit var homePager: ViewPager2
    private lateinit var callback: ViewPager2.OnPageChangeCallback
    private var isBackDropVisible = false
    private lateinit var chipGroup: ChipGroup
    private lateinit var appBar: Toolbar
    private lateinit var backDrop: LinearLayout
    private var counter = 0

//    private val chipsCategory = listOf<String>("Map","Odinigwe","Odim","HillTop","Green House",
//        "Beach", "Behind Flat","First Gate")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        homePager = view.findViewById(R.id.homePager)
        backDrop = view.findViewById<LinearLayout>(R.id.backDrop)
        appBar = view.findViewById<Toolbar>(R.id.app_bar)
        val luxBtn = view.findViewById<MaterialButton>(R.id.luxBtn)
        val accountBtn = view.findViewById<MaterialButton>(R.id.myAccountBtn)
        val cheapBtn = view.findViewById<MaterialButton>(R.id.cheapBtn)

        chipGroup = view.findViewById<ChipGroup>(R.id.chipGroup)

        setUpChips()
        initPagerCallback()
        homePager.isUserInputEnabled = false //dis-activate viewpager swiping
        val position = (activity as MainActivity).chipState

        homePager.setCurrentItem(position,false)
        (activity as AppCompatActivity).setSupportActionBar(appBar)
        appBar.setNavigationOnClickListener(NavigationIconClickListener(
            requireActivity(),
            homePager,
            AccelerateDecelerateInterpolator(),
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_menu),
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_nav_close)
        ) {
            if (!isBackDropVisible) {
                backDrop.visibility = View.VISIBLE
                isBackDropVisible = true
            } else {
                backDrop.visibility = View.GONE
                isBackDropVisible = false
            }
        })

        luxBtn.setOnClickListener {
            isBackDropVisible = false
            val bundle = bundleOf("choice" to "Classic")
            val action = R.id.action_homeFragment_to_filterFragment
            findNavController().navigate(action, bundle)
        }

        cheapBtn.setOnClickListener {
            isBackDropVisible = false
            val bundle = bundleOf("choice" to "Simple")
            val action = R.id.action_homeFragment_to_filterFragment
            findNavController().navigate(action,bundle)
        }

        accountBtn.setOnClickListener {
            isBackDropVisible = false
            val action = R.id.action_homeFragment_to_profileFragment
            findNavController().navigate(action)
        }
        return view
    }

    override fun onStop() {
        super.onStop()
        isBackDropVisible = false
    }

    private fun setUpChips() {
        val chipsCategory = resources.getStringArray(R.array.lodges_location)

        val adapter = HomePager(
            this, this, chipsCategory
        )
        homePager.adapter = adapter

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
                    val query = (view as Chip).tag as Int

                    view.isChecked = true
                    (activity as MainActivity).chipState = query

                    moveToNextPager(query)
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

    private fun moveToNextPager(pageNumber: Int) {
        //make sure to put smoothScroll to false to prevent double calling The Fragment
        homePager.setCurrentItem(pageNumber, false)//increments the pager.
    }

    private fun initPagerCallback() {
        callback = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
            }
        }
    }

    class HomePager(
        val fragment: Fragment,
        private val homeFragment: HomeFragment,
        private val cats: Array<String>
    ) : FragmentStateAdapter(fragment) {

        override fun getItemCount() = cats.size
        override fun createFragment(position: Int): Fragment =
            LodgesFragment.newInstance(homeFragment, cats[position])

    }

}