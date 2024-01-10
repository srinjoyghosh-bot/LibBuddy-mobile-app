package com.srinjoy.libbuddy.view.activities

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
import com.srinjoy.libbuddy.databinding.ActivityStudentMainBinding

class StudentMainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityStudentMainBinding
    private lateinit var mNavController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityStudentMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mNavController = findNavController(R.id.student_nav_host_fragment)

        val appBarConfiguration = AppBarConfiguration(setOf(R.id.nav_all_books, R.id.nav_profile))
        setupActionBarWithNavController(mNavController, appBarConfiguration)
        mBinding.studentNavView.setupWithNavController(mNavController)


    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(mNavController, null)
    }

    fun hideBottomNavigationView() {
        mBinding.studentNavView.clearAnimation()
        mBinding.studentNavView.animate()
            .translationY(mBinding.studentNavView.height.toFloat()).duration = 300
        mBinding.studentNavView.visibility = View.GONE
    }


    fun showBottomNavigationView() {
        mBinding.studentNavView.clearAnimation()
        mBinding.studentNavView.animate().translationY(0f).duration = 300
        mBinding.studentNavView.visibility = View.VISIBLE
    }

    fun setActionBarTitle(title: String) {
        actionBar?.let {
            it.title = title
        }
    }
}