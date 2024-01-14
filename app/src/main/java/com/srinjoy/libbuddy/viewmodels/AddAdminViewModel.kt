package com.srinjoy.libbuddy.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.srinjoy.libbuddy.application.LibraryApplication
import com.srinjoy.libbuddy.data.repository.AdminRepository
import com.srinjoy.libbuddy.data.repository.StudentRepository
import com.srinjoy.libbuddy.models.Admin
import com.srinjoy.libbuddy.view.fragments.admin.AddAdminFragment
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers

class AddAdminViewModel(private val repository: AdminRepository) : BaseViewModel() {
    fun addAdmin(email: String, password: String, fragment: AddAdminFragment) {
        startLoading()
        val token = (fragment.activity?.application as LibraryApplication?)!!.prefs.token
        val data = Admin.BodyDataModel(email, password)
        addDisposable(
            repository.add(data, "Bearer $token").subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<Admin.AddResponseModel>() {
                    override fun onSuccess(t: Admin.AddResponseModel) {
                        stopLoading()
                        setSuccess()
                    }

                    override fun onError(e: Throwable) {
                        stopLoading()
                        setError(e.message)
                    }
                })
        )

    }
}

class AddAdminViewModelFactory(private val adminRepository: AdminRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddAdminViewModel::class.java)) {
            return AddAdminViewModel(adminRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}