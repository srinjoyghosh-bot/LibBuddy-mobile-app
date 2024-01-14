package com.srinjoy.libbuddy.view.fragments.admin

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.srinjoy.libbuddy.R
import com.srinjoy.libbuddy.application.LibraryApplication
import com.srinjoy.libbuddy.view.activities.AuthActivity
import com.srinjoy.libbuddy.viewmodels.AdminSettingsViewModel

class AdminSettingsFragment : Fragment() {

    companion object {
        fun newInstance() = AdminSettingsFragment()
    }

    private lateinit var viewModel: AdminSettingsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_admin_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }



    private fun logout(){
        val application=(requireActivity().application as LibraryApplication)
        application.prefs.token=null
        application.prefs.isAdminLoggedIn=false
    }

}