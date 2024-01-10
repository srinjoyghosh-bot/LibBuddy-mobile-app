package com.srinjoy.libbuddy.view.activities

import android.app.Dialog
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import com.srinjoy.libbuddy.R
import com.srinjoy.libbuddy.application.LibraryApplication
import com.srinjoy.libbuddy.core.Constants
import com.srinjoy.libbuddy.databinding.ActivityAddEditBookBinding
import com.srinjoy.libbuddy.models.Book
import com.srinjoy.libbuddy.viewmodels.AddEditBookViewModel
import com.srinjoy.libbuddy.viewmodels.AddEditBookViewModelFactory

class AddEditBookActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityAddEditBookBinding
    private var isEdit: Boolean = false
    private var mBook: Book.Book? = null
    private var mProgressDialog: Dialog? = null

    private val mViewModel: AddEditBookViewModel by viewModels {
        AddEditBookViewModelFactory((application as LibraryApplication).adminRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityAddEditBookBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        if (intent.hasExtra(Constants.EXTRA_BOOK_DETAILS)) {
            isEdit = true
            mBook = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(Constants.EXTRA_BOOK_DETAILS, Book.Book::class.java)
            } else {
                intent.getParcelableExtra<Book.Book>(Constants.EXTRA_BOOK_DETAILS)
            }
            setInfoIntoForm()
        }

        supportActionBar?.let {
            if (!isEdit)
                it.title = getString(R.string.title_add_book)
            else
                it.title = getString(R.string.title_edit_book)
            it.setDisplayHomeAsUpEnabled(true)
        }

        onBackPressedDispatcher.addCallback(
            this /* lifecycle owner */,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    finish()
                }
            })

        setUpLoader()

        viewModelObservers()

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_edit_book, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_save_book -> {
                if (isEdit) {
                    Toast.makeText(this, "Edit book", Toast.LENGTH_SHORT).show()
                } else {
                    checkData()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }

    private fun viewModelObservers() {
        mViewModel.loading.observe(this) { value ->
            value?.let {
                if (value)
                    showLoader()
                else
                    stopLoader()
            }
        }

        mViewModel.success.observe(this) { value ->
            value?.let {
                if (value) {
                    if (!isEdit)
                        Toast.makeText(this, getString(R.string.msg_book_added), Toast.LENGTH_SHORT)
                            .show()
                    else
                        Toast.makeText(
                            this,
                            getString(R.string.msg_book_updated),
                            Toast.LENGTH_SHORT
                        ).show()
                }
            }
        }

        mViewModel.error.observe(this) { value ->
            value?.let {
                if (value) {
                    Toast.makeText(
                        this,
                        mViewModel.errorMessage.value.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }


    private fun setInfoIntoForm() {
        mBinding.etBookName.setText(mBook!!.name)
        mBinding.etAuthor.setText(mBook!!.author)
        mBinding.etPublisher.setText(mBook!!.publisher)
        mBinding.etDescription.setText(mBook!!.description)
    }

    private fun checkData() {
        val title = mBinding.etBookName.text.toString().trim { it <= ' ' }
        val author = mBinding.etAuthor.text.toString().trim { it <= ' ' }
        val publisher = mBinding.etPublisher.text.toString().trim { it <= ' ' }
        val description = mBinding.etDescription.text.toString().trim { it <= ' ' }

        when {
            TextUtils.isEmpty(title) -> mBinding.tilBookName.error =
                getString(R.string.err_book_form, "book name")
            TextUtils.isEmpty(author) -> mBinding.tilAuthor.error =
                getString(R.string.err_book_form, "author name")
            TextUtils.isEmpty(publisher) -> mBinding.tilPublisher.error =
                getString(R.string.err_book_form, "publisher name")
            TextUtils.isEmpty(description) -> mBinding.tilBookDescription.error =
                getString(R.string.err_book_form, "description")
            else -> mViewModel.addBook(title, author, publisher, description, this)
        }

    }

    private fun setUpLoader() {
        mProgressDialog = Dialog(this)
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