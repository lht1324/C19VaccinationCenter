package com.overeasy.c19vaccinationcenter.model.datasource

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import io.reactivex.Single

@Dao
interface VCenterDao {
    @Query("Select * from centerTable")
    fun getCenterDatas(): Single<List<CenterData>>

    @Insert
    fun insert(centerData: CenterData): Long

    @Insert
    fun insertAll(centerDatas: List<CenterData>)
}