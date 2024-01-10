package com.srinjoy.libbuddy.view.fragments.admin

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.srinjoy.libbuddy.R
import com.srinjoy.libbuddy.viewmodels.AdminRequestsViewModel

class AdminRequestsFragment : Fragment() {

    companion object {
        fun newInstance() = AdminRequestsFragment()
    }

    private lateinit var viewModel: AdminRequestsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_admin_requests, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AdminRequestsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}