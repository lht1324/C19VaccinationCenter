package com.overeasy.c19vaccinationcenter.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.UiThread
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.jakewharton.rxbinding4.view.clicks
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.*
import com.overeasy.c19vaccinationcenter.R
import com.overeasy.c19vaccinationcenter.databinding.ActivityMainBinding
import com.overeasy.c19vaccinationcenter.viewmodel.MainViewModel

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityMainBinding
    private val mapFragment by lazy {
        MapFragment.newInstance(NaverMapOptions()
            .camera(CameraPosition(LatLng(36.0, 127.83), 5.7))
            .mapType(NaverMap.MapType.Navi)
            .zoomControlEnabled(false)).also {
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

        supportActionBar!!.hide()

        mapFragment.getMapAsync(this)

        binding.helpButton.clicks().subscribe {
            showToast("버튼 확인")
        }
    }

    @UiThread
    override fun onMapReady(naverMap: NaverMap) {
        binding.zoom.map = naverMap
        binding.initButton.clicks().subscribe {
            naverMap.moveCamera(CameraUpdate
                    .toCameraPosition(CameraPosition(LatLng(36.0, 127.83), 5.7))
                    .animate(CameraAnimation.Easing))
        }
        naverMap.extent = LatLngBounds(LatLng(31.43, 122.37), LatLng(44.35, 132.0))

        viewModel.getCenterDatas().observe(this, {
            val iterator = it.keys.iterator()

            while(iterator.hasNext()) {
                val centerData = it[iterator.next()]

                centerData!!.marker!!.apply {
                    setOnClickListener {
                        showToast("센터명: ${centerData.centerName}\n" +
                                "센터 구분: ${centerData.centerType}\n" +
                                (if(centerData.org.isEmpty()) "" else "관할 기관: ${centerData.org}\n") +
                                "시설명: ${centerData.facilityName}\n" +
                                "주소: ${centerData.address} (${centerData.zipCode})\n" +
                                "전화번호: ${centerData.phoneNumber}")

                        true
                    }
                    map = naverMap
                }
            }
        })
    }

    private fun println(data: String) = Log.d("MainActivity", data)

    private fun showToast(data: String) = Toast.makeText(this, data, Toast.LENGTH_LONG).show()
}