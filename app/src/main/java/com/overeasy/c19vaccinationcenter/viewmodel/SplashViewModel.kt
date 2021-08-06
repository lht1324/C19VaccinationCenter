package com.overeasy.c19vaccinationcenter.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.overeasy.c19vaccinationcenter.model.repository.RepositoryImpl
import com.overeasy.c19vaccinationcenter.model.datasource.pojo.Center
import com.overeasy.c19vaccinationcenter.model.datasource.CenterData
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
    private val centerDatas = MutableLiveData<ArrayList<CenterData>>()

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
            val tempList = ArrayList<Center>()

            for (i in it)
                tempList.addAll(i)

            tempList
        }
        .observeOn(Schedulers.computation())
        .map {
            val resultList = ArrayList<CenterData>()

            for (i in it.indices) {
                resultList.add(CenterData().apply {
                    lat = it[i].lat.toDouble()
                    lng = it[i].lng.toDouble()
                    centerType = it[i].centerType
                    facilityName = it[i].facilityName
                })
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
                println("error: ${it.message}")
            }
        )
    )

    private fun insertAll(centerDatas: List<CenterData>) = repository.insertAll(centerDatas)

    private fun println(data: String) = Log.d("SplashViewModel", data)
}