package com.srinjoy.libbuddy.view.activities

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.srinjoy.libbuddy.R
import com.srinjoy.libbuddy.databinding.ActivityAdminMainBinding

class AdminMainActivity : AppCompatActivity() {

    private lateinit var mBinding:ActivityAdminMainBinding
    private lateinit var mNavController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding= ActivityAdminMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mNavController=findNavController(R.id.admin_nav_host_fragment)

        val appBarConfiguration= AppBarConfiguration(setOf(
            R.id.adminBooksFragment,
            R.id.adminAllStudentsFragment,
            R.id.adminRequestsFragment,
            R.id.adminSettingsFragment
        ))

        setupActionBarWithNavController(mNavController,appBarConfiguration)

        mBinding.adminNavView.setupWithNavController(mNavController)

        supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(mNavController, null)
    }

    fun hideBottomNavigationView() {
        mBinding.adminNavView.clearAnimation()
        mBinding.adminNavView.animate()
            .translationY(mBinding.adminNavView.height.toFloat()).duration = 300
        mBinding.adminNavView.visibility = View.GONE
    }


    fun showBottomNavigationView() {
        mBinding.adminNavView.clearAnimation()
        mBinding.adminNavView.animate().translationY(0f).duration = 300
        mBinding.adminNavView.visibility = View.VISIBLE
    }

}