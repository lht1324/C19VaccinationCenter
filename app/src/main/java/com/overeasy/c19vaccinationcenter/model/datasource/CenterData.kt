package com.overeasy.c19vaccinationcenter.model.datasource

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.naver.maps.map.overlay.Marker
import com.overeasy.c19vaccinationcenter.model.datasource.pojo.Center

@Entity(tableName = "centerTable", primaryKeys = ["centerType", "facilityName"])
class CenterData {
    var id = 0
    var lat = 0.0 //
    var lng = 0.0 //
    var centerType = "" //
    var facilityName = "" //

    var address = ""
    var centerName = ""
    var createdAt = ""
    var phoneNumber = ""
    var sido = ""
    var sigungu = ""
    var updatedAt = ""
    var zipCode = ""

    @Ignore
    var marker: Marker? = null
}