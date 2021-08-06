package com.overeasy.c19vaccinationcenter.model.datasource

import androidx.room.Entity
import androidx.room.Ignore
import com.naver.maps.map.overlay.Marker

@Entity(tableName = "centerTable", primaryKeys = ["centerType", "facilityName"])
class CenterData {
    var lat = 0.0
    var lng = 0.0
    var centerType = ""
    var facilityName = ""

    @Ignore var marker: Marker? = null
}