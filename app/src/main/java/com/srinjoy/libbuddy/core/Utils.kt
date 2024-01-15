package com.srinjoy.libbuddy.core

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.JsonParser
import okhttp3.ResponseBody
import retrofit2.HttpException
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
object Utils {

    private const val dateTimePattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
    private const val datePatten = "dd-MM-yyyy"
    private val mDateTimeFormatter =
        DateTimeFormatter.ofPattern(dateTimePattern).withZone(ZoneOffset.UTC)
    private val dateFormatter = DateTimeFormatter.ofPattern(datePatten)
    fun formatDate(dateString: String): String {
        val dateTime = ZonedDateTime.parse(dateString, mDateTimeFormatter)
            .withZoneSameInstant(ZoneId.systemDefault())
        return dateTime.format(dateFormatter)
    }

    fun getFormattedDate(dateString: String): String {
        return dateString.substringBefore('T')
    }

    fun getErrorMessage(error: Throwable): String {
        var message: String = "Some error occurred"
        if (error is HttpException) {
            val errorJsonString = error.response()?.errorBody()?.string()
            errorJsonString?.let {
                message = JsonParser().parse(errorJsonString)
                    .asJsonObject["message"]
                    .asString
            }
        }
        return message
    }
}