package com.overeasy.c19vaccinationcenter.model.network

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class Client {
    // url은 제대로 들어간다
    private val baseUrl = "https://api.odcloud.kr/api/15077586/v1/"
    private val serviceKey = "bNmSjmL3NWL%2FmAmsQV0SyDT%2B8DCdZckhVg5%2FtSsmJHa47eBZBE%2BaFvCHYxeM1Dsz2FcgQ64elqYL3mr6GUyjOg%3D%3D"
    // encode and encodeError
    // bNmSjmL3NWL%2FmAmsQV0SyDT%2B8DCdZckhVg5%2FtSsmJHa47eBZBE%2BaFvCHYxeM1Dsz2FcgQ64elqYL3mr6GUyjOg%3D%3D
    // bNmSjmL3NWL%252FmAmsQV0SyDT%252B8DCdZckhVg5%252FtSsmJHa47eBZBE%252BaFvCHYxeM1Dsz2FcgQ64elqYL3mr6GUyjOg%253D%253D
    // decode: bNmSjmL3NWL/mAmsQV0SyDT+8DCdZckhVg5/tSsmJHa47eBZBE+aFvCHYxeM1Dsz2FcgQ64elqYL3mr6GUyjOg==
    private val clientBuilder = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })

    fun getCentersData(page: Int) = getService().getCenters(page, 10, serviceKey)

    private fun getService(): RetrofitService = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addCallAdapterFactory((RxJava2CallAdapterFactory.create()))
        .addConverterFactory(GsonConverterFactory.create())
        .client(clientBuilder.build())
        .build()
        .create(RetrofitService::class.java)

    private fun println(data: String) = Log.d("Client", data)
}