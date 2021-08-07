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

    init {
        getAndProcessDatas()
    }

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return SplashViewModel(application) as T
        }
    }

    // 뷰모델 제거 시 메모리 누수 방지를 위해 compositeDisposable.dispose()를 실행해 메모리 참조를 해제한다
    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }

    fun getDownloadFinished() = downloadFinished

    private fun getAndProcessDatas() = compositeDisposable.add(Observable.range(1, 10)
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.io())
        .map {
            repository.getCentersData(it)
        }
        .observeOn(Schedulers.computation())
        .mergeAll()
        .map {
            // Response<Centers>를 여러 개 통지하는 하나의 Observable
            it.body()!!.data
        }.toList()
        .map {
            val resultList = ArrayList<Center>()

            it.forEach { centerList ->
                resultList.addAll(centerList)
            }

            resultList
        }
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
                println("error in getAndProcessData() of SplashViewModel: ${it.message}")
            }
        )
    )

    private fun centerToCenterData(center: Center) = CenterData().apply {
        lat = center.lat.toDouble()
        lng = center.lng.toDouble()
        centerType = center.centerType
        facilityName = center.facilityName

        address = center.address
        centerName = center.centerName
        org = center.org
        phoneNumber = if (center.phoneNumber.isNotEmpty()) center.phoneNumber else "없음"
        zipCode = center.zipCode
    }

    private fun insertAll(centerDatas: List<CenterData>) = repository.insertAll(centerDatas)

    private fun println(data: String) = Log.d("SplashViewModel", data)
}