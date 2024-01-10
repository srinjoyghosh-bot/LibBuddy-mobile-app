package com.srinjoy.libbuddy.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable

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

    fun setError(message: String?) {
        errorMessage.value = message ?: "Some error occurred"
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
}