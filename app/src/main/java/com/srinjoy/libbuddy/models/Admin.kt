package com.srinjoy.libbuddy.models

object Admin {
    data class AddResponseModel(
        val admin: AdminModel?,
        val message: String
    )

    data class LoginResponseModel(
        val message: String,
        val token: String,
    )

    data class AdminModel(
        val createdAt: String,
        val email: String,
        val id: Int,
        val password: String,
        val updatedAt: String
    )

    data class BodyDataModel(
        val email: String,
        val password: String,
    )

    data class AllRequestsModel(
        val requests:List<Book.IssueDetails>
    )
}