package com.srinjoy.libbuddy.data.repository

import com.srinjoy.libbuddy.data.service.LibraryApiService
import com.srinjoy.libbuddy.models.Admin
import com.srinjoy.libbuddy.models.Book
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

    fun deleteBook(id: String, token: String): Single<Book.DeleteResponseModel> {
        return apiService.deleteBook(id, token)
    }

    fun editBook(book: Book.Book, token: String): Single<Book.ResponseModel> {
        return apiService.editBook(book, token)
    }
}