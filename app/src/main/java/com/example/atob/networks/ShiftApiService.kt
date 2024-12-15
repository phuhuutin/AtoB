package com.example.atob.networks

import com.example.atob.model.FindShift
import com.example.atob.model.UserShift
import com.example.atob.model.ShiftDTO
import com.example.atob.model.ClockInOutRecord
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ShiftApiService {
    @GET("/api/shift/user")
    suspend fun getAllShifts(): Response<List<UserShift>>

    @GET("/api/shift/{id}")
    suspend fun getShiftById(@Path("id") id: Long): Response<UserShift>

    @GET("/api/shift/getshifts/{employeeId}")
    suspend fun getShiftsAfterNow(@Path("employeeId") employeeId: Long): Response<Set<FindShift>>

    @POST("/api/shift")
    suspend fun createShift(@Body shiftDTO: ShiftDTO): Response<String>

    @PUT("/api/clock/{id}")
    suspend fun udpateClock(@Path("id") id: Long, @Body clockInOutRecords: ClockInOutRecord): Response<String>

    @DELETE("/api/shift/{id}")
    suspend fun deleteShift(@Path("id") id: Long): Response<String>

    @Headers("Content-Type: text/plain;charset=UTF-8")
    @POST("/api/shift/clock/{shiftId}")
    suspend fun addClockInOutRecord(@Path("shiftId") shiftId: Long): Response<String>

    @POST("/api/shift/{shiftId}/addEmployee")
    suspend fun addEmployeeToShift(@Path("shiftId") shiftId: Long): Response<String>


    @POST("/api/shift/{id}/drop")
    suspend fun dropShift(@Path("id") id: Long): Response<String>
}