package cz.strnad.stargate_dhd

import retrofit2.http.GET
import retrofit2.http.Path

interface GateService {

    @GET("/light/{pattern}")
    suspend fun light(@Path("pattern")pattern: String)
}