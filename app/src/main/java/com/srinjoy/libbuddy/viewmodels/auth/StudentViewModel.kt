package com.srinjoy.libbuddy.viewmodels.auth

import android.app.Activity
import android.content.Context
import android.os.Build
import android.provider.ContactsContract.CommonDataKinds.Email
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.srinjoy.libbuddy.application.LibraryApplication
import com.srinjoy.libbuddy.core.Utils
import com.srinjoy.libbuddy.data.repository.StudentRepository
import com.srinjoy.libbuddy.data.service.LibraryApiService
import com.srinjoy.libbuddy.models.Student
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers

class StudentViewModel : ViewModel() {
    private val apiService = LibraryApiService()
    private val repository = StudentRepository(apiService)

    val loading = MutableLiveData<Boolean>()
    val loginSuccess = MutableLiveData<Boolean>()
    val registerSuccess = MutableLiveData<Boolean>()
    val error = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String>()

    fun add(
        id: String,
        password: String,
        name: String,
        branch: String?,
        degree: String?,
        year: String?
    ) {
        loading.value = true
        val data: Student.StudentBodyDataModel = Student.StudentBodyDataModel(
            branch = branch,
            degree = degree,
            id = id,
            name = name,
            password = password,
            year = year
        )
        repository.add(data)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<Student.AddResponseModel>() {
                override fun onSuccess(t: Student.AddResponseModel) {
                    registerSuccess.value = true
                    loading.value = false
                }


                @RequiresApi(Build.VERSION_CODES.O)
                override fun onError(e: Throwable) {
                    errorMessage.value = Utils.getErrorMessage(e)
                    error.value = true
                    loading.value = false

                }

            })
    }

    fun login(
        id: String,
        password: String,
        activity: Activity

    ) {
        loading.value = true
        val data: Student.StudentBodyDataModel = Student.StudentBodyDataModel(
            branch = null,
            degree = null,
            id = id,
            name = null,
            password = password,
            year = null
        )
        repository.login(data)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<Student.LoginResponseModel>() {
                override fun onSuccess(t: Student.LoginResponseModel) {
                    loginSuccess.value = true
                    loading.value = false
                    (activity.application as LibraryApplication).prefs.isStudentLoggedIn = true
                    (activity.application as LibraryApplication).prefs.token = t.token
                }

                @RequiresApi(Build.VERSION_CODES.O)
                override fun onError(e: Throwable) {
                    errorMessage.value = Utils.getErrorMessage(e)
                    error.value = true
                    loading.value = false
                    e.printStackTrace()
                    print(e.message)
                }

            })
    }

}