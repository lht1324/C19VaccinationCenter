package com.overeasy.c19vaccinationcenter.model.datasource.pojo

data class Center(
    val id: Int,
    val lat: String,
    val lng: String,
    val centerType: String,
    val facilityName: String,

    val address: String,
    val centerName: String,
    val createdAt: String,
    val phoneNumber: String,
    val sido: String,
    val sigungu: String,
    val updatedAt: String,
    val zipCode: String
)