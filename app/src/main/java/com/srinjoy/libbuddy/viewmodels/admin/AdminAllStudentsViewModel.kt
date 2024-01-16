package com.srinjoy.libbuddy.viewmodels.admin

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.srinjoy.libbuddy.application.LibraryApplication
import com.srinjoy.libbuddy.data.repository.AdminRepository
import com.srinjoy.libbuddy.models.Student
import com.srinjoy.libbuddy.viewmodels.BaseViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AdminAllStudentsViewModel(private val repository: AdminRepository) : BaseViewModel() {
    val students = MutableLiveData<List<Student.Student>>()

    private var searchJob: Job? = null

    fun searchDebounced(searchText: String,fragment: Fragment) {
        val token = (fragment.activity?.application as LibraryApplication?)!!.prefs.token
        searchJob?.cancel()
        startLoading()
        Log.i("TOKEN",token.toString())
        searchJob = viewModelScope.launch {
            delay(500)
            Log.i("In search debounced","true")
            if (token != null) {
                Log.i("In search debounced","token not null")
                search(searchText,token)
            }
        }
    }

    private fun search(query: String, token:String) {

        addDisposable(
            repository.searchStudents(query, "Bearer $token").subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<Student.StudentsModel>() {
                    override fun onSuccess(t: Student.StudentsModel) {
                        Log.i("In search","success")
                        students.value = t.students
                        stopLoading()
                        setSuccess()
                    }

                    override fun onError(e: Throwable) {
                        Log.e("In search debounced","error")
                        stopLoading()
                        setError(e)
                    }
                })
        )
    }
}

class AdminAllStudentsViewModelFactory(private val repository: AdminRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminAllStudentsViewModel::class.java)) {
            return AdminAllStudentsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}