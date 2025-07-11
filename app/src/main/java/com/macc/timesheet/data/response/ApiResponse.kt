package com.macc.timesheet.data.response

data class ApiResponse(
    val success: Boolean,
    val message: String,
    val data: Any?
)