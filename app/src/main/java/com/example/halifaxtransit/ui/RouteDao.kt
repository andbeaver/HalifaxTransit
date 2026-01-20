package com.example.halifaxtransit.ui

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RouteDao {

    //Query to get all routes from the database
    @Query("SELECT * FROM routes")
    fun getAllRoutes(): Flow<List<RouteData>>

    //Insert a route into the database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoute(route: RouteData)

    // Update a route in the database
    @Update
    suspend fun updateRoute(route: RouteData)

    // Clear all selections in the database
    @Query("UPDATE routes SET IsSelected = 0")
    suspend fun clearAllSelections()
}