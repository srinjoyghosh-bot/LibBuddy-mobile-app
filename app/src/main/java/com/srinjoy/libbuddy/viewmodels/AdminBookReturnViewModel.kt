package com.srinjoy.libbuddy.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.srinjoy.libbuddy.application.LibraryApplication
import com.srinjoy.libbuddy.data.repository.AdminRepository
import com.srinjoy.libbuddy.models.Book
import com.srinjoy.libbuddy.view.fragments.admin.AdminBookReturnFragment
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers

class AdminBookReturnViewModel(private val repository: AdminRepository) : BaseViewModel() {
    fun returnBook(bookId:String,studentId:String,fragment: AdminBookReturnFragment){
        startLoading()
        val token=(fragment.activity?.application as LibraryApplication?)!!.prefs.token
        val data=Book.BorrowBodyDataModel(bookId,studentId)
        addDisposable(repository.returnBook(data,"Bearer $token").subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(object : DisposableSingleObserver<Book.SingleMessageResponseModel>(){
            override fun onSuccess(t: Book.SingleMessageResponseModel) {
                stopLoading()
                setSuccess()
            }

            override fun onError(e: Throwable) {
                stopLoading()
                setError(e)
            }
        }))
    }
}

class AdminBookReturnViewModelFactory(private val adminRepository: AdminRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminBookReturnViewModel::class.java)) {
            return AdminBookReturnViewModel(adminRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}