package com.overeasy.c19vaccinationcenter.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.UiThread
import androidx.appcompat.app.AlertDialog
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
            showHelpsDialog()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (System.currentTimeMillis() - backPressedLast < 2000) {
            finish()
            return
        }

        Toast.makeText(
            this@MainActivity,
            "종료하려면 뒤로 가기 버튼을\n한 번 더 눌러주세요.",
            Toast.LENGTH_SHORT
        ).show()

        backPressedLast = System.currentTimeMillis()
    }

    @UiThread
    override fun onMapReady(naverMap: NaverMap) {
        binding.apply {
            zoom.map = naverMap
            initButton.clicks().subscribe {
                naverMap.moveCamera(CameraUpdate
                    .toCameraPosition(CameraPosition(LatLng(36.0, 127.83), 5.7))
                    .animate(CameraAnimation.Easing))
            }
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

    private fun showHelpsDialog() = AlertDialog.Builder(this, R.style.DialogTheme)
        .setTitle("도움말")
        // .setMessage("마커를 터치하면 각 예방접종센터의 정보를 볼 수 있습니다.")
        /* .setPositiveButton("다음") { dialog, _ ->
            (dialog as AlertDialog).apply {
                setTitle("지도 초기화")
                setMessage("'지도 초기화' 버튼을 누르면 처음 시작할 때 위치로 지도를 초기화합니다.")
            }
        } */
        .setNegativeButton("완료") { dialog, _ ->
            dialog.dismiss()
        }
        .create()
        .show()

    private fun showHelpDialog(title: String) = AlertDialog.Builder(this, R.style.DialogTheme)
        .setTitle(title)
        .setMessage(resources.getString(resources.getIdentifier(if (title == "마커") "marker" else "initMap", "String", packageName)))
        .setPositiveButton("뒤로") { _, _ ->

        }
        .setNegativeButton("완료") { dialog, _ ->
            dialog.dismiss()
        }
        .create()
        .show()

    private fun println(data: String) = Log.d("MainActivity", data)

    private fun showToast(data: String) = Toast.makeText(this, data, Toast.LENGTH_LONG).show()
}