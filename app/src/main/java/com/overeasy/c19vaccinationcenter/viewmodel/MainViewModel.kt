package com.overeasy.c19vaccinationcenter.viewmodel

import android.app.Application
import android.graphics.Color
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.MarkerIcons
import com.overeasy.c19vaccinationcenter.model.datasource.CenterData
import com.overeasy.c19vaccinationcenter.model.repository.RepositoryImpl
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MainViewModel(application: Application) : ViewModel() {
    private val compositeDisposable by lazy {
        CompositeDisposable()
    }
    private val repository by lazy {
        RepositoryImpl(application)
    }
    private val centerDatas = SingleLiveEvent<HashMap<String, CenterData>>()

    init {
        processData()
    }

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return MainViewModel(application) as T
        }
    }

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }

    fun getCenterDatas() = centerDatas

    private fun processData() = compositeDisposable.add(repository.getSavedCenterDatas()
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.computation())
        .map {
            val centerDataMap = HashMap<String, CenterData>()

            for (i in it.indices) {
                val centerData = it[i]

                it[i].marker = Marker().apply {
                    width = Marker.SIZE_AUTO
                    height = Marker.SIZE_AUTO
                    position = LatLng(centerData.lat, centerData.lng)
                    captionText = centerData.facilityName
                    icon = MarkerIcons.BLACK
                    iconTintColor = Color.parseColor(if (centerData.centerType == "지역") "#4AE18E" else "#008000")
                    captionMinZoom = 9.0
                    zIndex = if (centerData.centerType == "지역") 0 else 100
                    isHideCollidedSymbols = true
                    isHideCollidedCaptions = true
                }

                centerDataMap[it[i].facilityName] = it[i]
            }

            centerDataMap
        }
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            {
                // 맵을 내보낼까?
                // HashMap<String, CenterData>
                centerDatas.value = it
            },
            {
                println("error: ${it.message}")
            }
        )
    )

    private fun println(data: String) = Log.d("MainViewModel", data)
}