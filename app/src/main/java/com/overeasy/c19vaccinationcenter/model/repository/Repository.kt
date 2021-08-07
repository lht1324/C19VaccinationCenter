package com.overeasy.c19vaccinationcenter.model.repository

import com.overeasy.c19vaccinationcenter.model.datasource.CenterData
import com.overeasy.c19vaccinationcenter.model.datasource.pojo.Centers
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.Response

// Repository의 인터페이스
interface Repository {
    fun getCentersData(page: Int): Observable<Response<Centers>>

    fun getSavedCenterDatas(): Single<List<CenterData>>

    fun insertAll(centerDatas: List<CenterData>)
}