package com.srinjoy.libbuddy.view.fragments

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import com.srinjoy.libbuddy.R
import com.srinjoy.libbuddy.application.LibraryApplication
import com.srinjoy.libbuddy.core.Constants
import com.srinjoy.libbuddy.databinding.FragmentBookDetailsBinding
import com.srinjoy.libbuddy.models.Admin
import com.srinjoy.libbuddy.models.Book
import com.srinjoy.libbuddy.view.activities.AddEditBookActivity
import com.srinjoy.libbuddy.view.activities.AdminMainActivity
import com.srinjoy.libbuddy.view.activities.StudentMainActivity
import com.srinjoy.libbuddy.viewmodels.BookDetailsViewModel
import com.srinjoy.libbuddy.viewmodels.BookDetailsViewModelFactory
import kotlin.reflect.typeOf

class BookDetailsFragment : Fragment() {

    private lateinit var mBinding: FragmentBookDetailsBinding

    private val mViewModel: BookDetailsViewModel by viewModels {
        BookDetailsViewModelFactory(
            bookRepository = (requireActivity().application as LibraryApplication).bookRepository,
            studentRepository = (requireActivity().application as LibraryApplication).studentRepository,
            adminRepository = (requireActivity().application as LibraryApplication).adminRepository,
            activity = requireActivity()
        )
    }

    private lateinit var mBook: Book.Book
    private var mProgressDialog: Dialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentBookDetailsBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBook = arguments?.getParcelable("book", Book.Book::class.java)!!

        //setActionBarTitle(mBook.id)

        setBookInfoIntoUI()

        setUpLoader()

        viewModelObservers()

        mBinding.srlBookDetails.setOnRefreshListener {
            mViewModel.getBook(mBook.id!!, false)
        }


    }

    private fun setBookInfoIntoUI() {
        mBinding.tvBookName.text = mBook.name
        mBinding.tvBookId.text = mBook.id
        mBinding.tvPublisher.text = buildString {
            append("Publisher ")
            append(mBook.publisher)
        }
        mBinding.tvBookDescription.text = mBook.description
        mBinding.tvAuthor.text = mBook.author

//        when {
//            (activity?.application as LibraryApplication?)!!.prefs.isStudentLoggedIn ->
//                makeRequestButtonFunctional()
//            (activity?.application as LibraryApplication?)!!.prefs.isAdminLoggedIn ->
//                mBinding.btnBorrowRequest.visibility = View.GONE
//        }

        when (requireActivity()) {
            is StudentMainActivity -> makeRequestButtonFunctional()
            is AdminMainActivity -> mBinding.btnBorrowRequest.visibility = View.GONE
        }


    }

    private fun makeRequestButtonFunctional() {
        mBinding.btnBorrowRequest.visibility = View.VISIBLE
        mBinding.btnBorrowRequest.isEnabled = mBook.available
        mBinding.btnBorrowRequest.setOnClickListener {
            if (it.isEnabled) {
                mViewModel.requestBorrow(mBook.id!!, this@BookDetailsFragment)
            } else {
                Toast.makeText(
                    requireActivity(),
                    getString(R.string.msg_book_unavailable),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    private fun viewModelObservers() {
        mViewModel.loading.observe(viewLifecycleOwner) { value ->
            value?.let {
                if (value) {
                    showLoader()
                } else {
                    stopLoader()
                }
            }
        }
        mViewModel.success.observe(viewLifecycleOwner) { value ->
            value?.let {
                mBinding.srlBookDetails.isRefreshing = false
                mBook = mViewModel.book.value!!
                setBookInfoIntoUI()
            }
        }
        mViewModel.bookRequestSuccess.observe(viewLifecycleOwner) { value ->
            value?.let {
                if (value) {
                    mBinding.btnBorrowRequest.isEnabled = false
                    Toast.makeText(
                        requireActivity(),
                        getString(R.string.msg_book_requested),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        mViewModel.error.observe(viewLifecycleOwner) { value ->
            value?.let {
                if (value) {
                    mBinding.srlBookDetails.isRefreshing = false
                    Toast.makeText(
                        requireActivity(),
                        mViewModel.errorMessage.value,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        }
    }

    private fun setActionBarTitle(title: String) {
        if (requireActivity() is StudentMainActivity) {
            (requireActivity() as StudentMainActivity?)!!.setActionBarTitle(title)
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if ((requireActivity().application as LibraryApplication).prefs.isAdminLoggedIn) {
            Log.i("Inflate ho rha", "true")
            inflater.inflate(R.menu.admin_book_details, menu)
        }
        Log.i(
            "Admin logged in",
            (requireActivity().application as LibraryApplication).prefs.isAdminLoggedIn.toString()
        )
//        if (requireActivity() is AdminMainActivity) {
//            Log.i("Inflate ho rha", "true")
//            inflater.inflate(R.menu.admin_book_details, menu)
//        }

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {

            R.id.action_delete_book -> {
                Toast.makeText(requireActivity(), "Delete Book", Toast.LENGTH_SHORT).show()
                true
            }

            R.id.action_edit_book -> {
                val intent = Intent(requireActivity(), AddEditBookActivity::class.java)
                intent.putExtra(Constants.EXTRA_BOOK_DETAILS, mBook)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mViewModel.getBook(arguments?.getParcelable("book", Book.Book::class.java)!!.id!!)
    }
}