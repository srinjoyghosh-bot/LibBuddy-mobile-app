package com.srinjoy.libbuddy.data.repository

import com.srinjoy.libbuddy.data.service.LibraryApiService
import com.srinjoy.libbuddy.models.Student
import io.reactivex.rxjava3.core.Single

class StudentRepository(private val apiService: LibraryApiService) {
    fun login(data: Student.StudentBodyDataModel): Single<Student.LoginResponseModel> {
        return apiService.loginStudent(bodyDataModel = data);
    }

    fun add(data: Student.StudentBodyDataModel): Single<Student.AddResponseModel> {
        return apiService.addStudent(data);
    }

    fun borrowRequest(
        data: Student.BorrowRequestBodyModel,
        token: String
    ): Single<Student.BorrowRequestResponseModel> {
        return apiService.requestBorrow(data, token)
    }

    fun getProfile(token: String):Single<Student.ProfileModel>{
        return apiService.getSelfProfile(token)
    }
}