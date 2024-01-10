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
import com.srinjoy.libbuddy.databinding.FragmentStudentBinding
import com.srinjoy.libbuddy.view.activities.StudentMainActivity
import com.srinjoy.libbuddy.viewmodels.StudentViewModel

class StudentFragment : Fragment() {


    private var mBinding: FragmentStudentBinding? = null

    private lateinit var mViewModel: StudentViewModel

    private var mProgressDialog: Dialog? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentStudentBinding.inflate(inflater, container, false)
        return mBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel = ViewModelProvider(this)[StudentViewModel::class.java]
        mBinding?.tvGoToAdmin?.setOnClickListener {
            findNavController().navigate(R.id.action_studentFragment_to_adminFragment)
        }
        mBinding!!.tvGoToLogin.setOnClickListener {
            showLoginForm()
        }
        mBinding!!.tvGoToRegister.setOnClickListener {
            showRegisterForm()
        }
        mBinding!!.btnLogin.setOnClickListener {
            checkLoginForm()
        }
        mBinding!!.btnRegister.setOnClickListener {
            checkRegisterForm()
        }

        mProgressDialog = Dialog(requireActivity())
        mProgressDialog!!.setContentView(R.layout.dialog_custom_progress)
        mProgressDialog!!.setCancelable(false)
        mProgressDialog!!.setCanceledOnTouchOutside(false)

        mViewModel.loading.observe(viewLifecycleOwner) { value ->
            value?.let {
                if (value) {
                    showLoader()
                } else {
                    stopLoader()
                }
            }
        }

        mViewModel.loginSuccess.observe(viewLifecycleOwner) { value ->
            value?.let {
                if (value) {
                    val intent = Intent(requireActivity(), StudentMainActivity::class.java)
                    requireActivity().startActivity(intent)
                }
            }
        }
        mViewModel.registerSuccess.observe(viewLifecycleOwner) { value ->
            value?.let {
                if (value) {
                    Toast.makeText(requireActivity(), "Student registered!", Toast.LENGTH_SHORT)
                        .show()
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

    }

    private fun checkLoginForm() {
        val id = mBinding!!.etEnrollmentLogin.text.toString().trim { it <= ' ' }
        val password = mBinding!!.etPasswordLogin.text.toString().trim { it <= ' ' }
        when {
            TextUtils.isEmpty(id) -> {
                Toast.makeText(requireActivity(), "Give enrollment", Toast.LENGTH_SHORT).show()
            }
            TextUtils.isEmpty(password) -> {
                Toast.makeText(requireActivity(), "Give password", Toast.LENGTH_SHORT).show()
            }
            else -> {
                mViewModel.login(id, password, requireActivity())

            }
        }
    }

    private fun checkRegisterForm() {
        val id = mBinding!!.etEnrollmentRegister.text.toString().trim { it <= ' ' }
        val password = mBinding!!.etPasswordRegister.text.toString().trim { it <= ' ' }
        val name = mBinding!!.etNameRegister.text.toString().trim { it <= ' ' }
        when {
            TextUtils.isEmpty(id) -> {
                Toast.makeText(requireActivity(), "Give enrollment", Toast.LENGTH_SHORT).show()
            }
            TextUtils.isEmpty(password) -> {
                Toast.makeText(requireActivity(), "Give password", Toast.LENGTH_SHORT).show()
            }
            TextUtils.isEmpty(name) -> {
                Toast.makeText(requireActivity(), "Give email", Toast.LENGTH_SHORT).show()
            }
            else -> {
                mViewModel.add(
                    id = id,
                    password = password,
                    name = name,
                    year = null,
                    branch = null,
                    degree = null
                )
            }
        }
    }

    private fun showLoginForm() {
        mBinding!!.llLogin.visibility = View.VISIBLE
        mBinding!!.llRegister.visibility = View.GONE

    }

    private fun showRegisterForm() {
        mBinding!!.llRegister.visibility = View.VISIBLE
        mBinding!!.llLogin.visibility = View.GONE
    }

    fun showLoader() {
        mProgressDialog?.show()
    }

    fun stopLoader() {
        mProgressDialog?.dismiss()
    }
}