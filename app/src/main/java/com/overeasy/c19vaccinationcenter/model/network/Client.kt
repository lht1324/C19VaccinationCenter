package com.overeasy.c19vaccinationcenter.model.network

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class Client {
    private val baseUrl = "https://api.odcloud.kr/api/15077586/v1/"
    private val serviceKey = "bNmSjmL3NWL%2FmAmsQV0SyDT%2B8DCdZckhVg5%2FtSsmJHa47eBZBE%2BaFvCHYxeM1Dsz2FcgQ64elqYL3mr6GUyjOg%3D%3D"
    private val client = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }).build()

    fun getCentersData(page: Int) = getService().getCenters(page, 10, serviceKey)

    private fun getService(): RetrofitService = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addCallAdapterFactory((RxJava2CallAdapterFactory.create()))
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()
        .create(RetrofitService::class.java)

    private fun println(data: String) = Log.d("Client", data)
}