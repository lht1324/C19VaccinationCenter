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

    private val centerDatas = SingleLiveEvent<ArrayList<CenterData>>()

    init {
        processSavedData()
    }

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return MainViewModel(application) as T
        }
    }

    // 뷰모델 제거 시 메모리 누수 방지를 위해 compositeDisposable.dispose()를 실행해 메모리 참조를 해제한다
    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }

    fun getCenterDatas() = centerDatas

    // DB에서 저장된 CenterData의 리스트를 가져와 marker를 추가하고 LiveData의 value에 넣어준다
    private fun processSavedData() = compositeDisposable.add(repository.getSavedCenterDatas()
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.computation())
        .map {
            // val centerDataMap = HashMap<String, CenterData>()
            val centerDatas = ArrayList<CenterData>()

            for (i in it.indices) {
                val centerData = it[i]

                // 각 centerData의 Marker를 생성, map은 뷰에 가까운 객체이니 액티비티에서 LiveData의 변경이 관찰된 뒤 넣어준다.
                it[i].marker = Marker().apply {
                    width = Marker.SIZE_AUTO
                    height = Marker.SIZE_AUTO
                    position = LatLng(centerData.lat, centerData.lng)
                    captionText = centerData.facilityName
                    icon = MarkerIcons.BLACK
                    iconTintColor = Color.parseColor(if (centerData.centerType == "지역") "#4AE18E" else "#008000")
                    captionMinZoom = 8.4
                    zIndex = if (centerData.centerType == "지역") 0 else 100
                    isHideCollidedSymbols = true
                    isHideCollidedCaptions = true
                }

                centerDatas.add(it[i])
            }

            centerDatas
        }
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            {
                centerDatas.value = it
            },
            {
                println("error in processSavedData() of MainViewModel: ${it.message}")
            }
        )
    )

    private fun println(data: String) = Log.d("MainViewModel", data)
}