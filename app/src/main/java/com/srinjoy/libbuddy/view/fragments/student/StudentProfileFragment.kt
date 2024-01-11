package com.srinjoy.libbuddy.view.fragments.student

import android.app.Dialog
import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.srinjoy.libbuddy.R
import com.srinjoy.libbuddy.application.LibraryApplication
import com.srinjoy.libbuddy.databinding.FragmentStudentProfileBinding
import com.srinjoy.libbuddy.models.Book
import com.srinjoy.libbuddy.models.Student
import com.srinjoy.libbuddy.view.adapters.IssueHistoryAdapter
import com.srinjoy.libbuddy.viewmodels.StudentProfileViewModel
import com.srinjoy.libbuddy.viewmodels.StudentProfileViewModelFactory

class StudentProfileFragment : Fragment() {


    private val mViewModel: StudentProfileViewModel by viewModels {
        StudentProfileViewModelFactory((requireActivity().application as LibraryApplication).studentRepository)
    }

    private lateinit var mBinding: FragmentStudentProfileBinding
    private lateinit var mIssueHistoryAdapter: IssueHistoryAdapter
    private var mProgressDialog: Dialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentStudentProfileBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mIssueHistoryAdapter = IssueHistoryAdapter(this@StudentProfileFragment)
        mBinding.rvHistory.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        mBinding.rvHistory.adapter = mIssueHistoryAdapter

        setUpLoader()

        viewModelObservers()

        mBinding.srlStudentProfile.setOnRefreshListener {
            mViewModel.getProfile(this@StudentProfileFragment, false)
        }

        Log.i("On view created", "called")
    }

    private fun viewModelObservers() {
        mViewModel.loading.observe(viewLifecycleOwner) { value ->
            value.let {
                if (value) {
                    showLoader()
                } else {
                    stopLoader()
                }
            }
        }
        mViewModel.success.observe(viewLifecycleOwner) { value ->
            value.let {
                if (value) {
                    mBinding.srlStudentProfile.isRefreshing = false
                    setInfoIntoUI(
                        mViewModel.student.value!!,
                        mViewModel.fine.value!!,
                        mViewModel.history.value!!
                    )
                }
            }
        }

        mViewModel.error.observe(viewLifecycleOwner) { value ->
            value?.let {
                if (value) {
                    mBinding.srlStudentProfile.isRefreshing = false
                    Toast.makeText(
                        requireActivity(),
                        mViewModel.errorMessage.value,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun setInfoIntoUI(
        student: Student.Student,
        fine: Double,
        history: List<Book.IssueDetails>
    ) {
        mBinding.tvStudentName.text = student.name
        mBinding.tvStudentId.text = student.id ?: student.enrollment_id
        mBinding.tvFineValue.text = getString(R.string.val_fine, fine.toString())
        if (history.isNotEmpty()) {
            mIssueHistoryAdapter.setHistory(history)
            mBinding.rvHistory.visibility = View.VISIBLE
            mBinding.tvNoIssues.visibility = View.GONE
        } else {
            mBinding.rvHistory.visibility = View.GONE
            mBinding.tvNoIssues.visibility = View.VISIBLE
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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.i("On attach", "called")
        if (mViewModel.student.value == null)
            mViewModel.getProfile(this@StudentProfileFragment)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("On create", "called")
    }
}