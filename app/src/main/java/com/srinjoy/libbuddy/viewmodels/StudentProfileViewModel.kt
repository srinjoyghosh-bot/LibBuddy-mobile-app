package com.srinjoy.libbuddy.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.srinjoy.libbuddy.application.LibraryApplication
import com.srinjoy.libbuddy.data.repository.BookRepository
import com.srinjoy.libbuddy.data.repository.StudentRepository
import com.srinjoy.libbuddy.models.Student
import com.srinjoy.libbuddy.view.fragments.student.StudentProfileFragment
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers

class StudentProfileViewModel(private val studentRepository: StudentRepository) : BaseViewModel() {

    var student = MutableLiveData<Student.Student>()
    var fine = MutableLiveData<Double>()
    var history = MutableLiveData<List<Student.IssueDetails>>()
    fun getProfile(fragment: StudentProfileFragment, showLoader: Boolean = true) {
        if (showLoader)
            startLoading()
        val token = (fragment.activity?.application as LibraryApplication?)!!.prefs.token
        addDisposable(
            studentRepository.getProfile("Bearer $token").subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<Student.ProfileModel>() {
                    override fun onSuccess(t: Student.ProfileModel) {
                        student.value = t.student
                        fine.value = t.borrow.fine
                        history.value = t.borrow.history
                        setSuccess()
                        if (showLoader)
                            stopLoading()
                    }

                    override fun onError(e: Throwable) {
                        if (showLoader)
                            stopLoading()
                        setError(e.message.toString())

                    }

                })
        )
    }
}

class StudentProfileViewModelFactory(private val repository: StudentRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StudentProfileViewModel::class.java)) {
            return StudentProfileViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}