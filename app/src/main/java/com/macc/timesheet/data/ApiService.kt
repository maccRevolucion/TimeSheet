package com.macc.timesheet.data

import com.macc.timesheet.data.response.ApiResponse
import com.macc.timesheet.data.response.ApiResponseEmployee
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @FormUrlEncoded
    @POST("attendances")
    suspend fun postAsistencia(
        @Field("id_empleado") idEmployee: Int
    ): ApiResponse

    @GET("employees")
    suspend fun getEmployees(@Query("pages") page: Int): ApiResponseEmployee

    @GET("search/employee/{employee_name}/cedis/{id_cedis}")
    suspend fun searchEmployeeByName(
        @Path("employee_name") name: String,
        @Path("id_cedis") cedisId: Int
    ): ApiResponseEmployee

}