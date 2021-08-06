package com.overeasy.c19vaccinationcenter.model.repository

import android.app.Application
import com.overeasy.c19vaccinationcenter.model.datasource.CenterData
import com.overeasy.c19vaccinationcenter.model.network.Client
import com.overeasy.c19vaccinationcenter.model.datasource.VCenterDatabase

class RepositoryImpl(private val application: Application): Repository {
    private val client by lazy {
        Client()
    }
    private val vCenterDao by lazy {
        VCenterDatabase.getInstance(application)!!.vCenterDao()
    }

    override fun getCentersData(page: Int) = client.getCentersData(page)

    override fun getSavedCenterDatas() = vCenterDao.getCenterDatas()

    override fun insertAll(centerDatas: List<CenterData>) = vCenterDao.insertAll(centerDatas)
}