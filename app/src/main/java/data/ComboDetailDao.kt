package com.bebidas.donjorge.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ComboDetailDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComboDetail(detalle: ComboDetail)

    @Query("SELECT * FROM combo_details WHERE comboId = :comboId")
    fun getComboRecipe(comboId: Int): Flow<List<ComboDetail>>
}