package com.overeasy.c19vaccinationcenter.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.overeasy.c19vaccinationcenter.model.datasource.CenterData
import com.overeasy.c19vaccinationcenter.model.repository.RepositoryImpl
import com.overeasy.c19vaccinationcenter.model.datasource.pojo.Center
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.mergeAll
import io.reactivex.schedulers.Schedulers

class SplashViewModel(application: Application): ViewModel() {
    private val compositeDisposable by lazy {
        CompositeDisposable()
    }
    private val repository by lazy {
        RepositoryImpl(application)
    }
    private val downloadFinished = SingleLiveEvent<Void>()

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return SplashViewModel(application) as T
        }
    }

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }

    fun getDownloadFinished() = downloadFinished

    fun downloadDatas() = getAndProcessDatas()

    private fun getAndProcessDatas() = compositeDisposable.add(Observable.range(1, 10)
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.io())
        .map {
            repository.getCentersData(it)
        }
        .mergeAll()
        .map {
            // Response<Centers>를 여러 개 통지하는 하나의 Observable
            it.body()!!.data
        }.toList()
        .map {
            val resultList = ArrayList<Center>()

            it.forEach { centerList -> resultList.addAll(centerList) }

            resultList
        }
        .observeOn(Schedulers.computation())
        .map {
            val resultList = ArrayList<CenterData>()

            it.forEach { center ->
                resultList.add(centerToCenterData(center))
            }

            resultList
        }
        .observeOn(Schedulers.io())
        .map {
            insertAll(it.toList())
        }
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            {
                downloadFinished.call()
            },
            {
                println("error in getAndProcessData(): ${it.message}")
            }
        )
    )

    private fun centerToCenterData(center: Center) = CenterData().apply {
        id = center.id
        centerName = center.centerName
        centerType = center.centerType
        lat = center.lat.toDouble()
        lng = center.lng.toDouble()

        address = center.address
        createdAt = center.createdAt
        facilityName = center.facilityName
        phoneNumber = center.phoneNumber
        sido = center.phoneNumber
        sigungu = center.sigungu
        updatedAt = center.updatedAt
        zipCode = center.zipCode
    }

    private fun insertAll(centerDatas: List<CenterData>) = repository.insertAll(centerDatas)

    private fun println(data: String) = Log.d("SplashViewModel", data)
}