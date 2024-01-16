package com.srinjoy.libbuddy.view.fragments.admin

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.srinjoy.libbuddy.R
import com.srinjoy.libbuddy.application.LibraryApplication
import com.srinjoy.libbuddy.databinding.FragmentAdminSettingsBinding
import com.srinjoy.libbuddy.view.activities.AuthActivity
import com.srinjoy.libbuddy.viewmodels.admin.AdminSettingsViewModel

class AdminSettingsFragment : Fragment() {



    private lateinit var viewModel: AdminSettingsViewModel

    private lateinit var mBinding:FragmentAdminSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding= FragmentAdminSettingsBinding.inflate(inflater,container,false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.cvLogout.setOnClickListener{
            logout()
        }
        mBinding.cvAddAdmin.setOnClickListener{
            findNavController().navigate(R.id.action_adminSettingsFragment_to_addAdminFragment)
        }
        mBinding.cvBookReturn.setOnClickListener{
            findNavController().navigate(R.id.action_adminSettingsFragment_to_adminBookReturnFragment)
        }
    }





    private fun logout(){
        val application=(requireActivity().application as LibraryApplication)
        application.prefs.token=null
        application.prefs.isAdminLoggedIn=false
        val intent = Intent(requireActivity(), AuthActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

}