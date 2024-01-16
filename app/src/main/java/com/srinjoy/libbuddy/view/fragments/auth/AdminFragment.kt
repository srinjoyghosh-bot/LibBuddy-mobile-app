package com.srinjoy.libbuddy.view.fragments.auth

import android.app.Dialog
import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.srinjoy.libbuddy.R
import com.srinjoy.libbuddy.databinding.FragmentAdminBinding
import com.srinjoy.libbuddy.view.activities.AdminMainActivity
import com.srinjoy.libbuddy.viewmodels.auth.AdminViewModel

class AdminFragment : Fragment() {


    private lateinit var mViewModel: AdminViewModel
    private var mBinding: FragmentAdminBinding? = null
    private var mProgressDialog: Dialog? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentAdminBinding.inflate(inflater, container, false)
        return mBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel = ViewModelProvider(this)[AdminViewModel::class.java]
        mBinding!!.tvGoToStudent.setOnClickListener {
            findNavController().navigate(R.id.action_adminFragment_to_studentFragment)
        }
        mBinding!!.btnLoginAdmin.setOnClickListener {
            checkFormData()
        }
        setUpLoader()
        mViewModel.loading.observe(viewLifecycleOwner) { value ->
            value?.let {
                if (value) {
                    showLoader()
                } else {
                    stopLoader()
                }
            }
        }

        mViewModel.error.observe(viewLifecycleOwner) { value ->
            value?.let {
                if (value) {
                    Toast.makeText(
                        requireActivity(),
                        mViewModel.errorMessage.value.toString(),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
        }

        mViewModel.success.observe(viewLifecycleOwner){value->
            value?.let {
                if(value){
                    val intent=Intent(requireActivity(),AdminMainActivity::class.java)
                    requireActivity().startActivity(intent)
                }
            }

        }
    }

    private fun checkFormData() {
        val email = mBinding!!.etEmailLogin.text.toString().trim { it <= ' ' }
        val password = mBinding!!.etPasswordLogin.text.toString().trim { it <= ' ' }
        when {
            TextUtils.isEmpty(email) -> {
                Toast.makeText(requireActivity(), "Give email", Toast.LENGTH_SHORT).show()
            }
            TextUtils.isEmpty(password) -> {
                Toast.makeText(requireActivity(), "Give password", Toast.LENGTH_SHORT).show()
            }
            else -> {
                mViewModel.login(email, password,requireActivity())
            }
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
}