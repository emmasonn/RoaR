package com.column.roar.payment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.column.roar.R


class PaymentPager : Fragment() {

    private lateinit var paymentViewPager: ViewPager2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_payment_pager, container, false)
        paymentViewPager = view.findViewById<ViewPager2>(R.id.paymentViewPager)
        paymentViewPager.isUserInputEnabled = false

        paymentViewPager.adapter = PaymentPagerAdapter(this, this)
        return view
    }

    fun moveForward(currentItem: Int) {
        paymentViewPager.setCurrentItem(currentItem, true)
    }

    class PaymentPagerAdapter(fragment: Fragment, private val paymentPager: PaymentPager)
        : FragmentStateAdapter(fragment) {

        override fun getItemCount(): Int = 4

        override fun createFragment(position: Int): Fragment =
            when(position) {
                0 -> PaymentFragment.newInstance(paymentPager)
                1 -> ViaAccount.newInstance(paymentPager)
                2 -> AccountVerification.newInstance(paymentPager)
                else -> PaySuccessful()
            }

    }
}