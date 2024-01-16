package com.srinjoy.libbuddy.view.fragments.admin

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.srinjoy.libbuddy.R
import com.srinjoy.libbuddy.application.LibraryApplication
import com.srinjoy.libbuddy.databinding.FragmentAdminBooksBinding
import com.srinjoy.libbuddy.models.Book
import com.srinjoy.libbuddy.view.activities.AddEditBookActivity
import com.srinjoy.libbuddy.view.activities.AdminMainActivity
import com.srinjoy.libbuddy.view.adapters.AllBooksAdapter
import com.srinjoy.libbuddy.viewmodels.admin.AdminBooksViewModel
import com.srinjoy.libbuddy.viewmodels.admin.AdminBooksViewModelFactory

class AdminBooksFragment : Fragment() {


    private val mViewModel: AdminBooksViewModel by viewModels {
        AdminBooksViewModelFactory(repository = (requireActivity().application as LibraryApplication).bookRepository)
    }

    private var mBinding: FragmentAdminBooksBinding? = null
    private lateinit var mBooksAdapter: AllBooksAdapter
    private var mProgressDialog: Dialog? = null
    private lateinit var mCircularProgressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentAdminBooksBinding.inflate(inflater, container, false)
        return mBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mCircularProgressBar = mBinding!!.pbCircularBooks

        mBinding!!.rvAllBooks.layoutManager = GridLayoutManager(requireActivity(), 2)

        mBooksAdapter = AllBooksAdapter(this@AdminBooksFragment)


        mBinding!!.rvAllBooks.adapter = mBooksAdapter

        mBinding!!.srlAllBooks.setOnRefreshListener {
            mViewModel.getAllBooks(showLoader = false)
        }

        Log.i("ADMIN TOKEN",(requireActivity().application as LibraryApplication).prefs.token.toString())

        mBinding!!.floatingActionButton.setOnClickListener{
            val intent=Intent(requireActivity(),AddEditBookActivity::class.java)
            requireActivity().startActivity(intent)
        }

        setHasOptionsMenu(true)

//        setUpLoader()

        viewModelObservers()

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

    private fun viewModelObservers(){
        mViewModel.loading.observe(viewLifecycleOwner) { value ->
            value?.let {
                if(value){
                    showLoader()
                }else{
                    stopLoader()
                }
            }
        }

        mViewModel.success.observe(viewLifecycleOwner) { value ->
            value?.let {
                val books: List<Book.Book> = mViewModel.books.value ?: listOf()
                mBinding!!.srlAllBooks.isRefreshing = false
                if (books.isEmpty()) {
                    mBinding!!.tvNoBooks.visibility = View.VISIBLE
                    mBinding!!.rvAllBooks.visibility = View.GONE

                } else {
                    mBinding!!.tvNoBooks.visibility = View.GONE
                    mBinding!!.rvAllBooks.visibility = View.VISIBLE
                    mBooksAdapter.setBooks(books)
                }


            }

        }

        mViewModel.error.observe(viewLifecycleOwner) { value ->
            value?.let {
                if (value) {
                    mBinding!!.srlAllBooks.isRefreshing = false
                    Toast.makeText(
                        context,
                        mViewModel.errorMessage.value.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
    private fun setUpLoader(){
        mProgressDialog = Dialog(requireActivity())
        mProgressDialog!!.setContentView(R.layout.dialog_custom_progress)
        mProgressDialog!!.setCancelable(false)
        mProgressDialog!!.setCanceledOnTouchOutside(false)
    }

    private fun showLoader() {
//        mProgressDialog?.show()
        mCircularProgressBar.visibility = View.VISIBLE
        mBinding!!.rvAllBooks.visibility = View.GONE
        mBinding!!.tvNoBooks.visibility = View.GONE
    }

    private fun stopLoader() {
//        mProgressDialog?.dismiss()
        mCircularProgressBar.visibility = View.GONE
        mBinding!!.rvAllBooks.visibility = View.VISIBLE
        mBinding!!.tvNoBooks.visibility = View.GONE
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mViewModel.getAllBooks()
    }

    override fun onResume() {
        super.onResume()
        if (requireActivity() is AdminMainActivity) {
            (requireActivity() as AdminMainActivity?)!!.showBottomNavigationView()
        }
        if((requireActivity().application as LibraryApplication).prefs.loadBooks){
            Log.i("Books load karo","Karta hu")
            mViewModel.getAllBooks()
            (requireActivity().application as LibraryApplication).prefs.loadBooks=false
        }else{
            Log.i("Books load mt karo","Theek h bhai")
        }
    }

    fun goToBookDetails(book: Book.Book) {
        Log.i("ADMIN TOKEN",(requireActivity().application as LibraryApplication).prefs.token.toString())
        if (requireActivity() is AdminMainActivity) {
            (requireActivity() as AdminMainActivity?)!!.hideBottomNavigationView()
        }
        val bundle = bundleOf("book" to book)
        findNavController().navigate(R.id.action_adminBooksFragment_to_bookDetailsFragment2, bundle)
    }
}