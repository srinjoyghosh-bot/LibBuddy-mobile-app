package com.srinjoy.libbuddy.core

import com.srinjoy.libbuddy.models.Admin
import com.srinjoy.libbuddy.models.Book
import com.srinjoy.libbuddy.models.Student
import io.reactivex.rxjava3.core.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface LibraryAPI {
    @POST(Constants.ADMIN_ENDPOINT + Constants.ADD_ENDPOINT)
    fun addAdmin(
        @Body bodyDataModel: Admin.BodyDataModel,
        @Header(Constants.TOKEN_HEADER) token: String
    ): Single<Admin.AddResponseModel>

    @POST(Constants.ADMIN_ENDPOINT + Constants.LOGIN_ENDPOINT)
    fun loginAdmin(@Body bodyDataModel: Admin.BodyDataModel): Single<Admin.LoginResponseModel>

    @POST(Constants.STUDENT_ENDPOINT + Constants.ADD_ENDPOINT)
    fun addStudent(@Body bodyDataModel: Student.StudentBodyDataModel): Single<Student.AddResponseModel>

    @POST(Constants.STUDENT_ENDPOINT + Constants.LOGIN_ENDPOINT)
    fun loginStudent(@Body bodyDataModel: Student.StudentBodyDataModel): Single<Student.LoginResponseModel>

    @POST(Constants.STUDENT_ENDPOINT + Constants.BORROW_REQUEST_ENDPOINT)
    fun requestBorrow(
        @Body data: Student.BorrowRequestBodyModel,
        @Header(Constants.TOKEN_HEADER) token: String
    ): Single<Student.BorrowRequestResponseModel>

    @GET(Constants.STUDENT_ENDPOINT + Constants.PROFILE_ENDPOINT)
    fun getSelfProfile(@Header(Constants.TOKEN_HEADER) token: String): Single<Student.ProfileModel>

    @GET(Constants.BOOK_ENDPOINT)
    fun getBooks(): Single<Book.AllBooksModel>

    @GET(Constants.BOOK_ENDPOINT + Constants.FIND_ENDPOINT)
    fun getBook(@Query("id") id: String): Single<Book.Book>

    @POST(Constants.BOOK_ENDPOINT + Constants.ADD_ENDPOINT)
    fun addBook(
        @Body data: Book.Book,
        @Header(Constants.TOKEN_HEADER) token: String
    ): Single<Book.AddResponseModel>
}