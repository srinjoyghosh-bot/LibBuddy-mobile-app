package com.srinjoy.libbuddy.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

object Book {
    data class ResponseModel(
        val book: Book,
        val message: String
    )

    data class BooksModel(
        val books: List<Book>
    )

    data class DeleteIssueResponseModel(
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

    data class IssueDetails(
        val book_id: String,
        val createdAt: String,
        val id: Int,
        val status: String,
        val studentEnrollmentId: Int,
        val student_id: Int,
        val return_date: Any
    )
}