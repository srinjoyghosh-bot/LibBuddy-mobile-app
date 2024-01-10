package com.srinjoy.libbuddy.view.fragments.admin

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.srinjoy.libbuddy.R
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AdminSettingsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}