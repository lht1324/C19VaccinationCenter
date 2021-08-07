package com.overeasy.c19vaccinationcenter.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.UiThread
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
    private var backPressedLast: Long = 0
    private val mapFragment by lazy {
        MapFragment.newInstance(
            NaverMapOptions()
                .camera(CameraPosition(LatLng(36.0, 127.83), 5.7))
                .mapType(NaverMap.MapType.Navi)
                .zoomControlEnabled(false)
        ).also {
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

        // 비동기로 NaverMap 객체를 얻는다. NaverMap 객체가 준비되면 onMapReady() 메서드가 호출된다.
        mapFragment.getMapAsync(this)

        // '도움말' 버튼이 클릭되면 HelpDialog를 연다
        binding.helpButton.clicks().subscribe {
            HelpDialog(this@MainActivity).show()
        }
    }

    override fun onBackPressed() {
        if (System.currentTimeMillis() - backPressedLast < 2000) {
            finish()
            return
        }

        showShortToast("종료하려면 뒤로 가기 버튼을\n한 번 더 눌러주세요.")

        backPressedLast = System.currentTimeMillis()
    }

    @UiThread
    override fun onMapReady(naverMap: NaverMap) {
        binding.apply {
            zoom.map = naverMap
            initButton.clicks().subscribe {
                naverMap.moveCamera(
                    CameraUpdate
                        .toCameraPosition(CameraPosition(LatLng(36.0, 127.83), 5.7))
                        .animate(CameraAnimation.Easing)
                )
            }
        }
        naverMap.extent = LatLngBounds(LatLng(31.43, 122.37), LatLng(44.35, 132.0))

        viewModel.getCenterDatas().observe(this, {
            val iterator = it.iterator()

            while(iterator.hasNext()) {
                val centerData = iterator.next()

                centerData!!.marker!!.apply {
                    setOnClickListener {
                        showLongToast("센터명: ${centerData.centerName}\n" +
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

    private fun showShortToast(data: String) = Toast.makeText(this, data, Toast.LENGTH_SHORT).show()

    private fun showLongToast(data: String) = Toast.makeText(this, data, Toast.LENGTH_LONG).show()
}