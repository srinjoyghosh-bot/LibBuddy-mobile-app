package com.srinjoy.libbuddy.view.fragments.student

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.srinjoy.libbuddy.R
import com.srinjoy.libbuddy.application.LibraryApplication
import com.srinjoy.libbuddy.databinding.FragmentStudentBooksBinding
import com.srinjoy.libbuddy.models.Book
import com.srinjoy.libbuddy.view.activities.StudentMainActivity
import com.srinjoy.libbuddy.view.adapters.AllBooksAdapter
import com.srinjoy.libbuddy.viewmodels.student.StudentBooksViewModel
import com.srinjoy.libbuddy.viewmodels.student.StudentBooksViewModelFactory

class StudentBooksFragment : Fragment() {


    private lateinit var mBinding: FragmentStudentBooksBinding
    private lateinit var mBooksAdapter: AllBooksAdapter
    private var mProgressDialog: Dialog? = null
    private lateinit var mCircularProgressBar: ProgressBar

    private val mViewModel: StudentBooksViewModel by viewModels {
        StudentBooksViewModelFactory((requireActivity().application as LibraryApplication).bookRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentStudentBooksBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mCircularProgressBar = mBinding.pbCircularBooks

        mBinding.rvAllBooks.layoutManager = GridLayoutManager(requireActivity(), 2)

        mBooksAdapter = AllBooksAdapter(this@StudentBooksFragment)


        mBinding.rvAllBooks.adapter = mBooksAdapter

        mBinding.srlAllBooks.setOnRefreshListener {
            mViewModel.getAllBooks(showLoader = false)
        }

//        Log.i("On view c", "called")

//        setUpLoader()

//        mViewModel.getAllBooks()

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
                val books: List<Book.Book> = mViewModel.books.value ?: listOf()
                mBinding.srlAllBooks.isRefreshing = false
                if (books.isEmpty()) {
                    mBinding.tvNoBooks.visibility = View.VISIBLE
                    mBinding.rvAllBooks.visibility = View.GONE

                } else {
                    mBinding.tvNoBooks.visibility = View.GONE
                    mBinding.rvAllBooks.visibility = View.VISIBLE
                    mBooksAdapter.setBooks(books)
                }


            }

        }

        mViewModel.error.observe(viewLifecycleOwner) { value ->
            value?.let {
                if (value) {
                    mBinding.srlAllBooks.isRefreshing = false
                    Toast.makeText(
                        context,
                        mViewModel.errorMessage.value.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_search_book, menu)
        val searchItem: MenuItem = menu.findItem(R.id.action_search_books)
        val searchView: SearchView = searchItem.actionView as SearchView
        searchView.queryHint = getString(R.string.hint_search_book)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    if (it.isNotEmpty())
                        mViewModel.searchBooksDebounced(it)
                    else
                        mViewModel.getAllBooks()
                }
                return false
            }
        })

        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun setUpLoader() {
        mProgressDialog = Dialog(requireActivity())
        mProgressDialog!!.setContentView(R.layout.dialog_custom_progress)
        mProgressDialog!!.setCancelable(false)
        mProgressDialog!!.setCanceledOnTouchOutside(false)
    }

    private fun showLoader() {
//        mProgressDialog?.show()
        mCircularProgressBar.visibility = View.VISIBLE
        mBinding.rvAllBooks.visibility = View.GONE
        mBinding.tvNoBooks.visibility = View.GONE
    }

    private fun stopLoader() {
//        mProgressDialog?.dismiss()
        mCircularProgressBar.visibility = View.GONE
        mBinding.rvAllBooks.visibility = View.VISIBLE
        mBinding.tvNoBooks.visibility = View.GONE
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mViewModel.getAllBooks()
    }

    override fun onResume() {
        super.onResume()
        if (requireActivity() is StudentMainActivity) {
            (requireActivity() as StudentMainActivity?)!!.showBottomNavigationView()
        }
    }

    fun goToBookDetails(book: Book.Book) {
        if (requireActivity() is StudentMainActivity) {
            (requireActivity() as StudentMainActivity?)!!.hideBottomNavigationView()
        }
        val bundle = bundleOf("book" to book)
        findNavController().navigate(R.id.action_nav_all_books_to_bookDetailsFragment, bundle)
    }
}


