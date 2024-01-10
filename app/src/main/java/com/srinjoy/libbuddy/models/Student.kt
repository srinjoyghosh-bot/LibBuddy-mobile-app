package com.srinjoy.libbuddy.models

object Student {
    data class AddResponseModel(
        val created: Boolean,
        val error: String?,
        val message: String,
        val student: Student
    )

    data class Student(
        val branch: String,
        val createdAt: String,
        val degree: String,
        val id: String? = null,
        val enrollment_id: String? = null,
        val image_url: Any,
        val name: String,
        val password: String,
        val updatedAt: String,
        val year: String
    )

    data class LoginResponseModel(
        val message: String,
        val token: String,
    )

    data class StudentBodyDataModel(
        val branch: String?,
        val degree: String?,
        val id: String,
        val name: String?,
        val password: String,
        val year: String?
    )

    data class BorrowRequestBodyModel(
        val book_id: String
    )

    data class BorrowRequestResponseModel(
        val issue_details: IssueDetails,
        val message: String
    )

    data class IssueDetails(
        val book_id: String,
        val createdAt: String,
        val id: Int,
        val status: String,
        val studentEnrollmentId: Int,
        val student_id: Int,
        val return_date: Any
    )

    data class ProfileModel(
        val borrow: Borrow,
        val student: Student
    )

    data class Borrow(
        val fine: Double,
        val history: List<IssueDetails>
    )


}