package com.srinjoy.libbuddy.viewmodels

import android.app.Activity
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.srinjoy.libbuddy.application.LibraryApplication
import com.srinjoy.libbuddy.data.repository.AdminRepository
import com.srinjoy.libbuddy.data.service.LibraryApiService
import com.srinjoy.libbuddy.models.Admin
import io.reactivex.disposables.CompositeDisposable

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers

class AdminViewModel : ViewModel() {
    private val apiService: LibraryApiService = LibraryApiService()
    private val repository: AdminRepository = AdminRepository(apiService)

    private val compositeDisposable = CompositeDisposable()

    val loading = MutableLiveData<Boolean>()
    val success = MutableLiveData<Boolean>()
    val error = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String>()

    fun add(email: String, password: String, token: String) {
        loading.value = true
//        compositeDisposable.add()
        repository.add(token = token, data = Admin.BodyDataModel(email, password))
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<Admin.AddResponseModel>() {
                override fun onSuccess(value: Admin.AddResponseModel) {
                    loading.value = false
                    success.value = true
                }

                override fun onError(e: Throwable) {
                    loading.value = false
                    errorMessage.value = e.message
                    error.value = true
                    e.printStackTrace()
                }
            })
    }

    fun login(email: String, password: String, activity: Activity) {
        loading.value = true
        repository.login(Admin.BodyDataModel(email, password))
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<Admin.LoginResponseModel>() {

                override fun onError(e: Throwable) {

                    errorMessage.value = e.message
                    error.value = true
                    loading.value = false
                    e.printStackTrace()
                }

                override fun onSuccess(t: Admin.LoginResponseModel) {

                    (activity.application as LibraryApplication).prefs.isAdminLoggedIn = true

                    (activity.application as LibraryApplication).prefs.token = t.token
                    loading.value = false
                    success.value = true
                }
            })
    }


}


