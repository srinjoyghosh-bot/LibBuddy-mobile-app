package com.srinjoy.libbuddy.data.repository

import com.srinjoy.libbuddy.data.service.LibraryApiService
import com.srinjoy.libbuddy.models.Book
import io.reactivex.rxjava3.core.Single

class BookRepository(private val apiService: LibraryApiService) {
    fun getAll(): Single<Book.BooksModel> {
        return apiService.getBooks()
    }

    fun getBookById(id: String): Single<Book.Book> {
        return apiService.getBook(id)
    }

    fun search(query: String): Single<Book.BooksModel> {
        return apiService.searchBooks(query)
    }
}