package com.srinjoy.libbuddy.view.fragments.admin

import android.app.Dialog
import androidx.lifecycle.ViewModelProvider
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
import com.srinjoy.libbuddy.databinding.FragmentAdminBookReturnBinding
import com.srinjoy.libbuddy.viewmodels.AdminBookReturnViewModel
import com.srinjoy.libbuddy.viewmodels.AdminBookReturnViewModelFactory

class AdminBookReturnFragment : Fragment() {


    private val mViewModel: AdminBookReturnViewModel by viewModels {
        AdminBookReturnViewModelFactory((requireActivity().application as LibraryApplication).adminRepository)
    }

    private lateinit var mBinding: FragmentAdminBookReturnBinding
    private var mProgressDialog: Dialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentAdminBookReturnBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpLoader()
        viewModelObservers()
        mBinding.btnReturnBook.setOnClickListener {
            submit()
        }
    }

    private fun submit() {
        val bookId = mBinding.etBookId.text.toString().trim { it <= ' ' }
        val studentId = mBinding.etStudentId.text.toString().trim { it <= ' ' }

        when {
            TextUtils.isEmpty(bookId) -> Toast.makeText(
                requireActivity(),
                getString(R.string.msg_provide_book_id),
                Toast.LENGTH_SHORT
            ).show()
            TextUtils.isEmpty(studentId) -> Toast.makeText(
                requireActivity(),
                getString(R.string.msg_provide_student_id),
                Toast.LENGTH_SHORT
            ).show()
            else -> mViewModel.returnBook(bookId,studentId, this@AdminBookReturnFragment)
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
                        getString(R.string.msg_book_returned),
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