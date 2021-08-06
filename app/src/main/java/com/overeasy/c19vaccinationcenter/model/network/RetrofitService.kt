package com.overeasy.c19vaccinationcenter.model.network

import com.overeasy.c19vaccinationcenter.model.datasource.pojo.Centers
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitService {
    // https://api.odcloud.kr/api/15077586/v1/centers?page=1&perPage=10&serviceKey=bNmSjmL3NWL%2FmAmsQV0SyDT%2B8DCdZckhVg5%2FtSsmJHa47eBZBE%2BaFvCHYxeM1Dsz2FcgQ64elqYL3mr6GUyjOg%3D%3D
    // https://api.odcloud.kr/api/15077586/v1/centers?page={page}&perPage={perPage}&serviceKey={serviceKey}
    // page는 1씩 증가
    @GET("centers?")
    fun getCenters(
        @Query("page") page: Int,
        @Query("perPage") perPage: Int,
        @Query("serviceKey", encoded = true) serviceKey: String): Observable<Response<Centers>>
}