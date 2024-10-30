package com.example.atob.networks

import com.example.atob.model.Shift
import com.example.atob.model.ShiftDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ShiftApiService {
    @GET("/api/shift")
    suspend fun getAllShifts(): Response<List<Shift>>

    @GET("/api/shift/{id}")
    suspend fun getShiftById(@Path("id") id: Long): Response<Shift>

    @POST("/api/shift")
    suspend fun createShift(@Body shiftDTO: ShiftDTO): Response<String>

    @DELETE("/api/shift/{id}")
    suspend fun deleteShift(@Path("id") id: Long): Response<String>

    @POST("/api/shift/{shiftId}/addEmployee")
    suspend fun addEmployeeToShift(@Path("shiftId") shiftId: Long): Response<String>
}