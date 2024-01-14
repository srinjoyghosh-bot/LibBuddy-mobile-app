package com.srinjoy.libbuddy.data.repository

import android.provider.ContactsContract.Profile
import com.srinjoy.libbuddy.data.service.LibraryApiService
import com.srinjoy.libbuddy.models.Admin
import com.srinjoy.libbuddy.models.Book
import com.srinjoy.libbuddy.models.Student
import io.reactivex.rxjava3.core.Single

class AdminRepository(private val apiService: LibraryApiService) {
    fun add(data: Admin.BodyDataModel, token: String): Single<Admin.AddResponseModel> {
        return apiService.addAdmin(bodyDataModel = data, token = token)
    }

    fun login(data: Admin.BodyDataModel): Single<Admin.LoginResponseModel> {
        return apiService.loginAdmin(data)
    }

    fun addBook(book: Book.Book, token: String): Single<Book.ResponseModel> {
        return apiService.addBook(book, token)
    }

    fun deleteBook(id: String, token: String): Single<Book.DeleteIssueResponseModel> {
        return apiService.deleteBook(id, token)
    }

    fun editBook(book: Book.Book, token: String): Single<Book.ResponseModel> {
        return apiService.editBook(book, token)
    }

    fun getRequests(token: String) : Single<Admin.AllRequestsModel>{
        return apiService.getRequests(token)
    }

    fun approveBorrowRequest(id: String,token: String):Single<Book.DeleteIssueResponseModel>{
        return apiService.issueBook(id, token)
    }

    fun rejectBorrowRequest(id: String,token: String):Single<Book.DeleteIssueResponseModel>{
        return apiService.rejectBookIssue(id, token)
    }

    fun searchStudents(query:String,token: String) : Single<Student.StudentsModel>{
        return apiService.searchStudents(query, token)
    }

    fun getStudentProfile(id: String,token: String) : Single<Student.ProfileModel>{
        return apiService.getStudentProfile(id, token)
    }
}