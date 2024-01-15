package com.srinjoy.libbuddy.viewmodels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.JsonParser
import com.srinjoy.libbuddy.core.Utils
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import retrofit2.HttpException

open class BaseViewModel : ViewModel() {
    val loading = MutableLiveData<Boolean>()
    val success = MutableLiveData<Boolean>()
    val error = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String>()

    private val compositeDisposable = CompositeDisposable()

    fun startLoading() {
        loading.value = true
    }

    fun stopLoading() {
        loading.value = false
    }

    fun setError(err:Throwable) {
        errorMessage.value = getErrorMessage(err)
        error.value = true
    }

    fun setSuccess() {
        success.value = true
    }

    fun addDisposable(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    fun clearAllDisposables() {
        compositeDisposable.clear()
    }

    private fun getErrorMessage(error: Throwable): String {
        var message: String = "Some error occurred"
        if (error is HttpException) {
            val errorJsonString = error.response()?.errorBody()?.string()
            errorJsonString?.let {
                message = JsonParser().parse(errorJsonString)
                    .asJsonObject["message"]
                    .asString
            }
        }
        return message
    }
}