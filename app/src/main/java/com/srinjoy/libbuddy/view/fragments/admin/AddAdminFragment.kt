package com.srinjoy.libbuddy.view.fragments.admin

import android.app.Dialog
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.srinjoy.libbuddy.R
import com.srinjoy.libbuddy.application.LibraryApplication
import com.srinjoy.libbuddy.databinding.FragmentAddAdminBinding
import com.srinjoy.libbuddy.viewmodels.admin.AddAdminViewModel
import com.srinjoy.libbuddy.viewmodels.admin.AddAdminViewModelFactory

class AddAdminFragment : Fragment() {


    private val mViewModel: AddAdminViewModel by viewModels {
        AddAdminViewModelFactory((requireActivity().application as LibraryApplication).adminRepository)
    }
    private lateinit var mBinding: FragmentAddAdminBinding
    private var mProgressDialog: Dialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentAddAdminBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpLoader()
        viewModelObservers()
        mBinding.btnAddAdmin.setOnClickListener {
            submit()
        }


    }

    private fun submit() {
        val email = mBinding.etEmailAdmin.text.toString().trim { it <= ' ' }
        val password = mBinding.etPasswordAdmin.text.toString().trim { it <= ' ' }

        when {
            TextUtils.isEmpty(email) -> Toast.makeText(
                requireActivity(),
                getString(R.string.msg_provide_email),
                Toast.LENGTH_SHORT
            ).show()
            TextUtils.isEmpty(password) -> Toast.makeText(
                requireActivity(),
                getString(R.string.msg_provide_password),
                Toast.LENGTH_SHORT
            ).show()
            else -> mViewModel.addAdmin(email, password, this@AddAdminFragment)
        }
    }

    private fun setUpLoader() {
        mProgressDialog = Dialog(requireActivity())
        mProgressDialog!!.setContentView(R.layout.dialog_custom_progress)
        mProgressDialog!!.setCancelable(false)
        mProgressDialog!!.setCanceledOnTouchOutside(false)
    }

    private fun showLoader() {
        mProgressDialog?.show()
    }

    private fun stopLoader() {
        mProgressDialog?.dismiss()
    }

    private fun viewModelObservers() {
        mViewModel.loading.observe(viewLifecycleOwner) { value ->
            value?.let {
                if (it) {
                    showLoader()
                } else {
                    stopLoader()
                }
            }
        }
        mViewModel.success.observe(viewLifecycleOwner) { value ->
            value?.let {
                if (it) {
                    Toast.makeText(
                        requireActivity(),
                        getString(R.string.msg_admin_added),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        mViewModel.error.observe(viewLifecycleOwner) { value ->
            value?.let {
                if (it) {
                    Toast.makeText(
                        requireActivity(),
                        mViewModel.errorMessage.value,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }


}