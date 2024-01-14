package com.srinjoy.libbuddy.data.service

import com.srinjoy.libbuddy.core.Constants
import com.srinjoy.libbuddy.core.LibraryAPI
import com.srinjoy.libbuddy.models.Admin
import com.srinjoy.libbuddy.models.Book
import com.srinjoy.libbuddy.models.Student
import io.reactivex.rxjava3.core.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory

import retrofit2.converter.gson.GsonConverterFactory

class LibraryApiService {
    private val api = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .build()
        .create(LibraryAPI::class.java)

    fun addAdmin(
        bodyDataModel: Admin.BodyDataModel,
        token: String
    ): Single<Admin.AddResponseModel> {
        return api.addAdmin(bodyDataModel = bodyDataModel, token = token)
    }

    fun loginAdmin(bodyDataModel: Admin.BodyDataModel): Single<Admin.LoginResponseModel> {
        return api.loginAdmin(bodyDataModel);
    }

    fun addStudent(bodyDataModel: Student.StudentBodyDataModel): Single<Student.AddResponseModel> {
        return api.addStudent(bodyDataModel = bodyDataModel)
    }

    fun loginStudent(bodyDataModel: Student.StudentBodyDataModel): Single<Student.LoginResponseModel> {

        return api.loginStudent(bodyDataModel)
    }

    fun requestBorrow(
        data: Student.BorrowRequestBodyModel,
        token: String
    ): Single<Student.BorrowRequestResponseModel> {
        return api.requestBorrow(data, token)
    }

    fun getSelfProfile(token: String): Single<Student.ProfileModel> {
        return api.getSelfProfile(token)
    }

    fun getStudentProfile(id:String,token: String): Single<Student.ProfileModel> {
        return api.getStudentProfile(id=id,token=token)
    }

    fun getBooks(): Single<Book.BooksModel> {
        return api.getBooks()
    }

    fun getBook(id: String): Single<Book.Book> {
        return api.getBook(id)
    }

    fun addBook(data: Book.Book, token: String): Single<Book.ResponseModel> {
        return api.addBook(data, token)
    }

    fun deleteBook(id: String, token: String): Single<Book.SingleMessageResponseModel> {
        return api.deleteBook(id, token)
    }

    fun editBook(book: Book.Book, token: String): Single<Book.ResponseModel> {
        return api.editBook(data = book, token = token)
    }

    fun getRequests(token: String): Single<Admin.AllRequestsModel> {
        return api.getRequests(token)
    }

    fun issueBook(id: String,token: String) : Single<Book.SingleMessageResponseModel>{
        return api.issueBook(id, token)
    }

    fun rejectBookIssue(id: String,token: String) : Single<Book.SingleMessageResponseModel>{
        return api.rejectBookIssue(id, token)
    }

    fun searchStudents(query:String,token: String) : Single<Student.StudentsModel>{
        return api.searchStudents(query, token)
    }

    fun searchBooks(query: String) : Single<Book.BooksModel>{
        return api.searchBooks(query)
    }

    fun returnBook(data:Book.BorrowBodyDataModel,token: String) : Single<Book.SingleMessageResponseModel>{
        return api.returnBook(data, token)
    }
}