package com.srinjoy.libbuddy.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

object Book {
    data class AddResponseModel(
        val book: Book,
        val message: String
    )

    data class AllBooksModel(
        val books: List<Book>
    )

    data class DeleteResponseModel(
        val message: String
    )

    @Parcelize
    data class Book(
        val author: String,
        val available: Boolean = true,
        val createdAt: String? = null,
        val description: String,
        val id: String? = null,
        val image_url: String? = null,
        val name: String,
        val publisher: String,
        val updatedAt: String? = null
    ) : Parcelable
}