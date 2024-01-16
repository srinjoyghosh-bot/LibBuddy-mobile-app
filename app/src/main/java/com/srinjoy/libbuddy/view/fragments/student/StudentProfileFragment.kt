package com.srinjoy.libbuddy.view.fragments.student

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.srinjoy.libbuddy.R
import com.srinjoy.libbuddy.application.LibraryApplication
import com.srinjoy.libbuddy.core.Constants
import com.srinjoy.libbuddy.databinding.FragmentStudentProfileBinding
import com.srinjoy.libbuddy.models.Book
import com.srinjoy.libbuddy.models.Student
import com.srinjoy.libbuddy.view.activities.AuthActivity
import com.srinjoy.libbuddy.view.adapters.IssueHistoryAdapter
import com.srinjoy.libbuddy.viewmodels.student.StudentProfileViewModel
import com.srinjoy.libbuddy.viewmodels.student.StudentProfileViewModelFactory

class StudentProfileFragment : Fragment() {


    private val mViewModel: StudentProfileViewModel by viewModels {
        StudentProfileViewModelFactory(
            (requireActivity().application as LibraryApplication).studentRepository,
            (requireActivity().application as LibraryApplication).adminRepository
        )
    }

    private lateinit var mBinding: FragmentStudentProfileBinding
    private lateinit var mIssueHistoryAdapter: IssueHistoryAdapter
    private var mProgressDialog: Dialog? = null
    private var mStudentID: String? = null
    private var isSelfProfile: Boolean = true

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
            if (isSelfProfile)
                mViewModel.getProfile(this@StudentProfileFragment, false)
            else
                mViewModel.getProfile(this@StudentProfileFragment, false, mStudentID)
        }

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (isSelfProfile)
            inflater.inflate(R.menu.menu_student_profile, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                logout()
                val intent = Intent(requireActivity(), AuthActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun logout() {
        val application = (requireActivity().application as LibraryApplication)
        application.prefs.token = null
        application.prefs.isStudentLoggedIn = false
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
        if (mViewModel.student.value == null) {
            mStudentID = arguments?.getString(Constants.EXTRA_STUDENT_ID)
            if (mStudentID != null) {
                isSelfProfile = false
                mViewModel.getProfile(this@StudentProfileFragment, id = mStudentID)
            } else
                mViewModel.getProfile(this@StudentProfileFragment)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("On create", "called")
    }
}