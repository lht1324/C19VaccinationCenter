package com.overeasy.c19vaccinationcenter.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.UiThread
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.overeasy.c19vaccinationcenter.R
import com.overeasy.c19vaccinationcenter.databinding.ActivityMainBinding
import com.overeasy.c19vaccinationcenter.viewmodel.MainViewModel

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityMainBinding
    // private var naverMap: NaverMap? = null
    private val mapFragment by lazy {
        MapFragment.newInstance().also {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.mapFragment, it as Fragment)
                .commit()
        }
    }
    private val viewModel by lazy {
        ViewModelProvider(this, MainViewModel.Factory(application)).get(MainViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mapFragment.getMapAsync(this)
        /* viewModel.getCenterDatas().observe(this, {
            val iterator = it.iterator()

            while(iterator.hasNext()) {
                val centerData = iterator.next()
                println("centerData[${centerData.id - 1}] = ${centerData.facilityName}")
                centerData.marker!!.map = naverMap
            }
        }) */

        // mapFragment.getMapAsync(this)
    }

    @UiThread
    override fun onMapReady(naverMap: NaverMap) {
        // this.naverMap = naverMap

        naverMap.mapType = NaverMap.MapType.Navi

        viewModel.getCenterDatas().observe(this, {
            val iterator = it.iterator()

            while(iterator.hasNext()) {
                val centerData = iterator.next()
                println("centerData[${centerData.id - 1}] = ${centerData.facilityName}")
                centerData.marker!!.map = naverMap
            }
        })
    }

    private fun println(data: String) = Log.d("MainActivity", data)
}