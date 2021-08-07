package com.overeasy.c19vaccinationcenter.model.datasource

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Single

@Dao
interface VCenterDao {
    @Query("Select * from centerDataTable")
    fun getCenterDatas(): Single<List<CenterData>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(centerDatas: List<CenterData>)
}