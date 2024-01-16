package com.srinjoy.libbuddy.viewmodels.admin

import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.srinjoy.libbuddy.application.LibraryApplication
import com.srinjoy.libbuddy.data.repository.AdminRepository
import com.srinjoy.libbuddy.models.Admin
import com.srinjoy.libbuddy.models.Book
import com.srinjoy.libbuddy.viewmodels.BaseViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers

class AdminRequestsViewModel(private val repository: AdminRepository) : BaseViewModel() {
    val requests = MutableLiveData<List<Book.IssueDetails>>()
    val approveSuccess = MutableLiveData<Boolean>()
    val rejectSuccess = MutableLiveData<Boolean>()
    val borrowPosition = MutableLiveData<Int>()

    fun getRequests(fragment: Fragment, showLoader: Boolean = true) {
        if (showLoader)
            startLoading()
        val token = (fragment.activity?.application as LibraryApplication?)!!.prefs.token
        addDisposable(
            repository.getRequests("Bearer $token").subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<Admin.AllRequestsModel>() {
                    override fun onSuccess(t: Admin.AllRequestsModel) {
                        requests.value = t.requests
                        if (showLoader)
                            stopLoading()
                        setSuccess()
                    }

                    override fun onError(e: Throwable) {
                        if (showLoader)
                            stopLoading()
                        setError(e)
                    }
                })
        )
    }

    fun approveBorrow(id: String, position: Int, fragment: Fragment) {
        startLoading()
        val token = (fragment.activity?.application as LibraryApplication?)!!.prefs.token
        addDisposable(
            repository.approveBorrowRequest(id, "Bearer $token").subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<Book.SingleMessageResponseModel>() {
                    override fun onSuccess(t: Book.SingleMessageResponseModel) {
                        borrowPosition.value = position
                        approveSuccess.value = true
                        stopLoading()
                    }

                    override fun onError(e: Throwable) {
                        stopLoading()
                        setError(e)
                    }

                })
        )
    }

    fun rejectBorrow(id: String, position: Int, fragment: Fragment) {
        startLoading()
        val token = (fragment.activity?.application as LibraryApplication?)!!.prefs.token
        addDisposable(
            repository.rejectBorrowRequest(id, "Bearer $token").subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<Book.SingleMessageResponseModel>() {
                    override fun onSuccess(t: Book.SingleMessageResponseModel) {
                        borrowPosition.value = position
                        rejectSuccess.value = true
                        stopLoading()
                    }

                    override fun onError(e: Throwable) {
                        stopLoading()
                        setError(e)
                    }

                })
        )
    }
}

class AdminRequestsViewModelFactory(private val repository: AdminRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminRequestsViewModel::class.java)) {
            return AdminRequestsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}