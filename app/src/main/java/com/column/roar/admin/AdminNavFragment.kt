package com.column.roar.admin

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.findNavController
import com.column.roar.MainActivity
import com.column.roar.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView

class AdminNavFragment : Fragment() {

    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPref = (activity as MainActivity).sharedPref
        userLoggedState(true)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_admin_nav, container, false)
        val drawer = view.findViewById<DrawerLayout>(R.id.drawerNav)
        val menu = view.findViewById<ImageView>(R.id.adminMenu)
        val navView = view.findViewById<NavigationView>(R.id.nav_view)

        menu.setOnClickListener {
            drawer.open()
        }

//        val navController = Navigation.findNavController(view.findViewById(R.id.adminNavHost))
//        navController.setGraph(R.navigation.admin_nav)
//        AppBarConfiguration(navController.graph,drawer)
//
//        navView.setNavigationItemSelectedListener { menuItem ->
//
//            when (menuItem.itemId) {
//
//                R.id.exitProfile -> {
//                    findNavController().popBackStack(R.id.homeFragment,false)
//                    true
//                }
//
//                R.id.adminFragment2 -> {
//                    drawer.close()
//                    navController.popBackStack(R.id.adminFragment2,false)
//                    true
//                }
//                R.id.businessFragment2 -> {
//                    drawer.close()
//                    navController.navigate(R.id.businessFragment2)
//                    true
//                }
//
//                R.id.realtorFragment -> {
//                    drawer.close()
//                    navController.navigate(R.id.realtorFragment)
//                    true
//                }
//
//                R.id.admins -> {
//                    drawer.close()
//                    true
//                }
//
//                R.id.setUpFragment -> {
//                    drawer.close()
//                    navController.navigate(R.id.setUpFragment2)
//                    true
//                }
//
//                R.id.manageAccount -> {
//                    drawer.close()
//                    navController.navigate(R.id.manageAccount2)
//                    true
//                }
//
//                R.id.signOut -> {
//                    showSignOutDialog()
//                    true
//                }
//
//                else -> {
//                    true
//                }
//            }
//        }

        return view
    }

    private fun userLoggedState(state: Boolean) {
        with(sharedPref.edit()){
            putBoolean("logged", state)
            apply()
        }
    }

    private fun showSignOutDialog() {
        MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle("You're About to Sign out")
            setPositiveButton("Okay") { dialog , _ ->
                dialog.dismiss()
                userLoggedState(false)
                findNavController().popBackStack(R.id.homeFragment,false)
            }
            setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            show()
        }
    }

}