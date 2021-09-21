package com.beaconinc.roarhousing.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import android.widget.ImageView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.beaconinc.roarhousing.R
import com.beaconinc.roarhousing.dashBoard.SettingsFragment
import com.beaconinc.roarhousing.payment.*


class SettingsPager : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        val settingBack = view.findViewById<ImageView>(R.id.settingsBack)
        val viewPager = view.findViewById<ViewPager2>(R.id.settingsViewPager)
        val adapter = SettingsPagerAdapter(this)
        viewPager.adapter = adapter
        settingBack.setOnClickListener {
            findNavController().navigateUp()
        }
        return view
    }

    class SettingsPagerAdapter(fragment: Fragment)
        : FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int = 1
        override fun createFragment(position: Int): Fragment = SettingsFragment()
    }

}