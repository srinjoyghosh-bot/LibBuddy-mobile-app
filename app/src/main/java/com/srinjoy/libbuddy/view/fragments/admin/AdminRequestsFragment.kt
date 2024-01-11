package com.srinjoy.libbuddy.view.fragments.admin

import android.app.Dialog
import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.srinjoy.libbuddy.R
import com.srinjoy.libbuddy.application.LibraryApplication
import com.srinjoy.libbuddy.databinding.FragmentAdminRequestsBinding
import com.srinjoy.libbuddy.models.Book
import com.srinjoy.libbuddy.view.adapters.IssueHistoryAdapter
import com.srinjoy.libbuddy.view.adapters.SwipeCallback
import com.srinjoy.libbuddy.viewmodels.AdminRequestsViewModel
import com.srinjoy.libbuddy.viewmodels.AdminRequestsViewModelFactory

class AdminRequestsFragment : Fragment() {

    private val mViewModel: AdminRequestsViewModel by viewModels {
        AdminRequestsViewModelFactory((requireActivity().application as LibraryApplication).adminRepository)
    }
    private lateinit var mBinding: FragmentAdminRequestsBinding
    private var mProgressDialog: Dialog? = null
    private lateinit var mIssueHistoryAdapter: IssueHistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentAdminRequestsBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mIssueHistoryAdapter = IssueHistoryAdapter(this@AdminRequestsFragment)
        mBinding.rvAllRequests.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        mBinding.rvAllRequests.adapter = mIssueHistoryAdapter

        val itemTouchHelper = ItemTouchHelper(
            SwipeCallback(
                onAccept = ::approveBorrow,
                onReject = ::rejectBorrow,
                context = context!!,
            )
        )
        itemTouchHelper.attachToRecyclerView(mBinding.rvAllRequests)

        mBinding.srlAllRequests.setOnRefreshListener {
            mViewModel.getRequests(this@AdminRequestsFragment, false)
        }

        setUpLoader()
        viewModelObservers()
    }

    private fun viewModelObservers() {
        mViewModel.loading.observe(viewLifecycleOwner) { value ->
            value?.let {
                if (value)
                    showLoader()
                else
                    stopLoader()
            }
        }
        mViewModel.success.observe(viewLifecycleOwner) { value ->
            value?.let {
                val requests = mViewModel.requests.value ?: listOf<Book.IssueDetails>()
                mBinding.srlAllRequests.isRefreshing = false
                if (requests.isEmpty()) {
                    mBinding.tvNoRequests.visibility = View.VISIBLE
                    mBinding.rvAllRequests.visibility = View.GONE
                } else {
                    mBinding.tvNoRequests.visibility = View.GONE
                    mBinding.rvAllRequests.visibility = View.VISIBLE
                    mIssueHistoryAdapter.setHistory(requests)
                }
            }
        }
        mViewModel.approveSuccess.observe(viewLifecycleOwner) { value ->
            value?.let {
                mIssueHistoryAdapter.removeItem(mViewModel.borrowPosition.value!!)
                Toast.makeText(
                    requireActivity(),
                    getString(R.string.msg_request_approved),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        mViewModel.rejectSuccess.observe(viewLifecycleOwner) { value ->
            value?.let {
                mIssueHistoryAdapter.removeItem(mViewModel.borrowPosition.value!!)
                Toast.makeText(
                    requireActivity(),
                    getString(R.string.msg_request_rejected),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        mViewModel.error.observe(viewLifecycleOwner) { value ->
            value?.let {
                if (value) {
                    mBinding.srlAllRequests.isRefreshing = false
                    Toast.makeText(
                        requireActivity(),
                        mViewModel.errorMessage.value.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun approveBorrow(position: Int) {
        val issue = mIssueHistoryAdapter.getItemByPosition(position)
        issue?.let {
            mViewModel.approveBorrow(it.id.toString(), position, this@AdminRequestsFragment)
        }

    }

    private fun rejectBorrow(position: Int) {
        val issue = mIssueHistoryAdapter.getItemByPosition(position)
        issue?.let {
            mViewModel.rejectBorrow(it.id.toString(), position, this@AdminRequestsFragment)
        }
    }

    fun updateBorrowsListInViewModel(borrows: List<Book.IssueDetails>) {
        mViewModel.requests.value = borrows
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
        if (mViewModel.requests.value == null) {
            mViewModel.getRequests(this@AdminRequestsFragment)
        }
        super.onAttach(context)
    }

}